import processing.core.PImage;

import java.util.*;

public abstract class Fruit extends PlayerAnimated {

    private PathingStrategy strategy = new AStarPathingStrategyforGame();
    public Fruit(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (super.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            GhostFactory gf = new RedGhostFactory();
            Ghost ghost = gf.createGhost();
            world.addEntity(ghost);
            ghost.scheduleActions(scheduler, world, VirtualWorld.getImageStore());
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

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

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        Optional<Entity> target =
                world.findNearest(super.getPosition(), new ArrayList<>(Arrays.asList(GhostSpawn.class)));

        if (!target.isPresent() || !moveTo(world, target.get(), scheduler))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    super.getActionPeriod());
        }

        Optional<Entity> player =
                world.findNearest(super.getPosition(), new ArrayList<>(Arrays.asList(Player.class)));
        if (super.getPosition().adjacent(player.get().getPosition())){
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
        }
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {
        List<Point> points;
        points = strategy.computePath(this.getPosition(), destPos,
                p ->  world.withinBounds(p) && !(world.getOccupant(world, p).isPresent()),
                (p1, p2) -> Point.neighbors(p1,p2),
                PathingStrategy.DIAGONAL_CARDINAL_NEIGHBORS);

        return points.get(0);
    }


    public abstract int getBonus();
}
