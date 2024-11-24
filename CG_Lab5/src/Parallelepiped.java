import java.awt.image.BufferedImage;

public class Parallelepiped {
    private final double xSize;
    private final double ySize;
    private final double zSize;
    private final Point3D center;
    private final Point3D axis;
    private double phi;

    public Parallelepiped(double xSize, double ySize, double zSize, Point3D center, Point3D axis, double phi) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.center = center.copy();
        this.axis = axis.copy();
        this.phi = phi;
    }

    public double getXSize() { return xSize; }
    public double getYSize() { return ySize; }
    public double getZSize() { return zSize; }
    public Point3D getCenter() { return center.copy(); }
    public Point3D getAxis() { return axis.copy(); }
    public double getPhi() { return phi; }
    public void setPhi(double phi) { this.phi = phi; }

    public Point3D[] getVertices() {
        double cx = center.getX(), cy = center.getY(), cz = center.getZ();
        Point3D[] vertices = {
                new Point3D(cx - xSize / 2, cy - ySize / 2, cz - zSize / 2),
                new Point3D(cx - xSize / 2, cy - ySize / 2, cz + zSize / 2),
                new Point3D(cx - xSize / 2, cy + ySize / 2, cz - zSize / 2),
                new Point3D(cx - xSize / 2, cy + ySize / 2, cz + zSize / 2),
                new Point3D(cx + xSize / 2, cy - ySize / 2, cz - zSize / 2),
                new Point3D(cx + xSize / 2, cy - ySize / 2, cz + zSize / 2),
                new Point3D(cx + xSize / 2, cy + ySize / 2, cz - zSize / 2),
                new Point3D(cx + xSize / 2, cy + ySize / 2, cz + zSize / 2),
        };

        Point3D[] verticesRotated = new Point3D[8];
        for (int i = 0; i < vertices.length; i++) {
            verticesRotated[i] = vertices[i].rotate(axis, phi);
        }

        return verticesRotated;
    }

    public void drawParallel(BufferedImage image, int scale, int xShift, int yShift, int edgeColor, int faceColor, boolean backFaceCulling) {
        Point3D[] vertices = getVertices();

        // отмечаем индексы вершин для каждой грани с обходом против часовой при виде снаружи параллелепипеда
        int[][] indexes = {
                {0, 1, 3, 2},
                {0, 4, 5, 1},
                {0, 2, 6, 4},
                {1, 5, 7, 3},
                {2, 3, 7, 6},
                {4, 6, 7, 5}
        };

        Polygon2D[] polygons = new Polygon2D[6];
        Point3D view = new Point3D(0.0, 0.0, -1.0);
        for (int i = 0; i < polygons.length; i++) {
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            if (backFaceCulling) {
                Point3D edge1 = Point3D.subtract(vertices[indexes[i][1]], vertices[indexes[i][0]]);
                Point3D edge2 = Point3D.subtract(vertices[indexes[i][2]], vertices[indexes[i][1]]);
                // получаем нормаль к грани
                Point3D normal = Point3D.crossProduct(edge1, edge2);
                // рисуем грань, если (n, v) > 0
                if (Point3D.dotProduct(normal, view) > 0) {
                    for (int j = 0; j < 4; j++) {
                        xPoints[j] = (int) (scale * vertices[indexes[i][j]].getX()) + xShift;
                        yPoints[j] = (int) (scale * vertices[indexes[i][j]].getY()) + yShift;
                    }

                    polygons[i] = new Polygon2D(xPoints, yPoints);
                    polygons[i].fill(image, faceColor, Polygon2D.FillType.EO);
                    polygons[i].draw(image, edgeColor);
                }
            }
            else {
                for (int j = 0; j < 4; j++) {
                    xPoints[j] = (int) (scale * vertices[indexes[i][j]].getX()) + xShift;
                    yPoints[j] = (int) (scale * vertices[indexes[i][j]].getY()) + yShift;
                }

                polygons[i] = new Polygon2D(xPoints, yPoints);
                polygons[i].fill(image, faceColor, Polygon2D.FillType.EO);
            }
        }

        // если не отбрасыватся нелицевые грани, удобнее осуществлять отрисовку ребер отдельно уже после отрисовки граней
        if (!backFaceCulling) {
            for (int i = 0; i < polygons.length; i++) {
                polygons[i].draw(image, edgeColor);
            }
        }
    }

    public void drawPerspective(BufferedImage image, int scale, int xShift, int yShift, int edgeColor, int faceColor, double k, boolean backFaceCulling) {
        Point3D[] vertices = getVertices();

        Point3D[] verticesProjected = new Point3D[8];
        for (int i = 0; i < vertices.length; i++) {
            verticesProjected[i] = vertices[i].projectPerspective(k);
        }

        // отмечаем индексы вершин для каждой грани с обходом против часовой при виде снаружи параллелепипеда
        int[][] indexes = {
                {0, 1, 3, 2},
                {0, 4, 5, 1},
                {0, 2, 6, 4},
                {1, 5, 7, 3},
                {2, 3, 7, 6},
                {4, 6, 7, 5}
        };

        Polygon2D[] polygons = new Polygon2D[6];
        Point3D view = new Point3D(0.0, 0.0, -1.0);
        for (int i = 0; i < polygons.length; i++) {
            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            if (backFaceCulling) {
                Point3D edge1 = Point3D.subtract(verticesProjected[indexes[i][1]], verticesProjected[indexes[i][0]]);
                Point3D edge2 = Point3D.subtract(verticesProjected[indexes[i][2]], verticesProjected[indexes[i][1]]);
                // получаем нормаль к грани
                Point3D normal = Point3D.crossProduct(edge1, edge2);
                // рисуем грань, если (n, v) > 0
                if (Point3D.dotProduct(normal, view) > 0) {
                     for (int j = 0; j < 4; j++) {
                         xPoints[j] = (int) (scale * verticesProjected[indexes[i][j]].getX()) + xShift;
                         yPoints[j] = (int) (scale * verticesProjected[indexes[i][j]].getY()) + yShift;
                     }

                    polygons[i] = new Polygon2D(xPoints, yPoints);
                    polygons[i].fill(image, faceColor, Polygon2D.FillType.EO);
                    polygons[i].draw(image, edgeColor);
                }
            }
            else {
                for (int j = 0; j < 4; j++) {
                    xPoints[j] = (int) (scale * verticesProjected[indexes[i][j]].getX()) + xShift;
                    yPoints[j] = (int) (scale * verticesProjected[indexes[i][j]].getY()) + yShift;
                }

                polygons[i] = new Polygon2D(xPoints, yPoints);
                polygons[i].fill(image, faceColor, Polygon2D.FillType.EO);
            }
        }

        // если не отбрасыватся нелицевые грани, удобнее осуществлять отрисовку ребер отдельно уже после отрисовки граней
        if (!backFaceCulling) {
            for (int i = 0; i < polygons.length; i++) {
                polygons[i].draw(image, edgeColor);
            }
        }
    }
}
