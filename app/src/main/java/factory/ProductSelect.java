package factory;

import processing.core.PVector;

public class ProductSelect extends Select {

    public ProductType type;

    public ProductSelect(ProductType type, PVector position) {
        switch (type) {
            case LIGHT:
                icon = ImageDataBase.get("LightProduct.png").copy();
                stats = LightBase.stats();
                cost = LightBase.cost();
                boundingBox = new BoundingBox(position, icon.width);
                break;
            case NORMAL:
                icon = ImageDataBase.get("NormalProduct.png").copy();
                stats = NormalBase.stats();
                cost = NormalBase.cost();
                boundingBox = new BoundingBox(position, new PVector(icon.width, icon.height));
                break;
            case HEAVY:
                icon = ImageDataBase.get("HeavyProduct.png").copy();
                stats = HeavyBase.stats();
                cost = HeavyBase.cost();
                boundingBox = new BoundingBox(position, new PVector(icon.width, icon.height));
                break;
            default:
                icon = ImageDataBase.get("Default.png").copy();
                stats = new Stats(0, 0, 0, 0);
                cost = 0;
                boundingBox = new BoundingBox(position, new PVector(Inventory.itemBackground.width, Inventory.itemBackground.height));
        }
        this.type = type;
    }

    @Override
    protected void drawValue() {
        Game.sketch.fill(0xFFFFFFFF);
        stats.render(boundingBox);
    }
}
