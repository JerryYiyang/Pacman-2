public class StrawberryFactory extends FruitFactory {
    public Strawberry createFruit() {
        return new Strawberry("strawberry", VirtualWorld.pressed, VirtualWorld.getImageStore().getImageList("strawberry"), 200, 0);
    }
}
