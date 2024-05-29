package factory;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import processing.core.PImage;

public class ImageDataBase {
    private static HashMap<String, PImage> imageDataBase;
    private static PImage defaultImage;

    /**
     * Loads all .png files in the ./Sprites/ directory
     * and populates the imageDataBase hashmap with the
     * local image path ("Example.png") as the key
     */
    public static void populate(Factory factory) {
        imageDataBase = new HashMap<String, PImage>();
        File spritesDir = new File(factory.sketchPath() + File.separator + "Sprites" + File.separator);
        // If sprites directory is not present, bail
        if (!spritesDir.exists()) {
            System.out.println("Could not load sprites!");
            factory.exit();
            return;
        }
        for (String imagePath : spritesDir.list()) {
            if (!imagePath.endsWith(".png")) {
                continue; // Skip non .png files
            }

            System.out.println("Loading image: " + imagePath);
            // Remove absolute path
            imagePath.replace(factory.sketchPath(), "");
            PImage image = factory.loadImage("Sprites" + File.separator + imagePath);
            imageDataBase.put(imagePath, image);
        }
        assert defaultImage != null;
    }

    public static PImage get(String imageName) {
        if (imageDataBase.containsKey(imageName)) {
            return imageDataBase.get(imageName);
        } else {
            return defaultImage;
        }
    }

	public static Set<String> keySet() {
		return imageDataBase.keySet();
	}
}
