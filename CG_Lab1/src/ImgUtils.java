import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
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

    public static BufferedImage mirrorImageHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getRaster().getNumDataElements();

        BufferedImage mirroredImage = new BufferedImage(width, height, image.getType());
        byte[] originalPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] mirroredPixels = ((DataBufferByte) mirroredImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalIndex = (y * width + x) * channels;
                int mirroredIndex = (y * width + (width - 1 - x)) * channels;

                for (int c = 0; c < channels; c++) {
                    mirroredPixels[mirroredIndex + c] = originalPixels[originalIndex + c];
                }
            }
        }

        return mirroredImage;
    }

    public static BufferedImage mirrorImageVertically(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getRaster().getNumDataElements();

        BufferedImage mirroredImage = new BufferedImage(width, height, image.getType());
        byte[] originalPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] mirroredPixels = ((DataBufferByte) mirroredImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalIndex = (y * width + x) * channels;
                int mirroredIndex = ((height - 1 - y) * width + x) * channels;

                for (int c = 0; c < channels; c++) {
                    mirroredPixels[mirroredIndex + c] = originalPixels[originalIndex + c];
                }
            }
        }

        return mirroredImage;
    }

    public static BufferedImage transposeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getRaster().getNumDataElements();

        BufferedImage transposedImage = new BufferedImage(height, width, image.getType());
        byte[] originalPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] transposedPixels  = ((DataBufferByte) transposedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalIndex = (y * width + x) * channels;
                int transposedIndex = (x * height + y) * channels;

                for (int c = 0; c < channels; c++) {
                    transposedPixels[transposedIndex + c] = originalPixels[originalIndex + c];
                }
            }
        }

        return transposedImage;
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

    public static BufferedImage applyCircularMask(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getRaster().getNumDataElements();

        BufferedImage maskedImage = new BufferedImage(width, height, image.getType());
        byte[] originalPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] maskedPixels = ((DataBufferByte) maskedImage.getRaster().getDataBuffer()).getData();

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;

                int dx = x - centerX;
                int dy = y - centerY;
                if (dx * dx + dy * dy <= radius * radius) {
                    for (int c = 0; c < channels; c++) {
                        maskedPixels[index + c] = originalPixels[index + c];
                    }
                } else {
                    for (int c = 0; c < channels; c++) {
                        maskedPixels[index + c] = 0;
                    }
                }
            }
        }

        return maskedImage;
    }

    @FunctionalInterface
    public interface BlendFunction {
        double apply(double value1, double value2);
    }

    public static double normalBlending(double value1, double value2) {
        return value2;
    }

    public static double multiplyBlending(double value1, double value2) {
        return value1 * value2;
    }

    public static double screenBlending(double value1, double value2) {
        return 1 - (1 - value1) * (1 - value2);
    }

    public static double darkenBlending(double value1, double value2) {
        return Math.min(value1, value2);
    }

    public static double lightenBlending(double value1, double value2) {
        return Math.max(value1, value2);
    }

    public static double differenceBlending(double value1, double value2) {
        return Math.abs(value1 - value2);
    }

    public static double colorDodgeBlending(double value1, double value2) {
        if (value2 < 1) return Math.min(1, value1 / (1 - value2));
        else return 1;
    }

    public static double colorBurnBlending(double value1, double value2) {
        if (value2 > 0) return 1 - Math.min(1, (1 - value1) / value2);
        else return 0;
    }

    public static double D(double x) {
        if (x <= 0.25) return ((16 * x - 12) * x + 4) * x;
        else return Math.sqrt(x);
    }

    public static double softLightBlending(double value1, double value2) {
        if (value2 <= 0.5) return value1 - (1 - 2 * value2) * value1 * (1 - value1);
        else return value1 + (2 * value2 - 1) * (D(value1) - value1);
    }

    public static BufferedImage blendImages(BufferedImage img1, BufferedImage img2, BufferedImage imgAlpha) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int channels = img1.getRaster().getNumDataElements();

        if (width != img2.getWidth() || height != img2.getHeight() || channels != img2.getRaster().getNumDataElements() || width != imgAlpha.getWidth() || height != imgAlpha.getHeight()) {
            throw new IllegalArgumentException("Изображения должны иметь одинаковые размеры");
        }
        if (imgAlpha.getRaster().getNumDataElements() != 1 || imgAlpha.getRaster().getNumDataElements() != 1) {
            throw new IllegalArgumentException("Альфа-изображения должны иметь один канал");
        }

        BufferedImage blendedImage = new BufferedImage(width, height, img1.getType());
        byte[] pixels1 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
        byte[] pixels2 = ((DataBufferByte) img2.getRaster().getDataBuffer()).getData();
        byte[] pixelsAlpha = ((DataBufferByte) imgAlpha.getRaster().getDataBuffer()).getData();
        byte[] blendedPixels = ((DataBufferByte) blendedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;
                int indexAlpha = y * width + x;

                double alpha = (double) (pixelsAlpha[indexAlpha] & 0xFF) / 255;

                for (int c = 0; c < channels; c++) {
                    double value1 = (double) (pixels1[index + c] & 0xFF) / 255;
                    double value2 = (double) (pixels2[index + c] & 0xFF) / 255;
                    double blendedValue = (1 - alpha) * value1 + alpha * value2;

                    blendedPixels[index + c] = (byte) (blendedValue * 255);
                }
            }
        }

        return blendedImage;
    }

    public static BufferedImage blendImages(BufferedImage img1, BufferedImage img2, double alpha) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int channels = img1.getRaster().getNumDataElements();

        if (width != img2.getWidth() || height != img2.getHeight() || channels != img2.getRaster().getNumDataElements()) {
            throw new IllegalArgumentException("Изображения должны иметь одинаковые размеры");
        }

        BufferedImage blendedImage = new BufferedImage(width, height, img1.getType());
        byte[] pixels1 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
        byte[] pixels2 = ((DataBufferByte) img2.getRaster().getDataBuffer()).getData();
        byte[] blendedPixels = ((DataBufferByte) blendedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;

                for (int c = 0; c < channels; c++) {
                    double value1 = (double) (pixels1[index + c] & 0xFF) / 255;
                    double value2 = (double) (pixels2[index + c] & 0xFF) / 255;
                    double blendedValue = (1 - alpha) * value1 + alpha * value2;

                    blendedPixels[index + c] = (byte) (blendedValue * 255);
                }
            }
        }

        return blendedImage;
    }

    public static BufferedImage generalBlendImages(BufferedImage img1, BufferedImage img2, BufferedImage imgAlpha1, BufferedImage imgAlpha2, BlendFunction blendingFunction) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int channels = img1.getRaster().getNumDataElements();

        if (width != img2.getWidth() || height != img2.getHeight() || channels != img2.getRaster().getNumDataElements() || width != imgAlpha1.getWidth() || height != imgAlpha1.getHeight() || width != imgAlpha2.getWidth() || height != imgAlpha2.getHeight()) {
            throw new IllegalArgumentException("Изображения должны иметь одинаковые размеры");
        }
        if (imgAlpha1.getRaster().getNumDataElements() != 1 || imgAlpha2.getRaster().getNumDataElements() != 1) {
            throw new IllegalArgumentException("Альфа-изображения должны иметь один канал");
        }

        BufferedImage blendedImage = new BufferedImage(width, height, img1.getType());
        byte[] pixels1 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
        byte[] pixels2 = ((DataBufferByte) img2.getRaster().getDataBuffer()).getData();
        byte[] pixelsAlpha1 = ((DataBufferByte) imgAlpha1.getRaster().getDataBuffer()).getData();
        byte[] pixelsAlpha2 = ((DataBufferByte) imgAlpha2.getRaster().getDataBuffer()).getData();
        byte[] blendedPixels = ((DataBufferByte) blendedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;
                int indexAlpha = y * width + x;

                double alpha1 = (double) (pixelsAlpha1[indexAlpha] & 0xFF) / 255;
                double alpha2 = (double) (pixelsAlpha2[indexAlpha] & 0xFF) / 255;

                for (int c = 0; c < channels; c++) {
                    double value1 = (double) (pixels1[index + c] & 0xFF) / 255;
                    double value2 = (double) (pixels2[index + c] & 0xFF) / 255;
                    double blendedValue = (1 - alpha2) * alpha1 * value1 + (1 - alpha1) * alpha2 * value2 + alpha1 * alpha2 * blendingFunction.apply(value1, value2);

                    blendedPixels[index + c] = (byte) (blendedValue * 255);
                }
            }
        }

        return blendedImage;
    }

    public static BufferedImage generalBlendImages(BufferedImage img1, BufferedImage img2, double alpha1, double alpha2, BlendFunction blendingFunction) {
        if (alpha1 < 0 || alpha1 > 1 || alpha2 < 0 || alpha2 > 1) {
            throw new IllegalArgumentException("Параметры alpha1 и alpha2 должны быть на отрезке от 0 до 1.");
        }
        int width = img1.getWidth();
        int height = img1.getHeight();
        int channels = img1.getRaster().getNumDataElements();

        if (width != img2.getWidth() || height != img2.getHeight() || channels != img2.getRaster().getNumDataElements()) {
            throw new IllegalArgumentException("Изображения должны иметь одинаковые размеры");
        }

        BufferedImage blendedImage = new BufferedImage(width, height, img1.getType());
        byte[] pixels1 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
        byte[] pixels2 = ((DataBufferByte) img2.getRaster().getDataBuffer()).getData();
        byte[] blendedPixels = ((DataBufferByte) blendedImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;

                for (int c = 0; c < channels; c++) {
                    double value1 = (double) (pixels1[index + c] & 0xFF) / 255;
                    double value2 = (double) (pixels2[index + c] & 0xFF) / 255;
                    double blendedValue = (1 - alpha2) * alpha1 * value1 + (1 - alpha1) * alpha2 * value2 + alpha1 * alpha2 * blendingFunction.apply(value1, value2);

                    blendedPixels[index + c] = (byte) (blendedValue * 255);
                }
            }
        }

        return blendedImage;
    }
}