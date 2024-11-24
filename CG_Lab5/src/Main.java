import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {

        int red1 = 0, green1 = 0, blue1 = 0, alpha1 = 255;
        int edgeColor = (alpha1 << 24) | (red1 << 16) | (green1 << 8) | blue1;
        int red2 = 255, green2 = 255, blue2 = 255, alpha2 = 255;
        int faceColor = (alpha2 << 24) | (red2 << 16) | (green2 << 8) | blue2;

        double xSize = 1.25, ySize = 1.0, zSize = 0.75;
        Point3D center = new Point3D(0.0, 0.0, 0.0);
        Point3D axis = new Point3D(1.0, 1.5, 1.0);
        double phi = 0.0;
        Parallelepiped parallelepiped = new Parallelepiped(xSize, ySize, zSize, center, axis, phi);

//        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
//
//        phi = Math.PI / 4;
//        parallelepiped.setPhi(phi);
//        // parallelepiped.drawParallel(image, 250, 300, 300, edgeColor, faceColor, true);
//        parallelepiped.drawPerspective(image, 250, 300, 300, edgeColor, faceColor, 2.0, false);
//
//        ImgExport.exportImage(image, "src\\outputs\\perspectiveBackFaceCulling.png");

        int frames = 200;
        for (int t = 0; t < frames; t++) {
            BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

            phi = 2.0 * Math.PI * t / frames;
            parallelepiped.setPhi(phi);
            // parallelepiped.drawParallel(image, 250, 300, 300, edgeColor, faceColor, true);
            parallelepiped.drawPerspective(image, 250, 300, 300, edgeColor, faceColor, 2.0, true);

            ImgExport.exportImage(image, "src\\outputs\\perspectiveBackFaceCulling\\" + t + ".png");
        }
    }
}