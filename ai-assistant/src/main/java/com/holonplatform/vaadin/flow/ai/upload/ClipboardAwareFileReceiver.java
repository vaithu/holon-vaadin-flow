package com.holonplatform.vaadin.flow.ai.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.ai.common.AIAttachment;
import com.vaadin.flow.component.ai.ui.AIFileReceiver;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

/**
 * A dependency-free {@link AIFileReceiver} implementation that merges attachments coming from
 * <b>two</b> distinct end-user actions into a single queue consumed by the {@code AIOrchestrator}:
 * <ul>
 *   <li>files dropped/browsed through a regular {@link Upload} component
 *       ({@link #getUpload()}), and</li>
 *   <li>images pasted directly from the OS clipboard (e.g. a screenshot) anywhere within this
 *       component's subtree, captured through a small client-side {@code paste} event listener —
 *       no extra npm/web-component dependency is required, just plain browser APIs
 *       ({@code ClipboardEvent}, {@code FileReader}).</li>
 * </ul>
 *
 * <p>This class extends {@link Component} (tag {@code <div style="display:contents">}) purely so
 * it can register the client-side paste listener and expose the matching {@link #onClipboardPaste}
 * {@code @ClientCallable} target; {@code display:contents} means it never affects layout — only
 * its {@link #getUpload() Upload} child is visually rendered.
 *
 * <p><b>Guardrails:</b> a maximum attachment count and a maximum per-file size are enforced (both
 * configurable) to avoid unbounded server-side memory usage, since every attachment is held
 * in-memory as a {@code byte[]} until the next chat submission consumes it.
 *
 * @see com.holonplatform.vaadin.flow.ai.components.AIChatClient
 */
@Tag("div")
public class ClipboardAwareFileReceiver extends Component implements AIFileReceiver {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Default maximum number of pending attachments held at once. */
    public static final int DEFAULT_MAX_ATTACHMENTS = 5;

    /** Default maximum size, in bytes, accepted for a single attachment (10 MB). */
    public static final long DEFAULT_MAX_FILE_SIZE = 10L * 1024 * 1024;

    private final transient List<AIAttachment> pending = new ArrayList<>();
    private final Upload upload;

    private int maxAttachments = DEFAULT_MAX_ATTACHMENTS;
    private long maxFileSize = DEFAULT_MAX_FILE_SIZE;
    private Set<String> acceptedMimePrefixes;
    private boolean clipboardPasteEnabled = true;
    private boolean pasteListenerRegistered = false;

    public ClipboardAwareFileReceiver() {
        getElement().getStyle().set("display", "contents");

        UploadHandler handler = UploadHandler.inMemory((metadata, data) -> addAttachment(
                metadata.fileName(), metadata.contentType(), data));
        this.upload = new Upload(handler);
        this.upload.setDropAllowed(true);
        this.upload.setMaxFiles(DEFAULT_MAX_ATTACHMENTS);
        this.upload.setMaxFileSize((int) DEFAULT_MAX_FILE_SIZE);
        getElement().appendChild(upload.getElement());
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (clipboardPasteEnabled && !pasteListenerRegistered) {
            registerPasteListener();
            pasteListenerRegistered = true;
        }
    }

    @Override
    protected void onDetach(com.vaadin.flow.component.DetachEvent detachEvent) {
        // PERFORMANCE FIX: Remove paste event listener to prevent memory leaks.
        // Without this cleanup, the listener would persist in the document even after
        // this component is removed from the UI, causing memory accumulation and duplicate
        // event handling if the component is re-attached.
        if (pasteListenerRegistered) {
            unregisterPasteListener();
            pasteListenerRegistered = false;
        }
        super.onDetach(detachEvent);
    }

    private void registerPasteListener() {
        // Listens for paste anywhere in the document while this component is attached; images
        // found in the clipboard data are base64-encoded client-side and handed to the
        // @ClientCallable below. Plain browser APIs only — no additional web component needed.
        getElement().executeJs("""
                const listener = (event) => {
                    const items = event.clipboardData && event.clipboardData.items;
                    if (!items) { return; }
                    for (const item of items) {
                        if (item.kind !== 'file') { continue; }
                        const file = item.getAsFile();
                        if (!file) { continue; }
                        const reader = new FileReader();
                        reader.onload = () => {
                            const dataUrl = reader.result;
                            const base64 = dataUrl.substring(dataUrl.indexOf(',') + 1);
                            this.$server.onClipboardPaste(file.name || 'clipboard-image',
                                file.type || 'application/octet-stream', base64);
                        };
                        reader.readAsDataURL(file);
                    }
                };
                document.addEventListener('paste', listener);
                this._aiClipboardPasteListener = listener;
                """);
    }

    private void unregisterPasteListener() {
        // PERFORMANCE: Remove the paste listener from the document to prevent memory leaks.
        // This is critical because the listener was registered on the global document object,
        // not on the component's element, so it persists even after component detachment.
        getElement().executeJs("""
                if (this._aiClipboardPasteListener) {
                    document.removeEventListener('paste', this._aiClipboardPasteListener);
                    this._aiClipboardPasteListener = null;
                }
                """);
    }

    /**
     * Invoked from the client when an image (or other file) is pasted from the clipboard while
     * this component is attached to the page.
     *
     * @param fileName a best-effort file name (browsers rarely provide one for pasted images)
     * @param mimeType the pasted item's MIME type
     * @param base64Data the raw bytes, base64-encoded
     */
    @com.vaadin.flow.component.ClientCallable
    private void onClipboardPaste(String fileName, String mimeType, String base64Data) {
        try {
            byte[] data = Base64.getDecoder().decode(base64Data);
            addAttachment(fileName, mimeType, data);
            showFeedback("Image attached from clipboard — press Enter to send it.");
        } catch (IllegalArgumentException e) {
            showFeedback("Could not read the pasted clipboard content.");
        }
    }

    private synchronized void addAttachment(String fileName, String mimeType, byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        if (data.length > maxFileSize) {
            showFeedback("The file exceeds the maximum allowed size and was not attached.");
            return;
        }
        if (acceptedMimePrefixes != null && acceptedMimePrefixes.stream().noneMatch(
                prefix -> mimeType != null && mimeType.startsWith(prefix))) {
            showFeedback("This file type is not accepted and was not attached.");
            return;
        }
        if (pending.size() >= maxAttachments) {
            showFeedback("Maximum number of attachments reached — send the current message first.");
            return;
        }
        pending.add(new AIAttachment(fileName != null ? fileName : "attachment", mimeType, data));
    }

    private void showFeedback(String message) {
        getUI().ifPresent(ui -> ui.access(() -> {
            Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }));
    }

    @Override
    public synchronized List<AIAttachment> takeAttachments() {
        List<AIAttachment> taken = new ArrayList<>(pending);
        pending.clear();
        return taken;
    }

    /**
     * Returns the internal {@link Upload} component (drag &amp; drop / click-to-browse) — add it
     * to your layout, or use {@link com.holonplatform.vaadin.flow.ai.components.AIChatClient} which
     * already does so.
     *
     * @return the upload component
     */
    public Upload getUpload() {
        return upload;
    }

    /**
     * Sets the maximum number of attachments held in the pending queue at once (default
     * {@value #DEFAULT_MAX_ATTACHMENTS}). Also applied as the {@link Upload}'s max file count.
     *
     * @param maxAttachments the maximum attachment count, must be &gt; 0
     */
    public void setMaxAttachments(int maxAttachments) {
        if (maxAttachments <= 0) {
            throw new IllegalArgumentException("maxAttachments must be > 0");
        }
        this.maxAttachments = maxAttachments;
        this.upload.setMaxFiles(maxAttachments);
    }

    /**
     * Sets the maximum size, in bytes, accepted for a single attachment (default
     * {@value #DEFAULT_MAX_FILE_SIZE}, 10&nbsp;MB). Also applied to the {@link Upload}.
     *
     * @param maxFileSize the maximum file size in bytes, must be &gt; 0
     */
    public void setMaxFileSize(long maxFileSize) {
        if (maxFileSize <= 0) {
            throw new IllegalArgumentException("maxFileSize must be > 0");
        }
        this.maxFileSize = maxFileSize;
        this.upload.setMaxFileSize((int) Math.min(maxFileSize, Integer.MAX_VALUE));
    }

    /**
     * Restricts accepted MIME types by prefix (e.g. {@code "image/"}, {@code "application/pdf"}).
     * Applies to both drag/drop-uploaded and clipboard-pasted content. {@code null} (the default)
     * accepts anything.
     *
     * @param mimeTypePrefixes accepted MIME type prefixes, or {@code null} to accept any type
     */
    public void setAcceptedMimeTypePrefixes(Set<String> mimeTypePrefixes) {
        this.acceptedMimePrefixes = mimeTypePrefixes;
        if (mimeTypePrefixes != null) {
            this.upload.setAcceptedMimeTypes(mimeTypePrefixes.toArray(new String[0]));
        } else {
            this.upload.setAcceptedMimeTypes();
        }
    }

    /**
     * Enables or disables clipboard-paste capture (enabled by default). Has no effect once the
     * component has already been attached and the listener registered — set this before adding
     * the component to a layout.
     *
     * @param clipboardPasteEnabled {@code true} to enable capturing pasted files
     */
    public void setClipboardPasteEnabled(boolean clipboardPasteEnabled) {
        this.clipboardPasteEnabled = clipboardPasteEnabled;
    }

}
