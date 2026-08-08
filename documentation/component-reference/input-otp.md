# InputOTP

A One-Time Password (OTP) input that groups individual character slots into a single, accessible, visually unified control.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/InputOTP.java`
- **Signature:** `public class InputOTP extends Div`

## Key APIs

### Factory methods

- `InputOTPBuilder builder()`

### Common methods

- `void add(Component... components)`
- `String getValue()`
- `void setValue(String value)`
- `void clear()`
- `int getLength()`
- `void setPattern(String pattern)`
- `void setReadOnly(boolean readOnly)`
- `void setEnabled(boolean enabled)`
- `void setValidator(SerializableFunction<String, String> validator)`
- `void setErrorMessage(String message)`
- `String getErrorMessage()`
- `void clearError()`

## Usage

```java
InputOTP component; // See source for constructor/builder options
```
