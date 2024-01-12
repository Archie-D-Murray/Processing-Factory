package factory;

/**
 * Enforces a set of state methods that concrete implementations
 * must implement
 */
public interface IState {
  public abstract void onEnter();
  public abstract void onExit();
  public abstract void update();
  public abstract void checkTransition();
  public abstract void keyDown(char key);
}
