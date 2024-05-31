package factory;

import processing.core.PImage;
import processing.core.PVector;

/**
 * Base in game UI component for Product and Component Buttons
 * containing some base shared behaviour
 */
public abstract class Select {
  public Stats stats;
  public PImage icon;
  protected final int HOVER_TINT = 0xFF444444;

  public void render(PVector position) {
    if (isTouchingMouse(position)) {
      Game.sketch.tint(HOVER_TINT);
    } else {
      Game.sketch.tint(0xFFFFFFFF);
    }
    Game.sketch.image(icon, position);
    Game.sketch.tint(0xFFFFFFFF);
    if (isTouchingMouse(position)) {
        drawValue(position);
    }
  }

  protected abstract void drawValue(PVector position);

  protected boolean isTouchingMouse(PVector position) {
    return new BoundingBox(position, new PVector(icon.width, icon.height)).isTouchingMouse();
  }
}
