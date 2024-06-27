package factory;

import processing.core.PVector;

/**
 * UI component to allow player to click to select components
 */
public class ComponentSelect extends Select {
    public ComponentType type;

    public ComponentSelect(ComponentType type) {
        switch (type) {
            case FAN:
                icon = ImageDataBase.get("FanComponent.png").copy();
                stats = FanComponent.stats();
                cost = FanComponent.cost();
                break;
            case GUN:
                icon = ImageDataBase.get("GunComponent.png").copy();
                stats = GunComponent.stats();
                cost = GunComponent.cost();
                break;
            case SHIELD:
                icon = ImageDataBase.get("ShieldComponent.png").copy();
                stats = ShieldComponent.stats();
                cost = ShieldComponent.cost();
                break;
            default:
                icon = ImageDataBase.get("Default.png").copy();
                stats = new Stats(0, 0, 0, 0);
                cost = 0;
        }
        this.boundingBox = new BoundingBox(new PVector(), new PVector(Inventory.itemBackground.width, Inventory.itemBackground.height));
        this.type = type;
    }

    public ComponentSelect(ComponentType type, PVector position) {
        switch (type) {
            case FAN:
                icon = ImageDataBase.get("FanComponent.png").copy();
                stats = FanComponent.stats();
                cost = FanComponent.cost();
                boundingBox = new BoundingBox(position, (float) icon.width);
                break;
            case GUN:
                icon = ImageDataBase.get("GunComponent.png").copy();
                stats = GunComponent.stats();
                cost = GunComponent.cost();
                boundingBox = new BoundingBox(position, new PVector(icon.width, icon.height));
                break;
            case SHIELD:
                icon = ImageDataBase.get("ShieldComponent.png").copy();
                stats = ShieldComponent.stats();
                cost = ShieldComponent.cost();
                boundingBox = new BoundingBox(position, new PVector(icon.width, icon.height));
                break;
            default:
                icon = ImageDataBase.get("Default.png").copy();
                stats = new Stats(0, 0, 0, 0);
                cost = 0;
                boundingBox = new BoundingBox(position, 10f);
        }
        this.type = type;
    }

    public void render(ComponentType type) {
        super.render();
        highlightSelectedType(type);
    }

    public void highlightSelectedType(ComponentType type) {
        if (this.type == type) {
            Game.sketch.image(ImageDataBase.get("Select.png"), boundingBox.position);
        }
    }

    @Override
    protected void drawValue() {
        Game.sketch.fill(0xFFFFFFFF);
        stats.render(boundingBox, true);
    }

    public boolean mouseTouching() {
        return boundingBox.isTouchingMouse();
    }

	public PVector position() {
        return boundingBox.position;
	}
}
