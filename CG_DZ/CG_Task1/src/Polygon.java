import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Polygon {
    private final int[] xPoints;
    private final int[] yPoints;

    public int[] getXPoints() { return xPoints.clone(); }

    public int[] getYPoints() { return yPoints.clone(); }

    public int getNumPoints() { return xPoints.length; }

    public Polygon(int[] xPoints, int[] yPoints) {
        int numPoints = xPoints.length;
        if (numPoints != yPoints.length)
            throw new IllegalArgumentException("Количество координат X и Y вершин полигона должно совпадать.");
        if (numPoints < 3)
            throw new IllegalArgumentException("Полигон должен иметь как минимум 3 вершины.");

        // проверка на то, что все соседние стороны полигона не лежат на одной прямой
        for (int i = 0; i < numPoints; i++) {
            int x1 = xPoints[i], y1 = yPoints[i];
            int x2 = xPoints[(i + 1) % numPoints], y2 = yPoints[(i + 1) % numPoints];
            int x3 = xPoints[(i + 2) % numPoints], y3 = yPoints[(i + 2) % numPoints];

            int crossProd = (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2);
            if (crossProd == 0)
                throw new IllegalArgumentException("Соседние стороны полигона не должны лежать на одной прямой.");
        }

        this.xPoints = xPoints.clone();
        this.yPoints = yPoints.clone();
    }

    public Polygon(int[] xPoints, int[] yPoints, boolean cw) {
        this(xPoints, yPoints);

        if (this.isClockwise() != cw)
            this.reversePoints();
    }

    public boolean isClockwise() {
        // определяется ориентированность полигона; если большинство вершин имеют положение RIGHT относительно всех сторон, то полигон ориентирован по часовой стрелке
        int numPoints = getNumPoints();
        int right_sides = 0;

        for (int i = 0; i < numPoints; i++) {
            Line line = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            for (int j = 0; j < numPoints; j++) {
                if (j == i || j == (i + 1) % numPoints)
                    continue;

                Line.PointType pointType = line.classifyPoint(xPoints[j], yPoints[j]);
                if (pointType == Line.PointType.RIGHT)
                    right_sides++;
                else if (pointType == Line.PointType.LEFT) {
                    right_sides--;
                }
            }
        }

        return right_sides > 0;
    }

    private void reversePoints() {
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints / 2; i++) {
            int xTemp = xPoints[i];
            xPoints[i] = xPoints[numPoints - 1 - i];
            xPoints[numPoints - 1 - i] = xTemp;

            int yTemp = yPoints[i];
            yPoints[i] = yPoints[numPoints - 1 - i];
            yPoints[numPoints - 1 - i] = yTemp;
        }
    }

    public boolean isConvex() {
        // определяется выпуклость полигона; проверяется, что все вершины имеют положение LEFT/RIGHT относительно всех сторон
        int numPoints = getNumPoints();
        Line.PointType lastPointType = null;

        for (int i = 0; i < numPoints; i++) {
            Line line = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            Line.PointType currentPointType = null;
            for (int j = 0; j < numPoints; j++) {
                if (j == i || j == (i + 1) % numPoints)
                    continue;

                Line.PointType pointType = line.classifyPoint(xPoints[j], yPoints[j]);

                if (currentPointType == null)
                    currentPointType = pointType;
                else if (currentPointType != pointType)
                    return false;
            }

            if (currentPointType != null) {
                if (lastPointType == null)
                    lastPointType = currentPointType;
                else if (lastPointType != currentPointType)
                    return false;
            }
        }

        return true;
    }

    public boolean isSimple() {
        // определяется наличие самопересечений у полигона; проверяется, что ни одна сторона полигона не пересекается ни с одной его несоседней стороной
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line line1 = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            for (int j = i + 2; j < numPoints - 1; j++) {
                Line line2 = new Line(xPoints[j], yPoints[j], xPoints[(j + 1) % numPoints], yPoints[(j + 1) % numPoints]);
                if (Line.classifyCross(line1, line2) == Line.IntersectionType.SKEW_CROSS)
                    return false;
            }
        }

        return true;
    }

    public void draw(BufferedImage image, int color) {
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line line = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            line.draw(image, color);
        }
    }

    public boolean pointInsidePolygonEO(double x, double y) {
        // определяется, находится ли точка (x, y) внутри полигона this в соответствии с правилом Even-Odd
        int param = 0;
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line line = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            switch (line.classifyRayIntersection(x, y)) {
                case TOUCHING -> {
                    return true;
                }
                case CROSS_LEFT, CROSS_RIGHT -> {
                    param = 1 - param;
                }
            }
        }
        return param == 1;
    }

    public boolean pointInsidePolygonNZW(double x, double y) {
        // определяется, находится ли точка (x, y) внутри полигона this в соответствии с правилом Non-Zero Winding
        int param = 0;
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line line = new Line(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            switch (line.classifyRayIntersection(x, y)) {
                case TOUCHING -> {
                    return true;
                }
                case CROSS_LEFT -> {
                    param++;
                }
                case CROSS_RIGHT -> {
                    param--;
                }
            }
        }
        return param != 0;
    }

    public enum FillType {EO, NZW}

    public void fill(BufferedImage image, int color, FillType type) {
        int xMin = Arrays.stream(xPoints).min().orElseThrow();
        int xMax = Arrays.stream(xPoints).max().orElseThrow();
        int yMin = Arrays.stream(yPoints).min().orElseThrow();
        int yMax = Arrays.stream(yPoints).max().orElseThrow();

        for (int y = yMin; y < yMax; y++) {
            for (int x = xMin; x < xMax; x++) {
                if ((type == FillType.EO && pointInsidePolygonEO(x, y)) || (type == FillType.NZW && pointInsidePolygonNZW(x, y))) {
                    PixelDrawing.setPixel(image, x, y, color);
                }
            }
        }
    }
}
