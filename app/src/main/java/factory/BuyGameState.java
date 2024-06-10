package factory;

import java.util.Arrays;

import processing.core.PVector;

public class BuyGameState implements IState {

    private ComponentSelect[] componentOptions;
    private ProductSelect[] productOptions;
    private Button confirmButton;
    private Inventory inventory;

    @Override
    public void onEnter() {
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
        inventory = new Inventory(new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.5f));
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
        PVector position = new PVector(
                Game.sketch.width / 2f - 0.5f * (componentOptions.length - 1) * Factory.COMPONENT_SPACING,
                Game.sketch.height * 0.8f);
        for (int i = 0; i < componentOptions.length; i++) {
            componentOptions[i].render(position);
            renderCost(componentOptions[i].getBoundingBox(position), componentOptions[i].cost, false);
            if (componentOptions[i].isTouchingMouse(position) && Game.mouseDown()
                    && !Game.config.unlockedComponents.contains(componentOptions[i].type)) {
                if (Game.money >= componentOptions[i].cost) {
                    Game.config.unlockedComponents.add(componentOptions[i].type);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= componentOptions[i].cost;
                    inventory.insertItem(new InventoryItem(componentOptions[i]));
                } else {
                    flashMoney();
                }
            }
            position.x += Factory.COMPONENT_SPACING;
        }
    }

	private void updateProductOptions() {
        PVector position = new PVector(
                Game.sketch.width / 2f - 0.5f * (productOptions.length - 1) * Factory.COMPONENT_SPACING,
                Game.sketch.height * 0.4f);
        for (int i = 0; i < productOptions.length; i++) {
            productOptions[i].render(position);
            renderCost(productOptions[i].getBoundingBox(position), productOptions[i].cost, true);
            if (productOptions[i].isTouchingMouse(position) && Game.mouseDown()
                    && !Game.config.unlockedProducts.contains(productOptions[i].type)) {
                if (Game.money >= productOptions[i].cost) {
                    Game.config.unlockedProducts.add(productOptions[i].type);
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    Game.money -= productOptions[i].cost;
                    inventory.insertItem(new InventoryItem(productOptions[i]));
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
}
