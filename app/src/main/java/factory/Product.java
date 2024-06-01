package factory;

import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PImage;
import processing.core.PVector;

/**
 * Base class representing a Product
 */
public abstract class Product {
  public PImage image;
  public PVector position;
  public float rotation;
  public Stats stats;
  public int targetPosIndex = 0;
  public float conveyorSpeed = 0f;
  public boolean hasBeenProcessed = false;
  ArrayList<ComponentSocket> components;
  
  protected Product(PImage image, PVector position, float rotation) {
    this.image = image.copy();
    this.position = position;
    this.rotation = rotation;
    this.components = new ArrayList<ComponentSocket>();
  }
  
  public static Product createBase(ProductType type) {
    switch (type) {
      case LIGHT:
        ArrayList<ComponentSocket> lightSockets = new ArrayList<ComponentSocket>(
          Arrays.asList( new ComponentSocket(null, new PVector()))
        );
        return new LightBase(0f, new PVector(), lightSockets);
      case NORMAL:
        ArrayList<ComponentSocket> normalSockets = new ArrayList<ComponentSocket>(
          Arrays.asList( new ComponentSocket(null, new PVector(50f, 0f)), new ComponentSocket(null, new PVector(-50f, 0f)))
        );
        return new NormalBase(0f, new PVector(), normalSockets);
      case HEAVY:
        ArrayList<ComponentSocket> heavySockets = new ArrayList<ComponentSocket>(
          Arrays.asList(new ComponentSocket(null, new PVector(50f, 50f)),  new ComponentSocket(null, new PVector(-50f, -50f)),
                        new ComponentSocket(null, new PVector(50f, -50f)), new ComponentSocket(null, new PVector(-50f, 50f)))
        );
        return new HeavyBase(0f, new PVector(), heavySockets);
      default:
        return null;
    }
  }
  /**
   * Renders self then all components on top
   */
  public void render() {
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.pushMatrix();
    Game.sketch.rotate(rotation);
    Game.sketch.translate(position.x, position.y);
    Game.sketch.image(image, 0f, 0f);
    Game.sketch.popMatrix();
    // Render components on top of product
    for (ComponentSocket component : components) {
      component.render(position);
    }
    if (getBoundingBox().isTouchingMouse()) {
        getValue().render(getBoundingBox());
    }
  }
  
  /**
   * Calculates value of product and all components
   */
  public Stats getValue() { // Used in reward calc
    Stats value = new Stats(stats);
    for (ComponentSocket socket : components) {
      if (socket.component == null) {
        continue;
      }
      value.add(socket.component.stats);
    }
    return value;
  }
  
  /**
   * Gets closest component socket to the current mousePosition
   */
  public ComponentSocket getClosestSocket() {
    // Convert mousePos to be local with offsets
    PVector localMousePos = PVector.sub(Game.sketch.getMousePosition(), position);
    ComponentSocket closest = null;
    float closestDist = Float.MAX_VALUE;
    
    //Iterate through each socket to find closest
    for (ComponentSocket socket : components) {
      float dist = PVector.dist(localMousePos, socket.offset);
      // Found new closest socket
      if (dist < closestDist && socket.component == null) {
        closestDist = dist;
        closest = socket;
      } else if (socket.component != null) {
        socket.component.getBoundingBox(PVector.add(socket.offset, position)).render();
      }
    }
    return closest;
  }
  
  /**
   * Checks where there is space to put the passed component into the target socket
   */
  public boolean checkCollisionAtSocket(Component component, ComponentSocket targetSocket) {
    for (ComponentSocket socket : components) {
      if (socket == targetSocket || socket.component == null) {
        continue; // Don't need to check collision here
      }
      PVector componentPos = PVector.add(position, targetSocket.offset); // Position of BoundingBox
      PVector existingComponentPos = PVector.add(position, socket.offset);
      if (component.getBoundingBox(componentPos).isOverlapping(socket.component.getBoundingBox(existingComponentPos))) {
        return true; // Have collision, can't place component here (without rotating possibly)
      }
    }
    return false;
  }
  
  /**
   * Gets bounding box of component, can be overridden for other components
   */
  public BoundingBox getBoundingBox() {
    return new BoundingBox(position, new PVector(image.width, image.height));
  }
}

