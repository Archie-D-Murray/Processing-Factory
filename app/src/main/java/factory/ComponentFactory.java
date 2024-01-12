package factory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Factory class used to define all component creation logic in one place
 */
public class ComponentFactory {
  
  private PVector[] gunPositions = new PVector[] { new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.5f), new PVector(Game.sketch.width * 0.6f, Game.sketch.height * 0.75f) };
  
  public Component createComponent(ComponentType type) {
    switch (type) {
    
    case FAN:
      return new FanComponent(0f, 1f);
      
    case GUN:
      return new GunComponent(-PApplet.HALF_PI, 10f, gunPositions);
      
    case SHIELD:
      return new ShieldComponent(0f, 0x88FFBB00, 200);
      
    default:
      return null;
    }
  }
}
