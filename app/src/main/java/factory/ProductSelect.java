package factory;

import processing.core.PVector;

public class ProductSelect extends Select {

    public ProductType type;

    public ProductSelect(ProductType type) {
        switch (type) {
            case LIGHT:
                icon = ImageDataBase.get("LightProduct.png").copy();
                stats = LightBase.stats();
                cost = LightBase.cost();
                break;
            case NORMAL:
                icon = ImageDataBase.get("NormalProduct.png").copy();
                stats = NormalBase.stats();
                cost = NormalBase.cost();
                break;
            case HEAVY:
                icon = ImageDataBase.get("HeavyProduct.png").copy();
                stats = HeavyBase.stats();
                cost = HeavyBase.cost();
                break;
            default:
                icon = ImageDataBase.get("Default.png").copy();
                stats = new Stats(0, 0, 0, 0);
                cost = 0;
        }
        this.type = type;
    }

    @Override
    protected void drawValue(PVector position) {
        Game.sketch.fill(0xFFFFFFFF);
        stats.render(getBoundingBox(position));
    }

    public boolean mouseTouching(PVector position) {
        return getBoundingBox(position).isTouchingMouse();
    }

    public BoundingBox getBoundingBox(PVector position) {
        return new BoundingBox(position, new PVector(icon.width, icon.height));
    }
}
