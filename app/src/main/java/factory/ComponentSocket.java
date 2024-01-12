package factory;

import processing.core.PVector;

/**
 * Wrapper class holding instance of child class of component and a PVector offset
 * Used as a tuple
 */
public class ComponentSocket {
  public Component component;
  public PVector offset;
  
  public ComponentSocket(Component component, PVector offset) {
    this.component = component;
    this.offset = offset;
  }
  
  public void render(PVector productPosition) {
    if (component == null) {
      return;
    }
    component.draw(PVector.add(productPosition, offset));
  }
  
  public PVector offset() {
    return offset;
  }
}
