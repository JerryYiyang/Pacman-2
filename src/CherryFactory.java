public class CherryFactory extends FruitFactory {

    public Cherry createFruit() {
        return new Cherry("cherry", VirtualWorld.pressed, VirtualWorld.getImageStore().getImageList("cherry"), 200, 0);
    }
}
