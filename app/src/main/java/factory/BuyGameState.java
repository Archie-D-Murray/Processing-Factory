package factory;

import java.util.Arrays;

import processing.core.PImage;
import processing.core.PVector;

public class BuyGameState implements IState {

    private final int MAX_COMPONENTS = 9;
    private final int MAX_PRODUCTS = 6;

    private ComponentSelect[] componentOptions;
    private ProductSelect[] productOptions;
    private Button confirmButton;
    private PImage inventoryBackground;
    private static PImage itemBackground;

    private InventoryItem[] inventoryItems = new InventoryItem[MAX_PRODUCTS + MAX_COMPONENTS];

    private class InventoryItem {
        public boolean hasData;
        public boolean isProduct;
        public Stats stats;
        public PImage icon;
        public PImage background;
        public BoundingBox boundingBox;

        public InventoryItem() {
            background = BuyGameState.itemBackground.copy();
            hasData = false;
            stats = null;
            icon = null;
            boundingBox = new BoundingBox(new PVector(), new PVector(Factory.COMPONENT_SPACING, Factory.COMPONENT_SPACING));
        }

        public InventoryItem(ComponentSelect data) {
            background = BuyGameState.itemBackground.copy();
            hasData = true;
            stats = new Stats(data.stats);
            icon = data.icon.copy();
            float max = Factory.max(icon.width, icon.height);
            if (max > itemBackground.width * 0.9f) {
                float resize = itemBackground.width * 0.9f / max;
                icon.resize(Factory.round(icon.width * resize), Factory.round(icon.height * resize));
            }
            boundingBox = data.getBoundingBox(new PVector());
            isProduct = false;
        }

        public InventoryItem(ProductSelect data) {
            background = BuyGameState.itemBackground.copy();
            hasData = true;
            stats = new Stats(data.stats);
            icon = data.icon.copy();
            float max = Factory.max(icon.width, icon.height);
            if (max > itemBackground.width * 0.9f) {
                float resize = itemBackground.width * 0.9f / max;
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

    @Override
    public void onEnter() {
        inventoryBackground = ImageDataBase.get("InventoryBackground.png");
        BuyGameState.itemBackground = ImageDataBase.get("ItemBackground.png");
        componentOptions = Arrays
                .stream(Game.config.getCurrentLevel().componentUnlocks)
                .map((ComponentType type) -> new ComponentSelect(type))
                .toArray(ComponentSelect[]::new);
        productOptions = Arrays
                .stream(Game.config.getCurrentLevel().productUnlocks)
                .map((ProductType type) -> new ProductSelect(type))
                .toArray(ProductSelect[]::new);
        confirmButton = new Button("Confirm", new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.8f),
                new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.1f), new int[] { 0xFF558866, 0xFFDDDDDD });
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

    @Override
    public void onExit() {
    }

    @Override
    public void update() {
        drawMoney();
        updateComponentOptions();
        updateProductOptions();
        renderUnlocked();
        showInventoryHover();
        confirmButton.update();
    }

    private void showInventoryHover() {
        for (InventoryItem item : inventoryItems) {
            if (!item.hasData) {
                continue;
            }
            if (item.boundingBox.isTouchingMouse()) {
                item.stats.render(item.boundingBox, item.isProduct);
            }
        }
    }

	private void drawMoney() {
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text(String.format("Money: %d", Game.money), Game.sketch.width * 0.01f, Game.sketch.height * 0.05f);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
    }

    private void updateComponentOptions() {
        PVector position = new PVector(
                Game.sketch.width / 2f - 0.5f * (componentOptions.length - 1) * Factory.COMPONENT_SPACING,
                Game.sketch.height * 0.4f);
        for (int i = 0; i < componentOptions.length; i++) {
            componentOptions[i].render(position);
            renderCost(componentOptions[i].getBoundingBox(position), componentOptions[i].cost, true);
            if (componentOptions[i].isTouchingMouse(position) && Game.mouseDown()
                    && !Game.config.unlockedComponents.contains(componentOptions[i].type)) {
                if (Game.money >= componentOptions[i].cost) {
                    Game.config.unlockedComponents.add(componentOptions[i].type);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= componentOptions[i].cost;
                    insertItem(new InventoryItem(componentOptions[i]));
                } else {
                    flashMoney();
                }
            }
            position.x += Factory.COMPONENT_SPACING;
        }
    }

    private boolean insertItem(InventoryItem inventoryItem) {
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

	private void updateProductOptions() {
        PVector position = new PVector(
                Game.sketch.width / 2f - 0.5f * (productOptions.length - 1) * Factory.COMPONENT_SPACING,
                Game.sketch.height * 0.8f);
        for (int i = 0; i < productOptions.length; i++) {
            productOptions[i].render(position);
            renderCost(productOptions[i].getBoundingBox(position), productOptions[i].cost, false);
            if (productOptions[i].isTouchingMouse(position) && Game.mouseDown()
                    && !Game.config.unlockedProducts.contains(productOptions[i].type)) {
                if (Game.money >= productOptions[i].cost) {
                    Game.config.unlockedProducts.add(productOptions[i].type);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= productOptions[i].cost;
                    insertItem(new InventoryItem(productOptions[i]));
                } else {
                    flashMoney();
                }
            }
            position.x += Factory.COMPONENT_SPACING;
        }
    }

    private void renderCost(BoundingBox boundingBox, int cost, boolean renderAbove) {
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.textSize(24f);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
        Game.sketch.text(
            String.format("Cost: %d", cost), 
            boundingBox.position.x, 
            renderAbove ? boundingBox.top() - Game.sketch.textAscent() : boundingBox.bottom() + Game.sketch.textAscent()
        ); 
    }

    @Override
    public void checkTransition() {
        if (confirmButton.isClicked) {
            if (Game.config.currentLevel >= Game.config.levels.length) {
                Game.config = new Config();
                Game.switchState(new MenuGameState());
            } else {
                Game.config.currentLevel++;
                Game.switchState(new SelectionGameState());
            }
        }
    }

    @Override
    public void keyDown(char key) {
    }

    protected void flashMoney() {
        float width = Game.sketch.textWidth(String.format(" Money: %d ", Game.money));
        Game.sketch.fill(0xAAFF0000);
        Game.sketch.strokeWeight(0f);
        Game.sketch.rectMode(Factory.CENTER);
        Game.sketch.rect(Game.sketch.width * 0.01f + width / 2f - Game.sketch.textWidth(' '), Game.sketch.height * 0.05f, width, Game.sketch.textAscent() * 2f);
        Game.sketch.fill(0xFFFFFFFF);
    }

    protected void calculateInventoryPositions() {
        float margin = 0.14f;
        Game.sketch.textSize(24f);
        PVector pos = new PVector(
            Game.sketch.width * 0.25f - (inventoryBackground.width * 0.5f - inventoryBackground.width * margin) + itemBackground.width * 0.5f,
            Game.sketch.height * 0.5f - inventoryBackground.height * 0.5f + inventoryBackground.height * margin
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

        pos.x = Game.sketch.width * 0.25f - (inventoryBackground.width * 0.5f - inventoryBackground.width * margin) + itemBackground.width * 0.5f;
        pos.y = Game.sketch.height * 0.5f + inventoryBackground.height * margin + itemBackground.height * 0.5f;

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

    protected void renderUnlocked() {
        Game.sketch.image(inventoryBackground, Game.sketch.width * 0.25f, Game.sketch.height * 0.5f);
        Game.sketch.textSize(24f);
        Game.sketch.text("Unlocked Components", Game.sketch.width * 0.25f, Game.sketch.height * 0.5f - inventoryBackground.height * 0.45f);
        Game.sketch.text("Unlocked Products", Game.sketch.width * 0.25f, Game.sketch.height * 0.6f);
        Game.sketch.textSize(36f);
        Game.sketch.fill(0xFF888888);
        Game.sketch.text("Inventory", Game.sketch.width * 0.25f, Game.sketch.height * 0.5f + inventoryBackground.height * 0.5f - Game.sketch.textAscent());
        Game.sketch.fill(0xFFFFFFFF);
        for (InventoryItem item : inventoryItems) {
            item.render();
        }
    }
}
