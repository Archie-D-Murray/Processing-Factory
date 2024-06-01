package factory;

import java.util.stream.*;

import processing.core.PImage;
import processing.core.PVector;

public class Stats {

    private static PVector leftPort     = new PVector(0.25f, 0.088f);
    private static PVector rightPort    = new PVector(0.54f, 0.088f);
    private static PVector speedPos     = new PVector(0.24f, 0.24f);
    private static PVector firePowerPos = new PVector(0.24f, 0.45f);
    private static PVector storagePos   = new PVector(0.24f, 0.56f);
    private static PVector weightPos    = new PVector(0.24f, 0.77f);
    public static PImage statBackground = null;

    public float speed;
	public float firePower;
	public float storage;
	public float weight;

    public Stats(float speed, float firePower, float storage, float weight) {
        this.speed = speed;
        this.firePower = firePower; 
        this.storage = storage;
        this.weight = weight;
    }

    public Stats(Stats other) {
        this.speed = other.speed;
        this.firePower = other.firePower;
        this.storage = other.storage;
        this.weight = other.weight;
    }

    public float compare(Stats other) {
        float[] stats = new float[] {
            other.speed / speed,
            other.firePower / firePower,
            other.storage / storage,
            weight / other.weight
        };
        return (float) IntStream.range(0, stats.length).mapToDouble(i -> stats[i]).sum() / stats.length;
    }

	public void add(Stats stats) {
		speed += stats.speed;
		firePower += stats.firePower;
		storage += stats.storage;
		weight += stats.weight;
	}

    @Override
    public String toString() {
        return String.format(
            "SPEED: %d\nFIREPOWER : %d\nSTORAGE: %d\nWEIGHT: %d", 
            Factory.round(speed),
            Factory.round(firePower),
            Factory.round(storage),
            Factory.round(weight)
        );
    }

    /**
    * @param position Position of component/product
    */
    public void render(BoundingBox boundingBox) {
        PVector renderPos = new PVector();
        if (boundingBox.bottom() + (float) statBackground.height * 0.5f < Game.sketch.height) { // Can render below
            renderPos = PVector.add(new PVector(boundingBox.position.x, boundingBox.bottom()), new PVector(0, statBackground.height * 0.5f));
        } else if (boundingBox.top() - (float) statBackground.height * 0.5f > 0f) { // Can render above
            renderPos = PVector.sub(new PVector(boundingBox.position.x, boundingBox.top()), new PVector(0, statBackground.height * 0.5f));
        } else {
            System.out.println("Not rendering!");
            return;
        }
        BoundingBox stats = new BoundingBox(renderPos, new PVector(statBackground.width, statBackground.height));
        renderPos.x = Factory.clamp(statBackground.width / 2, Game.sketch.width - statBackground.width / 2, renderPos.x);
        Game.sketch.image(statBackground, renderPos);
        Game.sketch.textSize(12f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        Game.sketch.text(String.format("Speed: %d", Factory.round(speed)), stats.left() + stats.size.x * speedPos.x, stats.top() + stats.size.y * speedPos.y);
        Game.sketch.text(String.format("Fire Power: %d", Factory.round(firePower)), stats.left() + stats.size.x * firePowerPos.x, stats.top() + stats.size.y * firePowerPos.y);
        Game.sketch.text(String.format("Storage: %d", Factory.round(storage)), stats.left() + stats.size.x * storagePos.x, stats.top() + stats.size.y * storagePos.y);
        Game.sketch.text(String.format("Weight: %d", Factory.round(weight)), stats.left() + stats.size.x * weightPos.x, stats.top() + stats.size.y * weightPos.y);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
        Game.sketch.strokeWeight(4f);
        if (stats.bottom() < boundingBox.position.y) { // Rendering above
            Game.sketch.stroke(0xFF299FB3);
            Game.sketch.line(boundingBox.position, new PVector(stats.left() + stats.size.x * leftPort.x, stats.bottom() - stats.size.y * leftPort.y));
            Game.sketch.line(boundingBox.position, new PVector(stats.left() + stats.size.x * rightPort.x, stats.bottom() - stats.size.y * rightPort.y));
            Game.sketch.stroke(0xFFFFFFFF);
        } else {
            Game.sketch.stroke(0xFF299FB3);
            Game.sketch.line(boundingBox.position, new PVector(stats.left() + stats.size.x * leftPort.x, stats.top() + stats.size.y * leftPort.y));
            Game.sketch.line(boundingBox.position, new PVector(stats.left() + stats.size.x * rightPort.x, stats.top() + stats.size.y * rightPort.y));
            Game.sketch.stroke(0xFFFFFFFF);
        }
    }
}

