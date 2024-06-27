package factory;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * All base game logic, inherited by Level classes which implement
 * setup functions to provide configurablity to levels
 */
public class PlayGameState implements IState {

    protected Crane crane;
    protected ProductReceiver reciever;
    protected Conveyor conveyor;
    protected Stats target;
    protected ComponentSocket closestSocket;
    protected ComponentSelect[] componentOptions;
    private ProgressBar speedProgress;
    private ProgressBar firePowerProgress;
    private ProgressBar storageProgress;
    private ProgressBar weightProgress;
    private PVector comparisonPos;
	private float[] textPos;
	private PImage componentPanel;

    /**
     * Initialises all config variables and sets up conveyor class
     */
    @Override
    public void onEnter() {
        PApplet.println("Entered play state");
        componentPanel = ImageDataBase.get("ComponentPanel.png");
        this.componentOptions = new ComponentSelect[Game.config.unlockedComponents.size()];
        float startPos = Game.sketch.width * 0.5f - componentPanel.width * 0.5f + 20f;
        for (int i = 0; i < Game.config.unlockedComponents.size(); i++) {
            componentOptions[i] = new ComponentSelect(
                Game.config.unlockedComponents.get(i), 
                new PVector(startPos + (i + 1) * Factory.COMPONENT_SPACING + 10f, Game.sketch.height * 0.9f)
            );
        }
        int level = Game.config.currentLevel;
        int index = Game.random.nextInt(0, Game.config.levels.length);
        target = Game.config.levels[level].possibleTargets[index];
        reciever = new ProductReceiver();
        PVector[] conveyorPositions = Game.config.levels[level].conveyorPositions;
        conveyor = new Conveyor(conveyorPositions, 50f);
        conveyor.addProduct(Product.createBase(Game.levelSelection.selectedBase));
        crane = new Crane(conveyor);
        closestSocket = null;
        comparisonPos = new PVector(Game.sketch.width * 0.3f, Game.sketch.height * 0.25f);
        PVector progressSize = new PVector(75, 10);
        float padding = 0f;
        int bgColour = 0xFFAAAAAA;
        textPos = new float[] {
            comparisonPos.y - Stats.statBackground.height * 0.3f,
            comparisonPos.y - Stats.statBackground.height * 0.1f,
            comparisonPos.y + Stats.statBackground.height * 0.1f,
            comparisonPos.y + Stats.statBackground.height * 0.3f,
        };
        speedProgress = new ProgressBar(PVector.sub(comparisonPos, new PVector(-progressSize.x * 0.75f, Stats.statBackground.height * 0.3f)), progressSize, padding, 0f, bgColour, 0xFFACD838, ProgressBar.LEFT);
        firePowerProgress = new ProgressBar(PVector.sub(comparisonPos, new PVector(-progressSize.x * 0.75f, Stats.statBackground.height * 0.1f)), progressSize, padding, 0f, bgColour, 0xFFFF4720, ProgressBar.LEFT);
        storageProgress = new ProgressBar(PVector.add(comparisonPos, new PVector(progressSize.x * 0.75f, Stats.statBackground.height * 0.1f)), progressSize, padding, 0f, bgColour, 0xFF20E3FF, ProgressBar.LEFT);
        weightProgress = new ProgressBar(PVector.add(comparisonPos, new PVector(progressSize.x * 0.75f, Stats.statBackground.height * 0.3f)), progressSize, padding, 0f, bgColour, 0xFF647577, ProgressBar.LEFT);
    }

    /**
     * Updates all member arrays and draws UI elements
     */
    @Override
    public void update() {
        updateConveyor();
        highlightComponentSockets();
        drawUI();
        AnimationPool.update();
        crane.update();
    }

    @Override
    public void checkTransition() {
        if (conveyor.isFinished()) {
            Game.switchState(new BuyGameState());
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public void keyDown(char key) {
    }

    /**
     * Returns closest product to mouse on conveyor
     */
    protected Product getClosestToMouse() {
        if (!crane.hasComponent()) {
            return null;
        }
        PVector mousePos = Game.sketch.getMousePosition();
        Product closestProduct = null;
        if (conveyor.getProduct().getBoundingBox().isOverlappingPoint(mousePos)) {
            closestProduct = conveyor.getProduct();
        }
        return closestProduct;
    }

    /**
     * Moves conveyor items and checks if a product has reached the end
     */
    protected void updateConveyor() {
        // Conveyor logic
        conveyor.update();
        if (conveyor.isFinished()) {
            Game.money += reciever.getMoneyFromSubmission(conveyor.getProduct(), target);
        }
    }

    /**
     * Highlights all empty sockets changing colour based on whether the currently
     * held item
     * will collide with the component the player has selected and attaches relevant
     * animation
     * if player successfully adds component
     */
    protected void highlightComponentSockets() {
        // Mouse logic
        Product closestProduct = getClosestToMouse();
        if (closestProduct != null && crane.hasComponent()) {
            // Find closest ComponentSocket
            closestSocket = closestProduct.getClosestSocket();
            if (closestSocket != null) {
                PVector worldSpaceSocketPos = PVector.add(closestSocket.offset, closestProduct.position);
                if (closestProduct.checkCollisionAtSocket(crane.getComponent(), closestSocket)) {
                    // Highlight invalid component socket
                    Game.sketch.fill(0xAAFF0000);
                    Game.sketch.rect(worldSpaceSocketPos.x, worldSpaceSocketPos.y, 20f, 20f);
                } else {
                    // Highlight valid component socket
                    Game.sketch.fill(0xAA00FF00);
                    Game.sketch.rect(worldSpaceSocketPos.x, worldSpaceSocketPos.y, 20f, 20f);
                    if (Game.mouseDown()) {
                        System.out.println("Set target");
                        crane.setTarget(closestSocket, closestProduct);
                        Game.mouseInputDelay = Factory.MOUSE_DELAY;
                    }
                }
            }
        }
    }

    /**
     * Draws UI buttons to allow player to add products
     * to the conveyor and add components to products
     */
    protected void drawComponents() {
        Game.sketch.image(componentPanel, new PVector(Game.sketch.width * 0.5f, Game.sketch.height * 0.9f));
        // Shows component values, keybinds and images
        for (ComponentSelect componentOption : componentOptions) {
            Game.sketch.image(Inventory.itemBackground, componentOption.position());
            componentOption.render();
            if (componentOption.isTouchingMouse() && Game.mouseDown()) {
                if (!crane.hasComponent()) {
                    crane.addComponent(Component.createComponent(componentOption.type), componentOption.position());
                } else if (crane.getComponent().type == componentOption.type) {
                    crane.removeComponent(componentOption.position());
                }
                Game.mouseInputDelay = Factory.MOUSE_DELAY;
            }
        }
    }

    /**
     * Draws target values and highlights current target
     * Draws score and remaining lives
     */
    protected void drawUI() {
        Game.sketch.textSize(40f);
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text(String.format("Money: %d", Game.money), Game.sketch.width * 0.1f - Stats.statBackground.width * 0.5f, Game.sketch.height * 0.05f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.textSize(40f);
        Game.sketch.text("Target:", Game.sketch.width * 0.1f - Stats.statBackground.width * 0.5f, Game.sketch.height * 0.25f - Stats.statBackground.height * 0.5f - Game.sketch.textAscent());
        target.render(new PVector(Game.sketch.width * 0.1f, Game.sketch.height * 0.25f));
        // Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        // Game.sketch.textSize(40f);
        // Game.sketch.text("Current:", Game.sketch.width * 0.3f - Stats.statBackground.width * 0.5f, Game.sketch.height * 0.25f - Stats.statBackground.height * 0.5f - Game.sketch.textAscent());
        // conveyor.getProduct().getValue().render(new PVector(Game.sketch.width * 0.3f, Game.sketch.height * 0.25f));
        drawComparison();
        drawComponents();
    }

    private void drawComparison() {
        float[] stats = target.compareValues(conveyor.getProduct().getValue());
        Game.sketch.image(Stats.statBackground, comparisonPos);
        speedProgress.progress = stats[0];
        Game.sketch.fill(0xFFACD838);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text("Speed", comparisonPos.x - Stats.statBackground.width * 0.3f, textPos[0]);
        speedProgress.render(OverloadStrategy.POSITIVE);
        firePowerProgress.progress = stats[1];
        Game.sketch.fill(0xFFFF4720);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text("Fire Power", comparisonPos.x - Stats.statBackground.width * 0.3f, textPos[1]);
        firePowerProgress.render(OverloadStrategy.POSITIVE);
        Game.sketch.fill(0xFF20E3FF);
        storageProgress.progress = stats[2];
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text("Storage", comparisonPos.x - Stats.statBackground.width * 0.3f, textPos[2]);
        storageProgress.render(OverloadStrategy.POSITIVE);
        Game.sketch.fill(0xFF647577);
        weightProgress.progress = stats[3];
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text("Weight", comparisonPos.x - Stats.statBackground.width * 0.3f, textPos[3]);
        weightProgress.render(OverloadStrategy.NEGATIVE);
    }
}
