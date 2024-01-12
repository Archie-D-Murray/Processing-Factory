package factory;

/**
 * Enum representing all produict types with methods to get an enum type
 * from multiple different variables
 */
public enum ProductType { 
  
  LIGHT, NORMAL, HEAVY;
  
  public static ProductType[] types = ProductType.values();
  
  public static ProductType getRandom() {
    return types[Factory.random.nextInt(types.length)];
  }
  
  public static ProductType fromInt(int i) {
    return types[i];
  }
}
