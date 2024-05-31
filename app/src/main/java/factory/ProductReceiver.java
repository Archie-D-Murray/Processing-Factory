package factory;

import processing.core.PApplet;

/**
 * Class that processes a product and generates a rewards from it using various parameters
 */
public class ProductReceiver {
  public final int MARGIN = 500;
  public final int MAX_REWARD = 200;
  public final int MAX_PENALTY = -50;
  public float receiverModifier = 1f;
  public float reward = 100f;
  
  public int getMoneyFromSubmission(Product submission, Stats target) {
    submission.hasBeenProcessed = true;
    return PApplet.round(submission.getValue().compare(target) * reward * receiverModifier);
  }
}

