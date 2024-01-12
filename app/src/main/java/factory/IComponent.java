package factory;

import processing.core.PVector;

/**
 * Enforces implementation of a draw method on each component
 */
public interface IComponent {
  void draw(PVector rootPosition);
}
