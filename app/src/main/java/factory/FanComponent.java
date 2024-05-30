package factory;

import processing.core.PVector;

/**
 * Concrete implementation of Component creating a fan
 * that spins around its attachment point
 */
public class FanComponent extends Component {
  
  private float rotationSpeed;
  
  protected FanComponent(float rotation, float rotationSpeed) {
    super(ImageDataBase.get("FanComponent.png"), rotation);
    this.rotationSpeed = rotationSpeed;
    this.stats = new Stats(5f, 0f, 0f, 10f);
  }
  
  // Have fan rotate
  @Override public void draw(PVector position) {
    super.draw(position);
    rotation += rotationSpeed / 60f;
  }
  
  @Override public BoundingBox getBoundingBox(PVector position) {
    // Circular so image.width == image.height
    return new BoundingBox(position, image.width);
  }
}
