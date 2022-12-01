
import java.awt.*;
import java.util.*;
import java.util.List;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 * make all public nonstatic variables private
 */
public abstract class Entity
{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p){
        this.position = p;
    }

    public List<PImage> getImages() {
        return images;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public Entity(
            String id,
            Point position,
            List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public Action createAnimationAction(int repeatCount)
    {
        return new Animation(this, repeatCount);
    }

    public Action createActivityAction(WorldModel world, ImageStore imageStore)
    {
        return new Activity(this, world, imageStore);
    }

    public void nextImage() {this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    public int getNumFromRange(int max, int min)
    {
        Random rand = new Random();
        return min + rand.nextInt(
                max
                        - min);
    }

    public static PImage getCurrentImage(Object entity) {
        if (entity instanceof Background) {
            return ((Background)entity).getImages().get(
                    ((Background)entity).getImageIndex());
        }
        else if (entity instanceof Entity) {
            return ((Entity)entity).getImages().get(((Entity)entity).getImageIndex());
        }
        else {
            throw new UnsupportedOperationException(
                    String.format("getCurrentImage not supported for %s",
                            entity));
        }
    }
}
