package factory;

import java.util.ArrayList;

import processing.core.PVector;

/**
 * All implementations of Product and logic overrides if necessary
 */
class LightBase extends Product {
  protected LightBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("LightProduct.png"), position, rotation);
    baseValue = new Stats(50, 0, 10, 100);
    components = sockets;
  }
  
  @Override public BoundingBox getBoundingBox() {
    return new BoundingBox(position, image.width); // Circular collision
  }
}

class NormalBase extends Product {
  protected NormalBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("NormalProduct.png"), position, rotation);
    baseValue = new Stats(70, 0, 20, 200);
    components = sockets;
  }
}

class HeavyBase extends Product {
  protected HeavyBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("HeavyProduct.png"), position, rotation);
    baseValue = new Stats(30, 0, 40, 400);
    components = sockets;
  }
}
