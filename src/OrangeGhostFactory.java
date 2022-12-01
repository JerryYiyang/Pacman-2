public class OrangeGhostFactory implements GhostFactory {

    public Ghost createGhost() {
        return new Ghost("orangeghost", VirtualWorld.newSpawn, VirtualWorld.getImageStore().getImageList("orangeghost"), 300, 0);
    }

}
