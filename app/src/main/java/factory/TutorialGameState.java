package factory;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Shows a tutorial guide to help the player understand the controls
 */
public class TutorialGameState implements IState {
  private Button backButton;
  private PImage background;
  
  @Override public void onEnter() {
    background = Game.sketch.imageDataBase.get("Tutorial.png");
    background.resize(Game.sketch.width, Game.sketch.height);
    backButton = new Button(
        "Back", 
        new PVector(Game.sketch.width * 0.125f, Game.sketch.height * 0.925f), 
        new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.05f), 
        new int[] { 0xFF8888FF, 0xFFDDDDDD }
    );
    PApplet.println("Entered tutorial state");
  }
  
  @Override public void update() {
    Game.sketch.image(background, Game.sketch.width * 0.5f, Game.sketch.height * 0.5f);
    backButton.update();
  }
  
  @Override public void checkTransition() {
    if (backButton.isClicked) {
      Game.sketch.switchState((IState) new MenuGameState());
    }
  }
  
  @Override public void onExit() {}
  
  @Override public void keyDown(char key) {}
}
