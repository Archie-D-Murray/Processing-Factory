package factory;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
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
  protected int score;
  protected int lives;
  protected ArrayList<Integer> targetValues;
  protected PlayerSelection playerSelection = null;
  protected ComponentSocket closestSocket;
  protected ComponentSelect[] componentOptions;
  protected ProductSelect[] productOptions;
  protected ArrayList<Asteroid> asteroids;
  protected float mouseInputDelay = 0f;
  
  public PlayGameState(int score) {
    this.score = score;
  }

  /**
   * Initialises all config variables and sets up conveyor class
   */
  @Override public void onEnter() {
    PApplet.println("Entered play state");
    populateTargetValues();
    populateAsteroids();
    componentFactory = new ComponentFactory();
    productFactory = new ProductFactory();
    reciever = new ProductReceiver();
    reciever.targetValue = targetValues.iterator().next();
    PVector[] conveyorPositions = getConveyorPositions();
    initSelectOptions();
    initProductOptions();
    conveyor = new Conveyor(conveyorPositions, 2f);
    crane = new Crane();
    closestSocket = null;
  }

  /**
   * Updates all member arrays and draws UI elements
   */
  @Override public void update() {
    mouseInputDelay = PApplet.max(0f, mouseInputDelay - Game.sketch.deltaTime);
    updateConveyor();
    highlightComponentSockets();
    drawUI();
    Game.sketch.animationPool.update();
    checkAsteroidCollision();
    crane.update();
  }

  @Override public void onExit() { }

  @Override public void keyDown(char key) { }
  
  protected abstract void populateAsteroids();

  protected void addReward(int amount) {
    score += amount;
    if (amount < 0) {
      lives--;
    }
  }
  
  /**
   * Check asteroid collisions and remove upon being touched
   */
  protected void checkAsteroidCollision() {
    ArrayList<Asteroid> toRemove = new ArrayList<Asteroid>();
    for (Asteroid asteroid : asteroids) {
      asteroid.render();
      if (asteroid.isTouchingMouse()) {
        lives--;
        toRemove.add(asteroid);
      }
    }
    asteroids.removeAll(toRemove);
  }
  
  protected abstract PVector[] getConveyorPositions();

  protected abstract void populateTargetValues();

  protected int genRandomValue(ProductType type) {
    int value = 0;
    switch (type) {
    case LIGHT:
      value = Factory.random.nextInt(150, 500);
      break;
    case NORMAL:
      value = Factory.random.nextInt(550, 1000);
      break;
    case HEAVY:
      value = Factory.random.nextInt(1000, 2000);
      break;
    }
    return value;
  }

  /**
   * Returns closest product to mouse on conveyor
   */
  protected Product getClosestToMouse() {
    if (playerSelection == null) {
      return null;
    }
    float closest = Float.MAX_VALUE;
    PVector mousePos = Game.sketch.getMousePosition();
    Product closestProduct = null;
    for (Product product : conveyor.conveyorItems) {
      if (PVector.dist(mousePos, product.position) < closest && playerSelection.component.getBoundingBox(Game.sketch.getMousePosition()).isOverlapping(product.getBoundingBox())) {
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
    //Conveyor logic
    conveyor.moveConveyorItems();
    ArrayList<Product> toRemove = new ArrayList<Product>();
    for (Product product : conveyor.productsAwaitingReceiver) {
      addReward(reciever.getMoneyFromSubmission(product));
      if (targetValues.size() > 0) {
        targetValues.remove(targetValues.indexOf(reciever.targetValue));
        if (targetValues.size() > 0) {
          reciever.targetValue = targetValues.iterator().next();
        }
      }
      toRemove.add(product);
      product = null;
      // TODO: Play money animation
    }
    conveyor.productsAwaitingReceiver.removeAll(toRemove);
  }

  /**
   * Highlights all empty sockets changing colour based on whether the currently held item
   * will collide with the component the player has selected and attaches relevant animation
   * if player successfully adds component
   */
  protected void highlightComponentSockets() {
    if (playerSelection != null) {
      playerSelection.component.draw(Game.sketch.getMousePosition());
    }
    //Mouse logic
    Product closestProduct = getClosestToMouse();
    if (closestProduct != null && playerSelection != null) {

      //Find closest ComponentSocket
      closestSocket = closestProduct.getClosestSocket();
      if (closestSocket != null) {
        PVector worldSpaceSocketPos = PVector.add(closestSocket.offset, closestProduct.position);
        if (closestProduct.checkCollisionAtSocket(playerSelection.component, closestSocket)) {
          //Highlight invalid component socket
          Game.sketch.fill(0xAAFF0000);
          Game.sketch.rect(worldSpaceSocketPos.x, worldSpaceSocketPos.y, 20f, 20f);
        } else {
          //Highlight valid component socket
          Game.sketch.fill(0xAA00FF00);
          Game.sketch.rect(worldSpaceSocketPos.x, worldSpaceSocketPos.y, 20f, 20f);
          if (Game.sketch.mousePressed) {
            crane.setTarget(Game.sketch.getMousePosition());
            closestSocket.component = playerSelection.component;
            // Spark effect when adding component
            Game.sketch.animationPool.play(AnimationType.COMPONENT_ADD, closestProduct, closestSocket);
            switch (playerSelection.type) {
              case GUN: // Attach animations to gun component
                Game.sketch.animationPool.play(AnimationType.GUN_FLAME, closestProduct, closestSocket);
                break;
                
              case SHIELD: // Attach animations to shield component
                Game.sketch.animationPool.play(AnimationType.SHIELD_PARTICLES, closestProduct, closestSocket);
              default:
                break;
            }
            playerSelection = null; // Reset player select
            PApplet.println("Reset playerSelection");
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
    float startPos = Game.sketch.width * 0.5f - (componentOptions.length) * Factory.COMPONENT_SPACING * 0.5f + Factory.COMPONENT_SPACING * 0.5f;  // Calculate padded start x value
    // Shows component values, keybinds and images
    for (int i = 0; i < componentOptions.length ; i++) {
      PVector position = new PVector(startPos + i * Factory.COMPONENT_SPACING, Game.sketch.height * 0.9f);
      if (playerSelection == null) {
        componentOptions[i].render(null, position);
      } else { 
        componentOptions[i].render(playerSelection.type, position); // Draw highlight box if component and selection type matches
      }
      if (componentOptions[i].mouseTouching(position) && Game.sketch.mousePressed && mouseInputDelay == 0f) {
        playerSelection = new PlayerSelection(componentOptions[i].type, componentFactory);
        crane.setTarget(Game.sketch.getMousePosition());
        mouseInputDelay = Factory.MOUSE_DELAY;
      }
    }
    
    // Shows product values, keybinds and images
    startPos = Game.sketch.height * 0.5f - (productOptions.length) * Factory.COMPONENT_SPACING * 0.5f + Factory.COMPONENT_SPACING * 0.5f;  // Calculate padded start y value
    for (int i = 0; i < productOptions.length; i++) {
      PVector position = new PVector(Game.sketch.width * 0.1f, startPos + i * Factory.COMPONENT_SPACING);
      productOptions[i].render(position);
      if (productOptions[i].isTouchingMouse(position) && Game.sketch.mousePressed && mouseInputDelay == 0f) {
        conveyor.addProduct(productFactory.createBase(productOptions[i].type));
        mouseInputDelay = Factory.MOUSE_DELAY;
      }
    }
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
    Game.sketch.text(String.format("Score: %d\nLives: %d", score, lives), Game.sketch.width * 0.01f, Game.sketch.height * 0.05f);
    Game.sketch.text("Target: ", Game.sketch.width * 0.01f, Game.sketch.height * 0.05f + (3.25f * Game.sketch.textAscent()));
    PVector pos = new PVector(Game.sketch.width * 0.01f + Game.sketch.textWidth("Target: "), Game.sketch.height * 0.05f + (3.25f * Game.sketch.textAscent()));
    for (int i = 0; i < targetValues.size(); i++) {
      if (i == 0) { // Drawing first element (needs arrow and highlight colour
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text(targetValues.get(i).toString(), pos.x, pos.y);
        PImage pointer = Game.sketch.imageDataBase.get("Pointer.png");
        Game.sketch.image(pointer, pos.x + Game.sketch.textWidth(targetValues.get(i).toString()) * 0.5f, pos.y + pointer.height + Game.sketch.textAscent() * 0.5f);
        if (targetValues.size() > 1) { // Only draw comma if necessary
          pos.x += Game.sketch.textWidth(targetValues.get(i).toString());
          Game.sketch.fill(0xFF888888);
          Game.sketch.text(", ", pos.x, pos.y);
          pos.x += Game.sketch.textWidth(", ");
        }
      } else if (i == targetValues.size() - 1) { // Drawing end values
        Game.sketch.fill(0xFF888888);
        Game.sketch.text(targetValues.get(i), pos.x, pos.y);
      } else { // Drawing middle values
        Game.sketch.fill(0xFF888888);
        Game.sketch.text(targetValues.get(i).toString() + ", ", pos.x, pos.y);
        pos.x += Game.sketch.textWidth(targetValues.get(i) + ", ");
      }
    }
    drawSelectElements();
  }
}
