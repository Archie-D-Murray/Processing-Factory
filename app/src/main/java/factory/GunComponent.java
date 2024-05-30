package factory;

import processing.core.PVector;

/**
 * Component where the gun rotates to look at the closest PVector in gunTarget
 */
public class GunComponent extends Component {
  private float rotationSpeed;
  
  protected GunComponent(float rotation, float rotationSpeed) {
    super(ImageDataBase.get("GunComponent.png"), rotation);
    this.rotationSpeed = rotationSpeed;
    this.stats = new Stats(0f, 10f, 0f, 15f);
  }
  
  @Override public void draw(PVector position) {
    rotation += rotationSpeed * Game.deltaTime;
    super.draw(position);
  }
  
  @Override public BoundingBox getBoundingBox(PVector position) {
    //Collider will be attach point not entire gun
    return new BoundingBox(position, image.width / 2f);
  }
}
