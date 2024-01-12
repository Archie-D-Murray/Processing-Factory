package factory;

import processing.core.PApplet;

/**
 * Class that processes a product and generates a rewards from it using various parameters
 */
public class ProductReceiver {
  public final int MARGIN = 500;
  public final int MAX_REWARD = 200;
  public final int MAX_PENALTY = -50;
  public int targetValue;
  
  public int getMoneyFromSubmission(Product submission) {
    int submissionValue = submission.getValue();
    submission.hasBeenProcessed = true;
    return PApplet.round(reward(submissionValue, targetValue));
  }
  
  private float reward(float submission, float target) {
    return Factory.remap(MAX_PENALTY, MAX_REWARD, MARGIN, 0f, Math.abs(target - submission));
  }
}
