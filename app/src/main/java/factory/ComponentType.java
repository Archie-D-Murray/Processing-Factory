package factory;

public enum ComponentType { 
  FAN, GUN, SHIELD;
  
  // Caching as only needs to be computed once
  public static ComponentType[] types = ComponentType.values();
  
  public static ComponentType fromInt(int x) {
    return types[x];
  }
}
