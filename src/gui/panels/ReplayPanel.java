package gui.panels;

import javax.swing.*;
import java.awt.*;

public class ReplayPanel extends JPanel implements InformationPanel {

    private String realPath;
    private final JLabel replayText = new JLabel(".osr File Path");
    private final JButton replayFileButton = new JButton("Select .osr File");

    public ReplayPanel() {
        JPanel replayPanel = new JPanel();
        replayPanel.setLayout(new FlowLayout());
        replayPanel.setBackground(Color.BLACK); // Set replay panel background to black

        replayText.setForeground(new Color(134, 0, 180)); // Text color

        //replayFileButton.setIcon(img);
        //replayFileButton.setBorderPainted(false);

        replayFileButton.setActionCommand("replayFile");
        replayFileButton.setBackground(new Color(134, 0, 180));
        replayFileButton.setFocusPainted(false);

        replayText.setFont(new Font("Helvetica", Font.BOLD, 14));
        replayPanel.add(replayFileButton);
        replayPanel.add(replayText);
        replayFileButton.setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        add(replayPanel);
    }

    @Override
    public JButton getButton() {
        return replayFileButton;
    }

    @Override
    public JLabel getLabel() {
        return replayText;
    }

    @Override
    public String getRealPath() {
        return realPath;
    }

    @Override
    public void changeText(String path) {
        realPath = path;
        String[] list = path.split("\\\\");
        String visualPath = list[list.length - 1];
        replayText.setText(visualPath);
    }
}
