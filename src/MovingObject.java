import processing.core.PImage;

import java.util.List;

public abstract class MovingObject extends Entity{

    private int actionPeriod;

    public MovingObject(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    public int getActionPeriod() {
        return actionPeriod;
    }

    public abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

    public abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
