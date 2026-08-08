# TimelineStepper

Vertical audit-log / event-history timeline with infinite-scroll lazy loading.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/TimelineStepper.java`
- **Signature:** `public class TimelineStepper extends Component implements HasSize, HasEnabled`

## Key APIs

### Factory methods

- `TimelineStepperBuilder builder()`

### Common methods

- `String value()`
- `AuditEntry actorRole(String r)`
- `AuditEntry detail(String d)`
- `AuditEntry severity(Severity s)`
- `AuditEntry severity(String s)`
- `AuditEntry category(String c)`
- `String toJson()`
- `void setPageSize(int size)`
- `int getPageSize()`
- `void setHasMore(boolean hasMore)`
- `boolean isHasMore()`
- `void setLoading(boolean loading)`

## Usage

```java
TimelineStepper component; // See source for constructor/builder options
```
