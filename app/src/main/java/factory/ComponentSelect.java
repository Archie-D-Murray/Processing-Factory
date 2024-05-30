package factory;

import processing.core.PVector;

/**
 * UI component to allow player to click to select components
 */
public class ComponentSelect extends Select {
  public ComponentType type;

  public ComponentSelect(ComponentType type) {
    switch (type) {
    case FAN:
      icon = ImageDataBase.get("FanComponent.png").copy();
      value = 100;
      break;
    case GUN:
      icon = ImageDataBase.get("GunComponent.png").copy();
      value = 250;
      break;
    case SHIELD:
      icon = ImageDataBase.get("ShieldComponent.png").copy();
      value = 300;
      break;
    default:
      icon = ImageDataBase.get("Default.png").copy();
    }
    this.type = type;
  }

  public void render(ComponentType type, PVector position) {
    super.render(position);
    highlightSelectedType(position, type);
  }

  public void highlightSelectedType(PVector position, ComponentType type) {
    if (this.type == type) {
      Game.sketch.image(ImageDataBase.get("Select.png"), position);
    }
  }

  @Override protected void drawValue(PVector position) {
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.text(String.format("%d", value), position.x, position.y - Factory.COMPONENT_SPACING);
  }
  
  public boolean mouseTouching(PVector position) {
    return new BoundingBox(position, new PVector(icon.width, icon.height)).isTouchingMouse();
  }

  public BoundingBox getBoundingBox(PVector position) {
      return new BoundingBox(position, new PVector(icon.width, icon.height));
  }
}
