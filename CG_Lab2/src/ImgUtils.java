import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;

public class ImgUtils {
    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        // Копирование данных из mat в массив
        mat.get(0, 0, data);
        return image;
    }

    public static BufferedImage meanGrayscale(BufferedImage colorImage) {
        int width = colorImage.getWidth();
        int height = colorImage.getHeight();
        int channels = colorImage.getRaster().getNumDataElements();

        if (channels != 3) {
            throw new IllegalArgumentException("Входное изображение должно быть цветным (3 канала)");
        }

        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] colorPixels = ((DataBufferByte) colorImage.getRaster().getDataBuffer()).getData();
        byte[] grayPixels = ((DataBufferByte) grayscaleImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int colorIndex = (y * width + x) * channels;
                int grayIndex = y * width + x;

                int blue = colorPixels[colorIndex] & 0xFF;
                int green = colorPixels[colorIndex + 1] & 0xFF;
                int red = colorPixels[colorIndex + 2] & 0xFF;

                int grayValue = (red + green + blue) / 3;
                grayPixels[grayIndex] = (byte) grayValue;
            }
        }

        return grayscaleImage;
    }

    private static void distributeError(BufferedImage img, int x, int y, int error, double factor) {
        int pixel = img.getRaster().getSample(x, y, 0);
        int newPixel = pixel + (int) (error * factor);
        newPixel = Math.max(0, Math.min(255, newPixel));
        img.getRaster().setSample(x, y, 0, newPixel);
    }

    public static BufferedImage floydSteinbergDithering(BufferedImage image, int n, boolean switchDirections) {
        if (n > 8) {
            throw new IllegalArgumentException("Итоговое изображение должно иметь глубину не больше 8 bpp");
        }
        if (image.getColorModel().getPixelSize() != 8) {
            image = meanGrayscale(image);
        }
        if (image.getRaster().getDataBuffer().getDataType() != DataBuffer.TYPE_BYTE || image.getColorModel().getPixelSize() != 8) {
            throw new IllegalArgumentException("Входное изображение должно иметь глубину 8 bpp");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage ditheredImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRaster().getSample(x, y, 0);
                ditheredImage.getRaster().setSample(x, y, 0, pixel);
            }
        }

        int scale = (int) Math.pow(2, n) - 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int xNew = x;
                if (switchDirections && y % 2 != 0) {
                    xNew = width - 1 - x;
                }
                int oldPixel = ditheredImage.getRaster().getSample(xNew, y, 0);
                int newPixel = Math.round((float) oldPixel / 255 * scale) * (255 / scale);
                ditheredImage.getRaster().setSample(xNew, y, 0, newPixel);

                int error = oldPixel - newPixel;

                if (!switchDirections || y % 2 == 0) {
                    if (xNew + 1 < width)                      distributeError(ditheredImage, xNew + 1, y, error, 7.0 / 16);
                    if (xNew + 1 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 1, y + 1, error, 1.0 / 16);
                    if (y + 1 < height)                        distributeError(ditheredImage, xNew, y + 1, error, 5.0 / 16);
                    if (xNew - 1 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 1, y + 1, error, 3.0 / 16);
                }
                else {
                    if (xNew - 1 >= 0)                         distributeError(ditheredImage, xNew - 1, y, error, 7.0 / 16);
                    if (xNew - 1 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 1, y + 1, error, 1.0 / 16);
                    if (y + 1 < height)                        distributeError(ditheredImage, xNew, y + 1, error, 5.0 / 16);
                    if (xNew + 1 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 1, y + 1, error, 3.0 / 16);
                }
            }
        }

        return ditheredImage;
    }

    public static BufferedImage stuckiDithering(BufferedImage image, int n, boolean switchDirections) {
        if (n > 8) {
            throw new IllegalArgumentException("Итоговое изображение должно иметь глубину не больше 8 bpp");
        }
        if (image.getColorModel().getPixelSize() != 8) {
            image = meanGrayscale(image);
        }
        if (image.getRaster().getDataBuffer().getDataType() != DataBuffer.TYPE_BYTE || image.getColorModel().getPixelSize() != 8) {
            throw new IllegalArgumentException("Входное изображение должно иметь глубину 8 bpp");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage ditheredImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRaster().getSample(x, y, 0);
                ditheredImage.getRaster().setSample(x, y, 0, pixel);
            }
        }

        int scale = (int) Math.pow(2, n) - 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int xNew = x;
                if (switchDirections && y % 2 != 0) {
                    xNew = width - 1 - x;
                }
                int oldPixel = ditheredImage.getRaster().getSample(xNew, y, 0);
                int newPixel = Math.round((float) oldPixel / 255 * scale) * (255 / scale);
                ditheredImage.getRaster().setSample(xNew, y, 0, newPixel);

                int error = oldPixel - newPixel;

                if (!switchDirections || y % 2 == 0) {
                    if (xNew + 2 < width)                      distributeError(ditheredImage, xNew + 2, y, error, 4.0 / 42);
                    if (xNew + 1 < width)                      distributeError(ditheredImage, xNew + 1, y, error, 8.0 / 42);
                    if (xNew + 2 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 2, y + 1, error, 2.0 / 42);
                    if (xNew + 1 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 1, y + 1, error, 4.0 / 42);
                    if (y + 1 < height)                        distributeError(ditheredImage, xNew, y + 1, error, 8.0 / 42);
                    if (xNew - 1 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 1, y + 1, error, 4.0 / 42);
                    if (xNew - 2 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 2, y + 1, error, 2.0 / 42);
                    if (xNew + 2 < width && y + 2 < height)    distributeError(ditheredImage, xNew + 2, y + 2, error, 1.0 / 42);
                    if (xNew + 1 < width && y + 2 < height)    distributeError(ditheredImage, xNew + 1, y + 2, error, 2.0 / 42);
                    if (y + 2 < height)                        distributeError(ditheredImage, xNew, y + 2, error, 4.0 / 42);
                    if (xNew - 1 >= 0 && y + 2 < height)       distributeError(ditheredImage, xNew - 1, y + 2, error, 2.0 / 42);
                    if (xNew - 2 >= 0 && y + 2 < height)       distributeError(ditheredImage, xNew - 2, y + 2, error, 1.0 / 42);
                }
                else {
                    if (xNew - 2 >= 0)                         distributeError(ditheredImage, xNew - 2, y, error, 4.0 / 42);
                    if (xNew - 1 >= 0)                         distributeError(ditheredImage, xNew - 1, y, error, 8.0 / 42);
                    if (xNew - 2 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 2, y + 1, error, 2.0 / 42);
                    if (xNew - 1 >= 0 && y + 1 < height)       distributeError(ditheredImage, xNew - 1, y + 1, error, 4.0 / 42);
                    if (y + 1 < height)                        distributeError(ditheredImage, xNew, y + 1, error, 8.0 / 42);
                    if (xNew + 1 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 1, y + 1, error, 4.0 / 42);
                    if (xNew + 2 < width && y + 1 < height)    distributeError(ditheredImage, xNew + 2, y + 1, error, 2.0 / 42);
                    if (xNew - 2 >= 0 && y + 2 < height)       distributeError(ditheredImage, xNew - 2, y + 2, error, 1.0 / 42);
                    if (xNew - 1 >= 0 && y + 2 < height)       distributeError(ditheredImage, xNew - 1, y + 2, error, 2.0 / 42);
                    if (y + 2 < height)                        distributeError(ditheredImage, xNew, y + 2, error, 4.0 / 42);
                    if (xNew + 1 < width && y + 2 < height)    distributeError(ditheredImage, xNew + 1, y + 2, error, 2.0 / 42);
                    if (xNew + 2 < width && y + 2 < height)    distributeError(ditheredImage, xNew + 2, y + 2, error, 1.0 / 42);
                }
            }
        }

        return ditheredImage;
    }


}