package factory;

import java.util.Arrays;
import java.util.Iterator;

import processing.core.PImage;
import processing.core.PVector;

/**
 * ItemMenu
 */
public class ItemMenu implements Iterable<InventoryItem> {

    static PImage backgroundLeft;
    static PImage backgroundRight;
    static PImage backgroundSingle;
    static PImage backgroundCenter;

    final float PADDING = 20f;

    private InventoryItem[] items;
    private PVector position;

    public static void initialise() {
        backgroundLeft = ImageDataBase.get("ItemMenuLeft.png");
        backgroundRight = ImageDataBase.get("ItemMenuRight.png");
        backgroundSingle = ImageDataBase.get("ItemMenuSingle.png");
        backgroundCenter = ImageDataBase.get("ItemMenuCenter.png");
    }

    public ItemMenu(ComponentType[] components, PVector position) {
        items = new InventoryItem[components.length];
        float x = position.x - Factory.COMPONENT_SPACING * components.length / 2 - PADDING * (components.length + 1) / 2;
        for (int i = 0; i < components.length; i++) {
            items[i] = new InventoryItem(new ComponentSelect(components[i], new PVector(x, position.y)));
            x += Factory.COMPONENT_SPACING + PADDING;
        }
        this.position = position;
    }

    public ItemMenu(ProductType[] products, PVector position) {
        items = new InventoryItem[products.length];
        float x = position.x - Factory.COMPONENT_SPACING * products.length / 2 - PADDING * (products.length + 1) / 2;
        for (int i = 0; i < products.length; i++) {
            items[i] = new InventoryItem(new ProductSelect(products[i], new PVector(x, position.y)));
            x += Factory.COMPONENT_SPACING + PADDING;
        }
        this.position = position;
    }

    public void drawBackground() {
        if (items.length == 1) {
            Game.sketch.image(backgroundSingle, position);
        } else {
            for (int i = 0; i < items.length; i++) {
                if (i == 0) {
                    Game.sketch.image(backgroundLeft, items[i].boundingBox.position);
                } else if (i == items.length - 1) {
                    Game.sketch.image(backgroundRight, items[i].boundingBox.position);
                } else {
                    Game.sketch.image(backgroundCenter, items[i].boundingBox.position);
                }
            }
        }
    }

	@Override
	public Iterator<InventoryItem> iterator() {
		return Arrays.stream(items).iterator();
	}
}
