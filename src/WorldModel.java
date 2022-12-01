import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    public static final Random rand = new Random();

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    public static final int KEYED_RED_IDX = 2;
    public static final int KEYED_GREEN_IDX = 3;
    public static final int KEYED_BLUE_IDX = 4;

    public static final int PROPERTY_KEY = 0;

    public static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right",
            "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));
    public static final String WALL_KEY = "wall";
    public static final int WALL_NUM_PROPERTIES = 4;
    public static final int WALL_ID = 1;
    public static final int WALL_COL = 2;
    public static final int WALL_ROW = 3;

    public static final String BGND_KEY = "background";
    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;

    public static final String DUDE_KEY = "redghost";
    public static final int DUDE_NUM_PROPERTIES = 7;
    public static final int DUDE_ID = 1;
    public static final int DUDE_COL = 2;
    public static final int DUDE_ROW = 3;
    public static final int DUDE_LIMIT = 4;
    public static final int DUDE_ACTION_PERIOD = 6;
    public static final int DUDE_ANIMATION_PERIOD = 3;

    public static final String PLAYER_KEY = "player";
    public static final int PLAYER_NUM_PROPERTIES = 6;
    public static final int PLAYER_ID = 1;
    public static final int PLAYER_COL = 2;
    public static final int PLAYER_ROW = 3;
    public static final int PLAYER_ANIMATION_PERIOD = 5;
    public static final int PLAYER_ACTION_PERIOD = 3;

    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public Optional<Entity> findNearest(
            Point pos, List<Class> kinds)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind: kinds)
        {
            for (Entity entity : this.entities) {
                if (kind.isInstance(entity)) {
                    ofType.add(entity);
                }
            }
        }
        return pos.nearestEntity(ofType, pos);
    }

    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < 15 && pos.getX() >= 0
                && pos.getX() < 18;
    }

    private void setOccupancyCell(
            Point pos, Entity entity)
    {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void tryAddEntity(WorldModel world, Entity entity) {
        if (isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        world.addEntity(entity);
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            setOccupancyCell(oldPos, null);
            removeEntityAt(pos);
            setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }

    private void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }

    private Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(
            WorldModel world, Point pos, Background background)
    {
        world.background[pos.getY()][pos.getX()] = background;
    }

    public Optional<Entity> getOccupant(WorldModel world, Point pos) {
        if (world.isOccupied(pos)) {
            return Optional.of(world.getOccupancyCell(pos));
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (world.withinBounds(pos)) {
            return Optional.of(Background.getCurrentImage(world.getBackgroundCell(world, pos)));
        }
        else {
            return Optional.empty();
        }
    }

    private void setBackground(
            WorldModel world, Point pos, Background background)
    {
        if (world.withinBounds(pos)) {
            world.setBackgroundCell(world, pos, background);
        }
    }

    public void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                e.getMessage()));
            }
            lineNumber++;
        }
    }

    private boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case DUDE_KEY:
                    return parseDude(properties, world, imageStore);
                case PLAYER_KEY:
                    return parsePlayer(properties, world, imageStore);
                case WALL_KEY:
                    return parseWall(properties, world, imageStore);
            }
        }

        return false;
    }

    private boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            world.setBackground(world, pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    private boolean parseWall(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == WALL_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[WALL_COL]),
                    Integer.parseInt(properties[WALL_ROW]));
            Entity entity = createWall(properties[WALL_ID], pt,
                    imageStore.getImageList(WALL_KEY));
            world.tryAddEntity(world, entity);
        }

        return properties.length == WALL_NUM_PROPERTIES;
    }

    private boolean parsePlayer(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == PLAYER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[PLAYER_COL]),
                    Integer.parseInt(properties[PLAYER_ROW]));
            Player entity = createPlayer(properties[PLAYER_ID],
                    pt,
                    Integer.parseInt(properties[PLAYER_ACTION_PERIOD]),
                    Integer.parseInt(properties[PLAYER_ANIMATION_PERIOD]),
                    imageStore.getImageList(PLAYER_KEY));
            world.tryAddEntity(world, entity);
        }

        return properties.length == PLAYER_NUM_PROPERTIES;
    }

    private boolean parseDude(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[DUDE_COL]),
                    Integer.parseInt(properties[DUDE_ROW]));
            Ghost entity = createDude(properties[DUDE_ID],
                    pt,
                    Integer.parseInt(properties[DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[DUDE_ANIMATION_PERIOD]),
                    imageStore.getImageList(DUDE_KEY));
            world.tryAddEntity(world, entity);
        }

        return properties.length == DUDE_NUM_PROPERTIES;
    }
    public Wall createWall(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Wall(id, position, images);
    }

    private Player createPlayer(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Player(id, position, images,
                actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public Ghost createDude(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Ghost(id, position, images,
                actionPeriod, animationPeriod);
    }

}
