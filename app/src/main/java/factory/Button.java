package factory;

import processing.core.PVector;

/**
 * Solid background button, changes colour on mouse over
 * onClick logic must be implemented outside of this class
 * as Java doesn't support delegates and addListener doesn't work
 * in Processing
 */
public class Button {
  public PVector size, position;
  public String text;
  public boolean isClicked = false;
  protected boolean isTouchingMouse = false;
  protected int[] backgroundColours;
  
  public Button(String text, PVector position, PVector size, int[] backgroundColours) {
    this.text = text;
    this.size = size;
    this.position = position;
    this.backgroundColours = backgroundColours;
  }

  public void update() {    
    isTouchingMouse = checkMouseCollision(false);
    if (Game.sketch.mousePressed && isTouchingMouse) {
      isClicked = true;
    } else {
      isClicked = false;
    }
    // Uses backgroundColours[0] unless mouse is touching button
    Game.sketch.fill(isTouchingMouse ? backgroundColours[0] : backgroundColours[1]);
    Game.sketch.rect(position.x, position.y, size.x, size.y);
    Game.sketch.fill(0xFF000000);
    Game.sketch.text(text, position.x - Game.sketch.textWidth(text) * 0.5f, position.y + Game.sketch.textAscent() * 0.5f);
  }

  /**
   * Updates isClicked variable each frame
   */
  private boolean checkMouseCollision(boolean showDebug) {
   PVector mousePos = Game.sketch.getMousePosition();
    if (showDebug) {
      Game.sketch.fill(0x55FF0000);
      Game.sketch.rect(position.x, position.y, size.x, size.y);
    }
    if (mousePos.x >= position.x - size.x * 0.5f && mousePos.x <= position.x + size.x * 0.5f) { // Within horz bounds
      if (mousePos.y >= position.y - size.y * 0.5f && mousePos.y <= position.y + size.y * 0.5f) { //Within vert bounds
        return true;
      }
    }
    return false;
  }
}
