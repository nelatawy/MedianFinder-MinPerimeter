package perimeterFinder;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MinPerimeterFinder {

    /**
     * returns perimeter of triangle formed by 3 defined points
     * @param a first point
     * @param b second point
     * @param c third point
     * @return the perimeter
     */
    public static Double getPerimeter(Point2D a, Point2D b, Point2D c){
        Double ab = a.distance(b);
        Double bc = b.distance(c);
        Double ca = c.distance(a);
        return ab + bc + ca;
    }

    private static List<Point2D> getPointsAroundSep(List<Point2D> pointsY, Double medianX, Double stripWidth){
        List<Point2D> crossingSegmentPts = new ArrayList<>();
        for (Point2D point2D : pointsY) {
            double xCoord = point2D.getX();
            if (xCoord > medianX - stripWidth && xCoord < medianX + stripWidth) {
                crossingSegmentPts.add(point2D);
            }
        }
        return crossingSegmentPts;
    }

    private static Double getMinAroundSep(List<Point2D> crossingSegmentPts, int checkCntLimit){
        double minFound = Double.MAX_VALUE;
        int sz = crossingSegmentPts.size();
        for(int i = 0; i < sz; i++){
            Point2D a = crossingSegmentPts.get(i);
            for (int j = i + 1; j < Math.min(i + checkCntLimit, sz); j++){
                Point2D b = crossingSegmentPts.get(j);
                for (int k = j + 1; k < Math.min(i + checkCntLimit, sz); k++){
                    Point2D c = crossingSegmentPts.get(k);
                    minFound = Math.min(minFound, getPerimeter(a, b, c));
                }
            }
        }
        return minFound;
    }

    /**
     * @param pointsX points sorted on X coords.
     * @param pointsY points sorted on Y coords.
     * @return the minimum perimeter
     */
    private Double minPerimeterHelper(List<Point2D> pointsX, List<Point2D> pointsY) {
        if (pointsX.size() == 3) {
            return getPerimeter(pointsX.get(0), pointsX.get(1), pointsX.get(2));
        }
        if (pointsX.size() < 3){
            return Double.MAX_VALUE; // heavily penalized result (can't form a triangle)
        }

        Point2D medianEle = pointsX.get(pointsX.size()/2);



        // preparing the arguments for left/right recursive calls
        List<Point2D> pointsXleft = new ArrayList<>(pointsX.subList(0, pointsX.size()/2)); //from 0 to medianIdx - 1
        List<Point2D> pointsXright = new ArrayList<>(pointsX.subList(pointsX.size()/2, pointsX.size())); // from medianIdx to size - 1


        List<Point2D> pointsYleft = new ArrayList<>();
        List<Point2D> pointsYright = new ArrayList<>();
        for (Point2D d : pointsY) {
            if (d.getX() < medianEle.getX()) {
                pointsYleft.add(d);
            } else if (d.getX() == medianEle.getX()) {
                //to account for tie breaking and making sure it doesn't break with many values equal on X
                if (d.getY() < medianEle.getY()) {
                    pointsYleft.add(d);
                } else {
                    pointsYright.add(d);
                }
            }
            else {
                pointsYright.add(d);
            }
        }

        double minFound = Math.min(
                minPerimeterHelper(pointsXleft, pointsYleft),
                minPerimeterHelper(pointsXright, pointsYright));

        // now we need to account for triangles crossing the boundaries

        //we collect all candidate points around the crossing line
        double stripWidth = minFound/2;
        List<Point2D> crossingSegmentPts = getPointsAroundSep(pointsY, medianEle.getX(), stripWidth);

        // check all candidate triplets O(n*(limit^2)) -> O(n)
        int checkCntLimit = 15; //how many elements after a point do we compare it with.
        minFound = Math.min(minFound, getMinAroundSep(crossingSegmentPts, checkCntLimit));
        return minFound;
    }

    public Double minPerimeter(List<Point2D> points){
        List<Point2D> pointsX = new ArrayList<>(points);
        List<Point2D> pointsY = new ArrayList<>(points);

        pointsX.sort(Comparator.comparingDouble(Point2D::getX)
                .thenComparingDouble(Point2D::getY));

        pointsY.sort(Comparator.comparingDouble(Point2D::getY));
        return minPerimeterHelper(pointsX, pointsY);
    }
}
