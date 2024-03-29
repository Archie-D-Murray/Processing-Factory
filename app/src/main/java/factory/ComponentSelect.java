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
      icon = Game.sketch.imageDataBase.get("FanComponent.png").copy();
      value = 100;
      break;
    case GUN:
      icon = Game.sketch.imageDataBase.get("GunComponent.png").copy();
      value = 250;
      break;
    case SHIELD:
      icon = Game.sketch.imageDataBase.get("ShieldComponent.png").copy();
      value = 300;
      break;
    default:
      icon = Game.sketch.imageDataBase.get("Default.png").copy();
    }
    this.type = type;
  }

  public void render(ComponentType type, PVector position) {
    super.render(position);
    highlightSelectedType(position, type);
  }

  public void highlightSelectedType(PVector position, ComponentType type) {
    if (this.type == type) {
      Game.sketch.image(Game.sketch.imageDataBase.get("Select.png"), position);
    }
  }

  @Override protected void drawValue(PVector position) {
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.text(String.format("%d", value), position.x - Game.sketch.textWidth(String.format("%d", value)) * 0.5f, position.y - Game.sketch.COMPONENT_SPACING * 0.5f - Game.sketch.textAscent() * 2f);
  }
  
  public boolean mouseTouching(PVector position) {
    return new BoundingBox(position, new PVector(icon.width, icon.height)).isTouchingMouse();
  }
}
