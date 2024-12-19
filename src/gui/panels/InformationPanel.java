package gui.panels;

import javax.swing.*;

public interface InformationPanel {

    void changeText(String path);

    JButton getButton();

    JLabel getLabel();

    String getRealPath();
}
