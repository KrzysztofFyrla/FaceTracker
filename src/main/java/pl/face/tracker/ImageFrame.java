package pl.face.tracker;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Krzysztof
 * @project FaceTracker
 */
public class ImageFrame {

    private boolean isOpen;
    private boolean shouldSave;
    private Color color;
    private ImagePanel imagePanel;

    private JFrame frame;
    private JTextField txtFileName;

    private static final Color DEFAULT_COLOR = Color.BLUE;

    ImageFrame()
    {
        color = DEFAULT_COLOR;
        buildGUI();
    }

    private void buildGUI()
    {
        imagePanel = new ImagePanel();
        isOpen = true;

        frame = new JFrame("Face Tracker");
        frame.addWindowListener(createWindowListener());
        frame.setLayout(new BorderLayout());
        frame.add("Center", imagePanel);
        frame.add("South", createToolbarPanel());
        frame.setVisible(true);
    }

    private WindowListener createWindowListener()
    {
        return new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowClosed)
            {
                isOpen = false;
            }
        };
    }

    private JPanel createToolbarPanel()
    {
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.LINE_AXIS));

        toolbarPanel.add(createSavePanel());
        toolbarPanel.add(Box.createHorizontalGlue());
        toolbarPanel.add(createColorPanel());

        return toolbarPanel;
    }

    private JPanel createSavePanel()
    {
        JPanel savePanel = new JPanel();
        savePanel.setBorder(BorderFactory.createLineBorder(Color.red));

        txtFileName = new JTextField(20);
        JButton btnSaveFile = new JButton("Zapisz twarz");
        btnSaveFile.addActionListener(actionEvent -> shouldSave = true);

        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Imię: "));
        namePanel.add(txtFileName);
        savePanel.add(namePanel, btnSaveFile);

        return savePanel;
    }

    private JPanel createColorPanel()
    {
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JComboBox<String> colorDropDown = new JComboBox<>();
        String[] colorOptions = {"BLUE", "CYAN", "GREEN", "MAGENTA", "ORANGE", "RED"};
        Arrays.stream(colorOptions).forEach(colorDropDown::addItem);

        colorDropDown.addActionListener(actionEvent -> {
            try
            {
                Field field = Color.class.getField((String) Objects.requireNonNull(colorDropDown.getSelectedItem()));
                color = (Color) field.get(null);
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                color = DEFAULT_COLOR;
            }
        });

        colorPanel.add(colorDropDown);

        return colorPanel;
    }

    boolean isOpen()
    {
        return isOpen;
    }

    boolean shouldSave()
    {
        boolean prevState = shouldSave;
        shouldSave = false;
        return prevState;
    }

    String getFileName()
    {
        return txtFileName.getText();
    }

    Scalar getTextColor()
    {
        return new Scalar(color.getBlue(), color.getGreen(), color.getRed());
    }

    void showImage(Mat image)
    {
        imagePanel.setImage(convertMatToImage(image));
        frame.repaint();
        frame.pack();
    }

    private static BufferedImage convertMatToImage(Mat matrix)
    {
        int width = matrix.width();
        int height = matrix.height();
        int type = matrix.channels() != 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;

        if (type == BufferedImage.TYPE_3BYTE_BGR)
            Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2RGB);

        byte[] data = new byte[width * height * (int) matrix.elemSize()];
        matrix.get(0, 0, data);

        BufferedImage out = new BufferedImage(width, height, type);
        out.getRaster().setDataElements(0, 0, width, height, data);

        return out;
    }
}
