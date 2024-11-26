import java.awt.image.BufferedImage;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public Point copy() {
        return new Point(x, y);
    }

    public static Point add(Point point1, Point point2) {
        return new Point(point1.x + point2.x, point1.y + point2.y);
    }

    public static Point multiply(Point point, double scalar) {
        return new Point(scalar * point.x, scalar * point.y);
    }

    public double distance() {
        return Math.abs(x) + Math.abs(y);
    }

    public void draw(BufferedImage image, int color, double radius) {
        int xMin = (int) Math.floor(x - radius), xMax = (int) Math.ceil(x + radius);
        int yMin = (int) Math.floor(y - radius), yMax = (int) Math.ceil(y + radius);

        for (int xPix = xMin; xPix <= xMax; xPix++) {
            for (int yPix = yMin; yPix <= yMax; yPix++) {
                if ((xPix - x) * (xPix - x) + (yPix - y) * (yPix - y) <= radius * radius) {
                    PixelDrawing.setPixel(image, xPix, yPix, color);
                }
            }
        }
    }
}
