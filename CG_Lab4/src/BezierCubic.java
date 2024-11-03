import java.awt.image.BufferedImage;

public class BezierCubic {
    private final int[] P0;
    private final int[] P1;
    private final int[] P2;
    private final int[] P3;

    public int[] getP0() { return P0; }

    public int[] getP1() { return P1; }

    public int[] getP2() { return P2; }

    public int[] getP3() { return P3; }

    public BezierCubic(int[] P0, int[] P1, int[] P2, int[] P3) {
        if (P0.length != 2 || P1.length != 2 || P2.length != 2 || P3.length != 2)
            throw new IllegalArgumentException("Вершины кривой должны быть двумерными массивами.");

        this.P0 = P0.clone();
        this.P1 = P1.clone();
        this.P2 = P2.clone();
        this.P3 = P3.clone();
    }

    private int dist(int x, int y) {
        return Math.abs(x) + Math.abs(y);
    }

    public int[] getPoint(double t) {
        int x = (int) Math.round((1 - t) * (1 - t) * (1 - t) * P0[0] + 3 * t * (1 - t) * (1 - t) * P1[0] + 3 * t * t * (1 - t) * P2[0] + t * t * t * P3[0]);
        int y = (int) Math.round((1 - t) * (1 - t) * (1 - t) * P0[1] + 3 * t * (1 - t) * (1 - t) * P1[1] + 3 * t * t * (1 - t) * P2[1] + t * t * t * P3[1]);
        return new int[]{x, y};
    }

    public void draw(BufferedImage image, int color) {
        int dist1 = dist(P0[0] - 2 * P1[0] + P2[0], P0[1] - 2 * P1[1] + P2[1]);
        int dist2 = dist(P1[0] - 2 * P2[0] + P3[0], P1[1] - 2 * P2[1] + P3[1]);
        int H = Math.max(dist1, dist2);
        int N = 1 + (int) Math.sqrt(H);

        double t1, t2;
        for (int i = 0; i < N; i++) {
            t1 = (double) i / N;
            t2 = (double) (i + 1) / N;

            int[] point1 = getPoint(t1), point2 = getPoint(t2);
            Line line = new Line(point1[0], point1[1], point2[0], point2[1]);
            line.draw(image, color);
        }
    }
}
