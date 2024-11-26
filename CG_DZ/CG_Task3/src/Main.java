import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Mat img = Imgcodecs.imread("src\\inputsAndOutputs\\Space.png");
        // Mat img = Imgcodecs.imread("src\\inputsAndOutputs\\Sunrise.png");
        Mat img = Imgcodecs.imread("src\\inputsAndOutputs\\BrightSky.png");

        BufferedImage image = ImgUtils.matToBufferedImage(img);
        ImgShow.displayImage(image);

        BufferedImage correctedImage = ImgUtils.gammaCorrection128(image);
        ImgShow.displayImage(correctedImage);

        // ImgExport.exportImage(correctedImage, "src\\inputsAndOutputs\\SpaceCorrected.png");
        // ImgExport.exportImage(correctedImage, "src\\inputsAndOutputs\\SunriseCorrected.png");
        ImgExport.exportImage(correctedImage, "src\\inputsAndOutputs\\BrightSkyCorrected.png");

        System.out.println(ImgUtils.meanBrightness(image));
        System.out.println(ImgUtils.meanBrightness(correctedImage));
    }
}