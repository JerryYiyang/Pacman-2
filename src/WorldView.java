import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    private Viewport viewport;

    public Viewport getViewport() {
        return viewport;
    }

    public WorldView(
            int numRows,
            int numCols,
            PApplet screen,
            WorldModel world,
            int tileWidth,
            int tileHeight)
    {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    public int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public void shiftView(WorldView view, int colDelta, int rowDelta) {
        int newCol = clamp(view.viewport.getCol() + colDelta, 0,
                view.world.getNumCols() - view.viewport.getNumCols());
        int newRow = clamp(view.viewport.getRow() + rowDelta, 0,
                view.world.getNumRows() - view.viewport.getNumRows());

        view.viewport.shift(view.viewport, newCol, newRow);
    }

    public void drawBackground(WorldView view) {
        for (int row = 0; row < view.viewport.getNumRows(); row++) {
            for (int col = 0; col < view.viewport.getNumCols(); col++) {
                Point worldPoint = view.viewport.viewportToWorld(view.viewport, col, row);
                Optional<PImage> image =
                        world.getBackgroundImage(view.world, worldPoint);
                if (image.isPresent()) {
                    view.screen.image(image.get(), col * view.tileWidth,
                            row * view.tileHeight);
                }
            }
        }
    }

    public static void drawEntities(WorldView view) {
        for (Entity entity : view.world.getEntities()) {
            Point pos = entity.getPosition();

            if (view.viewport.contains(view.viewport, pos)) {
                Point viewPoint = view.viewport.worldToViewport(view.viewport, pos.getX(), pos.getY());
                view.screen.image(entity.getCurrentImage(entity),
                        viewPoint.getX() * view.tileWidth,
                        viewPoint.getY() * view.tileHeight);
            }
        }
    }

    public void drawViewport(WorldView view) {
        drawBackground(view);
        drawEntities(view);
    }
}
