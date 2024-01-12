package factory;

import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PVector;

/**
 * Factory class responsible for instantiating different product types with initial values
 */
public class ProductFactory {
  public Product createBase(ProductType type) {
    switch (type) {
      case LIGHT:
        ArrayList<ComponentSocket> lightSockets = new ArrayList<ComponentSocket>(
          Arrays.asList( new ComponentSocket(null, new PVector()) )
        );
        return new LightBase(0f, new PVector(), lightSockets);
      case NORMAL:
        ArrayList<ComponentSocket> normalSockets = new ArrayList<ComponentSocket>(
          Arrays.asList( new ComponentSocket(null, new PVector(50f, 0f)), new ComponentSocket(null, new PVector(-50f, 0f)))
        );
        return new NormalBase(0f, new PVector(), normalSockets);
      case HEAVY:
        ArrayList<ComponentSocket> heavySockets = new ArrayList<ComponentSocket>(
          Arrays.asList( new ComponentSocket(null, new PVector(50f, 50f)), new ComponentSocket(null, new PVector(-50f, -50f)),
                         new ComponentSocket(null, new PVector(50f, -50f)), new ComponentSocket(null, new PVector(-50f, 50f)))
        );
        return new HeavyBase(0f, new PVector(), heavySockets);
      default:
        return null;
    }
  }
}
