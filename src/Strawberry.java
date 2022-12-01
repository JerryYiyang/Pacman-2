import processing.core.PImage;

import java.util.List;

public class Strawberry extends Fruit {

    private final int bonus = 50;

    public Strawberry(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public int getBonus() {return bonus;}

}
