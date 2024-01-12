package factory;

import processing.core.PImage;
import processing.core.PVector;

/**
 * Simple object for player to avoid touching with mouse
 * As storing delegates is not in java, code responsible for 
 * acting on call of isTouchingMouse() must be handled elsewhere
 */
public class Asteroid {
  private PVector position;
  private PImage sprite;
  private float rotation;
  
  public Asteroid(PVector position, float rotation) {
    this.position = position;
    this.rotation = rotation;
    this.sprite = Game.sketch.imageDataBase.get("Asteroid.png").copy();
  }
  
  public void render() {
    Game.sketch.pushMatrix();
    Game.sketch.translate(position);
    Game.sketch.rotate(rotation);
    Game.sketch.image(sprite, 0f, 0f);
    Game.sketch.popMatrix();
  }
  
  public boolean isTouchingMouse() {
    float sqRad = (sprite.width * 0.5f) * (sprite.width * 0.5f);
    PVector mousePos = Game.sketch.getMousePosition();
    return PVector.sub(mousePos, position).magSq() < sqRad;
  }
}
