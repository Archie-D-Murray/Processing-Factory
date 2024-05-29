package factory;

import java.io.File;
import java.io.IOException;

import processing.core.PApplet;
import processing.core.PVector;

enum EndState { WIN, LOSE, LOWER_SCORE }

/**
 * Handles the various end states of the game
 * Player can win (new high score)
 * Player can win with a lower score (shows current high score)
 * Player can lose (score < 0)
 */
public class EndGameState implements IState {
  private int score;
  private EndState state;
  private Button quit;
  private int previousHighScore = 0;
  
  public EndGameState(int score) {
    this.score = score;
    
    if (score > 0) {
      state = checkNewHighScore() ? EndState.WIN : EndState.LOWER_SCORE;
    } else {
      state = EndState.LOSE;
    }
  }
  
  public void onEnter() {
    PApplet.println("Entered end screen state");
    if (state == EndState.WIN) {
      saveHighScore();
    }
    quit = new Button("Quit", new PVector(Game.sketch.width * 0.5f, Game.sketch.height * 0.75f), new PVector(200, 100), new int[] { 0xFFFF8888, 0xFFDDDDDD } );
    ImageDataBase.get("WinSplash.png").resize(Game.sketch.width, Game.sketch.height);
    ImageDataBase.get("LowerScoreSplash.png").resize(Game.sketch.width, Game.sketch.height);
    ImageDataBase.get("LoseSplash.png").resize(Game.sketch.width, Game.sketch.height);
  }

  /**
   * Handles drawing for different game states
   */
  public void update() {
    switch (state) {
      case WIN:
      drawWinScreen();
      break;
    case LOWER_SCORE:
      drawLowerScoreScreen();
      break;
    
    case LOSE:
      drawLoseScreen();
      break;
    }
    quit.update();
    if (quit.isClicked) {
      Game.sketch.exit();
    }
  }
  
  @Override public void checkTransition() {}
  @Override public void onExit() {}
  @Override public void keyDown(char key) {}
  
  /**
   * Loads previous high score from disk and return whether the previous highscore was higher or at least > 0
   */
  private boolean checkNewHighScore() {
    try {
      String[] previousHighScoreStrings = Game.sketch.loadStrings(Game.sketch.sketchPath() + File.separator + "Highscore.txt");
      if (previousHighScoreStrings != null && previousHighScoreStrings.length > 0) {
        previousHighScore = Integer.parseInt(previousHighScoreStrings[0]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return score >= previousHighScore;
  }
  
  private void drawWinScreen() {
    Game.sketch.background(0xFF000000);
    Game.sketch.image(ImageDataBase.get("WinSplash.png"), Game.sketch.width * 0.5f, Game.sketch.height * 0.5f);
    Game.sketch.textSize(18f);
    Game.sketch.fill(0xFFFFFFFF);
    String scoreText = String.format("You Win!\nScore: %d", score);
    Game.sketch.text(scoreText, Game.sketch.width * 0.5f - Game.sketch.textWidth(scoreText) * 0.5f, Game.sketch.height * 0.5f - Game.sketch.textAscent() * 0.5f);
  }
  
  private void drawLoseScreen() {
    Game.sketch.background(0xFF000000);
    Game.sketch.image(ImageDataBase.get("LoseSplash.png"), Game.sketch.width * 0.5f, Game.sketch.height * 0.5f);
    Game.sketch.textSize(18f);
    Game.sketch.fill(0xFFFFFFFF);
    String scoreText = String.format("You Lose!\nScore: %d", score);
    Game.sketch.text(scoreText, Game.sketch.width * 0.5f - Game.sketch.textWidth(scoreText) * 0.5f, Game.sketch.height * 0.5f - Game.sketch.textAscent() * 0.5f);  
  }
  
  private void drawLowerScoreScreen() {
    Game.sketch.background(0xFF000000);
    Game.sketch.image(ImageDataBase.get("LowerScoreSplash.png"), Game.sketch.width * 0.5f, Game.sketch.height * 0.5f);
    Game.sketch.textSize(18f);
    Game.sketch.fill(0xFFFFFFFF);
    String scoreText = String.format("Your score: %d\nScore to beat: %d", score, previousHighScore);
    Game.sketch.text(scoreText, Game.sketch.width * 0.5f - Game.sketch.textWidth(scoreText) * 0.5f, Game.sketch.height * 0.5f - Game.sketch.textAscent() * 0.5f);  
  }
  
  /**
   * Saves new high score to file
   */
  private void saveHighScore() {
    File saveFile = new File(Game.sketch.sketchPath() + File.separator + "Highscore.txt");
    try {
      saveFile.createNewFile();
      
    } catch (IOException ioe) {
      //ioe.printStackTrace();
    }
    PApplet.saveStrings(saveFile, new String[] { String.format("%d", score) });
  }
}
