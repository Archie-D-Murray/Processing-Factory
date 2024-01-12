package factory;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Used to hold all animation instances
 * Automatically deallocates animations
 * once they are in a 'done' state
 * (Parent deallocated/Reached final frame)
 * Global AnimationPool ref is allocated in Factory.pde
 */
public class AnimationPool {
  private HashMap<AnimationType, Animation> animationPool;
  public ArrayList<Animation> livePool;
  private ArrayList<Animation> toRemove;
  
  public AnimationPool() {
    toRemove = new ArrayList<Animation>();
    livePool = new ArrayList<Animation>();
    animationPool = new HashMap<AnimationType, Animation>();
    for (AnimationType type : AnimationType.values()) { // Init special values for specific animation s
      PVector position = new PVector();
      float speed = 60f;
      float rotation = 0f;
      switch (type) {
        case COMPONENT_ADD:
          rotation = 0f;
          speed = 9f;
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
      animationPool.put(type, new Animation(Game.sketch.animations.get(type), speed, rotation, position, type == AnimationType.COMPONENT_ADD));
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
   * @param destroyOnEnd - Will animation keep looping after reaching final frame
   */
  public void play(AnimationType type, float rotation, PVector position, boolean destroyOnEnd) {
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
  public void play(AnimationType type, Product parent, ComponentSocket socket) {
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
  
  public void update() {
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
}
