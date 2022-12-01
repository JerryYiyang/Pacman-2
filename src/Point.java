import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class Point {
   public final int x;
   public final int y;
   private int h;
   private double g;
   private double f;
   private Point parent;

   public Point(int x, int y) {
      this.x = x;
      this.y = y;
      parent = null;
   }

   public Point getParent() {
      return parent;
   }

   public int getX() {return x;}
   public int getY() {return y;}

   public double getF() {
      return f;
   }

   public double getG(){return g;}

   public String toString() {
      return "(" + x + "," + y + ")";
   }

   public boolean equals(Object other) {
      return other instanceof Point &&
              ((Point) other).x == this.x &&
              ((Point) other).y == this.y;
   }

   public int hashCode() {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   public Optional<Entity> nearestEntity(
           List<Entity> entities, Point pos)
   {
      if (entities.isEmpty()) {
         return Optional.empty();
      }
      else {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities) {
            int otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance) {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   private int distanceSquared(Point p1, Point p2) {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public boolean adjacent(Point p) {
      return (x == p.x && Math.abs(y - p.y) == 1) ||
              (y == p.y && Math.abs(x - p.x) == 1);
   }

   public void initValues(Point parent, Point goal) {
      int xHDist = Math.abs(x - goal.x);
      int yHDist = Math.abs(y - goal.y);
      h = xHDist + yHDist;

      int xGDist = Math.abs(parent.x - x);
      int yGDist = Math.abs(parent.y - y);
//      xGDist = xGDist * xGDist;
//      yGDist = yGDist * yGDist;
      g = xGDist + yGDist;
      g = parent.g + g;

      f = h + g;
      this.parent = parent;
   }

   public static boolean neighbors(Point p1, Point p2)
   {
      return p1.x+1 == p2.x && p1.y == p2.y ||
              p1.x-1 == p2.x && p1.y == p2.y ||
              p1.x == p2.x && p1.y+1 == p2.y ||
              p1.x == p2.x && p1.y-1 == p2.y;
   }
}

