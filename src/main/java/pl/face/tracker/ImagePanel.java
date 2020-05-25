package pl.face.tracker;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Krzysztof
 * @project FaceTracker
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    void setImage(BufferedImage image)
    {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (image == null)
            return;

        g.drawImage(image, 0, 0, null);
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }
}
