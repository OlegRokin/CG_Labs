import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static java.lang.Math.pow;

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

    public static BufferedImage copyImage(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int type = source.getType();
        int channels = source.getRaster().getNumDataElements();

        BufferedImage image = new BufferedImage(width, height, type);
        byte[] sourcePixels = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;

                for (int c = 0; c < channels; c++) {
                    pixels[index + c] = sourcePixels[index + c];
                }
            }
        }

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

    public static double meanBrightness(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getRaster().getNumDataElements();
        int sum = 0;

        BufferedImage grayScaleImage;
        if (channels == 3) {
            grayScaleImage = meanGrayscale(image);
        }
        else {
            grayScaleImage = copyImage(image);
        }
        byte[] grayScalePixels = ((DataBufferByte) grayScaleImage.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                int value = grayScalePixels[index] & 0xFF;
                sum += value;
            }
        }

        return (double) sum / (width * height);
    }

    public static BufferedImage gammaCorrection(BufferedImage image, double gamma) {
        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();
        int channels = image.getRaster().getNumDataElements();

        BufferedImage correctedImage = new BufferedImage(width, height, type);
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] correctedPixels = ((DataBufferByte) correctedImage.getRaster().getDataBuffer()).getData();

        // заранее создадим таблицу скорректированных значений, чтобы вместо многократных вычислений pow(value, gamma) для одних и тех же значений просто сразу брать готовое значение из таблицы
        byte[] valueLUT = new byte[256];
        for (int i = 0; i < valueLUT.length; i++) {
            valueLUT[i] = (byte) (pow((double) i / 255, 1.0 / gamma) * 255);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * channels;

                for (int c = 0; c < channels; c++) {
                    correctedPixels[index + c] = valueLUT[pixels[index + c] & 0xFF];
                }
            }
        }

        return correctedImage;
    }

    public static BufferedImage gammaCorrection128(BufferedImage image) {
        // значение gamma подбирается методом дихотомии
        // если начальная средняя яркость > 128, то рассматриваем значения gamma 0.5, 0.25, 0.125 и так далее до тех пор, пока новая нынешняя яркость не окажется < 128
        // если начальная средняя яркость < 128, то наоборот - рассматриваем значения gamma 2.0, 4.0, 8.0 до тех пор, пока новая нынешняя яркость не окажется > 128
        double brightnessMean = meanBrightness(image);
        double factorGamma;
        if (brightnessMean > 128.0) {
            factorGamma = 0.5;
        }
        else if (brightnessMean < 128.0) {
            factorGamma = 2.0;
        }
        else {
            return copyImage(image);
        }

        double gamma1 = 1.0, gamma2 = factorGamma;

        BufferedImage correctedImage;
        BufferedImage correctedImage2 = gammaCorrection(image, gamma2);
        double brightnessMean1 = brightnessMean;
        double brightnessMean2 = meanBrightness(correctedImage2);
        while ((brightnessMean1 - 128.0) * (brightnessMean2 - 128.0) > 0) {
            gamma1 = gamma2;
            gamma2 *= factorGamma;
            correctedImage2 = gammaCorrection(image, gamma2);
            brightnessMean1 = brightnessMean2;
            brightnessMean2 = meanBrightness(correctedImage2);
        }

        if (brightnessMean2 == 128.0) return correctedImage2;

        // после того как удалось достичь значения средней яркости, имеющей в сравнении с 128 противоположный знак от того, что был у изначальной яркости,
        // у нас получены значения gamma1 и gamma2, в интервале между которыми и будет выполняться подбор gamma методом дихотомии с точностью до заданного значения epsilon
        double eps = 1e-9;
        double gamma;
        while (Math.abs(gamma2 - gamma1) > eps) {
            gamma = (gamma1 + gamma2) / 2;
            correctedImage = gammaCorrection(image, gamma);
            brightnessMean = meanBrightness(correctedImage);

            if (brightnessMean == 128.0) return correctedImage;
            else if ((brightnessMean1 - 128.0) * (brightnessMean - 128.0) < 0) {
                gamma2 = gamma;
                brightnessMean2 = brightnessMean;
            } else if ((brightnessMean2 - 128.0) * (brightnessMean - 128.0) < 0) {
                gamma1 = gamma;
                brightnessMean1 = brightnessMean;
            }
        }
        gamma = (gamma1 + gamma2) / 2;
        System.out.println("gamma = " + gamma);

        correctedImage = gammaCorrection(image, gamma);
        return correctedImage;
    }
}