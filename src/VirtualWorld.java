import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

import static java.util.Objects.isNull;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;
    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 1;
    private static final int WORLD_HEIGHT_SCALE = 1;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;
    private static ImageStore imageStore;

    public static Point newSpawn;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;
    private Entity player;
    private long nextTime;
    private int num = 0;
    private GhostSpawn GhostSpawn;
    private static Point GhostSpawnLocation = new Point(9, 7);
    public static Point pressed;
    public static ImageStore getImageStore() {return imageStore;}
    public static Point getGhostSpawnLocation() {return GhostSpawnLocation;}
    public void settings() {
        size(570, 480);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);
        player = new Player("p", new Point(1, 1), imageStore.getImageList(WorldModel.PLAYER_KEY), 0, 0);
        this.world.addEntity(player);
        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
        GhostSpawn = new GhostSpawn("GhostSpawn_50_50", GhostSpawnLocation, imageStore.getImageList("ghostspawn"));
        this.world.addEntity(GhostSpawn);
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }
        view.drawViewport(view);
        fill(0, 200, 200);
        textSize(20);
        text("Score: " + String.valueOf(num), 15, 30);
        if (((Player)player).getGame() == false) {endGame();}
        else num++;
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
            pressed = mouseToPoint(mouseX, mouseY);
            System.out.println("CLICK! " + pressed.getX() + ", " + pressed.getY());

            Random r = new Random();
            int index = r.nextInt(2);
            ArrayList<String> fruits = new ArrayList<>(Arrays.asList("cherry", "strawberry"));
            String fruitKey = fruits.get(index);

            FruitFactory ff;
            Fruit fruit = null;
            if (fruitKey == "cherry") {
                ff = new CherryFactory();
                fruit = ff.createFruit();
            } else if (fruitKey == "strawberry") {
                ff = new StrawberryFactory();
                fruit = ff.createFruit();
            }
            if (!(world.isOccupied(pressed)) && !isNull(fruit)) {
                world.addEntity(fruit);
                fruit.scheduleActions(scheduler, world, imageStore);
            }

            for (int i = pressed.getX() - 1; i < pressed.getX() + 2; i++) {
                for (int j = pressed.getY() - 1; j < pressed.getY() + 2; j++) {
                    if (world.withinBounds(new Point(i, j)))
                        world.setBackgroundCell(world, new Point(i, j), new Background("flowers", imageStore.getImageList("flowers")));
                }
            }
            Optional<Entity> targetEntity = world.findNearest(pressed, new ArrayList<>(Arrays.asList(Ghost.class)));
            Entity target = targetEntity.get();
            newSpawn = target.getPosition();
            world.removeEntity(target);
            GhostFactory gf = new OrangeGhostFactory();
            Ghost orange = gf.createGhost();
            world.addEntity(orange);
            orange.scheduleActions(scheduler, world, VirtualWorld.getImageStore());
    }

    private Point mouseToPoint(int x, int y)
    {
        return view.getViewport().viewportToWorld(view.getViewport(), mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED && (!((Player)player).getGame() == false)) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            Point p = player.getPosition();
            Point newP = new Point(p.x + dx, p.y + dy);
            if (world.withinBounds(newP) && !world.isOccupied(newP)){
                world.moveEntity(player, newP);
            }
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                              imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }


    static void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, imageStore, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            world.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if(!(entity instanceof Wall)){
                MovingObject m = (MovingObject) entity;

                //Only start actions for entities that include action (not those with just animations)
                if (m.getActionPeriod() > 0) {
                    m.scheduleActions(scheduler, world, imageStore);
                }
            }
        }
    }

    private void endGame() {
        fill(0, 0, 0);
        textSize(30);
        text("You lost! Score: " + String.valueOf(num), 142, 120);
    }

    public static void parseCommandLine(String[] args) {
        if (args.length > 1)
        {
            if (args[0].equals("file"))
            {

            }
        }
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
