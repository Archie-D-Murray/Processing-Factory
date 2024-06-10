package factory;

import processing.core.PImage;
import processing.core.PVector;

public class Stats {
    private static PVector leftPort     = new PVector(0.248f, 0.033f);
    private static PVector rightPort    = new PVector(0.753f, 0.033f);
    private static PVector speedPos     = new PVector(0.240f, 0.20f, 0.70f); // Z is x offset for value
    private static PVector firePowerPos = new PVector(0.240f, 0.40f, 0.70f); 
    private static PVector storagePos   = new PVector(0.240f, 0.60f, 0.70f);
    private static PVector weightPos    = new PVector(0.240f, 0.80f, 0.70f);
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
        System.out.print("Stats: ");
        float sum = 0f;
        for (float stat : stats) {
            if (Float.isNaN(stat)) {
                stat = 1f;
            }
            sum += stat;
            System.out.print(stat + " ");
        }
        System.out.println();
        return sum / (float) stats.length;
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
            "SPEED: %d\nFIREPOWER: %d\nSTORAGE: %d\nWEIGHT: %d", 
            Factory.round(speed),
            Factory.round(firePower),
            Factory.round(storage),
            Factory.round(weight)
        );
    }

    public void render(PVector position) {
        PVector renderPos = new PVector();
        if (position.y + (float) statBackground.height * 0.5f < Game.sketch.height) { // Can render below
            renderPos = PVector.add(position, new PVector(0, statBackground.height * 0.5f));
        } else if (position.y - (float) statBackground.height * 0.5f > 0f) { // Can render above
            renderPos = PVector.sub(position, new PVector(0, statBackground.height * 0.5f));
        } else {
            System.out.println("Not rendering!");
            return;
        }
        renderPos.x = Factory.clamp(statBackground.width / 2, Game.sketch.width - statBackground.width / 2, renderPos.x);
        Game.sketch.image(statBackground, renderPos);
        Game.sketch.textSize(24f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        // Speed
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Speed", (position.x - statBackground.width * 0.5f) + statBackground.width * speedPos.x, (position.y) + statBackground.height * speedPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(speed), (position.x - statBackground.width * 0.5f) + statBackground.width * speedPos.z, (position.y) + statBackground.height * speedPos.y);
        // Fire Power
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Fire Power", (position.x - statBackground.width * 0.5f) + statBackground.width * firePowerPos.x, (position.y) + statBackground.height * firePowerPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(firePower), (position.x - statBackground.width * 0.5f) + statBackground.width * firePowerPos.z, (position.y) + statBackground.height * firePowerPos.y);
        // Storage
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Storage", (position.x - statBackground.width * 0.5f) + statBackground.width * storagePos.x, (position.y) + statBackground.height * storagePos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(storage), (position.x - statBackground.width * 0.5f) + statBackground.width * storagePos.z, (position.y) + statBackground.height * storagePos.y);
        // Weight
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Weight", (position.x - statBackground.width * 0.5f) + statBackground.width * weightPos.x, (position.y) + statBackground.height * weightPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(weight), (position.x - statBackground.width * 0.5f) + statBackground.width * weightPos.z, (position.y) + statBackground.height * weightPos.y);
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.textAlign(Factory.CENTER, Factory.CENTER);
    }

    /**
    * @param boundingBox BoundingBox of component/product to render stats of
    */
    public void render(BoundingBox boundingBox, boolean renderAbove) {
        PVector renderPos = new PVector();
        if (renderAbove) {
            if (boundingBox.top() - (float) statBackground.height * 0.5f > 0f) { // Can render below
                renderPos = PVector.sub(new PVector(boundingBox.position.x, boundingBox.top()), new PVector(0, statBackground.height * 0.5f));
            } else if (boundingBox.bottom() + (float) statBackground.height * 0.5f < Game.sketch.height) { // Can render above
                renderPos = PVector.add(new PVector(boundingBox.position.x, boundingBox.bottom()), new PVector(0, statBackground.height * 0.5f));
            } else {
                System.out.println("Not rendering!");
                return;
            }
        } else {
            if (boundingBox.bottom() + (float) statBackground.height * 0.5f < Game.sketch.height) { // Can render below
                renderPos = PVector.add(new PVector(boundingBox.position.x, boundingBox.bottom()), new PVector(0, statBackground.height * 0.5f));
            } else if (boundingBox.top() - (float) statBackground.height * 0.5f > 0f) { // Can render above
                renderPos = PVector.sub(new PVector(boundingBox.position.x, boundingBox.top()), new PVector(0, statBackground.height * 0.5f));
            } else {
                System.out.println("Not rendering!");
                return;
            }
        }
        renderPos.x = Factory.clamp(statBackground.width / 2, Game.sketch.width - statBackground.width / 2, renderPos.x);
        BoundingBox stats = new BoundingBox(renderPos, new PVector(statBackground.width, statBackground.height));
        Game.sketch.image(statBackground, renderPos);
        Game.sketch.textSize(24f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        // Speed
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Speed", stats.left() + stats.size.x * speedPos.x, stats.top() + stats.size.y * speedPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(speed), stats.left() + stats.size.x * speedPos.z, stats.top() + stats.size.y * speedPos.y);
        // Fire Power
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Fire Power", stats.left() + stats.size.x * firePowerPos.x, stats.top() + stats.size.y * firePowerPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(firePower), stats.left() + stats.size.x * firePowerPos.z, stats.top() + stats.size.y * firePowerPos.y);
        // Storage
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Storage", stats.left() + stats.size.x * storagePos.x, stats.top() + stats.size.y * storagePos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(storage), stats.left() + stats.size.x * storagePos.z, stats.top() + stats.size.y * storagePos.y);
        // Weight
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Weight", stats.left() + stats.size.x * weightPos.x, stats.top() + stats.size.y * weightPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(weight), stats.left() + stats.size.x * weightPos.z, stats.top() + stats.size.y * weightPos.y);
        Game.sketch.fill(0xFFFFFFFF);
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

    /**
    * @param boundingBox BoundingBox of component/product to render stats of
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
        renderPos.x = Factory.clamp(statBackground.width / 2, Game.sketch.width - statBackground.width / 2, renderPos.x);
        BoundingBox stats = new BoundingBox(renderPos, new PVector(statBackground.width, statBackground.height));
        Game.sketch.image(statBackground, renderPos);
        Game.sketch.textSize(24f);
        Game.sketch.textAlign(Factory.LEFT, Factory.CENTER);
        // Speed
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Speed", stats.left() + stats.size.x * speedPos.x, stats.top() + stats.size.y * speedPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(speed), stats.left() + stats.size.x * speedPos.z, stats.top() + stats.size.y * speedPos.y);
        // Fire Power
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Fire Power", stats.left() + stats.size.x * firePowerPos.x, stats.top() + stats.size.y * firePowerPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(firePower), stats.left() + stats.size.x * firePowerPos.z, stats.top() + stats.size.y * firePowerPos.y);
        // Storage
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Storage", stats.left() + stats.size.x * storagePos.x, stats.top() + stats.size.y * storagePos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(storage), stats.left() + stats.size.x * storagePos.z, stats.top() + stats.size.y * storagePos.y);
        // Weight
        Game.sketch.fill(0xFFFFFFFF);
        Game.sketch.text("Weight", stats.left() + stats.size.x * weightPos.x, stats.top() + stats.size.y * weightPos.y);
        Game.sketch.fill(0xFF299FB3);
        Game.sketch.text(Factory.round(weight), stats.left() + stats.size.x * weightPos.z, stats.top() + stats.size.y * weightPos.y);
        Game.sketch.fill(0xFFFFFFFF);
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

