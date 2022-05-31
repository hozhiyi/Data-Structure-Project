package MyGUI.GUI;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image image;

    public BackgroundPanel(Image image) {
        this.image = image;
    }

    public BackgroundPanel(String imagePath) {
        this(new ImageIcon(imagePath).getImage());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
}
