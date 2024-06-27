package factory;

import processing.core.PVector;

public class BuyGameState implements IState {

    private ItemMenu componentOptions;
    private ItemMenu productOptions;
    private Button confirmButton;
    private Inventory inventory;

    @Override
    public void onEnter() {
        componentOptions = new ItemMenu(
            Game.config.unlockedComponents.toArray(ComponentType[]::new), 
            new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.2f)
        );
        productOptions = new ItemMenu(
            Game.config.unlockedProducts.toArray(ProductType[]::new), 
            new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.2f)
        );
        confirmButton = new Button("Confirm", new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.8f),
                new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.1f), new int[] { 0xFF558866, 0xFFDDDDDD });
        inventory = new Inventory(
                new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.5f),
                Game.config.unlockedComponents.toArray(ComponentType[]::new),
                Game.config.unlockedProducts.toArray(ProductType[]::new)
        );
    }

    @Override
    public void onExit() {
    }

    @Override
    public void update() {
        drawMoney();
        updateComponentOptions();
        updateProductOptions();
        inventory.renderUnlocked();
        inventory.showInventoryHover();
        confirmButton.update();
    }

    

	private void drawMoney() {
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text(String.format("Money: %d", Game.money), Game.sketch.width * 0.01f, Game.sketch.height * 0.05f);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
    }

    private void updateComponentOptions() {
        componentOptions.drawBackground();
        for (InventoryItem item : componentOptions) {
            item.render();
            renderCost(item.boundingBox, item.cost, false);
            if (item.boundingBox.isTouchingMouse() && Game.mouseDown() && !Game.config.unlockedComponents.contains(item.componentType)) {
                if (Game.money >= item.cost) {
                    Game.config.unlockedComponents.add(item.componentType);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= item.cost;
                    inventory.insertItem(item);
                } else {
                    flashMoney();
                }
            }
        }
    }

	private void updateProductOptions() {
        productOptions.drawBackground();
       for (InventoryItem item : productOptions) {
            item.render();
            renderCost(item.boundingBox, item.cost, false);
            if (item.boundingBox.isTouchingMouse() && Game.mouseDown() && !Game.config.unlockedProducts.contains(item.productType)) {
                if (Game.money >= item.cost) {
                    Game.config.unlockedProducts.add(item.productType);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= item.cost;
                    inventory.insertItem(item);
                } else {
                    flashMoney();
                }
            }
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
            if (Game.config.currentLevel >= Game.config.levels.length - 1) {
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
}
