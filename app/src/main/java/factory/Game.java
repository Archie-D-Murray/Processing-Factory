package factory;

import java.util.Random;

public class Game {
    public static Factory sketch;
    public static float deltaTime;
    public static Random random = new Random();
    public static IState state;
    public static Config config;
    public static LevelSelection levelSelection;
    public static float mouseInputDelay = 0f;
	public static int money;

    public static void switchState(IState newState) {
        Game.state.onExit();
        Game.state = newState;
        Game.state.onEnter();
    }

	public static boolean mouseDown() {
		return sketch.mousePressed && mouseInputDelay == 0f;
	}
}
