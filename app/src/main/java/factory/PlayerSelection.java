package factory;

/**
 * Class acting as a tuple to hold player selected type and components 
 * in a single instance
 */
public class PlayerSelection {
  public ComponentType type;
  public Component component;
  
  public PlayerSelection(ComponentType type, ComponentFactory factory) {
    this.type = type;
    this.component = factory.createComponent(type);
    this.component.rotation = 0f;
  }
}
