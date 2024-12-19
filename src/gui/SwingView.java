package gui;

import gui.panels.AudioPanel;
import gui.panels.OsuPanel;
import gui.panels.ReplayPanel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;

public class SwingView extends JFrame {

  private final int timeRemaining = 0;
  private final int maxTime = 0;

  public boolean inProgress = false;

  private final JButton startRender;

  private final ReplayPanel replayPanel;
  private final AudioPanel audioPanel;
  private final OsuPanel osuPanel;

  private final JLabel timeText;
  private final JCheckBox qualityCheck;
  private final JCheckBox fpsCheck;
  private final JCheckBox isNvidia;

  private final boolean rendering = false;

  // Constructor to set up the GUI
  public SwingView() {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    }

    ImageIcon img = new ImageIcon("assets\\osumania.png");
    setIconImage(img.getImage());


    setTitle("Mania Renderer");
    setSize(600, 700);

    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    getContentPane().setBackground(Color.BLACK);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
    mainPanel.setBackground(Color.BLACK); // Set main panel background to black

    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    mainScrollPane.setBackground(Color.BLACK); // Set scroll pane background to black
    add(mainScrollPane);

    JPanel fileSelectorPanel = new JPanel();
    FlowLayout flowLayout2 = new FlowLayout();
    flowLayout2.setAlignment(FlowLayout.CENTER);
    fileSelectorPanel.setLayout(flowLayout2);
    fileSelectorPanel.setBackground(Color.BLACK); // Set file selector panel background to black

    mainPanel.add(fileSelectorPanel);


    JPanel imagePanel = new JPanel();
    imagePanel.setLayout(new FlowLayout());
    imagePanel.setBackground(Color.BLACK);
    imagePanel.add(new JLabel(new ImageIcon("assets\\maniaGlow.png")));

    mainPanel.add(imagePanel);

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new FlowLayout());
    titlePanel.setBackground(Color.BLACK);

    JPanel subTitlePanel = new JPanel();
    subTitlePanel.setLayout(new FlowLayout());
    subTitlePanel.setBackground(Color.BLACK);

    JLabel title = new JLabel("Crabchip's Mania Renderer");
    title.setFont(new Font("Helvetica", Font.BOLD, 25));
    JLabel subTitle = new JLabel("<Select files, or drag them onto the text areas>");
    subTitle.setFont(new Font("Helvetica", Font.BOLD, 15));

    title.setForeground(new Color(134, 0, 180));
    subTitle.setForeground(new Color(134, 0, 180));

    titlePanel.add(title);
    subTitlePanel.add(subTitle);
    mainPanel.add(titlePanel);
    mainPanel.add(subTitlePanel);

    // Replay Panel
    replayPanel = new ReplayPanel();

    mainPanel.add(replayPanel);

    // Osu Panel
    osuPanel = new OsuPanel();

    mainPanel.add(osuPanel);

    // Audio Panel
    audioPanel = new AudioPanel();

    mainPanel.add(audioPanel);

    // adds ability to drag and drop
    ImprovedTransferHandler itr = new ImprovedTransferHandler(replayPanel, this);
    replayPanel.setTransferHandler(itr);

    itr = new ImprovedTransferHandler(osuPanel, audioPanel, this);
    osuPanel.setTransferHandler(itr);

    itr = new ImprovedTransferHandler(audioPanel, this);
    audioPanel.setTransferHandler(itr);

    qualityCheck = new JCheckBox("Performance Mode");
    fpsCheck = new JCheckBox("Reduced FPS");
    isNvidia = new JCheckBox("Render Quicker (Nvidia Drivers Needed)");

    JPanel checkPanel = new JPanel();
    checkPanel.setLayout(new FlowLayout());
    checkPanel.setBackground(Color.BLACK);
    mainPanel.add(checkPanel);

    checkPanel.add(qualityCheck);
    checkPanel.add(fpsCheck);
    checkPanel.add(isNvidia);

    // Render Panel
    JPanel renderPanel = new JPanel();
    renderPanel.setLayout(new FlowLayout());
    renderPanel.setBackground(Color.BLACK);

    startRender = new JButton("Start Rendering!");
    startRender.setActionCommand("render");

    startRender.setBackground(Color.BLACK);

    mainPanel.add(renderPanel);
    renderPanel.add(startRender);

    JPanel timePanel = new JPanel();
    timePanel.setLayout(new FlowLayout());
    timePanel.setBackground(Color.BLACK);

    timeText = new JLabel("");
    timeText.setForeground(new Color(134, 0, 180)); // Text color

    timePanel.add(timeText);
    mainPanel.add(timePanel);

    // Set JFrame properties
    setVisible(true);
    setDefaultLookAndFeelDecorated(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public void setListeners(ActionListener listener) {
    replayPanel.getButton().addActionListener(listener);
    audioPanel.getButton().addActionListener(listener);
    osuPanel.getButton().addActionListener(listener);
    startRender.addActionListener(listener);
  }

  public void updateTimeRemaining(int time, int finalTime) {
    int percentage = ((int) (((double) (time + 3000) /(finalTime + 3000)) * 100));
    //System.out.println(time + " , " + finalTime + " , " + percentage);
    timeText.setText("Generating Images: " + percentage + "%");
  }

  public void creatingVideo() {
    timeText.setText("Converting to mp4...");
  }

  public void overlayingAudio() {
    timeText.setText("Overlaying audio...");
  }

  public void cleaningFiles() {
    timeText.setText("Cleaning up files...");
  }


  public void renderComplete(String outputFilePath) {
    timeText.setText("Render complete!");

    Desktop desktop = Desktop.getDesktop();
    File file = new File(outputFilePath);
    try {
      if(file.exists()) {
        desktop.open(file);
      }
    } catch (IOException e) {
      System.out.println("File was not real...");
    }

  }

  public void setReplayText(String text) {
    replayPanel.changeText(text);
  }

  public void setAudioText(String text) {
    audioPanel.changeText(text);
  }

  private String extractAudioFileName(String file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("AudioFilename")) {
          // Split the line at ':' to get the OD value
          String[] parts = line.split(":");
          if (parts.length > 1) {
            return parts[1].trim();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Return null if OD value is not found
    return audioPanel.getLabel().getText();
  }

  public void setOsuText(String text) {
    osuPanel.changeText(text);

    String targetChar = "\\";

    int lastIndex = text.lastIndexOf(targetChar);

    if (lastIndex != -1) {
      String result = text.substring(0, lastIndex);
      String audioFile = extractAudioFileName(text);

      audioPanel.changeText(result + "\\" + audioFile);
    }
  }

  public String getReplayText() {
    return replayPanel.getRealPath();
  }

  public String getAudioText() {
    return audioPanel.getRealPath();
  }

  public String getOsuText() {
    return osuPanel.getRealPath();
  }

  public boolean getIsNvidiaCheck() {
    return isNvidia.isSelected();
  }

  public boolean getQualityCheck() {
    return qualityCheck.isSelected();
  }

  public boolean getFPSCheck() {
    return fpsCheck.isSelected();
  }

  public void fileError() {
    JOptionPane.showMessageDialog(this,
            "Error parsing files! Make sure a proper .osr and .osu file were submitted!", "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  public boolean isInProgress() {
    return inProgress;
  }

  public void setProgress(boolean inProgress) {
    this.inProgress = inProgress;
  }

  public void renderInProgress() {
    JOptionPane.showMessageDialog(this,
            "Render already in progress!", "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  public void ffmpegError() {
    JOptionPane.showMessageDialog(this,
            "Error with Ffmpeg video creation!", "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  public void audioError() {
    JOptionPane.showMessageDialog(this,
            "Invalid audio file submitted!", "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  public void filesSubmittedError() {
    JOptionPane.showMessageDialog(this,
            "Please select a file for all fields!", "Error",
            JOptionPane.ERROR_MESSAGE);
  }
}