import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

        int red1 = 0, green1 = 0, blue1 = 0, alpha1 = 255;
        int color1 = (alpha1 << 24) | (red1 << 16) | (green1 << 8) | blue1;
        int red2 = 0, green2 = 0, blue2 = 255, alpha2 = 255;
        int color2 = (alpha2 << 24) | (red2 << 16) | (green2 << 8) | blue2;

        Line line1 = new Line(10, 15, 50, 75);
        Line line2 = new Line(50, 85, 10, 25);  // отраженный отрезок
        line1.draw(image1, color1);
        line2.draw(image1, color1);

        Line line3 = new Line(30, 20, 90, 21);
        Line line4 = new Line(90, 24, 30, 23);  // отраженный отрезок
        line3.draw(image1, color2);
        line4.draw(image1, color2);

        ImgShow.displayImage(image1);
        ImgExport.exportImage(image1, "src\\outputs\\lines.png");


        BufferedImage image2 = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);

        int[] xPoints = {100, 500, 200, 700, 300, 700, 200, 600};
        int[] yPoints = {200, 200, 500, 550, 150, 150, 700, 700};
        Polygon poly = new Polygon(xPoints, yPoints);
        poly.draw(image2, color1);
        System.out.println("Полигон выпуклый: " + poly.isConvex());
        System.out.println("Полигон простой: " + poly.isSimple());
        ImgShow.displayImage(image2);
        ImgExport.exportImage(image2, "src\\outputs\\polygon.png");


        BufferedImage image3 = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        poly.fill(image3, color2, Polygon.FillType.EO);
        ImgShow.displayImage(image3);
        ImgExport.exportImage(image3, "src\\outputs\\polygonFilledEO.png");

        BufferedImage image4 = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        poly.fill(image4, color2, Polygon.FillType.NZW);
        ImgShow.displayImage(image4);
        ImgExport.exportImage(image4, "src\\outputs\\polygonFilledNZW.png");
    }
}