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
      stats = FanComponent.stats();
      cost = FanComponent.cost();
      break;
    case GUN:
      icon = ImageDataBase.get("GunComponent.png").copy();
      stats = GunComponent.stats();
      cost = GunComponent.cost();
      break;
    case SHIELD:
      icon = ImageDataBase.get("ShieldComponent.png").copy();
      stats = ShieldComponent.stats();
      cost = ShieldComponent.cost();
      break;
    default:
      icon = ImageDataBase.get("Default.png").copy();
      stats = new Stats(0, 0, 0, 0);
      cost = 0;
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
    stats.render(getBoundingBox(position), true);
  }
  
  public boolean mouseTouching(PVector position) {
    return new BoundingBox(position, new PVector(icon.width, icon.height)).isTouchingMouse();
  }

  public BoundingBox getBoundingBox(PVector position) {
      return new BoundingBox(position, new PVector(icon.width, icon.height));
  }
}
