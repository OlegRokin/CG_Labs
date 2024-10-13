import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImgShow extends JFrame {
    public static void displayImage(BufferedImage image) {
        // Создание окна для просмотра изображения
        JFrame window = new JFrame();
        JLabel screen = new JLabel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        // Установка изображения в JLabel
        screen.setIcon(new ImageIcon(image));
        window.getContentPane().add(screen);
        window.pack();
    }
}