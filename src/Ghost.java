import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Ghost extends PlayerAnimated {

    public Ghost(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                world.findNearest(super.getPosition(), new ArrayList<>(Arrays.asList(Player.class)));

        if (!target.isPresent() || !moveTo(world,
                target.get(),
                scheduler))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    super.getActionPeriod());
        }
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (super.getPosition().adjacent(target.getPosition())) {
            ((Player)target).setGame();
            return true;
        }
        else {
            Point nextPos = super.nextPosition(world, target.getPosition());

            if (!super.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
}
