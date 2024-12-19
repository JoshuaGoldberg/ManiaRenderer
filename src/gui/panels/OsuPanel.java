package gui.panels;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OsuPanel extends JPanel implements InformationPanel {

    private String realPath;
    private final JLabel osuText = new JLabel(".osu File Path");
    private final JButton osuFileButton = new JButton("Select .osu File");

    public OsuPanel() {
        JPanel osuPanel = new JPanel();
        osuPanel.setLayout(new FlowLayout());
        osuPanel.setBackground(Color.BLACK);

        osuText.setForeground(new Color(134, 0, 180)); // Text color

        osuFileButton.setActionCommand("osuFile");
        osuText.setFont(new Font("Helvetica", Font.BOLD, 14));

        osuPanel.add(osuFileButton);
        osuPanel.add(osuText);

        osuFileButton.setBackground(Color.BLACK);

        setBackground(Color.BLACK);
        add(osuPanel);
    }

    @Override
    public void changeText(String path) {
        realPath = path;
        String[] list = path.split("\\\\");
        String visualPath = list[list.length - 1];
        osuText.setText(visualPath);
    }

    @Override
    public JButton getButton() {
        return osuFileButton;
    }

    @Override
    public JLabel getLabel() {
        return osuText;
    }

    @Override
    public String getRealPath() {
        return realPath;
    }
}
