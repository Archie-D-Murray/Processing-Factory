package factory;

import processing.core.PVector;
public class Config {

    public int currentLevel;
    public Level[] levels;
	public ProductType[] unlockedProducts;
    public ComponentType[] unlockedComponents;
    
    public Config() {
        currentLevel = 0;
        levels = new Level[] {
            new Level(
                0,
                new PVector[] {
                  new PVector(0f, Game.sketch.height * 0.8f),
                  new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.8f),
                  new PVector(Game.sketch.width * 0.25f, Game.sketch.height * 0.5f),
                  new PVector(Game.sketch.width, Game.sketch.height * 0.5f),
                },
                new Stats[] { new Stats(10, 0, 10, 100) },
                new ComponentType[] { ComponentType.GUN },
                new ProductType[] { ProductType.NORMAL }
            )
        };
        unlockedProducts = new ProductType[] { ProductType.LIGHT };
        unlockedComponents = new ComponentType[] { ComponentType.GUN };
    }

    public class Level {

        public Level(int level, PVector[] conveyorPositions, Stats[] possibleTargets, ComponentType[] componentUnlocks, ProductType[] productUnlocks) {
            this.level = level;
            this.conveyorPositions = conveyorPositions;
            this.possibleTargets = possibleTargets;
            this.componentUnlocks = componentUnlocks;
            this.productUnlocks = productUnlocks;
        }

        public int level;
        public PVector[] conveyorPositions;
        public Stats[] possibleTargets;
        public ComponentType[] componentUnlocks;
        public ProductType[] productUnlocks;
    }

    public Level getCurrentLevel() {
        return levels[currentLevel];
    }
}
