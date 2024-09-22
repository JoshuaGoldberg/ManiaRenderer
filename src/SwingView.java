import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class SwingView extends JFrame {

  int timeRemaining = 0;
  int maxTime = 0;

  boolean inProgress = false;

  JButton replayFileButton;
  JButton audioFileButton;
  JButton osuFileButton;
  JButton startRender;

  JPanel mainPanel;
  JLabel replayText;
  JLabel audioText;
  JLabel osuText;
  JLabel timeText;
  JCheckBox qualityCheck;
  JCheckBox fpsCheck;


  boolean rendering = false;

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

    // Setting the background color of the JFrame
    getContentPane().setBackground(Color.BLACK);

    mainPanel = new JPanel();
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
    JPanel replayPanel = new JPanel();
    replayPanel.setLayout(new FlowLayout());
    replayPanel.setBackground(Color.BLACK); // Set replay panel background to black

    mainPanel.add(replayPanel);

    replayText = new JLabel(".osr File Path");
    replayText.setForeground(new Color(134, 0, 180)); // Text color
    replayFileButton = new JButton("Select .osr File");
    //replayFileButton.setIcon(img);
    //replayFileButton.setBorderPainted(false);
    replayFileButton.setActionCommand("replayFile");
    replayFileButton.setBackground(new Color(134, 0, 180));
    replayFileButton.setFocusPainted(false);

    ImprovedTransferHandler itr = new ImprovedTransferHandler(replayText);
    replayText.setTransferHandler(itr);

    replayText.setFont(new Font("Helvetica", Font.BOLD, 14));
    replayPanel.add(replayFileButton);
    replayPanel.add(replayText);

    // Audio Panel
    JPanel audioPanel = new JPanel();
    audioPanel.setLayout(new FlowLayout());
    audioPanel.setBackground(Color.BLACK); // Set audio panel background to black

    mainPanel.add(audioPanel);

    audioText = new JLabel("Audio File Path");
    audioText.setForeground(new Color(134, 0, 180)); // Text color

    itr = new ImprovedTransferHandler(audioText);
    audioText.setTransferHandler(itr);

    audioFileButton = new JButton("Select Audio File");
    audioFileButton.setActionCommand("audioFile");
    audioPanel.add(audioFileButton);
    audioText.setFont(new Font("Helvetica", Font.BOLD, 14));

    audioPanel.add(audioText);

    // Osu Panel
    JPanel osuPanel = new JPanel();
    osuPanel.setLayout(new FlowLayout());
    osuPanel.setBackground(Color.BLACK);

    mainPanel.add(osuPanel);

    osuText = new JLabel(".osu File Path");
    osuText.setForeground(new Color(134, 0, 180)); // Text color

    osuFileButton = new JButton("Select .osu File");
    osuFileButton.setActionCommand("osuFile");
    osuText.setFont(new Font("Helvetica", Font.BOLD, 14));

    itr = new ImprovedTransferHandler(osuText);
    osuText.setTransferHandler(itr);

    osuPanel.add(osuFileButton);
    osuPanel.add(osuText);

    osuFileButton.setBackground(Color.BLACK);
    replayFileButton.setBackground(Color.BLACK);
    audioFileButton.setBackground(Color.BLACK);

    qualityCheck = new JCheckBox("Performance Mode");
    fpsCheck = new JCheckBox("Reduced FPS");

    JPanel checkPanel = new JPanel();
    checkPanel.setLayout(new FlowLayout());
    checkPanel.setBackground(Color.BLACK);
    mainPanel.add(checkPanel);
    checkPanel.add(qualityCheck);
    checkPanel.add(fpsCheck);

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
    replayFileButton.addActionListener(listener);
    audioFileButton.addActionListener(listener);
    osuFileButton.addActionListener(listener);
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
    replayText.setText(text);
  }

  public void setAudioText(String text) {
    audioText.setText(text);
  }

  public void setOsuText(String text) {
    osuText.setText(text);
  }

  public String getReplayText() {
    return replayText.getText();
  }

  public String getAudioText() {
    return audioText.getText();
  }

  public String getOsuText() {
    return osuText.getText();
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
}