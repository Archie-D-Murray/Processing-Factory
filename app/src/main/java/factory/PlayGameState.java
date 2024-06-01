package factory;

import java.util.Arrays;

import processing.core.PApplet;
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

    /**
     * Initialises all config variables and sets up conveyor class
     */
    @Override
    public void onEnter() {
        PApplet.println("Entered play state");
        this.componentOptions = Arrays.stream(Game.levelSelection.selectedComponents).map((ComponentType type) -> new ComponentSelect(type)).toArray(ComponentSelect[]::new);
        int level = Game.config.currentLevel;
        int index = Game.random.nextInt(0, Game.config.levels.length);
        target = Game.config.levels[level].possibleTargets[index];
        reciever = new ProductReceiver();
        PVector[] conveyorPositions = Game.config.levels[level].conveyorPositions;
        conveyor = new Conveyor(conveyorPositions, 2f);
        conveyor.addProduct(Product.createBase(Game.levelSelection.selectedBase));
        crane = new Crane(componentOptions, conveyor);
        closestSocket = null;
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
            if (Game.config.currentLevel < Game.config.levels.length) {
                Game.config.currentLevel++;
                Game.switchState(new SelectionGameState());
            } else {
                Game.config.currentLevel = 0;
                Game.switchState(new MenuGameState());
            }
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void keyDown(char key) { }

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
        float startPos = Game.sketch.width * 0.5f - (componentOptions.length) * Factory.COMPONENT_SPACING * 0.5f
                + Factory.COMPONENT_SPACING * 0.5f; // Calculate padded start x value
        // Shows component values, keybinds and images
        for (int i = 0; i < componentOptions.length; i++) {
            PVector position = new PVector(startPos + i * Factory.COMPONENT_SPACING, Game.sketch.height * 0.9f);
            componentOptions[i].render(position);
            if (componentOptions[i].mouseTouching(position) && Game.mouseDown() && !crane.hasComponent()) {
                crane.addComponent(Component.createComponent(componentOptions[i].type), componentOptions[i].type);
                crane.setTarget(position);
                Game.mouseInputDelay = Factory.MOUSE_DELAY;
            }

            }
        }

    protected void flashMoney() {
        // TODO: Fix bounds
        Game.sketch.fill(0xAAFF0000);
        Game.sketch.rect(0, 0, 100, 20);
        Game.sketch.fill(0xFFFFFFFF);
    }

    /**
     * Draws target values and highlights current target
     * Draws score and remaining lives
     */
    protected void drawUI() {
        Game.sketch.textSize(40f);
        Game.sketch.fill(0xFFFFFFFF);
        // TODO: Draw target!
        Game.sketch.textAlign(Factory.TOP, Factory.LEFT);
        Game.sketch.text(String.format("Target: %s\nMoney: %d", target.toString(), Game.money), Game.sketch.width * 0.01f, Game.sketch.height * 0.05f);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
        drawComponents();
    }
}

