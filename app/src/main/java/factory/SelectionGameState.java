package factory;

import java.util.ArrayList;

import processing.core.PVector;

public class SelectionGameState implements IState {

    private Stats target;
    private ArrayList<ComponentType> selectedComponents;
    private ProductType selectedBase;
    private Button build;
    private Inventory inventory;

	@Override
	public void onEnter() {
        Factory.println("Entered select state");
        target = Game.config.getCurrentLevel().possibleTargets[Game.random.nextInt(0, Game.config.getCurrentLevel().possibleTargets.length)];
        selectedComponents = new ArrayList<ComponentType>();
        selectedBase = null;
        build = new Button("BUILD!", new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.8f), new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.2f), new int[] { 0xFF774499, 0xFFDDDDDD });
        inventory = new Inventory(
                new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.5f),
                Game.config.unlockedComponents.toArray(ComponentType[]::new),
                Game.config.unlockedProducts.toArray(ProductType[]::new)
        );
	}

	@Override
	public void onExit() {
        System.out.println("Initialised Level Selection");
        Game.levelSelection = new LevelSelection(selectedBase, selectedComponents);
    }

	@Override
	public void update() {
        Game.sketch.tint(0xFFFFFFFF);
        Game.sketch.fill(0xFFFFFFFF);
        drawTarget();
        inventory.renderUnlocked();
        for (InventoryItem item : inventory) {
            if (item.hasData) {
                boolean isSelected = item.isProduct ? item.productType == selectedBase : selectedComponents.contains(item.componentType);
                if (isSelected) {
                    Game.sketch.image(Inventory.selectImage, item.boundingBox.position);
                }
            }
            if (item.boundingBox.isTouchingMouse() && item.hasData) {
                
                item.stats.render(item.boundingBox);
                if (Game.mouseDown()) {
                    Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    if (item.isProduct) {
                        selectedBase = item.productType;
                    } else {
                        selectedComponents.add(item.componentType);
                    }
                }
            }
        }
        if (selectedBase != null && !selectedComponents.isEmpty()) {
            build.update();
        }
	}

    private void drawTarget() {
        Game.sketch.textSize(40f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text("Target:", Game.sketch.width * 0.8f - Stats.statBackground.width * 0.5f, Game.sketch.height * 0.2f - Stats.statBackground.height * 0.5f - Game.sketch.textAscent());
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
        target.render(new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.2f));
        Game.sketch.textSize(18f);
    }

	@Override
	public void checkTransition() {
        if (build.isClicked) {
            Game.switchState(new PlayGameState());
        }
    }

	@Override
	public void keyDown(char key) { }
    
}
