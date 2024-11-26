import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class ImgExport {
    public static void exportImage(BufferedImage image, String filePath) {
        try {
            File outputFile = new File(filePath);

            String format = filePath.substring(filePath.lastIndexOf(".") + 1);

            ImageIO.write(image, format, outputFile);
            System.out.println("Изображение успешно экспортировано: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении изображения: " + e.getMessage());
        }
    }
}