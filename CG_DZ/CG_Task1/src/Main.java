import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        int red1 = 0, green1 = 0, blue1 = 0, alpha1 = 255;
        int color1 = (alpha1 << 24) | (red1 << 16) | (green1 << 8) | blue1;
        int red2 = 0, green2 = 192, blue2 = 0, alpha2 = 255;
        int color2 = (alpha2 << 24) | (red2 << 16) | (green2 << 8) | blue2;
        int red3 = 255, green3 = 0, blue3 = 0, alpha3 = 255;
        int color3 = (alpha3 << 24) | (red3 << 16) | (green3 << 8) | blue3;
        int red4 = 0, green4 = 0, blue4 = 255, alpha4 = 128;
        int color4 = (alpha4 << 24) | (red4 << 16) | (green4 << 8) | blue4;

        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_ARGB);

        int[] xPointsClip = {100, 500, 200, 700, 300, 700, 200, 600};
        int[] yPointsClip = {200, 200, 500, 550, 150, 150, 700, 700};
        Polygon clipPolygon = new Polygon(xPointsClip, yPointsClip);

        Polygon.FillType type = Polygon.FillType.EO;

        clipPolygon.fill(image, color4, type);
        clipPolygon.draw(image, color1);

        Line line1 = new Line(50, 300, 700, 350);
        line1.draw(image, color2);

        Line[] clippedLines1 = line1.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines1.length; i++) {
            clippedLines1[i].draw(image, color3);
        }

        Line line2 = new Line(200, 150, 800, 150);
        line2.draw(image, color2);

        Line[] clippedLines2 = line2.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines2.length; i++) {
            clippedLines2[i].draw(image, color3);
        }

        Line line3 = new Line(560, 410, 800, 650);
        line3.draw(image, color2);

        Line[] clippedLines3 = line3.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines3.length; i++) {
            clippedLines3[i].draw(image, color3);
        }

        Line line4 = new Line(425, 100, 375, 800);
        line4.draw(image, color2);

        Line[] clippedLines4 = line4.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines4.length; i++) {
            clippedLines4[i].draw(image, color3);
        }

        Line line5 = new Line(50, 475, 700, 450);
        line5.draw(image, color2);

        Line[] clippedLines5 = line5.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines5.length; i++) {
            clippedLines5[i].draw(image, color3);
        }

        Line line6 = new Line(350, 250, 550, 275);
        line6.draw(image, color2);

        Line[] clippedLines6 = line6.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines6.length; i++) {
            clippedLines6[i].draw(image, color3);
        }

        Line line7 = new Line(200, 250, 300, 300);
        line7.draw(image, color2);

        Line[] clippedLines7 = line7.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines7.length; i++) {
            clippedLines7[i].draw(image, color3);
        }

        Line line8 = new Line(700, 800, 800, 450);
        line8.draw(image, color2);

        Line[] clippedLines8 = line8.clip(clipPolygon, type);
        for (int i = 0; i < clippedLines8.length; i++) {
            clippedLines8[i].draw(image, color3);
        }

        ImgShow.displayImage(image);
        ImgExport.exportImage(image, "src\\outputs\\linesClipped" + type + ".png");
    }
}