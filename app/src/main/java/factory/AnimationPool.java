package factory;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Used to hold all animation instances
 * Automatically deallocates animations
 * once they are in a 'done' state
 * (Parent deallocated/Reached final frame)
 * Global AnimationPool ref is allocated in Factory.pde
 */
public class AnimationPool {
    private static HashMap<AnimationType, Animation> animationPool;
    public static ArrayList<Animation> livePool;
    private static ArrayList<Animation> toRemove;

    public static void populate(Factory factory) {
        toRemove = new ArrayList<Animation>();
        livePool = new ArrayList<Animation>();
        HashMap<AnimationType, PImage[]> animations = populateAnimations(factory);
        animationPool = new HashMap<AnimationType, Animation>();
        for (AnimationType type : AnimationType.values()) { // Init special values for specific animation s
            PVector position = new PVector();
            float speed = 60f;
            float rotation = 0f;
            switch (type) {
                case COMPONENT_ADD:
                    rotation = 0f;
                    speed = 48f;
                    break;
                case SHIELD_PARTICLES:
                    rotation = 0f;
                    speed = 10f;
                    break;
                case GUN_FLAME:
                    position = new PVector(0, 118);
                    rotation = 0f;
                    speed = 47f;
                    break;
            }
            PApplet.println("Initialising animation: " + type);
            animationPool.put(
                type, 
                new Animation(animations.get(type), speed, rotation, position, type == AnimationType.COMPONENT_ADD)
            );
        }
    }

    /*
     * Plays an animation at a set point looping infinitely
     */
    public void play(AnimationType type, float rotation, PVector position) {
        Animation anim = animationPool.get(type).copy();
        if (anim == null) {
            PApplet.println("Could not find animation");
            return;
        }
        anim.didHaveParent = false;
        anim.offset = position;
        anim.rotation = rotation;
        anim.visible = true;
        livePool.add(anim);
    }

    /*
     * Plays an animation at a set point, optionally looping
     * 
     * @param destroyOnEnd - Will animation keep looping after reaching final frame
     */
    public static void play(AnimationType type, float rotation, PVector position, boolean destroyOnEnd) {
        Animation anim = animationPool.get(type).copy();
        if (anim == null) {
            PApplet.println("Could not find animation");
            return;
        }
        anim.didHaveParent = false;
        anim.offset = position;
        anim.rotation = rotation;
        anim.visible = true;
        anim.hideOnFinish = destroyOnEnd;
        livePool.add(anim);
    }

    /*
     * 'Attaches' an animation to a ComponentSocket
     */
    public static void play(AnimationType type, Product parent, ComponentSocket socket) {
        Animation anim = animationPool.get(type).copy();
        if (anim == null) {
            PApplet.println("Could not find animation");
            return;
        }
        anim.didHaveParent = true;
        anim.parent = parent;
        anim.socket = socket;
        anim.visible = true;
        livePool.add(anim);
    }

    public static void update() {
        toRemove.clear();
        for (Animation anim : livePool) {
            if (!anim.visible) {
                toRemove.add(anim);
            } else {
                anim.render();
            }
        }
        livePool.removeAll(toRemove);
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
    private static HashMap<AnimationType, PImage[]> populateAnimations(Factory factory) {
        // Allocate hashmap
        HashMap<AnimationType, PImage[]> animations = new HashMap<AnimationType, PImage[]>();
        AnimationType[] types = AnimationType.values();
        String[] animationNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            animationNames[i] = animationTypeToFileName(types[i]); // Parse enum values to file name
        }

        for (int i = 0; i < animationNames.length; i++) {
            ArrayList<PImage> animationFrames = new ArrayList<PImage>();
            for (String imageName : ImageDataBase.keySet()) { // Find PImages that are part of animation
                if (imageName.contains(animationNames[i])) {
                    animationFrames.add(ImageDataBase.get(imageName)); // Add image to list of PImages
                }
            }
            PImage[] frames = new PImage[animationFrames.size()];
            frames = animationFrames.toArray(frames); // ArrayList.toArray returns Object[] :(
            animations.put(types[i], frames); // Add to animation hashmap
        }
        return animations;
    }

    /**
     * Converts AnimationType enum to a filename substring to
     * allow for animation frames to be found
     */
    private static String animationTypeToFileName(AnimationType type) {
        String[] words = type.toString().split("_"); // File names are in format: FooBar.png, enum is FOO_BAR
        ArrayList<String> convertedWords = new ArrayList<String>();
        for (String word : words) {
            StringBuilder wordBuilder = new StringBuilder(word.toLowerCase());
            wordBuilder.setCharAt(0, Character.toUpperCase(wordBuilder.charAt(0))); // Title case
            convertedWords.add(wordBuilder.toString());
        }
        return String.join("", convertedWords); // Concatenate all array values
    }
}
