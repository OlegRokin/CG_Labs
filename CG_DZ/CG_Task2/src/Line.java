import java.awt.image.BufferedImage;
import java.util.Optional;

public class Line {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line(Point point1, Point point2) {
        this((int) point1.getX(), (int) point1.getY(), (int) point2.getX(), (int) point2.getY());
    }

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }

    public void draw(BufferedImage image, int color) {
        int x = x1, y = y1;
        int dx = x2 - x1, dy = y2 - y1;
        int ix, iy;
        int e;
        int i;

        if (dx > 0) ix = 1;
        else if (dx < 0) { ix = -1; dx = -dx; }
        else ix = 0;
        if (dy > 0) iy = 1;
        else if (dy < 0) { iy = -1; dy = -dy; }
        else iy = 0;

        if (dx >= dy) {
            e = 2 * dy - dx;
            for (i = 0; i <= dx; i++) {
                PixelDrawing.setPixel(image, x, y, color);
                if ((iy >= 0 && e >= 0) || (iy < 0 && e > 0)) {
                    y += iy;
                    e -= 2 * dx;
                }
                x += ix;
                e += 2 * dy;
            }
        }
        else {
            e = 2 * dx - dy;
            for (i = 0; i <= dy; i++) {
                PixelDrawing.setPixel(image, x, y, color);
                if ((ix >= 0 && e >= 0) || (ix < 0 && e > 0)) {
                    x += ix;
                    e -= 2 * dy;
                }
                y += iy;
                e += 2 * dx;
            }
        }
    }
}
