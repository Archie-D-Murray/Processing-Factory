package factory;

import processing.core.PImage;
import processing.core.PVector;

public class Crane {
    private static final float OFFSET_MAGNITUDE = 50f;
    final float MAX_SPEED = 500;
    final float MIN_SPEED = 50;
    final float MAX_ACCEL = 50;
    final int WIDTH = 32;

    private int targetIndex;

    private PImage vertical;
    private PImage horizontal;
    private PImage center;
    private PImage binding;
    private PVector startPos;
    private PVector currentPos;
    private PVector[] targetPositions;
    private Component component;
    private ComponentSocket socket;
    private Product target;
    private boolean showComponent;
    private Conveyor conveyor;
    private boolean goingToProduct;

    public Crane(Conveyor conveyor) {
        this.horizontal = ImageDataBase.get("CraneHorizontal.png").copy();
        this.vertical = ImageDataBase.get("CraneVertical.png").copy();
        this.center = ImageDataBase.get("CraneCenter.png").copy();
        this.binding = ImageDataBase.get("CraneBinding.png").copy();
        this.horizontal.resize(Game.sketch.width, WIDTH);
        this.vertical.resize(WIDTH, Game.sketch.height);
        this.center.resize(center.width / 2, center.height / 2);
        this.binding.resize(binding.width / 2, binding.height / 2);
        currentPos = new PVector(Game.sketch.width / 2, Game.sketch.height / 2);
        showComponent = false;
        target = null;
        socket = null;
        startPos = new PVector(Game.sketch.width / 2, Game.sketch.height / 2);
        targetPositions = new PVector[] { new PVector(), new PVector() };
        targetIndex = 0;
        this.conveyor = conveyor;
    }

    public void addComponent(Component component, PVector position) {
        this.component = component;
        targetIndex = 0;
        targetPositions[0] = position.copy().add(getOffset(position));
        targetPositions[1] = position.copy();
        showComponent = false;
        startPos = currentPos;
    }

    public void setTarget(PVector position) {
        targetIndex = 0;
        startPos = currentPos;
    }

    public void setTarget(ComponentSocket socket, Product product) {
        target = product;
        this.socket = socket;
        targetIndex = 0;
        goingToProduct = false;
    }

    private PVector getOffset(PVector targetPosition) {
        return PVector.sub(targetPosition, currentPos).normalize().mult(OFFSET_MAGNITUDE);
    }

    public void update() {
        if (component != null && !showComponent) { // Going to component
            if (PVector.dist(currentPos, targetPositions[targetIndex]) <= 10f && targetIndex == 1) { // Touching component
                System.out.println("Enabling component rendering");
                showComponent = true;
                targetIndex = 0;
                conveyor.stop();
                if (!hasProduct()) {
                    targetPositions[0] = currentPos.copy();
                    targetPositions[1] = currentPos.copy();
                }
            }
        }
        if (target != null && showComponent && !goingToProduct) { // Go to product
            targetPositions[0] = PVector.add(PVector.add(target.position, socket.offset),
                    getOffset(PVector.add(target.position, socket.offset)));
            targetPositions[1] = PVector.add(target.position, socket.offset);
            goingToProduct = true;
        }

        if (hasComponent() || hasProduct()) {
            Game.sketch.circle(5, 5, 10);
            float moveDelta = Factory.ease(MIN_SPEED, MAX_SPEED,
                    PVector.dist(currentPos, startPos) / PVector.dist(startPos, targetPositions[targetIndex]))
                    * Game.deltaTime;
            currentPos = Factory.moveTowards(currentPos, targetPositions[targetIndex], moveDelta);
        }
        if (target != null && component != null && showComponent && targetIndex == 1) {
            if (PVector.dist(targetPositions[targetIndex], currentPos) <= 10f) {
                socket.component = component;
                // Spark effect when adding component
                AnimationPool.play(AnimationType.COMPONENT_ADD, target, socket);
                switch (component.type) {
                    case GUN: // Attach animations to gun component
                        AnimationPool.play(AnimationType.GUN_FLAME, target, socket);
                        break;

                    case SHIELD: // Attach animations to shield component
                        AnimationPool.play(AnimationType.SHIELD_PARTICLES, target, socket);
                        break;

                    default:
                        break;
                }
                component = null;
                target = null;
                socket = null;
                showComponent = false;
                targetIndex = 0;
                targetPositions[0] = currentPos;
                targetPositions[1] = currentPos;
                conveyor.start();
            }
        }
        if (PVector.dist(currentPos, targetPositions[targetIndex]) <= 10f && targetIndex == 0
                && (hasComponent() || hasProduct())) {
            targetIndex = 1;
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
        if (targetIndex == 1) {
            Game.sketch.tint(0xFFFF0000);
        } else {
            Game.sketch.tint(0xFFFFFFFF);
        }
        Game.sketch.image(binding, position);
        Game.sketch.tint(0xFFFFFFFF);
    }

    public boolean hasComponent() {
        return component != null;
    }

    public Component getComponent() {
        return component;
    }

    public boolean hasProduct() {
        return target != null;
    }
}
