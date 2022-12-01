import processing.core.PImage;

import java.util.List;

public class Cherry extends Fruit {

    private final int bonus = 35;
    public Cherry(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public int getBonus() {return bonus;}
}
