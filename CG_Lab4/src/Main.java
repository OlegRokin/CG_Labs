import java.awt.image.BufferedImage;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        int red1 = 0, green1 = 0, blue1 = 0, alpha1 = 255;
        int color1 = (alpha1 << 24) | (red1 << 16) | (green1 << 8) | blue1;
        int red2 = 0, green2 = 192, blue2 = 0, alpha2 = 255;
        int color2 = (alpha2 << 24) | (red2 << 16) | (green2 << 8) | blue2;
        int red3 = 255, green3 = 0, blue3 = 0, alpha3 = 255;
        int color3 = (alpha3 << 24) | (red3 << 16) | (green3 << 8) | blue3;

        BufferedImage image1 = new BufferedImage(450, 225, BufferedImage.TYPE_INT_ARGB);

        int[] xPointsClip = {50, 200, 350, 300, 200};
        int[] yPointsClip = {125, 25, 50, 175, 200};
        Polygon clipPolygon = new Polygon(xPointsClip, yPointsClip, true);
        clipPolygon.draw(image1, color1);

        Line line1 = new Line(100, 50, 275, 200);
        line1.draw(image1, color2);

        Optional<Line> maybeClippedLine1 = line1.clip(clipPolygon);
        if (maybeClippedLine1.isPresent()) {
            Line lineClipped1 = maybeClippedLine1.get();
            lineClipped1.draw(image1, color3);
        }

        Line line2 = new Line(300, 25, 250, 125);
        line2.draw(image1, color2);

        Optional<Line> maybeClippedLine2 = line2.clip(clipPolygon);
        if (maybeClippedLine2.isPresent()) {
            Line lineClipped2 = maybeClippedLine2.get();
            lineClipped2.draw(image1, color3);
        }

        Line line3 = new Line(200, 50, 250, 75);
        line3.draw(image1, color2);

        Optional<Line> maybeClippedLine3 = line3.clip(clipPolygon);
        if (maybeClippedLine3.isPresent()) {
            Line lineClipped3 = maybeClippedLine3.get();
            lineClipped3.draw(image1, color3);
        }

        Line line4 = new Line(400, 50, 425, 200);
        line4.draw(image1, color2);

        Optional<Line> maybeClippedLine4 = line4.clip(clipPolygon);
        if (maybeClippedLine4.isPresent()) {
            Line lineClipped4 = maybeClippedLine4.get();
            lineClipped4.draw(image1, color3);
        }

        ImgShow.displayImage(image1);
        ImgExport.exportImage(image1, "src\\outputs\\lineClippings.png");


        BufferedImage image2 = new BufferedImage(450, 225, BufferedImage.TYPE_INT_ARGB);

        clipPolygon.draw(image2, color1);

        int[] xPoints1 = {125, 100, 175, 275};
        int[] yPoints1 = {25, 200, 150, 200};
        Polygon polygon1 = new Polygon(xPoints1, yPoints1, true);
        polygon1.draw(image2, color2);

        Optional<Polygon> maybeClippedPolygon1 = polygon1.clip(clipPolygon);
        if (maybeClippedPolygon1.isPresent()) {
            Polygon polygonClipped1 = maybeClippedPolygon1.get();
            polygonClipped1.fill(image2, color3, Polygon.FillType.EO);
        }

        int[] xPoints2 = {275, 425, 400, 250, 350};
        int[] yPoints2 = {25, 50, 100, 100, 75};
        Polygon polygon2 = new Polygon(xPoints2, yPoints2, true);
        polygon2.draw(image2, color2);

        Optional<Polygon> maybeClippedPolygon2 = polygon2.clip(clipPolygon);
        if (maybeClippedPolygon2.isPresent()) {
            Polygon polygonClipped2 = maybeClippedPolygon2.get();
            polygonClipped2.fill(image2, color3, Polygon.FillType.EO);
        }

        int[] xPoints3 = {200, 225, 250};
        int[] yPoints3 = {75, 50, 75};
        Polygon polygon3 = new Polygon(xPoints3, yPoints3, true);
        polygon3.draw(image2, color2);

        Optional<Polygon> maybeClippedPolygon3 = polygon3.clip(clipPolygon);
        if (maybeClippedPolygon3.isPresent()) {
            Polygon polygonClipped3 = maybeClippedPolygon3.get();
            polygonClipped3.fill(image2, color3, Polygon.FillType.EO);
        }

        int[] xPoints4 = {325, 375, 425};
        int[] yPoints4 = {200, 125, 150};
        Polygon polygon4 = new Polygon(xPoints4, yPoints4, true);
        polygon4.draw(image2, color2);

        Optional<Polygon> maybeClippedPolygon4 = polygon4.clip(clipPolygon);
        if (maybeClippedPolygon4.isPresent()) {
            Polygon polygonClipped4 = maybeClippedPolygon4.get();
            polygonClipped4.fill(image2, color3, Polygon.FillType.EO);
        }

        ImgShow.displayImage(image2);
        ImgExport.exportImage(image2, "src\\outputs\\polygonClippings.png");


        BufferedImage image3 = new BufferedImage(450, 225, BufferedImage.TYPE_INT_ARGB);

        int[] P10 = {25, 175}, P11 = {75, 25}, P12 = {125, 50}, P13 = {150, 150};
        Line bezierLine11 = new Line(P10[0], P10[1], P11[0], P11[1]);
        Line bezierLine12 = new Line(P11[0], P11[1], P12[0], P12[1]);
        Line bezierLine13 = new Line(P12[0], P12[1], P13[0], P13[1]);
        BezierCubic bezierCubic1 = new BezierCubic(P10, P11, P12, P13);

        bezierLine11.draw(image3, color1);
        bezierLine12.draw(image3, color1);
        bezierLine13.draw(image3, color1);
        bezierCubic1.draw(image3, color3);

        int[] P20 = {175, 175}, P21 = {250, 50}, P22 = {275, 200}, P23 = {325, 25};
        Line bezierLine21 = new Line(P20[0], P20[1], P21[0], P21[1]);
        Line bezierLine22 = new Line(P21[0], P21[1], P22[0], P22[1]);
        Line bezierLine23 = new Line(P22[0], P22[1], P23[0], P23[1]);
        BezierCubic bezierCubic2 = new BezierCubic(P20, P21, P22, P23);

        bezierLine21.draw(image3, color1);
        bezierLine22.draw(image3, color1);
        bezierLine23.draw(image3, color1);
        bezierCubic2.draw(image3, color3);

        int[] P30 = {350, 75}, P31 = {400, 200}, P32 = {425, 50}, P33 = {325, 175};
        Line bezierLine31 = new Line(P30[0], P30[1], P31[0], P31[1]);
        Line bezierLine32 = new Line(P31[0], P31[1], P32[0], P32[1]);
        Line bezierLine33 = new Line(P32[0], P32[1], P33[0], P33[1]);
        BezierCubic bezierCubic3 = new BezierCubic(P30, P31, P32, P33);

        bezierLine31.draw(image3, color1);
        bezierLine32.draw(image3, color1);
        bezierLine33.draw(image3, color1);
        bezierCubic3.draw(image3, color3);

        ImgShow.displayImage(image3);
        ImgExport.exportImage(image3, "src\\outputs\\bezierCubic.png");
    }
}