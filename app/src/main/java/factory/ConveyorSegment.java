package factory;

import processing.core.PImage;
import processing.core.PVector;

public class ConveyorSegment {
    final float SPEED = 50f;
    final float TURNING_DISTANCE = 5f;
    final float TURNING_SPEED = 90f / (2 * TURNING_DISTANCE / SPEED);
    private PImage segment;
    private int targetIndex;
    private PVector position;
    private float rotation;
	public boolean sendToFront;

    public ConveyorSegment(PImage segment, PVector position, float rotation, int targetIndex) {
        this.segment = segment;
        this.position = position;
        this.rotation = rotation;
        this.targetIndex = targetIndex;
        sendToFront = false;
    }

    public void update(PVector[] points, boolean move) {
        if (move) {
            if (PVector.dist(position, points[targetIndex]) <= 0.01f) {
                if (targetIndex == points.length - 1) {
                    position = points[0];
                    targetIndex = 1;
                    sendToFront = true;
                } else {
                    targetIndex++;
                }
            }
            position = Factory.moveTowards(position, points[targetIndex], SPEED * Game.deltaTime);
            if (targetIndex != points.length - 1 && PVector.dist(position, points[0]) >= TURNING_DISTANCE) {
                if (PVector.dist(position, points[targetIndex]) <= TURNING_DISTANCE || PVector.dist(position, points[targetIndex - 1]) <= TURNING_DISTANCE) {
                    float targetRotation = PVector.angleBetween(new PVector(0, 1), PVector.sub(points[targetIndex + 1], points[targetIndex]));
                    float initial = PVector.angleBetween(new PVector(0, 1), PVector.sub(points[targetIndex], points[targetIndex - 1]));
                    float progress = 0f;
                    if (PVector.dist(position, points[targetIndex]) >= PVector.dist(position, points[targetIndex - 1])) { // Gone past corner
                        progress = 0.5f + PVector.dist(position, points[targetIndex - 1]) / (2 * TURNING_DISTANCE);
                    } else {
                        progress = PVector.dist(position, points[targetIndex]) / (2 * TURNING_DISTANCE);
                    }
                    rotation = Factory.lerp(initial, targetRotation, progress);
                } else {
                    rotation = PVector.angleBetween(new PVector(0f, 1f), PVector.sub(points[targetIndex], points[targetIndex - 1]).normalize());
                }
            } else {
                rotation = PVector.angleBetween(new PVector(0f, 1f), PVector.sub(points[targetIndex], points[targetIndex - 1]).normalize());
            }
        }
        Game.sketch.image(segment, position, rotation);
    }
}
