import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        int red1 = 0, green1 = 0, blue1 = 0, alpha1 = 255;
        int color1 = (alpha1 << 24) | (red1 << 16) | (green1 << 8) | blue1;
        int red12 = 0, green12 = 0, blue12 = 0, alpha12 = 64;
        int color12 = (alpha12 << 24) | (red12 << 16) | (green12 << 8) | blue12;
        int red2 = 255, green2 = 0, blue2 = 0, alpha2 = 255;
        int color2 = (alpha2 << 24) | (red2 << 16) | (green2 << 8) | blue2;
        int red22 = 255, green22 = 0, blue22 = 0, alpha22 = 64;
        int color22 = (alpha22 << 24) | (red22 << 16) | (green22 << 8) | blue22;
        int red3 = 0, green3 = 0, blue3 = 255, alpha3 = 255;
        int color3 = (alpha3 << 24) | (red3 << 16) | (green3 << 8) | blue3;
        int red32 = 0, green32 = 0, blue32 = 255, alpha32 = 64;
        int color32 = (alpha32 << 24) | (red32 << 16) | (green32 << 8) | blue32;

        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);

        Point[] points1 = {
                new Point(50, 250),
                new Point(125, 75),
                new Point(250, 75),
                new Point(325, 150),
                new Point(200, 325),
                new Point(150, 400)
        };

        for (int i = 0; i < points1.length - 1; i++) {
            Line line = new Line(points1[i], points1[i + 1]);
            line.draw(image, color12);
        }

        CatmullRom catmullRom1 = new CatmullRom(points1);
        catmullRom1.draw(image, color1);

        for (int i = 0; i < points1.length; i++) {
            points1[i].draw(image, color1, 3.0);
        }

        Point[] points2 = {
                new Point(725, 725),
                new Point(325, 550),
                new Point(475, 475),
                new Point(325, 475),
                new Point(475, 650),
                new Point(75, 725)
        };

        for (int i = 0; i < points2.length - 1; i++) {
            Line line = new Line(points2[i], points2[i + 1]);
            line.draw(image, color22);
        }

        CatmullRom catmullRom2 = new CatmullRom(points2);
        catmullRom2.draw(image, color2);

        for (int i = 0; i < points2.length; i++) {
            points2[i].draw(image, color2, 3.0);
        }

        Point[] points3 = {
                new Point(450, 350),
                new Point(540, 140),
                new Point(650, 50),
                new Point(750, 100),
                new Point(700, 250),
                new Point(600, 240),
                new Point(650, 150)
        };

        for (int i = 0; i < points3.length - 1; i++) {
            Line line = new Line(points3[i], points3[i + 1]);
            line.draw(image, color32);
        }

        CatmullRom catmullRom3 = new CatmullRom(points3);
        catmullRom3.draw(image, color3);

        for (int i = 0; i < points3.length; i++) {
            points3[i].draw(image, color3, 3.0);
        }

        ImgShow.displayImage(image);
        ImgExport.exportImage(image, "src\\outputs\\catmullRom.png");
    }
}