package factory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class Factory extends PApplet {
    final float FPS = 60f;
    public static final float COMPONENT_SPACING = 150f;
    public static final float MOUSE_DELAY = 0.5f;
    final int WIDTH = 1920, HEIGHT = 1080;
    public static Random random = new Random();
    // Shared variables
    public HashMap<String, PImage> imageDataBase;
    public HashMap<AnimationType, PImage[]> animations;
    public AnimationPool animationPool;
    public IState state;
    public float deltaTime = 0f;

    private float lastFrameTime = 0f;
    private float currentFrameTime = 0f;

    public static void main(String[] args) {
        String[] defaultArgs = new String[] { "factory.Factory" };
        if (args != null) {
            PApplet.main(concat(args, defaultArgs));
        } else {
            PApplet.main(defaultArgs);
        }
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        println("Sketch path: " + sketchPath());
        frameRate(FPS);
        imageMode(CENTER);
        rectMode(CENTER);
        ellipseMode(CENTER);
        Game.sketch = this;
        // Initialise various Processing modes and variables
        PFont font = createFont("Fonts/AgencyFB-Bold.ttf", 18f);
        if (font != null) {
            textFont(font);
        }

        // Load all assets
        populateImages();
        populateAnimations();
        animationPool = new AnimationPool();

        // Instantiate states
        state = (IState) new MenuGameState();
        // state = (IState) new TestGameState();

        // Enter state
        state.onEnter();
    }

    /*
     * Updates current state and checks if the state should change
     */
    public void draw() {
        updateDeltaTime();
        background(0xFF000000);
        state.update();
        state.checkTransition();
    }

    public void keyPressed() {
        state.keyDown(Character.toLowerCase(key));
    }

    public void switchState(IState newState) {
        state.onExit();
        state = newState;
        state.onEnter();
    }

    private void updateDeltaTime() {
        currentFrameTime = millis() * 0.001f;
        deltaTime = currentFrameTime - lastFrameTime;
        lastFrameTime = currentFrameTime;
    }

    /**
     * Loads all .png files in the ./Sprites/ directory
     * and populates the imageDataBase hashmap with the
     * local image path ("Example.png") as the key
     */
    private void populateImages() {
        imageDataBase = new HashMap<String, PImage>();
        File spritesDir = new File(sketchPath() + File.separator + "Sprites" + File.separator);
        // If sprites directory is not present, bail
        if (!spritesDir.exists()) {
            println("Could not load sprites!");
            exit();
            return;
        }
        for (String imagePath : spritesDir.list()) {
            if (!imagePath.endsWith(".png")) {
                continue; // Skip non .png files
            }

            println("Loading image: " + imagePath);
            // Remove absolute path
            imagePath.replace(sketchPath(), "");
            PImage image = loadImage("Sprites" + File.separator + imagePath);
            imageDataBase.put(imagePath, image);
        }
    }

    /**
     * Iterates through all values in imageDataBase and finds all image names that
     * contain
     * the animation frame substring generated from all AnimationType values, each
     * frame of an
     * animation is put in a hashmap with the AnimationType as a key and a PImage
     * array containing
     * induvidual animation frames
     */
    private void populateAnimations() {
        // Allocate hashmap
        animations = new HashMap<AnimationType, PImage[]>();
        AnimationType[] types = AnimationType.values();
        String[] animationNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            animationNames[i] = animationTypeToFileName(types[i]); // Parse enum values to file name
        }

        for (int i = 0; i < animationNames.length; i++) {
            ArrayList<PImage> animationFrames = new ArrayList<PImage>();
            for (String imageName : imageDataBase.keySet()) { // Find PImages that are part of animation
                if (imageName.contains(animationNames[i])) {
                    animationFrames.add(imageDataBase.get(imageName)); // Add image to list of PImages
                }
            }
            PImage[] frames = new PImage[animationFrames.size()];
            frames = animationFrames.toArray(frames); // ArrayList.toArray returns Object[] :(
            animations.put(types[i], frames); // Add to animation hashmap
        }
    }

    /**
     * Converts AnimationType enum to a filename substring to
     * allow for animation frames to be found
     */
    private String animationTypeToFileName(AnimationType type) {
        String[] words = type.toString().split("_"); // File names are in format: FooBar.png, enum is FOO_BAR
        ArrayList<String> convertedWords = new ArrayList<String>();
        for (String word : words) {
            StringBuilder wordBuilder = new StringBuilder(word.toLowerCase());
            wordBuilder.setCharAt(0, Character.toUpperCase(wordBuilder.charAt(0))); // Title case
            convertedWords.add(wordBuilder.toString());
        }
        return String.join("", convertedWords); // Concatenate all array values
    }

    public void printfln(String format, Object... args) {
        println(String.format(format, args));
    }

    public static float clamp(float min, float max, float value) {
        return max(min, min(max, value));
    }

    public static int clampInt(int min, int max, int value) {
        return max(min, min(max, value));
    }

    public static float approach(float current, float target, float maxDelta) {
        if (abs(target - current) <= maxDelta) {
            return target;
        }
        return current + Math.signum(target - current) * maxDelta;
    }

    public static float randRange(float min, float max) {
        float range = (max - min) + 1;
        return ((float) Math.random() * range) + min;
    }

    public PVector getMousePosition() {
        return new PVector(mouseX, mouseY);
    }

    public static float invLerp(float min, float max, float value) {
        return clamp(0f, 1f, (value - min) / (max - min));
    }

    public static PVector moveTowards(PVector current, PVector target, float maxDelta) {
        if (PVector.dist(current, target) <= maxDelta) {
            return target;
        } else {
            PVector addition = PVector.sub(target, current);
            addition.normalize();
            addition.mult(maxDelta);
            return PVector.add(current, addition);
        }
    }

    public PVector circleVector(float radians) {
        return new PVector(sin(radians), cos(radians));
    }

    public void translate(PVector position) {
        translate(position.x, position.y);
    }

    public void image(PImage image, PVector position) {
        image(image, position.x, position.y);
    }

    public static float remap(float newMin, float newMax, float oldMin, float oldMax, float value) {
        return lerp(newMin, newMax, invLerp(oldMin, oldMax, value));
    }

    public void line(PVector pos1, PVector pos2) {
        line(pos1.x, pos1.y, pos2.x, pos2.y);
    }

}
