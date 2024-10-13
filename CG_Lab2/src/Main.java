import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat img = Imgcodecs.imread("src\\inputs\\Cats.png");

        BufferedImage image = ImgUtils.matToBufferedImage(img);
        BufferedImage imageDithered = ImgUtils.floydSteinbergDithering(image, 2, true);
        ImgShow.displayImage(imageDithered);
        ImgExport.exportImage(imageDithered, "src\\outputs\\Cats_floydSteinberg_switchedDirections_2bpp.png");
    }
}