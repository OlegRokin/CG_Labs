import java.awt.image.BufferedImage;

public class PixelDrawing {
    public static void setPixel(BufferedImage image, int x, int y, int color) {
        image.setRGB(x, y, color);
    }
}
