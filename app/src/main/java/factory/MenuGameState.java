package factory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Menu state handling all logic before the player enters the game
 */

class MenuGameState implements IState {

  private Button playButton, quitButton, tutorialButton;

  /**
   * Create start and quit buttons
   */
  @Override public void onEnter() {
    playButton = new Button(
        "Play", 
        new PVector(Game.sketch.width * 0.5f, Game.sketch.height * 0.3f), 
        new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.1f), 
        new int[] { 0xFF88FF88, 0xFFDDDDDD }
    );
    tutorialButton = new Button(
        "Tutorial", 
        new PVector(Game.sketch.width * 0.5f, Game.sketch.height * 0.5f), 
        new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.1f), 
        new int[] { 0xFF8888FF, 0xFFDDDDDD }
    );
    quitButton = new Button(
        "Quit", 
        new PVector(Game.sketch.width * 0.5f, Game.sketch.height * 0.7f), 
        new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.1f), 
        new int[] { 0xFFFF8888, 0xFFDDDDDD }
    );
    PApplet.println("Entered menu state");
  }

  @Override public void update() {
    Game.sketch.textSize(48f);
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.text("Factory Game", Game.sketch.width * 0.5f - Game.sketch.textWidth("Factory Game") * 0.5f, Game.sketch.height * 0.15f + Game.sketch.textAscent() * 0.5f);
    Game.sketch.textSize(18f);
    playButton.update();
    tutorialButton.update();
    quitButton.update();
  }

  /**
   * Act on button clicks
   */
  @Override public void checkTransition() {
    if (playButton.isClicked) {
      Game.switchState(new SelectionGameState());
      return;
    }
    
    if (tutorialButton.isClicked) {
      Game.switchState((IState) new TutorialGameState());
    }
    
    if (quitButton.isClicked) {
      Game.sketch.exit();
      return;
    }
  }
  
  @Override public void onExit() { }
  @Override public void keyDown(char key) { }
}
