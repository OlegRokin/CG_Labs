import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        // Задание 1
        Mat img = Imgcodecs.imread("src\\inputs\\CG_Lecture 3 (2024)_page1_image(2).png");

        BufferedImage image = ImgUtils.matToBufferedImage(img);
        image = ImgUtils.transposeImage(image);
        image = ImgUtils.meanGrayscale(image);
        image = ImgUtils.applyCircularMask(image);
        ImgShow.displayImage(image);
        ImgExport.exportImage(image, "src\\outputs\\img1.png");


        // Задание 2
        Mat img1 = Imgcodecs.imread("src\\inputs\\CG_Lecture 3 (2024)_page1_image(2).png");
        Mat img2 = Imgcodecs.imread("src\\inputs\\CG_Lecture 3 (2024)_page1_image(1).png");
        Mat imgAlpha1 = Imgcodecs.imread("src\\inputs\\CG_Lecture 3 (2024)_page2_image.png");

        BufferedImage image1 = ImgUtils.matToBufferedImage(img1);
        BufferedImage image2 = ImgUtils.matToBufferedImage(img2);
        BufferedImage imageAlpha = ImgUtils.matToBufferedImage(imgAlpha1);
        imageAlpha = ImgUtils.meanGrayscale(imageAlpha);
        // BufferedImage imageAlpha2 = ImgUtils.mirrorImageHorizontally(imageAlpha);

        BufferedImage blendedImage = ImgUtils.blendImages(image1, image2, imageAlpha);
        // Ниже примеры обобщённого смешивания
        // BufferedImage blendedImage = ImgUtils.generalBlendImages(image1, image2, 1, 1, ImgUtils::softLightBlending);
        // BufferedImage blendedImage = ImgUtils.generalBlendImages(image1, image2, imageAlpha, imageAlpha2, ImgUtils::softLightBlending);
        ImgExport.exportImage(blendedImage, "src\\outputs\\img2.png");

        ImgShow.displayImage(blendedImage);
    }
}