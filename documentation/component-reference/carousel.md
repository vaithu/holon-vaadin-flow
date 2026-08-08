# Carousel

Accessible carousel (slideshow) component inspired by shadcn/ui Carousel.

## Class

- **Package:** `com.holonplatform.vaadin.flow.vaadinplus.components`
- **Source:** `core/src/main/java/com/holonplatform/vaadin/flow/vaadinplus/components/Carousel.java`
- **Signature:** `public class Carousel extends Div`

## Key APIs

### Factory methods

- `CarouselBuilder builder()`
- `CarouselBuilder builder(Orientation orientation)`

### Common methods

- `String getCssClass()`
- `int getPreviousIndex()`
- `int getCurrentIndex()`
- `Orientation getOrientation()`
- `void setOrientation(Orientation orientation)`
- `boolean isLoop()`
- `void setLoop(boolean loop)`
- `void addItem(Component... components)`
- `CarouselContent getContent()`
- `int getItemCount()`
- `int getCurrentIndex()`
- `void previous()`

## Usage

```java
Carousel component; // See source for constructor/builder options
```
