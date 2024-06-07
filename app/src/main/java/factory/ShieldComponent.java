package factory;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Component with a rotating 'shield' that lerps its colour over time
 */
public class ShieldComponent extends Component {
    private int shieldColour;
    private PImage shieldSprite;
    private float currentColourLerp;
    private float lerpIncrement;
    private float currentShieldRotation;

    protected ShieldComponent(float rotation, int shieldColour, int shieldSize) {
        super(ImageDataBase.get("ShieldComponent.png"), rotation);
        this.shieldColour = shieldColour;
        this.shieldSprite = ImageDataBase.get("Shield.png").copy();
        this.shieldSprite.resize(shieldSize, shieldSize);
        this.stats = stats();
        this.currentColourLerp = 0f;
        this.lerpIncrement = 1 / 60f;
        this.currentShieldRotation = 0f;
        this.type = ComponentType.SHIELD;
    }

    /**
     * Draws shield behind component image
     */
    @Override
    public void draw(PVector position) {
        super.draw(position);
        Game.sketch.pushMatrix();
        Game.sketch.translate(position.x, position.y);
        Game.sketch.rotate(currentShieldRotation);
        currentShieldRotation -= 1f / 60f;
        Game.sketch.tint(Game.sketch.lerpColor(0x88FFFFFF, shieldColour, currentColourLerp));
        if (currentColourLerp > 1f || currentColourLerp < 0f) {
            lerpIncrement *= -1f;
        }
        currentColourLerp += lerpIncrement;
        Game.sketch.image(shieldSprite, 0f, 0f);
        Game.sketch.popMatrix();
        Game.sketch.tint(0xFFFFFFFF);
    }

    /**
     * As the component is rectangular it takes current rotation into account using
     * a basic approximation:
     * For example if the rectangle is rotated the bounding box is still axis
     * aligned
     * +----------+
     * |    /\    |
     * |   /  \   |
     * |  /    \  |
     * | /      \ |
     * |/        \|
     * |\        /|
     * | \      / |
     * |  \    /  |
     * |   \  /   |
     * |    \/    |
     * +----------+
     */
    @Override
    public BoundingBox getBoundingBox(PVector position) {
        float sinTheta = PApplet.abs(PApplet.sin(rotation));
        float cosTheta = PApplet.abs(PApplet.cos(rotation));
        float rotatedWidth = image.width * cosTheta + image.height * sinTheta;
        float rotatedHeight = image.width * sinTheta + image.height * cosTheta;
        return new BoundingBox(position, new PVector(rotatedWidth, rotatedHeight));
    }

    public static Stats stats() {
        return new Stats(0f, 0f, 0f, 20f);
    }

    public static int cost() {
        return 300;
    }
}
