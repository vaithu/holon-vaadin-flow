package com.holonplatform.vaadin.flow.demo.config;

import com.holonplatform.core.utils.PrettySqlFormat;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * P6Spy message formatter that appends the <em>application call site</em> to every logged
 * statement, so it is possible to tell which line of code triggered a given SQL statement
 * (and therefore which code path is responsible for duplicate queries).
 *
 * <p>Enable it from {@code spy.properties}:
 * <pre>
 * logMessageFormat=com.holonplatform.vaadin.flow.demo.config.SqlCallerLogFormat
 * </pre>
 *
 * <p>Tuning system properties:
 * <ul>
 *   <li>{@code demo.sqlcaller.enabled} — {@code true} to resolve and append call sites.
 *       <strong>Off by default.</strong> Resolving a call site walks the thread stack on
 *       <em>every</em> statement, which is far too expensive to leave on under real
 *       concurrency; this is a development diagnostic, not a production feature.</li>
 *   <li>{@code demo.sqlcaller.packages} — comma separated package prefixes considered
 *       "application code" (default {@code com.holonplatform.vaadin.flow,com.iyensoft.vaadin.flow}).
 *       Set to {@code com.holonplatform} to also see Holon Datastore internals.</li>
 *   <li>{@code demo.sqlcaller.frames} — how many matching frames to print (default {@code 5}).</li>
 *   <li>{@code demo.sqlcaller.full} — {@code true} to print the whole (filtered) stack.</li>
 * </ul>
 *
 * <p>When disabled the formatter degrades to plain {@link PrettySqlFormat} output with no
 * measurable overhead, so it is safe to leave configured in {@code spy.properties}.</p>
 */
public class SqlCallerLogFormat implements MessageFormattingStrategy {

	/**
	 * Whether call-site resolution is active. Opt-in: enable with
	 * {@code -Ddemo.sqlcaller.enabled=true} while diagnosing duplicate or unexpected queries.
	 */
	private static final boolean ENABLED = Boolean.getBoolean("demo.sqlcaller.enabled");

	/** Shared walker — {@code StackWalker.getInstance()} is thread-safe and immutable. */
	private static final StackWalker WALKER = StackWalker.getInstance();

	/**
	 * Package prefixes considered "application code". Defaults to the Vaadin/Holon UI
	 * packages rather than all of {@code com.holonplatform}, so that persistence internals
	 * (Holon Datastore, Hibernate, Spring JDBC) do not crowd out the view or service frame
	 * that actually triggered the query.
	 */
	private static final String[] APP_PACKAGES = System
			.getProperty("demo.sqlcaller.packages", "com.holonplatform.vaadin.flow,com.iyensoft.vaadin.flow")
			.split("\\s*,\\s*");

	private static final int MAX_FRAMES = Integer.getInteger("demo.sqlcaller.frames", 5);

	private static final boolean FULL_STACK = Boolean.getBoolean("demo.sqlcaller.full");

	/** Frames that are never interesting as a "caller". */
	private static final String[] IGNORED_PREFIXES = { "com.p6spy.", "java.", "jdk.", "sun." };

	private static final String SELF = SqlCallerLogFormat.class.getName();

	private final PrettySqlFormat delegate = new PrettySqlFormat();

	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,
			String sql, String url) {
		final String message = delegate.formatMessage(connectionId, now, elapsed, category, prepared, sql, url);
		if (!ENABLED || sql == null || sql.isBlank()) {
			return message;
		}
		final String caller = resolveCaller();
		return caller.isEmpty() ? message : message + System.lineSeparator() + caller;
	}

	private static String resolveCaller() {
		final List<StackWalker.StackFrame> frames = WALKER
				.walk(s -> s.filter(SqlCallerLogFormat::isCandidate).limit(FULL_STACK ? 200 : MAX_FRAMES)
						.collect(Collectors.toList()));
		if (frames.isEmpty()) {
			return "";
		}
		return frames.stream()
				.map(f -> "    at " + f.getClassName() + "." + f.getMethodName() + "(" + f.getFileName() + ":"
						+ f.getLineNumber() + ")")
				.collect(Collectors.joining(System.lineSeparator(), "  SQL caller:" + System.lineSeparator(), ""));
	}

	private static boolean isCandidate(StackWalker.StackFrame frame) {
		final String cn = frame.getClassName();
		if (cn.startsWith(SELF)) {
			return false;
		}
		if (FULL_STACK) {
			// full diagnostic mode: keep everything (including p6spy/JDBC internals) so that
			// proxy nesting is visible
			return true;
		}
		for (String ignored : IGNORED_PREFIXES) {
			if (cn.startsWith(ignored)) {
				return false;
			}
		}
		for (String pkg : APP_PACKAGES) {
			if (!pkg.isBlank() && cn.startsWith(pkg)) {
				return true;
			}
		}
		return false;
	}
}
