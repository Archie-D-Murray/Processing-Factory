package factory;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Where all product logic is updated
 * Handles moving products to relevant positions and rendering them
 */
public class Conveyor {
  final private int colour = 0xFF444444;
  private float size = 20f;
  private PVector[] positions;
  
  public ArrayList<Product> conveyorItems;
  public ArrayList<Product> productsAwaitingReceiver;
  float conveyorSpeed;
  
  public Conveyor(PVector[] positions, float speed) {
    this.positions = positions;
    this.conveyorSpeed = speed;
    this.conveyorItems = new ArrayList<Product>();
    productsAwaitingReceiver = new ArrayList<Product>();
  }
  
  /**
   * Adds move vector to position of all products then renders them
   */
  public void moveConveyorItems() {
    renderBelt();
    for (Product product : conveyorItems) {
      if (PVector.dist(product.position, positions[product.targetPosIndex]) < PApplet.EPSILON) { // Is product at target position
        if (product.targetPosIndex < positions.length - 1) { // If product is not at end of conveyor
          product.targetPosIndex++;
        } else {
          productsAwaitingReceiver.add(product);
        }
      }
      
      // Move towards takes a maximum move delta to interpolate from first position to second
      product.position = Factory.moveTowards(product.position, positions[product.targetPosIndex], conveyorSpeed); //Move to new pos
      product.render();
    }
    conveyorItems.removeAll(productsAwaitingReceiver); // Remove products that are being processed
  }
  
  /**
   * Adds product to start of belt
   */
  public void addProduct(Product product) {
    product.position = positions[0];
    conveyorItems.add(product);
  }
  
  /**
   * Belt is rendered by drawing lines between a series of points
   */
  private void renderBelt() {
    Game.sketch.strokeWeight(size);
    Game.sketch.stroke(colour);
    for (int i = 0; i < positions.length - 1; i++) { //Last index has no next point to draw to
      Game.sketch.line(positions[i], positions[i + 1]); // Using line allows for diagonal conveyor belts
    }
    Game.sketch.strokeWeight(0f); // Make sure not to create strange behaviour for other shape rendering
    Game.sketch.stroke(0x00FFFFFF);
  }
}
