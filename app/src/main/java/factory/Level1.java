package factory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Implementation of the PlayGameState class with some of the features of the game removed
 * (certain components are disabled)
 */
public class Level1 extends PlayGameState {
  
  public Level1() {
    super(500, 500f);
    this.lives = 3;
  }
  
  @Override public void checkTransition() {
    if (lives <= 0 || isTimeUp()) {
      Game.sketch.switchState(new Level2());
    }
  }
  
  @Override public void keyDown(char key) {
    if (Game.sketch.key == PApplet.CODED) {
      switch (Game.sketch.keyCode) {
      case PApplet.RIGHT:
        if (!crane.hasComponent()) {
          break;
        }
        crane.getComponent().rotation = PApplet.HALF_PI;
        break;

      case PApplet.LEFT:
        if (!crane.hasComponent()) {
          break;
        }
        crane.getComponent().rotation = 3f * PApplet.HALF_PI;
        break;

      case PApplet.UP:
        if (!crane.hasComponent()) {
          break;
        }
        crane.getComponent().rotation = 0f;
        break;

      case PApplet.DOWN:
        if (!crane.hasComponent()) {
          break;
        }
        crane.getComponent().rotation = PApplet.PI;
        break;

      default:
        break;
      }
    }
  }
  
  @Override protected  PVector[] getConveyorPositions() {
    return new PVector[] {
      new PVector(0f, Game.sketch.height * 0.8f),
      new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.8f),
      new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.5f),
      new PVector(Game.sketch.width, Game.sketch.height * 0.5f),
    };
  }
  
  @Override protected void initSelectOptions() {
    componentOptions = new ComponentSelect[] {
      new ComponentSelect(ComponentType.FAN),
      new ComponentSelect(ComponentType.SHIELD)
    };
  }

  @Override protected void initProductOptions() {
    productOptions = new ProductSelect[] {
      new ProductSelect(ProductType.LIGHT),
      new ProductSelect(ProductType.NORMAL)
    };
  }
}
