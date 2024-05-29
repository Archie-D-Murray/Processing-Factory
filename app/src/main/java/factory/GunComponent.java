package factory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Component where the gun rotates to look at the closest PVector in gunTarget
 */
public class GunComponent extends Component {
  private PVector[] gunTargets;
  private float rotationSpeed;
  
  public GunComponent(float rotation, float rotationSpeed, PVector[] gunTargets) {
    super(ImageDataBase.get("GunComponent.png"), rotation);
    this.rotationSpeed = rotationSpeed;
    this.gunTargets = gunTargets;
    this.value = 250;
  }
  
  @Override public void draw(PVector position) {
    PVector nearest = getNearestGunTarget(position);
    PVector lookAt = PVector.sub(getNearestGunTarget(position), position);
    rotation = Factory.approach(rotation, PApplet.PI - PVector.angleBetween(lookAt, new PVector(0f, -1f)), rotationSpeed / 60f);
    super.draw(position);
    Game.sketch.image(ImageDataBase.get("Crosshair.png"), nearest);
  }
  
  private PVector getNearestGunTarget(PVector position) {
    PVector nearest = new PVector(Float.MAX_VALUE, Float.MAX_VALUE);
    for (PVector target : gunTargets) {
      if (PVector.dist(position, target) < PVector.dist(position, nearest)) {
        nearest = target;
      }
    }
    return nearest;
  }
  
  @Override public BoundingBox getBoundingBox(PVector position) {
    //Collider will be attach point not entire gun
    return new BoundingBox(position, image.width / 2f);
  }
}
