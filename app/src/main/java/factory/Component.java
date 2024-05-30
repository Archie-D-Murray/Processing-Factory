package factory;

import processing.core.PImage;
import processing.core.PVector;
import processing.core.PApplet;

/**
 * Components can be added to a product using a ComponentSocket instance as a
 * tuple
 * this allows for a component socket to exist to check if a component can be
 * added
 * to a product
 */

public abstract class Component implements IComponent {
    protected float rotation;
    protected PImage image;
    public Stats stats;
    public Animation animation;

    protected Component(PImage image, float rotation) {
        this.image = image.copy();
        this.rotation = rotation;
    }

    public static Component createComponent(ComponentType type) {
        switch (type) {
            case FAN:
                return new FanComponent(0f, 1f);

            case GUN:
                return new GunComponent(-PApplet.HALF_PI, 10f);

            case SHIELD:
                return new ShieldComponent(0f, 0x88FFBB00, 200);

            default:
                return null;
        }
    }

    public void draw(PVector position) {
        Game.sketch.pushMatrix();
        Game.sketch.translate(position.x, position.y);
        Game.sketch.rotate(rotation);
        Game.sketch.image(image, 0f, 0f);
        Game.sketch.popMatrix();
    }

    public void draw(PVector position, float scale) {
        Game.sketch.pushMatrix();
        Game.sketch.scale(scale);
        Game.sketch.translate(new PVector(position.x, position.y).div(scale));
        Game.sketch.rotate(rotation);
        Game.sketch.image(image, 0f, 0f);
        Game.sketch.popMatrix();
    }

    public abstract BoundingBox getBoundingBox(PVector position);
}
