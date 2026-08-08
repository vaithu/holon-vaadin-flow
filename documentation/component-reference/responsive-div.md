# ResponsiveDiv

A responsive container {@link Div} with a fluent builder API covering the five universal responsive layout patterns: <ol> <li><b>Stack → Inline</b> — flex-col on mobile-first, flex-row on desktop</li> <li><b>1-col → N-col Grid</b> — column count adapts per breakpoint</li> <li><b>Asymmetric Split</b> — explicit {@link ColSpan} per child</li> <li><b>Responsive Spacing</b> — gap scales per viewport</li> <li><b>Show / Hide</b> — visibility toggled at breakpoints</li> </ol> <h3>Default gap</h3> <p>Both {@link #flex()} and {@link #grid()} containers default to {@code gap-m} (1 rem) so content is never accidentally squished.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/ResponsiveDiv.java`
- **Signature:** `public class ResponsiveDiv extends Div`

## Key APIs

### Factory methods

- `FlexBuilder flex()`
- `GridBuilder grid()`
- `ModeSwitchBuilder modeSwitch()`
- `GridEntry of(Component component)`

### Common methods

- `DivConfigurator configure(Div div)`
- `ModeSwitchBuilder mobile(Supplier<Component> supplier)`
- `ModeSwitchBuilder desktop(Supplier<Component> supplier)`
- `ModeSwitchBuilder onModeChange(Consumer<ViewMode> listener)`
- `ResponsiveDiv build()`
- `B noGap()`
- `B id(String id)`
- `B styleName(String... names)`
- `B add(Component... components)`
- `B slotOnce(ViewMode mode, Supplier<Component> supplier)`
- `D build()`
- `B hide(ViewMode mode)`

## Usage

```java
ResponsiveDiv component; // See source for constructor/builder options
```
