import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Polygon2D {
    private final int[] xPoints;
    private final int[] yPoints;

    public int[] getXPoints() { return xPoints.clone(); }

    public int[] getYPoints() { return yPoints.clone(); }

    public int getNumPoints() { return xPoints.length; }

    public Polygon2D(int[] xPoints, int[] yPoints) {
        int numPoints = xPoints.length;
        if (numPoints != yPoints.length)
            throw new IllegalArgumentException("Количество координат X и Y вершин полигона должно совпадать.");
        if (numPoints < 3)
            throw new IllegalArgumentException("Полигон должен иметь как минимум 3 вершины.");

        this.xPoints = xPoints.clone();
        this.yPoints = yPoints.clone();
    }

    public Polygon2D(int[] xPoints, int[] yPoints, boolean cw) {
        this(xPoints, yPoints);

        if (this.isClockwise() != cw)
            this.reversePoints();
    }

    public boolean isClockwise() {
        // определяется ориентированность полигона; если большинство вершин имеют положение RIGHT относительно всех сторон, то полигон ориентирован по часовой стрелке
        int numPoints = getNumPoints();
        int right_sides = 0;

        for (int i = 0; i < numPoints; i++) {
            Line2D line = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            for (int j = 0; j < numPoints; j++) {
                if (j == i || j == (i + 1) % numPoints)
                    continue;

                Line2D.PointType pointType = line.classifyPoint(xPoints[j], yPoints[j]);
                if (pointType == Line2D.PointType.RIGHT)
                    right_sides++;
                else if (pointType == Line2D.PointType.LEFT) {
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
        Line2D.PointType lastPointType = null;

        for (int i = 0; i < numPoints; i++) {
            Line2D line = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            Line2D.PointType currentPointType = null;
            for (int j = 0; j < numPoints; j++) {
                if (j == i || j == (i + 1) % numPoints)
                    continue;

                Line2D.PointType pointType = line.classifyPoint(xPoints[j], yPoints[j]);

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
            Line2D line1 = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            for (int j = i + 2; j < numPoints - 1; j++) {
                Line2D line2 = new Line2D(xPoints[j], yPoints[j], xPoints[(j + 1) % numPoints], yPoints[(j + 1) % numPoints]);
                if (Line2D.classifyCross(line1, line2) == Line2D.IntersectionType.SKEW_CROSS)
                    return false;
            }
        }

        return true;
    }

    public void draw(BufferedImage image, int color) {
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line2D line = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
            line.draw(image, color);
        }
    }

    public boolean pointInsidePolygonEO(double x, double y) {
        // определяется, находится ли точка (x, y) внутри полигона this в соответствии с правилом Even-Odd
        int param = 0;
        int numPoints = getNumPoints();

        for (int i = 0; i < numPoints; i++) {
            Line2D line = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
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
            Line2D line = new Line2D(xPoints[i], yPoints[i], xPoints[(i + 1) % numPoints], yPoints[(i + 1) % numPoints]);
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

    public Optional<Polygon2D> clip(Line2D clipLine) {
        List<Integer> xPointsNewList = new ArrayList<>();
        List<Integer> yPointsNewList = new ArrayList<>();

        int numPoints = getNumPoints();
        for (int i = 0; i < numPoints; i++) {
            int x1 = xPoints[i], y1 = yPoints[i];
            int x2 = xPoints[(i + 1) % numPoints], y2 = yPoints[(i + 1) % numPoints];
            Line2D line = new Line2D(x1, y1, x2, y2);
            Line2D.IntersectionResult intersection = Line2D.findIntersection(line, clipLine);

            if ((intersection.type == Line2D.IntersectionType.SKEW && (intersection.t < 0 || intersection.t > 1)) || intersection.type == Line2D.IntersectionType.PARALLEL || intersection.type == Line2D.IntersectionType.SAME) {
                if (clipLine.classifyPoint(x2, y2) != Line2D.PointType.LEFT) {
                    xPointsNewList.add(x2);
                    yPointsNewList.add(y2);
                }
            }
            else {
                xPointsNewList.add((int) Math.round(x1 + intersection.t * (x2 - x1)));
                yPointsNewList.add((int) Math.round(y1 + intersection.t * (y2 - y1)));
                if (clipLine.classifyPoint(x2, y2) == Line2D.PointType.RIGHT) {
                    xPointsNewList.add(x2);
                    yPointsNewList.add(y2);
                }
            }
        }

        if (!xPointsNewList.isEmpty()) {
            int[] xPointsNewArray = xPointsNewList.stream().mapToInt(i -> i).toArray();
            int[] yPointsNewArray = yPointsNewList.stream().mapToInt(i -> i).toArray();
            return Optional.of(new Polygon2D(xPointsNewArray, yPointsNewArray));
        }

        return Optional.empty();
    }

    public Optional<Polygon2D> clip(Polygon2D clipPolygon) {
        // отсечение простого CW полигона выпуклым CW полигоном с помощью алгоритма Сазерленда-Ходжмана
        // метод возвращает тип Optional<Polygon>, предполагая, что отсекаемый полигон может оказаться вне полигона-отсекателя, в случае чего возвращается пустой объект Optional.empty()

        if (!this.isClockwise())
            throw new IllegalArgumentException("Отсекаемый полигон должен быть ориентирован по часовой стрелке.");
        if (!this.isSimple())
            throw new IllegalArgumentException("Отсекаемый полигон должен быть простым.");
        if (!clipPolygon.isClockwise())
            throw new IllegalArgumentException("Полигон-отсекатель должен быть ориентирован по часовой стрелке.");
        if (!clipPolygon.isConvex())
            throw new IllegalArgumentException("Полигон-отсекатель должен быть выпуклым.");

        Optional<Polygon2D> polygonNew = Optional.of(new Polygon2D(xPoints, yPoints));

        int numPoints = clipPolygon.getNumPoints();
        for (int i = 0; i < numPoints; i++) {
            if (polygonNew.isPresent()) {
                Line2D clipLine = new Line2D(clipPolygon.xPoints[i], clipPolygon.yPoints[i], clipPolygon.xPoints[(i + 1) % numPoints], clipPolygon.yPoints[(i + 1) % numPoints]);
                polygonNew = polygonNew.get().clip(clipLine);
            }
            else break;
        }

        return polygonNew;
    }
}
