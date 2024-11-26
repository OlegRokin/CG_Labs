import java.awt.image.BufferedImage;

public class CatmullRom {
    private final Point[] points;

    public CatmullRom(Point[] points) {
        if (points.length < 4) {
            throw new IllegalArgumentException("Количество вершин должно быть не менее четырех.");
        }

        this.points = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = points[i].copy();
        }
    }

    public Point[] getPoints() {
        Point[] points = new Point[this.points.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = this.points[i].copy();
        }
        return points;
    }

    public Point getPoint(int elementNum, double t) {
        if (elementNum < 0 || elementNum > points.length - 4) {
            throw new IllegalArgumentException("Номер элемента кривой должен быть неотрицательным и не превосходить (количество точек - 4).");
        }
        if (t < 0.0 || t > 1.0) {
            throw new IllegalArgumentException("Параметр t должен лежать в отрезке от 0.0 до 1.0.");
        }

        double[] scalars = {
                - t * (1.0 - t) * (1.0 - t) / 2.0,
                (2.0 - 5.0 * t * t + 3.0 * t * t * t) / 2.0,
                t * (1.0 + 4.0 * t - 3.0 * t * t) / 2.0,
                - t * t * (1.0 - t) / 2.0
        };

        Point point = new Point(0.0, 0.0);
        for (int i = 0; i < 4; i++) {
            point = Point.add(point, Point.multiply(points[elementNum + i], scalars[i]));
        }

        return point;
    }

    public void draw(BufferedImage image, int color) {
        for (int elementNum = 0; elementNum <= points.length - 4; elementNum++) {
            Point pointN1 = Point.add(points[elementNum], Point.add(Point.multiply(points[elementNum + 1], -2.0), points[elementNum + 2]));
            Point pointN2 = Point.add(points[elementNum + 1], Point.add(Point.multiply(points[elementNum + 2], -2.0), points[elementNum + 3]));
            double H = Math.max(pointN1.distance(), pointN2.distance());
            int N = 1 + (int) Math.sqrt(3.0 * H);

            double t1, t2;
            for (int i = 0; i < N; i++) {
                t1 = (double) i / N;
                t2 = (double) (i + 1) / N;

                Point point1 = getPoint(elementNum, t1);
                Point point2 = getPoint(elementNum, t2);
                Line line = new Line(point1, point2);
                line.draw(image, color);
            }
        }
    }
}
