package gui.panels;

import gui.ImprovedTransferHandler;

import javax.swing.*;
import java.awt.*;

public class AudioPanel extends JPanel implements InformationPanel {

    private String realPath;
    private final JLabel audioText = new JLabel("Audio File Path");
    private final JButton audioFileButton = new JButton("Select Audio File");

    public AudioPanel() {
        JPanel audioPanel = new JPanel();
        audioPanel.setLayout(new FlowLayout());
        audioPanel.setBackground(Color.BLACK); // Set audio panel background to black

        audioText.setForeground(new Color(134, 0, 180)); // Text color

        audioFileButton.setActionCommand("audioFile");
        audioPanel.add(audioFileButton);
        audioPanel.add(audioText);

        audioText.setFont(new Font("Helvetica", Font.BOLD, 14));

        setBackground(Color.BLACK);
        add(audioPanel);
    }

    @Override
    public void changeText(String path) {
        realPath = path;
        String[] list = path.split("\\\\");
        String visualPath = list[list.length - 1];
        audioText.setText(visualPath);
    }

    @Override
    public JButton getButton() {
        return audioFileButton;
    }

    @Override
    public JLabel getLabel() {
        return audioText;
    }

    @Override
    public String getRealPath() {
        return realPath;
    }
}
