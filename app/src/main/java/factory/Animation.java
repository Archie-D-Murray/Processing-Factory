package factory;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

enum AnimationType { COMPONENT_ADD, SHIELD_PARTICLES, GUN_FLAME }

/**
 * Class to allow for frame by frame animation
 * Animations are only to be constructed by the AnimationPool class
 * As there is no ECS style system, animation need a ref to Product
 * they should be 'parented' to
 */
public class Animation {
  public Product parent = null;
  public ComponentSocket socket = null;
  private PImage[] frames;
  private int frameIndex = 0;
  private float fps;
  private float frameTime;
  private float timeSinceLastChange;
  public PVector offset;
  public float rotation;
  public boolean isAnimating;
  public boolean visible = false;
  public boolean hideOnFinish;
  public boolean didHaveParent;
  
  public Animation(PImage[] frames, float fps, float rotation, PVector position) {
    this.frames = frames;
    this.fps = fps;
    this.frameTime = 1f / fps;
    this.rotation = rotation;
    this.offset = position;
    this.isAnimating = true;
    this.visible = true;
  }
  
  public Animation(PImage[] frames, float fps, float rotation, PVector offset, boolean hideOnFinish) {
    this.frames = frames;
    this.fps = fps;
    this.frameTime = 1f / fps;
    this.rotation = rotation;
    this.offset = offset;
    this.isAnimating = true;
    this.visible = true;
    this.hideOnFinish = hideOnFinish;
    PApplet.println("Target frame rate for animation: " + frameTime);
  }
  
  public Animation(Animation anim) {
    this.frames = anim.frames;
    this.frameIndex = anim.frameIndex;
    this.fps = anim.fps;
    this.frameTime = anim.frameTime;
    this.timeSinceLastChange = anim.timeSinceLastChange;
    this.rotation = anim.rotation;
    this.offset = anim.offset;
    this.isAnimating = anim.isAnimating;
    this.visible = anim.visible;
    this.hideOnFinish = anim.hideOnFinish;
    this.parent = anim.parent;
    this.socket = anim.socket;
    this.didHaveParent = anim.didHaveParent;
  }
  
  public void render() {
    if (!visible) {
      PApplet.println("WARN: This animation instance should be recalled to the animation pool and reset");
      return;
    }
    if (isAnimating) {
      timeSinceLastChange += Game.deltaTime;
      
      // Hide if reached end of animation
      if (timeSinceLastChange >= frameTime && frameIndex == frames.length - 1 && hideOnFinish) {
        reset();
        return;
      }
      
      // Hide if attached parent is no longer on screen
      if (didHaveParent && parent.hasBeenProcessed) {
        reset();
        return;
      }
      
      // if running behind on frame rate, need to adjust time when frameIndex is incremented
      // -> if frameRate = 30 (running behind) need to wait half the time
      if (timeSinceLastChange >= frameTime) {
        frameIndex = ++frameIndex % frames.length;
        timeSinceLastChange = 0f;
      }
    }
    Game.sketch.pushMatrix();
    if (parent == null && socket == null) {
      Game.sketch.translate(offset.x, offset.y);
      Game.sketch.rotate(rotation);
    } else {
      // Rotate offset to match parent
      PVector offsetOnCircle = new PVector(offset.x, offset.y);
      offsetOnCircle.rotate(rotation + socket.component.rotation + parent.rotation);
      PVector pos = PVector.add(parent.position, socket.offset); // Get root position
      pos.add(offsetOnCircle); // Apply offset
      Game.sketch.translate(pos);
      Game.sketch.rotate(rotation + parent.rotation + socket.component.rotation);
    }
    Game.sketch.image(frames[frameIndex], 0f, 0f);
    Game.sketch.popMatrix();
  }
  
  private void reset() { // Hides animation
    visible = false;
    timeSinceLastChange = 0f;
    frameIndex = 0;
    offset = new PVector(0f, 0f);
    rotation = 0f;
    isAnimating = true;
  }
  
  public Animation copy() {
    return new Animation(this);
  }
}
