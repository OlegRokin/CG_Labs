import java.awt.image.BufferedImage;

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

    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }

    // возможные типы положения точки относительно отрезка
    public enum PointType {LEFT, RIGHT, BEYOND, BEHIND, BETWEEN, ORIGIN, DESTINATION}

    public PointType classifyPoint(double x, double y) {
        // определяется положение точки (x, y) относительно прямой this
        double ax = x2 - x1;
        double ay = y2 - y1;
        double bx = x - x1;
        double by = y - y1;
        double s = ax * by - bx * ay;

        if (s > 0)
            return PointType.LEFT;
        if (s < 0)
            return PointType.RIGHT;
        if ((ax * bx < 0) || (ay * by < 0))
            return PointType.BEHIND;
        if ((ax * ax + ay * ay) < (bx * bx + by * by))
            return PointType.BEYOND;
        if (x1 == x && y1 == y)
            return PointType.ORIGIN;
        if (x2 == x && y2 == y)
            return PointType.DESTINATION;
        return PointType.BETWEEN;
    }

    // возможные типы пересечения прямых/отрезков
    public enum IntersectionType {SAME, PARALLEL, SKEW, SKEW_CROSS, SKEW_NO_CROSS}

    public static class IntersectionResult {
        public final IntersectionType type;
        public final Double t;

        public IntersectionResult(IntersectionType type, Double t) {
            if ((type == IntersectionType.PARALLEL || type == IntersectionType.SAME) && t != null) {
                throw new IllegalArgumentException("Параметр t не может быть не null при отсутствии пересечения прямых.");
            }
            if ((type == IntersectionType.SKEW || type == IntersectionType.SKEW_CROSS || type == IntersectionType.SKEW_NO_CROSS) && t == null) {
                throw new IllegalArgumentException("Параметр t не может быть null при пересечении прямых.");
            }

            this.type = type;
            this.t = t;
        }
    }


    public static IntersectionResult findIntersection(Line line1, Line line2) {
        // определяется пересечение прямой и отрезка line1 с прямой (но не отрезком) line2
        double ax = line1.x1, ay = line1.y1;
        double bx = line1.x2, by =line1.y2;
        double cx = line2.x1, cy = line2.y1;
        double dx = line2.x2, dy = line2.y2;

        double nx = dy - cy;
        double ny = cx - dx;

        PointType pointType;

        double den = nx * (bx - ax) + ny * (by - ay);
        if (den == 0) {
            pointType = line2.classifyPoint(ax, ay);
            if (pointType == PointType.LEFT || pointType == PointType.RIGHT)
                return new IntersectionResult(IntersectionType.PARALLEL, null);
            else
                return new IntersectionResult(IntersectionType.SAME, null);
        }

        double num = nx * (ax - cx) + ny * (ay - cy);
        double t = - num / den;
        return new IntersectionResult(IntersectionType.SKEW, t);
    }

    public static IntersectionType classifyCross(Line line1, Line line2) {
        // определяется пересечение отрезка line1 с отрезком line2 посредством двойного вызова findIntersection
        IntersectionResult intersectAB = findIntersection(line1, line2);
        if (intersectAB.type == IntersectionType.SAME || intersectAB.type == IntersectionType.PARALLEL)
            return intersectAB.type;

        double tAB = intersectAB.t;
        if (tAB < 0 || tAB > 1)
            return IntersectionType.SKEW_NO_CROSS;

        IntersectionResult intersectCD = findIntersection(line2, line1);
        double tCD = intersectCD.t;
        if (tCD < 0 || tCD > 1)
            return IntersectionType.SKEW_NO_CROSS;

        return IntersectionType.SKEW_CROSS;
    }

    // возможные типы прямой/отрезка относительно луча
    public enum RayIntersectionType {TOUCHING, CROSS_LEFT, CROSS_RIGHT, INESSENTIAL}

    public RayIntersectionType classifyRayIntersection(double ax, double ay) {
        // классифицируется прямая this относительно направленного горизонтально вправо луча r с началом в точке (ax, ay)
        switch (classifyPoint(ax, ay)) {
            case LEFT -> {
                if (ay > y1 && ay <= y2) return RayIntersectionType.CROSS_LEFT;
                else return RayIntersectionType.INESSENTIAL;
            }
            case RIGHT -> {
                if (ay > y2 && ay <= y1) return RayIntersectionType.CROSS_RIGHT;
                else return RayIntersectionType.INESSENTIAL;
            }
            case BETWEEN, ORIGIN, DESTINATION -> {
                return RayIntersectionType.TOUCHING;
            }
            default -> {
                return RayIntersectionType.INESSENTIAL;
            }
        }
    }

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
