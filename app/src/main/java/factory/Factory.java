package factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class Factory extends PApplet {
    final float FPS = 60f;
    public static final float COMPONENT_SPACING = 150f;
    public static final float MOUSE_DELAY = 0.5f;
    final int WIDTH = 1920, HEIGHT = 1080;

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
        Game.sketch = this;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Game.config = gson.fromJson(String.join("", loadStrings("Config.json")), Config.class);
        frameRate(FPS);
        imageMode(CENTER);
        rectMode(CENTER);
        ellipseMode(CENTER);
        textAlign(CENTER, CENTER);
        // Initialise various Processing modes and variables
        PFont font = createFont("Fonts/AgencyFB-Bold.ttf", 18f);
        if (font != null) {
            textFont(font);
        }

        ImageDataBase.populate(this);

        Stats.statBackground = ImageDataBase.get("StatReadout.png");

        AnimationPool.populate(this);

        // Instantiate states
        Game.state = (IState) new MenuGameState();
        // state = (IState) new TestGameState();
        // Game.money = 600;
        // Game.state = new BuyGameState();

        // Enter state
        Game.state.onEnter();
    }

    /*
     * Updates current state and checks if the state should change
     */
    public void draw() {
        updateDeltaTime();
        Game.mouseInputDelay = max(0f, Game.mouseInputDelay - Game.deltaTime);
        background(0xFF000000);
        Game.state.update();
        Game.state.checkTransition();
    }

    public void keyPressed() {
        Game.state.keyDown(Character.toLowerCase(key));
    }

    private void updateDeltaTime() {
        currentFrameTime = millis() * 0.001f;
        Game.deltaTime = currentFrameTime - lastFrameTime;
        lastFrameTime = currentFrameTime;
    }

    public static void printfln(String format, Object... args) {
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

    public static float cLerp(float min, float max, float delta) {
        return min * (1 - delta) + max * delta;
    }

    public static float smoothStep(float min, float max, float delta) {
        delta = clamp(0f, 1f, delta);
        if (delta == Float.NaN) {
            delta = 0f;
        }
        return cLerp(min, max, 0.5f + 0.5f * (sin(PI * delta - 0.5f * PI)));
    }

    public static float easeStep(float min, float max, float delta) {
        delta = clamp(0f, 1f, delta);
        if (delta == Float.NaN) {
            delta = 0f;
        }
        return cLerp(min, max, 0.5f + 0.5f * (sin(PI * 2.0f * delta - 0.5f * PI)));
    }

    public static float ease(float min, float max, float delta) {
        delta = clamp(0f, 1f, delta);
        if (delta == Float.NaN) {
            delta = 0f;
        }
        return cLerp(min, max, easeValue(delta));
    }

    public static float easeValue(float value) {
        if (value <= 0.25f) {
            return 0.5f + 0.5f * sin((4f * PI * value) - 0.5f * PI);
        } else if (value >= 0.75f) {
            return 0.5f + 0.5f * sin((-4f * PI * value) - 0.5f * PI);
        } else {
            return 1f;
        }
    }

    public static float easeOutBack(float value) {
        return 1 + (1.70158f + 1) * pow(value - 1, 3) + 1.70158f * pow(value - 1, 2);
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

    public static PVector smoothTowards(PVector min, PVector max, float value) {
        return new PVector(smoothStep(min.x, max.x, value), smoothStep(min.y, max.y, value));
    }

    public static PVector circleVector(float radians) {
        return new PVector(sin(radians), cos(radians));
    }

    public void translate(PVector position) {
        Game.sketch.translate(position.x, position.y);
    }

    public void image(PImage image, PVector position) {
        Game.sketch.image(image, position.x, position.y);
    }

    public void image(PImage image, PVector position, float rotation) {
        Game.sketch.pushMatrix();
        Game.sketch.translate(position.x, position.y);
        Game.sketch.rotate(rotation);
        Game.sketch.image(image, 0f, 0f);
        Game.sketch.popMatrix();
    }

    public static float remap(float newMin, float newMax, float oldMin, float oldMax, float value) {
        return lerp(newMin, newMax, invLerp(oldMin, oldMax, value));
    }

    public void line(PVector pos1, PVector pos2) {
        line(pos1.x, pos1.y, pos2.x, pos2.y);
    }
}
