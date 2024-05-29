package factory;

import java.util.Random;

public class Game {
    public static Factory sketch;
    public static float deltaTime;
    public static Random random = new Random();
    public static IState state;

    public static void switchState(IState newState) {
        Game.state.onExit();
        Game.state = newState;
        Game.state.onEnter();
    }
}
