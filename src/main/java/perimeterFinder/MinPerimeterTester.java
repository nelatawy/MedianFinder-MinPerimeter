package perimeterFinder;

import java.awt.geom.Point2D;
import java.util.*;

public class MinPerimeterTester {
    private static void correctnessTest(int inputSize, int itrCnt){
        Random rand = new Random();
        for (int t = 0; t < itrCnt; t++) {
            List<Point2D> pts = new ArrayList<>();
            for (int i = 0; i < inputSize; i++) {
                double x = rand.nextDouble();
                double y = rand.nextDouble();
                pts.add(new Point2D.Double(x, y));
            }

            System.out.println(Arrays.toString(pts.toArray()));
            double normal = Double.MAX_VALUE;
            for(int i = 0; i < inputSize; i++){
                Point2D a = pts.get(i);
                for(int j = i + 1; j < inputSize; j++){
                    Point2D b = pts.get(j);
                    for (int k = j + 1; k < inputSize; k++){
                        Point2D c = pts.get(k);
                        normal = Math.min(normal, MinPerimeterFinder.getPerimeter(a, b, c));

                    }
                }
            }

            double finderRes = new MinPerimeterFinder().minPerimeter(pts);

            if (Math.abs(normal - finderRes) > 1e-4) {
                return;
            }
        }
    }

    public static void main(String[] args) {
//        correctnessTest((int)1e7, 100);
        Scanner sc = new Scanner(System.in);
        MinPerimeterFinder minPerimeterFinder = new MinPerimeterFinder();
        while (sc.hasNext()) {
            int ptCnt = sc.nextInt();
            List<Point2D> pts = new ArrayList<>();
            for(int i = 0; i <  ptCnt; i++){
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                pts.add(new Point2D.Double(x, y));
            }

            System.out.println(minPerimeterFinder.minPerimeter(pts));
        }

    }
}
