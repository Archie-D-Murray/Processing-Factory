package factory;

import java.util.Arrays;

import processing.core.PImage;
import processing.core.PVector;

public class Crane {
    final float MAX_SPEED = 500;
    final float MIN_SPEED = 200;
    final int WIDTH = 32;
    private PImage vertical;
    private PImage horizontal;
    private PImage center;
    private PImage binding;
    private PVector startPos;
    private PVector currentPos;
    private PVector targetPos;
    private Component component;
    private ComponentType componentType;
    private ComponentSocket socket;
    private Product target;
	  private boolean showComponent;
    private ComponentSelect[] componentSelects;

    public Crane(ComponentSelect[] componentSelects) {
        Game.sketch.printfln(
            "0f: %f, 0.5f: %f, 1f: %f", 
            Factory.easeStep(MIN_SPEED, MAX_SPEED, 0), 
            Factory.easeStep(MIN_SPEED, MAX_SPEED, 0.5f), 
            Factory.easeStep(MIN_SPEED, MAX_SPEED, 1)
        );      
        this.horizontal = Game.sketch.imageDataBase.get("CraneHorizontal.png").copy();
        this.vertical = Game.sketch.imageDataBase.get("CraneVertical.png").copy();
        this.center = Game.sketch.imageDataBase.get("CraneCenter.png").copy();
        this.binding = Game.sketch.imageDataBase.get("CraneBinding.png").copy();
        this.componentSelects = componentSelects;
        this.horizontal.resize(Game.sketch.width, WIDTH);
        this.vertical.resize(WIDTH, Game.sketch.height);
        this.center.resize(center.width / 2, center.height / 2);
        this.binding.resize(binding.width / 2, binding.height / 2);
        currentPos = new PVector(Game.sketch.width / 2, Game.sketch.height / 2);
        showComponent = false;
        target = null;
        socket = null;
        startPos = new PVector(Game.sketch.width / 2, Game.sketch.height / 2);
        targetPos = new PVector();
    }

    public void addComponent(Component component, ComponentType componentType) {
        this.component = component;
        this.componentType = componentType;
        showComponent = false;
    }

    public void setTarget(PVector position) {
        targetPos = position;
        startPos = currentPos;
    }

    public void setTarget(ComponentSocket socket, Product product) {
        target = product;
        this.socket = socket;
        startPos = currentPos;
    }

    public void update() {
        BoundingBox boundingBox = getBoundingBox();
        if (component != null && !showComponent) {
            if (getSelectionBoundingBox().isOverlapping(boundingBox)) {
                System.out.println("Enabling component rendering");
                showComponent = true;
            } 
        }
        if (target != null && showComponent) {
          targetPos = PVector.add(target.position, socket.offset);
        }
        float moveDelta = Factory.easeStep(MIN_SPEED, MAX_SPEED, PVector.dist(currentPos, startPos) / PVector.dist(startPos, targetPos)) * Game.sketch.deltaTime;
        currentPos = Factory.moveTowards(currentPos, targetPos, moveDelta);
        Game.sketch.text(moveDelta, currentPos.x + (float) vertical.width + Game.sketch.textWidth(Float.toString(moveDelta)) * 0.5f, currentPos.y + center.height);
        if (target != null && component != null && showComponent) {
            if (target.getBoundingBox().isOverlapping(boundingBox)) {
                socket.component = component;
                // Spark effect when adding component
                Game.sketch.animationPool.play(AnimationType.COMPONENT_ADD, target, socket);
                switch (componentType) {
                    case GUN: // Attach animations to gun component
                        Game.sketch.animationPool.play(AnimationType.GUN_FLAME, target, socket);
                        break;
                    
                    case SHIELD: // Attach animations to shield component
                        Game.sketch.animationPool.play(AnimationType.SHIELD_PARTICLES, target, socket);
                        break;

                    default:
                        break;
                }
                component = null;
                target = null;
                socket = null;
                showComponent = false;
            }
        }
        render(currentPos);

    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(currentPos, new PVector(center.width, center.height));
    }

    private void render(PVector position) {
        if (component != null && showComponent) {
            component.draw(position, 1.5f);
        }
        Game.sketch.image(center, position);
        Game.sketch.image(vertical, new PVector(position.x, Game.sketch.height / 2));
        Game.sketch.image(horizontal, new PVector(Game.sketch.width / 2, position.y));
        Game.sketch.image(binding, position);
    }

	public boolean hasComponent() {
        return component != null;
	}

	public Component getComponent() {
        return component;
	}

    private BoundingBox getSelectionBoundingBox() {
        return Arrays
            .stream(componentSelects)
            .filter((ComponentSelect select) -> select.type == componentType)
            .findFirst()
            .get()
            .getBoundingBox(targetPos);
    }
}
