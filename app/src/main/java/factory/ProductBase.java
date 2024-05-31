package factory;

import java.util.ArrayList;

import processing.core.PVector;

/**
 * All implementations of Product and logic overrides if necessary
 */
class LightBase extends Product {
  protected LightBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("LightProduct.png"), position, rotation);
    stats = stats();
    components = sockets;
  }
  
  @Override public BoundingBox getBoundingBox() {
    return new BoundingBox(position, image.width); // Circular collision
  }

  public static Stats stats() {
    return new Stats(50, 0, 10, 100);
  }
}

class NormalBase extends Product {
  protected NormalBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("NormalProduct.png"), position, rotation);
    stats = stats();
    components = sockets;
  }

  public static Stats stats() {
      return new Stats(70, 0, 20, 200);
  }
}

class HeavyBase extends Product {
  protected HeavyBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("HeavyProduct.png"), position, rotation);
    stats = stats();
    components = sockets;
  }

  public static Stats stats() {
      return new Stats(30, 0, 40, 400); 
  }
}

