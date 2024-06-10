package factory;

import processing.core.PImage;
import processing.core.PVector;

public class InventoryItem {
        public boolean hasData;
        public boolean isProduct;
        public Stats stats;
        public PImage icon;
        public PImage background;
        public BoundingBox boundingBox;

        public InventoryItem() {
            background = Inventory.itemBackground.copy();
            hasData = false;
            stats = null;
            icon = null;
            boundingBox = new BoundingBox(new PVector(), new PVector(Factory.COMPONENT_SPACING, Factory.COMPONENT_SPACING));
        }

        public InventoryItem(ComponentSelect data) {
            background = Inventory.itemBackground.copy();
            hasData = true;
            stats = new Stats(data.stats);
            icon = data.icon.copy();
            float max = Factory.max(icon.width, icon.height);
            if (max > background.width * 0.9f) {
                float resize = background.width * 0.9f / max;
                icon.resize(Factory.round(icon.width * resize), Factory.round(icon.height * resize));
            }
            boundingBox = data.getBoundingBox(new PVector());
            isProduct = false;
        }

        public InventoryItem(ProductSelect data) {
            background = Inventory.itemBackground.copy();
            hasData = true;
            stats = new Stats(data.stats);
            icon = data.icon.copy();
            float max = Factory.max(icon.width, icon.height);
            if (max > background.width * 0.9f) {
                float resize = background.width * 0.9f / max;
                icon.resize(Factory.round(icon.width * resize), Factory.round(icon.height * resize));
            }
            boundingBox = data.getBoundingBox(new PVector());
            isProduct = true;
        }

        public void render() {
            Game.sketch.image(background, boundingBox.position);
            if (hasData) {
                Game.sketch.image(icon, boundingBox.position);
            }
        }
    }
