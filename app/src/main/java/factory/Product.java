package factory;

import java.util.ArrayList;

import processing.core.PImage;
import processing.core.PVector;

/**
 * Base class representing a Product
 */
public abstract class Product {
  public PImage image;
  public PVector position;
  public float rotation;
  public int baseValue = 0;
  public int targetPosIndex = 0;
  public float conveyorSpeed = 0f;
  public boolean hasBeenProcessed = false;
  ArrayList<ComponentSocket> components;
  
  public Product(PImage image, PVector position, float rotation) {
    this.image = image.copy();
    this.position = position;
    this.rotation = rotation;
    this.components = new ArrayList<ComponentSocket>();
  }
  
  /**
   * Renders self then all components on top
   */
  public void render() {
    String value = String.format("%d", getValue());
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.text(value, position.x - Game.sketch.textWidth(value) * 0.5f, position.y + Factory.COMPONENT_SPACING);
    Game.sketch.pushMatrix();
    Game.sketch.rotate(rotation);
    Game.sketch.translate(position.x, position.y);
    Game.sketch.image(image, 0f, 0f);
    Game.sketch.popMatrix();
    // Render components on top of product
    for (ComponentSocket component : components) {
      component.render(position);
    }
  }
  
  /**
   * Calculates value of product and all components
   */
  public int getValue() { // Used in reward calc
    int value = baseValue;
    for (ComponentSocket socket : components) {
      if (socket.component == null) {
        continue;
      }
      value += socket.component.value;
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
