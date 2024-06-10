package factory;

import processing.core.PImage;
import processing.core.PVector;

public class Inventory {
    private final int MAX_COMPONENTS = 9;
    private final int MAX_PRODUCTS = 6;

    public static PImage inventoryBackground;
    public static PImage itemBackground;

    private InventoryItem[] inventoryItems = new InventoryItem[MAX_PRODUCTS + MAX_COMPONENTS];
    private PVector position;

    public static void init() {
        itemBackground = ImageDataBase.get("ItemBackground.png");
        inventoryBackground = ImageDataBase.get("InventoryBackground.png");
    }

    public Inventory(PVector position) {
        this.position = position;
        for (int i = 0; i < MAX_COMPONENTS; i++) {
            if (i < Game.config.unlockedComponents.size()) {
                inventoryItems[i] = new InventoryItem(new ComponentSelect(Game.config.unlockedComponents.get(i)));
            } else {
                inventoryItems[i] = new InventoryItem();
            }
        }
        for (int i = 0; i < MAX_PRODUCTS; i++) {
            if (i < Game.config.unlockedProducts.size()) {
                inventoryItems[MAX_COMPONENTS + i] = new InventoryItem(new ProductSelect(Game.config.unlockedProducts.get(i)));
            } else {
                inventoryItems[MAX_COMPONENTS + i] = new InventoryItem();
            }
        }
        calculateInventoryPositions();
    }
    
    public boolean insertItem(InventoryItem inventoryItem) {
        if (!inventoryItem.isProduct) {
            for (int i = 0; i < MAX_COMPONENTS; i++) {
                if (!inventoryItems[i].hasData) {
                    inventoryItems[i] = inventoryItem;
                    calculateInventoryPositions();
                    return true;
                }
            }
            return false;
        } else {
            for (int i = 0; i < MAX_PRODUCTS; i++) {
                if (!inventoryItems[MAX_COMPONENTS + i].hasData) {
                    inventoryItems[MAX_COMPONENTS + i] = inventoryItem;
                    calculateInventoryPositions();
                    return true;
                }
            }
            return false;
        }
	}
    
    protected void calculateInventoryPositions() {
        float margin = 0.14f;
        Game.sketch.textSize(24f);
        PVector pos = new PVector(
            position.x - (inventoryBackground.width * 0.5f - inventoryBackground.width * margin) + itemBackground.width * 0.5f,
            position.y - inventoryBackground.height * 0.5f + inventoryBackground.height * margin
        );
        int counter = 0;
        for (int i = 0; i < MAX_COMPONENTS; i++) {
            inventoryItems[i].boundingBox.position = pos.copy();
            counter++;
            if (counter % 3 == 0) {
                pos.y += Factory.COMPONENT_SPACING;
                pos.x -= 2f * Factory.COMPONENT_SPACING;
            } else {
                pos.x += Factory.COMPONENT_SPACING;
            }
        }

        pos.x = position.x - (inventoryBackground.width * 0.5f - inventoryBackground.width * margin) + itemBackground.width * 0.5f;
        pos.y = position.y + inventoryBackground.height * margin + itemBackground.height * 0.5f;

        for (int i = 0; i < MAX_PRODUCTS; i++) {
            inventoryItems[i + MAX_COMPONENTS].boundingBox.position = pos.copy();
            counter++;
            if (counter % 3 == 0) {
                pos.y += Factory.COMPONENT_SPACING;
                pos.x -= 2f * Factory.COMPONENT_SPACING;
            } else {
                pos.x += Factory.COMPONENT_SPACING;
            }
        }
    }

    public void showInventoryHover() {
        for (InventoryItem item : inventoryItems) {
            if (!item.hasData) {
                continue;
            }
            if (item.boundingBox.isTouchingMouse()) {
                item.stats.render(item.boundingBox, item.isProduct);
            }
        }
    }

    protected void renderUnlocked() {
        Game.sketch.image(inventoryBackground, position.x, position.y);
        Game.sketch.textSize(24f);
        Game.sketch.text("Unlocked Components", position.x, position.y - inventoryBackground.height * 0.45f);
        Game.sketch.text("Unlocked Products", position.x, position.y + inventoryBackground.height * 0.12f);
        Game.sketch.textSize(36f);
        Game.sketch.fill(0xFF888888);
        Game.sketch.text("Inventory", position.x, position.y + inventoryBackground.height * 0.5f - Game.sketch.textAscent());
        Game.sketch.fill(0xFFFFFFFF);
        for (InventoryItem item : inventoryItems) {
            item.render();
        }
    }
}
