package factory;

import processing.core.PVector;

enum OverloadStrategy {
    IGNORE,
    POSITIVE,
    NEGATIVE
}

public class ProgressBar {

    public static final int LEFT = 0, RIGHT = 1, CENTER = 2;

    public float progress;
    private float padding;
    private PVector position;
    private PVector size;

    private int[] colours;
    private int alignment;

    public ProgressBar(PVector position, PVector size, float padding, float progress, int bgColour, int fgColour, int alignment) {
        this.position = position;
        this.size = size;
        this.padding = padding;
        this.progress = progress;
        this.alignment = alignment;
        this.colours = new int[] { fgColour, bgColour };
    }

    public void render(OverloadStrategy strategy) {
        Game.sketch.tint(0xFFFFFFFF);
        Game.sketch.strokeWeight(0f);
        Game.sketch.fill(colours[1]);
        Game.sketch.rect(position.x, position.y, size.x, size.y);
        Game.sketch.fill(colours[0]);
        Game.sketch.strokeWeight(0f);
        if (progress == 0f) {
            return;
        }
        float renderProgress = Factory.clamp(0f, 1f, progress);
        switch (alignment) {
            case LEFT:
                Game.sketch.rectMode(Factory.CORNER);
                Game.sketch.rect(position.x - size.x * 0.5f + padding, position.y - size.y * 0.5f + padding,
                        (size.x - 2f * padding) * renderProgress, size.y - 2f * padding);
                break;
            case RIGHT:
                Game.sketch.rectMode(Factory.CORNER);
                Game.sketch.rect((position.x + size.x * 0.5f - padding) - (size.x - 2f * padding) * renderProgress,
                        position.y - size.y * 0.5f + padding, (size.x - 2f * padding) * renderProgress,
                        size.y - 2f * padding);
                break;
            case CENTER:
                Game.sketch.rectMode(Factory.CENTER);
                Game.sketch.rect(position.x, position.y, (size.x - 2f * padding) * renderProgress, size.y - 2f * padding);
                break;
            default:
                break;
        }
        Game.sketch.rectMode(Factory.CENTER);
        if (progress > 1f) {
            Game.sketch.fill(0x00FFFFFF);
            switch (strategy) {
                case IGNORE:
                    Game.sketch.strokeWeight(0f);
                    break;
                case POSITIVE:
                    Game.sketch.stroke(0xFF00AA00);
                    Game.sketch.strokeWeight(Factory.max(2f, padding / 3f));
                    break;
                case NEGATIVE:
                    Game.sketch.stroke(0xFFAA0000);
                    Game.sketch.strokeWeight(Factory.max(2f, padding / 3f));
                    break;
            }
            Game.sketch.rect(position.x, position.y, size.x, size.y);
            Game.sketch.fill(0xFFFFFFFF);
        }
    }
}
