package factory;

import processing.core.PApplet;
import processing.core.PVector;

enum CollisionShape {
    RECTANGLE, CIRCLE
}

/**
 * Basic collision class able to handle
 */
public class BoundingBox {
    public PVector position, size;
    public CollisionShape type;

    public BoundingBox(PVector position, float diameter) {
        this.position = position;
        this.size = new PVector(diameter, diameter);
        this.type = CollisionShape.CIRCLE;
    }

    public BoundingBox(PVector position, PVector size) {
        this.position = position;
        this.size = size;
        this.type = CollisionShape.RECTANGLE;
    }

    private float halfWidth() {
        return this.size.x / 2f;
    }

    private float halfHeight() {
        return this.size.y / 2f;
    }

    public float left() {
        return this.position.x - this.halfWidth();
    }

    public float right() {
        return this.position.x + this.halfWidth();
    }

    public float top() {
        return this.position.y - this.halfHeight();
    }

    public float bottom() {
        return this.position.y + this.halfHeight();
    }

    public String toString() {
        return String.format("(%f, %f) \nTop: %f\nBottom: %f\nLeft: %f\nRight %f", position.x, position.y, top(),
                bottom(), left(), right());
    }

    public void render() {
        Game.sketch.fill(0x8800FF00);
        switch (type) {
            case RECTANGLE:
                Game.sketch.rect(position.x, position.y, size.x, size.y);
                break;
            case CIRCLE:
                Game.sketch.circle(position.x, position.y, size.x);
                break;
            default:
                break;
        }
    }

    /*
     * General collision function with points
     */
    public boolean isOverlappingPoint(PVector point) {
        PVector diff = PVector.sub(position, point);
        if (this.type == CollisionShape.CIRCLE) {
            return diff.magSq() < halfWidth() * halfWidth();
        } else {
            if (PApplet.abs(diff.x) > halfWidth()) {
                return false;
            }
            if (PApplet.abs(diff.y) > halfHeight()) {
                return false;
            }
            return true;
        }
    }

    public boolean isTouchingMouse() {
        return isOverlappingPoint(Game.sketch.getMousePosition());
    }

    /**
     * General collision function for collisions with other bounding boxes
     */
    public boolean isOverlapping(BoundingBox other) {
        if (this.type == CollisionShape.RECTANGLE && this.type == other.type) {
            return rectOnRectCollision(this, other);
        } else if (other.type != this.type) {
            return circleOnRectCollision(
                    this.type == CollisionShape.RECTANGLE ? other : this,
                    this.type == CollisionShape.RECTANGLE ? this : other);
        } else {
            return circleOnCircleCollision(this, other);
        }
    }

    // ref:
    // https://www.geeksforgeeks.org/check-if-any-point-overlaps-the-given-circle-and-rectangle/
    private boolean circleOnRectCollision(BoundingBox circle, BoundingBox rect) {
        float radius = circle.halfWidth();

        // Position on rect closest to mouse => must be within rect bounds
        PVector closestPoint = new PVector(
            Factory.clamp(rect.left(), rect.right(), circle.position.x),
            Factory.clamp(rect.top(), rect.bottom(), circle.position.y)
        );

        PVector delta = PVector.sub(closestPoint, circle.position);
        return delta.magSq() <= radius * radius; // Avoiding sqrt for speed
    }

    private boolean rectOnRectCollision(BoundingBox rect1, BoundingBox rect2) {
        if (rect1.top() > rect2.bottom() || rect1.bottom() < rect2.top()) {
            return false;
        }

        if (rect1.right() < rect2.left() || rect1.left() > rect2.right()) {
            return false;
        }
        return true;
    }

    private boolean circleOnCircleCollision(BoundingBox circle1, BoundingBox circle2) {
        float maxDistance = circle1.halfWidth() + circle2.halfWidth();

        PVector displacement = PVector.sub(circle1.position, circle2.position);

        return displacement.magSq() < (maxDistance * maxDistance);
    }
}
