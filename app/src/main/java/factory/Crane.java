package factory;

import processing.core.PImage;
import processing.core.PVector;

public class Crane {
    final float SPEED = 500;
    final int WIDTH = 32;
    private PImage vertical;
    private PImage horizontal;
    private PImage center;
    private PImage binding;
    private PVector currentPos;
    private PVector targetPos;
    private Component component;
    private ComponentSocket socket;
    private Product target;
    private boolean hasComponent;
    private boolean hasTarget;

    public Crane() {
        this.horizontal = Game.sketch.imageDataBase.get("CraneHorizontal.png").copy();
        this.vertical = Game.sketch.imageDataBase.get("CraneVertical.png").copy();
        this.center = Game.sketch.imageDataBase.get("CraneCenter.png").copy();
        this.binding = Game.sketch.imageDataBase.get("CraneBinding.png").copy();
        this.horizontal.resize(Game.sketch.width, WIDTH);
        this.vertical.resize(WIDTH, Game.sketch.height);
        currentPos = new PVector(Game.sketch.width / 2, Game.sketch.height / 2);
        target = null;
        socket = null;
        targetPos = new PVector();
    }

    public void addComponent(Component component) {
        this.component = component;
    }

    public void setTarget(PVector position) {
        target = null;
        socket = null;
        targetPos = position;
    }

    public void setTarget(ComponentSocket socket, Product product) {
        target = product;
        this.socket = socket;
        hasTarget = true;
    }

    public void update() {
        if (hasTarget && hasComponent) {
            targetPos = PVector.add(socket.offset, target.position);
        }
        currentPos = Factory.moveTowards(currentPos, targetPos, SPEED * Game.sketch.deltaTime);
        render(currentPos);
    }

    private void render(PVector position) {
        Game.sketch.image(center, position);
        Game.sketch.image(vertical, new PVector(position.x, Game.sketch.height / 2));
        Game.sketch.image(horizontal, new PVector(Game.sketch.width / 2, position.y));
        Game.sketch.image(binding, position);
    }
}
