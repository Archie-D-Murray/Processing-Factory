package factory;

import java.util.ArrayList;

import processing.core.PVector;

/**
 * All implementations of Product and logic overrides if necessary
 */
class LightBase extends Product {
  public LightBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("LightProduct.png"), position, rotation);
    baseValue = 100;
    components = sockets;
  }
  
  @Override public BoundingBox getBoundingBox() {
    return new BoundingBox(position, image.width); // Circular collision
  }
}

class NormalBase extends Product {
  public NormalBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("NormalProduct.png"), position, rotation);
    baseValue = 500;
    components = sockets;
  }
}

class HeavyBase extends Product {
  public HeavyBase(float rotation, PVector position, ArrayList<ComponentSocket> sockets) {
    super(ImageDataBase.get("HeavyProduct.png"), position, rotation);
    baseValue = 1000;
    components = sockets;
  }
}
