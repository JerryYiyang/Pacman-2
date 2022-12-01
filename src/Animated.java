import processing.core.PImage;

import java.util.List;

public abstract class Animated extends MovingObject{

    private final int animationPeriod;

    public Animated(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }


    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

}