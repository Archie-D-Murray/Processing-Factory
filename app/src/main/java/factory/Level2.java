package factory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Second level of the game with all components and product enabled
 */
public class Level2 extends PlayGameState {
  
  public Level2() {
    super(1000, 100f);
    this.lives = 3;
  }
  
  @Override public void checkTransition() {
    if (lives <= 0 || isTimeUp()) {
      Game.switchState((IState) new EndGameState(money));
    }
  }
  
  @Override public void keyDown(char key) {
    if (key == PApplet.CODED) {
      switch (Game.sketch.keyCode) {
      case PApplet.RIGHT:
        if (playerSelection == null) {
          break;
        }
        playerSelection.component.rotation = PApplet.HALF_PI;
        break;
      
      case PApplet.LEFT:
        if (playerSelection == null) {
          break;
        }
        playerSelection.component.rotation = 3f * PApplet.HALF_PI;
        break;
      
      case PApplet.UP:
        if (playerSelection == null) {
          break;
        }
        playerSelection.component.rotation = 0f;
        break;
      
      case PApplet.DOWN:
        if (playerSelection == null) {
          break;
        }
        playerSelection.component.rotation = PApplet.PI;
        break;
      
      default:
        break;
      }
    }
  }

  @Override protected PVector[] getConveyorPositions() {
  return new PVector[] {
      new PVector(0f, Game.sketch.height * 0.85f),
      new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.85f),
      new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.3f),
      new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.3f),
      new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.5f),
      new PVector(Game.sketch.width, Game.sketch.height * 0.5f),
    };
  }
  
  @Override protected void initSelectOptions() {
    componentOptions = new ComponentSelect[] {
      new ComponentSelect(ComponentType.FAN),
      new ComponentSelect(ComponentType.GUN),
      new ComponentSelect(ComponentType.SHIELD)
    };
  }

  @Override protected void initProductOptions() {
    productOptions = new ProductSelect[] {
      new ProductSelect(ProductType.LIGHT),
      new ProductSelect(ProductType.NORMAL),
      new ProductSelect(ProductType.HEAVY)
    };
  }
}
