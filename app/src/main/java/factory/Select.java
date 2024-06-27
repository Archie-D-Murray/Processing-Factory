package factory;

import processing.core.PImage;

/**
 * Base in game UI component for Product and Component Buttons
 * containing some base shared behaviour
 */
public abstract class Select {
    public Stats stats;
    public int cost;
    public PImage icon;
    public BoundingBox boundingBox;
    protected final int HOVER_TINT = 0xFF444444;

    public void render() {
        if (isTouchingMouse()) {
            Game.sketch.tint(HOVER_TINT);
        } else {
            Game.sketch.tint(0xFFFFFFFF);
        }
        Game.sketch.image(icon, boundingBox.position);
        Game.sketch.tint(0xFFFFFFFF);
        if (isTouchingMouse()) {
            drawValue();
        }
    }

    protected abstract void drawValue();

    protected boolean isTouchingMouse() {
        return boundingBox.isTouchingMouse();
    }
}
