public class Point3D {
    private final double x;
    private final double y;
    private final double z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public Point3D copy() {
        return new Point3D(x, y, z);
    }

    public static Point3D add(Point3D point1, Point3D point2) {
        return new Point3D(point1.x + point2.x, point1.y + point2.y, point1.z + point2.z);
    }

    public static Point3D subtract(Point3D point1, Point3D point2) {
        return new Point3D(point1.x - point2.x, point1.y - point2.y, point1.z - point2.z);
    }

    public static Point3D multiply(Point3D point, double scalar) {
        return new Point3D(scalar * point.x, scalar * point.y, scalar * point.z);
    }

    public static double dotProduct(Point3D point1, Point3D point2) {
        return point1.x * point2.x + point1.y * point2.y + point1.z * point2.z;
    }

    public static Point3D crossProduct(Point3D point1, Point3D point2) {
        double x = point1.y * point2.z - point1.z * point2.y;
        double y = point1.z * point2.x - point1.x * point2.z;
        double z = point1.x * point2.y - point1.y * point2.x;
        return new Point3D(x, y, z);
    }

    private double[] homVector() {
        return new double[] {x, y, z, 1.0};
    }

    public Point3D transform(double[][] matrix) {
        if (matrix.length != 4) {
            throw new IllegalArgumentException("Матрица преобразования должна быть матрицей 4x4.");
        }
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length != 4) {
                throw new IllegalArgumentException("Матрица преобразования должна быть матрицей 4x4.");
            }
        }

        double[] oldHomVector = homVector();
        double[] newHomVector = new double[4];

        for (int i = 0; i < 4; i++) {
            newHomVector[i] = 0.0;
            for (int j = 0; j < 4; j++) {
                newHomVector[i] += oldHomVector[j] * matrix[j][i];
            }
        }

        return new Point3D(newHomVector[0] / newHomVector[3], newHomVector[1] / newHomVector[3], newHomVector[2] / newHomVector[3]);
    }

    public Point3D projectPerspective(double k) {
        double[][] matrix = {
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, 1.0 / k},
                {0.0, 0.0, 0.0, 1.0}
        };

        return transform(matrix);
    }

    public Point3D rotate(Point3D axis, double phi) {
        double xm = axis.x, ym = axis.y, zm = axis.z;
        double rm = Math.sqrt(xm * xm + ym * ym + zm * zm);
        double nx = xm / rm, ny = ym / rm, nz = zm / rm;
        double cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);

        double[][] matrix = {
                {cosPhi + nx * nx * (1.0 - cosPhi),         nx * ny * (1.0 - cosPhi) + nz * sinPhi,     nx * nz * (1.0 - cosPhi) - ny * sinPhi,     0.0},
                {nx * ny * (1.0 - cosPhi) - nz * sinPhi,    cosPhi + ny * ny * (1.0 - cosPhi),          ny * nz * (1.0 - cosPhi) + nx * sinPhi,     0.0},
                {nx * nz * (1.0 - cosPhi) + ny * sinPhi,    ny * nz * (1.0 - cosPhi) - nx * sinPhi,     cosPhi + nz * nz * (1.0 - cosPhi),          0.0},
                {0.0,                                       0.0,                                        0.0,                                        1.0}
        };

        return transform(matrix);
    }
}
