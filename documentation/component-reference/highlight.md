# Highlight

KPI / metric card with five composable slots: <ul> <li><b>cardHeader</b>  – optional top row: left icon + right action (Image-2 project cards)</li> <li><b>prefix</b>      – left icon / avatar</li> <li><b>heading</b>     – small label (H1–H6 or Span)</li> <li><b>value</b>       – prominent number / text, with optional inline metric beside it</li> <li><b>details</b>     – flex row below the value (trend chips, sub-labels)</li> <li><b>suffix</b>      – right icon / badge</li> <li><b>footer</b>      – optional progress label + progress bar (Image-2 project cards)</li> </ul> <p>Layout variants: <ul> <li>{@link #setValueFirst(boolean) setValueFirst(true)} – value above heading (KPI numbers)</li> <li>{@link #setAccentColor(AccentColor)} – coloured left-border stripe</li> </ul>

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/Highlight.java`
- **Signature:** `public class Highlight extends Layout`

## Key APIs

### Factory methods

- `HighlightBuilder builder(String heading, String value)`
- `HighlightBuilder builder(Component prefix, String heading, String value)`

### Common methods

- `String getClassName()`
- `void setAccentColor(AccentColor color)`
- `void setValueFirst(boolean valueFirst)`
- `void setInlineMetric(Component... components)`
- `void setCardHeader(Component icon, Component action)`
- `void setProgress(double percent)`
- `void setProgressIndeterminate(boolean indeterminate)`
- `void setProgressLabel(String label)`
- `void setSparkline(Component chart)`
- `void setPrefix(Component... components)`
- `void setHeading(String heading)`
- `void setSubheading(String text)`

## Usage

```java
Highlight component; // See source for constructor/builder options
```
