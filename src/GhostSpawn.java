import processing.core.PImage;

import java.util.List;

public class GhostSpawn extends Entity {

    private static GhostSpawn only;

    private GhostSpawn() {
        super("GhostSpawn_50_50", new Point(50, 50), VirtualWorld.getImageStore().getImageList("ghostspawn"));
    }

    public GhostSpawn(String id, Point position, List<PImage> images) {
        super(id, position, images);

    }

    public GhostSpawn getGhostSpawn() {
        if (only == null)
            only = new GhostSpawn();
        return only;
    }
}
