package factory;

import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PVector;

public class SelectionGameState implements IState {

    private Stats target;
    private ComponentSelect[] componentOptions;
    private ProductSelect[] productOptions;
    private ArrayList<ComponentType> selectedComponents;
    private ProductType selectedBase;
    private Button build;

	@Override
	public void onEnter() {
        target = Game.config.getCurrentLevel().possibleTargets[Game.random.nextInt(0, Game.config.getCurrentLevel().possibleTargets.length)];
        componentOptions = Arrays.stream(Game.config.unlockedComponents).map((ComponentType type) -> new ComponentSelect(type)).toArray(ComponentSelect[]::new);
        productOptions = Arrays.stream(Game.config.unlockedProducts).map((ProductType type) -> new ProductSelect(type)).toArray(ProductSelect[]::new);
        selectedComponents = new ArrayList<ComponentType>();
        selectedBase = null;
        build = new Button("BUILD!", new PVector(Game.sketch.width * 0.8f, Game.sketch.height * 0.8f), new PVector(Game.sketch.width * 0.2f, Game.sketch.height * 0.2f), new int[] { 0xFF774499, 0xFFDDDDDD });
	}

	@Override
	public void onExit() {
        System.out.println("Initialised Level Selection");
        Game.levelSelection = new LevelSelection(selectedBase, selectedComponents);
    }

	@Override
	public void update() {
        drawComponentOptions();
        drawProductOptions();
        Game.sketch.tint(0xFFFFFFFF);
        Game.sketch.fill(0xFFFFFFFF);
        drawTarget();
        drawSelection();
        if (selectedBase != null && !selectedComponents.isEmpty()) {
            build.update();
        }
	}

	private void drawComponentOptions() {
        PVector position = new PVector(Game.sketch.width / 2f - 0.5f * (componentOptions.length - 1) * Factory.COMPONENT_SPACING, Game.sketch.height * 0.4f);
		for (int i = 0; i < componentOptions.length; i++) {
            componentOptions[i].render(position);
            if (componentOptions[i].isTouchingMouse(position) && Game.mouseDown()) {
                selectedComponents.add(componentOptions[i].type);
                Game.mouseInputDelay = Factory.MOUSE_DELAY;
            }
            position.x += i * Factory.COMPONENT_SPACING;
        }
	}

	private void drawProductOptions() {
        PVector position = new PVector(Game.sketch.width / 2f - 0.5f * (productOptions.length - 1) * Factory.COMPONENT_SPACING, Game.sketch.height * 0.8f);
		for (int i = 0; i < productOptions.length; i++) {
            productOptions[i].render(position);
            if (productOptions[i].isTouchingMouse(position) && Game.mouseDown()) {
                selectedBase = productOptions[i].type;
                Game.mouseInputDelay = Factory.MOUSE_DELAY;
            }
            position.x += i * Factory.COMPONENT_SPACING;
        }	
	}

    private void drawTarget() {
        Game.sketch.textSize(40f);
        Game.sketch.text(target.toString(), Game.sketch.width - Game.sketch.textWidth(target.toString()) * 0.75f, Game.sketch.textAscent() * 4f);
        Game.sketch.textSize(18f);
    }

    private void drawSelection() {
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        if (!selectedComponents.isEmpty()) {
            String components = String.join(", ", selectedComponents.stream().map(x -> x.toString()).toList());
            Game.sketch.text("Selected Components: " + components, Game.sketch.width * 0.1f, Game.sketch.height * 0.1f);
        } else {
            Game.sketch.text("Selected Components: NONE", Game.sketch.width * 0.1f, Game.sketch.height * 0.1f);
        }
        if (selectedBase != null) {
            Game.sketch.text("Selected Base: " + selectedBase.toString(), Game.sketch.width * 0.1f, Game.sketch.height * 0.1f + Game.sketch.textAscent() * 1.5f);
        } else {
            Game.sketch.text("Selected Base: NONE", Game.sketch.width * 0.1f, Game.sketch.height * 0.1f + Game.sketch.textAscent() * 1.5f);
        }
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
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
