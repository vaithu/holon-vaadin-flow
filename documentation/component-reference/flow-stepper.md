# FlowStepper

A multi-step progress indicator that wraps the {@code <flow-stepper>} Shadow-DOM web component.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/FlowStepper.java`
- **Signature:** `public class FlowStepper extends Component implements HasSize, HasEnabled`

## Key APIs

### Factory methods

- `StepperBuilder builder()`

### Common methods

- `void setSteps(List<String> steps)`
- `void setSteps(String... steps)`
- `void setSteps(Localizable... steps)`
- `void setCurrentStep(int step)`
- `int getCurrentStep()`
- `void setOrientation(Orientation orientation)`
- `Orientation getOrientation()`
- `void setVariant(Variant variant)`
- `Variant getVariant()`
- `void setClickNavigation(ClickNavigation mode)`
- `ClickNavigation getClickNavigation()`
- `void goToStep(int index)`

## Usage

```java
FlowStepper component; // See source for constructor/builder options
```
