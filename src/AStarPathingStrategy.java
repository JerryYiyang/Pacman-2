import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Comparator<Point> fcomp = Comparator.comparing(Point::getF);
        PriorityQueue<Point> openL = new PriorityQueue<>(fcomp);
        HashMap<Point, Point> openH = new HashMap<>();
        List<Point> closed = new ArrayList<>();
        List<Point> path = new ArrayList<>();

        openL.add(start);
        openH.put(start, start);
        Point cur = start;
            while (!withinReach.test(cur, end)) {
                List<Point> neighbors = potentialNeighbors.apply(cur).filter(canPassThrough).collect(Collectors.toList());
                for (Point p : neighbors) {
                    if (!closed.contains(p)) {
                        p.initValues(cur, end);
                        if (!openH.containsKey(p)) {
                            openH.put(p, p);
                            openL.add(p);
                        }
                        if (p.getG() < openH.get(p).getG()) {
                            openH.remove(p);
                            openH.put(p, p);
                            cur = p.getParent();
                        }
                    }
                }
                if (!closed.contains(cur)) {
                    closed.add(cur);
                }
                if (openL.isEmpty()) {
                    return new ArrayList<>();
                }
                cur = openL.poll();
                openH.remove(cur);
            }
            while (cur.getParent() != start) {
                path.add(cur);
                cur = cur.getParent();
            }
        path.add(cur);
        Collections.reverse(path);
        return path;
    }
}
