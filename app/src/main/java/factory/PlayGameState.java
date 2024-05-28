package factory;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * All base game logic, inherited by Level classes which implement
 * setup functions to provide configurablity to levels
 */
public abstract class PlayGameState implements IState {
    protected Crane crane;
    protected ComponentFactory componentFactory;
    protected ProductFactory productFactory;
    protected ProductReceiver reciever;
    protected Conveyor conveyor;
    protected int money;
    protected int lives;
    protected float levelTime;
    protected PlayerSelection playerSelection = null;
    protected ComponentSocket closestSocket;
    protected ComponentSelect[] componentOptions;
    protected ProductSelect[] productOptions;
    protected ArrayList<Asteroid> asteroids;
    protected float mouseInputDelay = 0f;

    public PlayGameState(int money, float levelTime) {
        this.money = money;
        this.levelTime = levelTime;
    }

    /**
     * Initialises all config variables and sets up conveyor class
     */
    @Override
    public void onEnter() {
        PApplet.println("Entered play state");
        componentFactory = new ComponentFactory();
        productFactory = new ProductFactory();
        reciever = new ProductReceiver();
        PVector[] conveyorPositions = getConveyorPositions();
        initSelectOptions();
        initProductOptions();
        conveyor = new Conveyor(conveyorPositions, 2f);
        crane = new Crane(componentOptions);
        closestSocket = null;
    }

    /**
     * Updates all member arrays and draws UI elements
     */
    @Override
    public void update() {
        levelTime = PApplet.max(0f, levelTime - Game.sketch.deltaTime);
        mouseInputDelay = PApplet.max(0f, mouseInputDelay - Game.sketch.deltaTime);
        updateConveyor();
        highlightComponentSockets();
        drawUI();
        Game.sketch.animationPool.update();
        crane.update();
        if (crane.getMoneyUsed() != 0) {
            money -= crane.getMoneyUsed();
            crane.resetMoneyUsed();
        }
    }

    @Override
    public void onExit() { }

    @Override
    public void keyDown(char key) { }

    protected void addReward(int amount) {
        money += amount;
        if (amount < 0) {
            lives--;
        }
    }

    protected abstract PVector[] getConveyorPositions();

    /**
     * Returns closest product to mouse on conveyor
     */
    protected Product getClosestToMouse() {
        if (!crane.hasComponent()) {
            return null;
        }
        float closest = 100f;
        PVector mousePos = Game.sketch.getMousePosition();
        Product closestProduct = null;
        for (Product product : conveyor.conveyorItems) {
            if (PVector.dist(mousePos, product.position) < closest
                    && product.getBoundingBox().isOverlappingPoint(mousePos)) {
                closestProduct = product;
                closest = PVector.dist(mousePos, product.position);
            }
        }
        return closestProduct;
    }

    /**
     * Moves conveyor items and checks if a product has reached the end
     */
    protected void updateConveyor() {
        // Conveyor logic
        conveyor.moveConveyorItems();
        ArrayList<Product> toRemove = new ArrayList<Product>();
        for (Product product : conveyor.productsAwaitingReceiver) {
            money += reciever.getMoneyFromSubmission(product);
            toRemove.add(product);
            product = null;
            // TODO: Play money animation
        }
        conveyor.productsAwaitingReceiver.removeAll(toRemove);
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
                    if (Game.sketch.mousePressed && mouseInputDelay == 0f) {
                        System.out.println("Set target");
                        crane.setTarget(closestSocket, closestProduct);
                        mouseInputDelay = Factory.MOUSE_DELAY;
                    }
                }
            }
        }
    }

    /**
     * Draws UI buttons to allow player to add products
     * to the conveyor and add components to products
     */
    protected void drawSelectElements() {
        float startPos = Game.sketch.width * 0.5f - (componentOptions.length) * Factory.COMPONENT_SPACING * 0.5f
                + Factory.COMPONENT_SPACING * 0.5f; // Calculate padded start x value
        // Shows component values, keybinds and images
        for (int i = 0; i < componentOptions.length; i++) {
            PVector position = new PVector(startPos + i * Factory.COMPONENT_SPACING, Game.sketch.height * 0.9f);
            if (playerSelection == null) {
                componentOptions[i].render(null, position);
            } else {
                // Draw highlight box if component and selection type matches
                componentOptions[i].render(playerSelection.type, position);
            }
            if (componentOptions[i].mouseTouching(position) && Game.sketch.mousePressed && mouseInputDelay == 0f && !crane.hasComponent()) {
                if (money >= componentOptions[i].value) {
                    crane.addComponent(componentFactory.createComponent(componentOptions[i].type), componentOptions[i].type);
                    crane.setTarget(position);
                    mouseInputDelay = Factory.MOUSE_DELAY;
                } else {
                    flashMoney();
                }
            }
        }

        // Shows product values, keybinds and images
        startPos = Game.sketch.height * 0.5f - (productOptions.length) * Factory.COMPONENT_SPACING * 0.5f
                + Factory.COMPONENT_SPACING * 0.5f; // Calculate padded start y value
        for (int i = 0; i < productOptions.length; i++) {
            PVector position = new PVector(Game.sketch.width * 0.1f, startPos + i * Factory.COMPONENT_SPACING);
            productOptions[i].render(position);
            if (productOptions[i].isTouchingMouse(position) && Game.sketch.mousePressed && mouseInputDelay == 0f) {
                if (money >= productOptions[i].value) {
                    conveyor.addProduct(productFactory.createBase(productOptions[i].type));
                    mouseInputDelay = Factory.MOUSE_DELAY;
                    money -= productOptions[i].value;
                } else {
                    flashMoney();
                }
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
     * Allows for different select options to be enabled in concrete implementations
     */
    protected abstract void initSelectOptions();

    protected abstract void initProductOptions();

    /**
     * Draws target values and highlights current target
     * Draws score and remaining lives
     */
    protected void drawUI() {
        Game.sketch.textSize(40f);
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text(String.format("Remaining Time: %.2f\nMoney: %d\nLives: %d", levelTime, money, lives),
        Game.sketch.width * 0.01f, Game.sketch.height * 0.05f);
        drawSelectElements();
    }

    protected boolean isTimeUp() {
        return levelTime == 0f;
    }
}

