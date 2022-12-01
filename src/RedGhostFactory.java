public class RedGhostFactory implements GhostFactory {

    public Ghost createGhost() {
        Point tombstone = VirtualWorld.getGhostSpawnLocation();
        Point spawn = new Point(tombstone.getX() + 1, tombstone.getY());
        return new Ghost("ghost", spawn, VirtualWorld.getImageStore().getImageList("redghost"),
                0, 0);
    }
}
