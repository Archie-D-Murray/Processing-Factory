package factory;

import processing.core.PVector;

/**
 * Product UI component allowing player to add products to conveyor instance
 */
public class ProductSelect extends Select {
  public ProductType type;

  public ProductSelect(ProductType type) {
    this.type = type;
    switch (type) {
    case LIGHT:
      icon = ImageDataBase.get("LightProduct.png").copy();
      value = 100;
      break;

    case NORMAL:
      icon = ImageDataBase.get("NormalProduct.png").copy();
      value = 500;
      break;

    case HEAVY:
      icon = ImageDataBase.get("HeavyProduct.png").copy();
      value = 1000;
      break;

    default:
      icon = ImageDataBase.get("Default.png").copy();
      break;
    }
  }

  @Override protected void drawValue(PVector position) {
    Game.sketch.fill(0xFFFFFFFF);
    Game.sketch.text(String.format("%d", value), position.x - Factory.COMPONENT_SPACING - Game.sketch.textWidth(String.format("%d", value)) * 0.5f, position.y + Game.sketch.textAscent() * 0.5f);
  }
}
