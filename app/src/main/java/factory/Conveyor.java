package factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Where all product logic is updated
 * Handles moving products to relevant positions and rendering them
 */
public class Conveyor {
    final private float SEGMENT_LENGTH = 24f;
    private PVector[] positions;
    private PImage conveyorMouth;
    private ArrayList<ConveyorSegment> conveyorSegments;
    private ArrayList<ConveyorSegment> sendToFront;
    private Product product;
    float conveyorSpeed;
    private boolean move = true;
    private boolean productAtEnd = false;

  public Conveyor(PVector[] positions, float speed) {
    this.positions = positions;
    this.conveyorSpeed = speed;
    conveyorSegments = new ArrayList<ConveyorSegment>();
    sendToFront = new ArrayList<ConveyorSegment>(1);
    PImage beltSegment = ImageDataBase.get("BeltSegment.png");
    conveyorMouth = ImageDataBase.get("ConveyorMouth.png");
    float resize = SEGMENT_LENGTH / (float) beltSegment.width;
    beltSegment.resize(Factory.round(beltSegment.width * resize), Factory.round(beltSegment.height * resize));
    System.out.println("Belt: " + String.join(", ", Arrays.stream(positions).map(pos -> pos.toString()).collect(Collectors.toList())));
    for (float i = beltLength(); i >= 0f; i -= SEGMENT_LENGTH) {
        conveyorSegments.add(new ConveyorSegment(beltSegment, positionFromProgress(i), rotationFromProgress(i), indexFromProgress(i)));
    }
  }

    /**
     * Adds move vector to position of all products then renders them
     */
    public void moveConveyorItems() {
        renderBelt();
        // Is product at target position
        if (PVector.dist(product.position, positions[product.targetPosIndex]) < PApplet.EPSILON) {
            if (product.targetPosIndex < positions.length - 1) { // If product is not at end of conveyor
                product.targetPosIndex++;
            } else {
                productAtEnd = true;
            }
        }
        if (move) {
            product.position = Factory.moveTowards(product.position, positions[product.targetPosIndex], conveyorSpeed);
        }
        product.render();
        float startAngle = PVector.angleBetween(new PVector(0f, 1f), PVector.sub(positions[1], positions[0]).normalize());
        float endAngle = PVector.angleBetween(new PVector(0f, 1f), PVector.sub(positions[positions.length - 1], positions[positions.length - 2]).normalize());
        Game.sketch.image(conveyorMouth, positions[0], startAngle);
        Game.sketch.image(conveyorMouth, positions[positions.length - 1], endAngle);
    }

    public boolean isFinished() {
        return productAtEnd;
    }

    public Product getProduct() {
        return product;
    }

    public void stop() {
        move = false;
    }

    public void start() {
        move = true;
    }

    /**
     * Adds product to start of belt
     */
    public void addProduct(Product product) {
        product.position = positions[0];
        this.product = product;
    }

    /**
     * Belt is rendered by drawing lines between a series of points
     */
    private void renderBelt() {
        sendToFront.clear();
        for (ConveyorSegment segment : conveyorSegments) {
            segment.update(positions, move);
            if (segment.sendToFront) {
                sendToFront.add(segment);
            }
        }
        for (ConveyorSegment segment : sendToFront) {
            conveyorSegments.remove(segment);
            conveyorSegments.add(segment);
            segment.sendToFront = false;
        }
        // Game.sketch.strokeWeight(size / 2);
        // Game.sketch.stroke(colour);
        // for (int i = 0; i < positions.length - 1; i++) { // Last index has no next point to draw to
        //     Game.sketch.line(positions[i], positions[i + 1]); // Using line allows for diagonal conveyor belts
        // }
        // Game.sketch.strokeWeight(0f); // Make sure not to create strange behaviour for other shape rendering
        // Game.sketch.stroke(0x00FFFFFF);
    }

    private float beltLength() {
        float length = 0f;
        for (int i = 0; i < positions.length - 1; i++) {
            length += PVector.dist(positions[i], positions[i + 1]);
        }
        return length;
    }

    private PVector positionFromProgress(float progress) {
        float distance = 0f;
        for (int i = 0; i <= positions.length - 1; i++) {
            float pointDistance = PVector.dist(positions[i], positions[i + 1]);
            if (distance + pointDistance >= progress) {
                return PVector.lerp(positions[i], positions[i + 1], (progress - distance) / pointDistance);
            } else {
                distance += pointDistance;
            }
        }
        return null;
    }

    private float rotationFromProgress(float progress) {
        float distance = 0f;
        for (int i = 0; i <= positions.length - 1; i++) {
            float pointDistance = PVector.dist(positions[i], positions[i + 1]);
            if (distance + pointDistance >= progress) {
                return PVector.angleBetween(new PVector(0f, 1f), PVector.sub(positions[i + 1], positions[i]).normalize());
            } else {
                distance += pointDistance;
            }
        }
        return 0f;
    }

    private int indexFromProgress(float progress) {
        float distance = 0f;
        for (int i = 0; i <= positions.length - 2; i++) {
            float pointDistance = PVector.dist(positions[i], positions[i + 1]);
            if (distance + pointDistance >= progress) {
                return i + 1;
            } else {
                distance += pointDistance;
            }
        } 
        return 1;
    }
}
