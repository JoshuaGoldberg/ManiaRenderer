import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;

import javax.imageio.ImageIO;

public class GameRenderer extends Canvas implements Runnable {


  int captureInterval = 10;
  SwingView view;
  ArrayList<Note> notes;
  ArrayList<String> timings = new ArrayList<>();
  //private JFrame frame;
  double acc = 0.0;
  private int timeMS;
  private int timeUntilCapture = 0;
  int countMax = 0;
  int count300 = 0;
  int count200 = 0;
  int count100 = 0;
  int renderNum = 0;
  int count50 = 0;
  int countMiss = 0;
  int combo = 0;
  int OD;
  private final String saveDirectory = "renderedImages"; // Directory to save images
  ArrayList<Integer> nums = new ArrayList<>();

  boolean key1Pressed = false;
  boolean key2Pressed = false;
  boolean key3Pressed = false;
  boolean key4Pressed = false;

  boolean key1PressedHeldDown = false;
  boolean key2PressedHeldDown = false;
  boolean key3PressedHeldDown = false;
  boolean key4PressedHeldDown = false;
  private Timer timer;

  private final int targetFPS = 144;// Target frames per second
  int finalMS;

  ArrayList<IManiaKeyEvent> keyHits;

  ArrayList<Note> judgements = new ArrayList<>();

  ArrayList<JudgementEvent> judgementRenders = new ArrayList<>();

  private final int timeMulti = 1;

  ImageGrabber imageGrabber;

  ImageToVideoConverter converter;
  String ffmpegPath;
  String inputPattern;
  String outputVideo;
  int framerate;
  AudioOverlayer audioOverlay;
  String audioFile;
  String videoFile;
  String outputFile;
  int offset;
  boolean displayHits;

  String player;
  String title;
  String difficulty;
  ArrayList<String> mods;

  public GameRenderer(ArrayList<Note> notes, ArrayList<IManiaKeyEvent> keyHits, int OD, String player, String title, String difficulty
          , ImageToVideoConverter converter, String ffmpegPath, String inputPattern, String outputVideo, int framerate
          , AudioOverlayer audioOverlay, String audioFile, String videoFile, String outputFile, int offset, ImageGrabber imageGrabber, boolean displayHits, ArrayList<String> mods,
                      SwingView view) {

    this.mods = mods;
    this.view = view;
    this.OD = OD;
    this.player = player;
    this.title = title;
    this.difficulty = difficulty;
    this.displayHits = displayHits;
    this.keyHits = keyHits;
    this.notes = notes;
    this.timeMS = -2000;
    this.finalMS = notes.get(notes.size() - 1).getTime() + notes.get(notes.size() - 1).getLength() + 3000;

    this.converter = converter;

    this.ffmpegPath = ffmpegPath;
    this.inputPattern = inputPattern;
    this.outputVideo = outputVideo;
    this.framerate = framerate;
    this.audioOverlay = audioOverlay;
    this.audioFile = audioFile;
    this.videoFile = videoFile;
    this.outputFile = outputFile;
    this.offset = offset;

    this.imageGrabber = imageGrabber;

    // Creating the render window
//    this.setPreferredSize(new Dimension(1200, 1200));
//    frame = new JFrame("Osu!Mania Renderer");
//    frame.add(this);
//    frame.pack();
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    frame.setLocationRelativeTo(null); // Center the window
//    frame.setResizable(false);
//    frame.setVisible(false);

    File folder = new File(saveDirectory);

    File folder_0 = new File(videoFile);
    File folder_1 = new File(outputFile);

    folder_0.delete();
    folder_1.delete();

    if (folder.exists() && folder.isDirectory()) {
      File[] files = folder.listFiles();

      if (files != null) {
        for (File file : files) {
          boolean deleted = file.delete();

          if (!deleted) {
            System.out.println("Failed to delete file " + file.getName());
          }
        }
      }
    }
  }

  public ArrayList<Note> getJudgements() {
    return judgements;
  }

  public boolean nextValidLNExist(String key, int initialHit) {

    for (Note note : judgements) {

      if ((((note.isLN()) && !note.isReleased()) && !note.isHit() && note.getKey().equals(key)
              && note.getTime() != initialHit) || (!note.isLN() && !note.isHit() && note.getKey().equals(key))) {
        return true;
      }
    }

    return false;

  }

  public Note getNextValidLN(String key) {
    for (Note note : judgements) {

      if ((((note.isLN()) && !note.isReleased()) && !note.isHit() && note.getKey().equals(key))
              || (!note.isLN() && !note.isHit() && note.getKey().equals(key))) {
        return note;
      }
    }

    return null;

  }

  public ArrayList<Note> getNoteSet() {

    ArrayList<Note> noteSet = new ArrayList<>();
    ArrayList<String> seen = new ArrayList<>();

    for (Note note : judgements) {

      // last part on this line is dealing with disappearing ln logic, similar to doNotDelete for rice. MAY BE WRONG!!!
      if (((!note.isHit() || ((note.doNotDelete() && (note.getTime() - timeMS >= -8)) || (note.doNotDelete() && note.isLN() && (note.getTime() + note.getLength() - timeMS >= 0))))
              || ((note.isLN()) && !note.isReleased())) && !seen.contains(note.getKey())) {
        noteSet.add(note);
        seen.add(note.getKey());
      }

    }

    return noteSet;
  }

  public void stop() {

    converter.createVideoFromImages(ffmpegPath, inputPattern, outputVideo, framerate, audioOverlay
            , audioFile, videoFile, outputFile, offset, view);

    //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    // frame.dispose();  // Dispose of the JFrame
    //timer.cancel();
    //timer.purge();
  }

  public void checkEvents() {

    IManiaKeyEvent prevEvent = null;

    for (int i = 0; i < keyHits.size(); i++) {

      IManiaKeyEvent keyEvent = keyHits.get(i);

      if (prevEvent == null) {
        prevEvent = keyEvent;
      }

      if (timeMS == keyEvent.getEventTime()) {

        key1Pressed = false;
        key2Pressed = false;
        key3Pressed = false;
        key4Pressed = false;

        for (String s : keyEvent.getActiveKeys()) {
          switch (s) {
            case "key1" -> key1Pressed = true;
            case "key2" -> key2Pressed = true;
            case "key3" -> key3Pressed = true;
            case "key4" -> key4Pressed = true;
          }
        }

        ArrayList<String> heldDown = new ArrayList<>();

        if (key1PressedHeldDown) {
          heldDown.add("key1");
        }

        if (key2PressedHeldDown) {
          heldDown.add("key2");
        }

        if (key3PressedHeldDown) {
          heldDown.add("key3");
        }

        if (key4PressedHeldDown) {
          heldDown.add("key4");
        }

        ArrayList<Note> relevantJudgements = this.getNoteSet();

        for (Note note : relevantJudgements) {
          note.updateStatus(this, keyEvent.getActiveKeys(), timeMS, heldDown, timings, prevEvent, judgements, judgementRenders);
        }

        key1PressedHeldDown = key1Pressed;
        key2PressedHeldDown = key2Pressed;
        key3PressedHeldDown = key3Pressed;
        key4PressedHeldDown = key4Pressed;

        keyHits.remove(i);
        break;
      }

      prevEvent = keyEvent;
    }

    judgements.clear();
  }

  private BufferedImage image;
  private Graphics2D g2d;

  private void initGraphics() {
    if (image == null) {
      image = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_RGB);
      g2d = image.createGraphics();

      // Ensure the save directory exists
      File dir = new File(saveDirectory);
      if (!dir.exists()) {
        dir.mkdirs();
      }
    }
  }

  public VolatileImage createVolatileImage(int width, int height) {
    GraphicsConfiguration gc = getGraphicsConfiguration();
    return gc.createCompatibleVolatileImage(width, height);
  }

  private void render() {

    image = null;
    g2d = null;


    // Initialize graphics objects if not already done
    //could bring back
    // Prepare for rendering
//    BufferStrategy bs = this.getBufferStrategy();
//    if (bs == null) {
//      this.createBufferStrategy(3);
//      return;
//    }

    // Clear the image

    // Render game elements efficiently

    //could bring back
    // Display the image on screen
//    Graphics g = bs.getDrawGraphics();
//    try {
//      g.drawImage(image, 0, 0, null);
//    } finally {
//      g.dispose();
//    }
//    bs.show();

    // Sync with the display to avoid tearing
    Toolkit.getDefaultToolkit().sync();

    // Capture the rendered frame if necessary

    drawGameElements();

  }

  private void drawGameElements() {
    int xPos = 0;
    int yPos;
    int length;
    int offset;

    if(timeUntilCapture >=  captureInterval) {
      initGraphics();

      g2d.setComposite(AlphaComposite.Src);
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0, 0, getWidth(), getHeight());

      // Pre-render static elements (if they don't change, only render once)
      g2d.setColor(Color.white);
      g2d.fillRect(390, -50/2, 420/2 + 10, 1250/2);
      g2d.setColor(Color.black);
      g2d.fillRect(400, -50/2, 400/2, 1250/2);

    }

    if (judgementRenders.size() > 1) {
      judgementRenders.remove(0); // Remove the oldest judgement to keep the list small
    }

    int count = 100;
    if (notes.size() < count) {
      count = notes.size();
    }

    for (int i = 0; i < count; i++) {

      Note note = notes.get(i);

      if (!note.isLN() && (note.getTime() - timeMS < -200)) {
        notes.remove(i);
        i--;
        count --;
      } else if (note.isLN() && (note.getTime() + note.getLength() - timeMS < -200)) {
        notes.remove(i);
        i--;
        count --;
      } else {
        offset = 0;
        length = note.getLength();

        // Add notes for potential judgement
        if (((!note.isLN() && Math.abs(note.getTime() - timeMS) <= (188 - (OD))) &&
                note.getTime() - timeMS >= -(151 - (OD))) ||
                ((note.isLN() && !note.shouldExclude() && (Math.abs(note.getTime() - timeMS) <= (188 - (OD)) ||
                        ((Math.abs(note.getTime() + note.getLength() - timeMS)) <= (188 - (OD)) * 1.5) || ((note.getTime() - timeMS <= -(188 - (OD))) &&
                        (note.getTime() + note.getLength() - timeMS) >= -(188 - (OD)) * 1.5))))) {
          judgements.add(note);
        }

        if (note.getTime() - timeMS < -(151 - (OD)) && !note.isHit()) {

          if (!note.isLN()) {
            System.out.println("miss at time: " + timeMS + " ms, key needed: " + note.getKey());
            countMiss++;
          } else {
            // note.setLNFirstMiss();
            countMiss++;
            System.out.println("ln start miss at time: " + timeMS + ", need key: " + note.getKey());
          }

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));
          note.setOffMap();
          note.setHit();
          this.combo = 0;
        }

        if (note.isLN() && (note.getTime() + note.getLength()) - timeMS < -(151 - (OD)) * 1.5
                && !note.isReleased() && note.isHit()) {
          if (!note.isKeyDown()) {
            countMiss++;
            this.combo = 0;
            System.out.println("LN miss at " + timeMS + " ms, note at: " + note.getTime() + " and " + (note.getTime() + note.getLength()));
          } else {
            System.out.println("ln release miss at time: " + timeMS + ", need key: " + note.getKey());
            countMiss++;
            this.combo = 0;
          }

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));
          note.setReleased();
        }

        if (timeUntilCapture >= captureInterval) {

          if (length > 0) {
            length = length * 2 + 50;
            offset = (length - 50);
          }

          if (length == 0) {
            length = 50;
          }

          if (note.getTime() - timeMS <= 500 + offset && note.getTime() + offset - timeMS >= 0 &&
                  ((!note.isHit() || note.doNotDelete()) || length > 50)) {
            switch (note.getKey()) {
              case "key1":
                xPos = 400;
                break;
              case "key2":
                xPos = 450;
                break;
              case "key3":
                xPos = 500;
                break;
              case "key4":
                xPos = 550;
                break;
            }

            yPos = (((500) - (note.getTime() - timeMS)) * 2);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(xPos, (yPos - offset)/2, 100/2, length/2);
          }
        }
      }
    }

    if(timeUntilCapture >=  captureInterval) {

      if (displayHits) {
        for (IManiaKeyEvent keyEvent : keyHits) {
          if (keyEvent.getEventTime() - timeMS <= 500 && keyEvent.getEventTime() - timeMS >= 0) {

            for (String key : keyEvent.getActiveKeys()) {
              xPos = switch (key) {
                case "key1" -> 400;
                case "key2" -> 500;
                case "key3" -> 600;
                case "key4" -> 700;
                default -> xPos;
              };

              yPos = (500 - (keyEvent.getEventTime() - timeMS)) * 2;
              g2d.setColor(Color.RED);
              g2d.fillRect(xPos + 35, yPos + 50, 30, 10);
            }
          }
        }
      }

      if (!judgementRenders.isEmpty()) {
        g2d.setComposite(AlphaComposite.SrcOver);

        JudgementEvent jEvent = judgementRenders.getFirst();

        BufferedImage judgementImage = jEvent.getJudgement();

        if (!jEvent.complete) {
          g2d.drawImage(judgementImage, null,
                  500 - ((judgementImage.getWidth()/2)),
                  ((100) + (int) (((500 - jEvent.timeLeft) * 0.1)/2)));
          jEvent.updateTime();
        }

      }

      drawKeyPressIndicators();
      drawStatistics();
      captureFrame();
    }
  }

  private void drawKeyPressIndicators() {

    // Reuse and minimize drawing operations
    g2d.setColor(key1Pressed ? Color.YELLOW : Color.CYAN);
    g2d.fillRect(400, 1000/2, 100/2, 200/2);

    g2d.setColor(key2Pressed ? Color.YELLOW : Color.CYAN);
    g2d.fillRect(450, 1000/2, 100/2, 200/2);

    g2d.setColor(key3Pressed ? Color.YELLOW : Color.CYAN);
    g2d.fillRect(500, 1000/2, 100/2, 200/2);

    g2d.setColor(key4Pressed ? Color.YELLOW : Color.CYAN);
    g2d.fillRect(550, 1000/2, 100/2, 200/2);
  }

  ArrayList<String> digits = new ArrayList<>();

  private void drawStatistics() {

    digits.clear();

    if(count50 + count100 + count200 + count300 + countMax + countMiss == 0) {
      acc = 100.00;
    } else {
      acc = (double) ((305 * countMax) + (300 * count300) + (200 * count200) + (100 * count100) + (50 * count50)) * 100
              /(305 * (countMiss + count50 + count100 + count200 + count300 + countMax));
    }
    int offset100 = 0;

    if(acc == 100) {
      offset100 = 50;
    }

    String finalAcc = new DecimalFormat("#.##").format(acc);

    if(!finalAcc.contains(".")) {
      finalAcc = finalAcc + ".00";
    } else if(finalAcc.charAt(finalAcc.length() - 2) == '.') {
      finalAcc = finalAcc + "0";
    }


    finalAcc = finalAcc + "%";

    g2d.setComposite(AlphaComposite.SrcOver);

    while(!finalAcc.isEmpty()) {
      digits.add(Character.toString(finalAcc.charAt(0)));
      finalAcc = finalAcc.substring(1);
    }


    int accOffset = 0;
    int percentOffset = 0;
    for(String digit : digits) {

      if(digit.equals("%")) {
        percentOffset = 10;
      } else {
        percentOffset = 0;
      }

      g2d.drawImage(imageGrabber.grabImage(digit), 750 - offset100 + accOffset + percentOffset, 40, null);
      accOffset += 40;

      if(digit.equals(".")) {
        accOffset -= 20;
      }
    }

    if (timings.size() > 3) {
      timings.remove(0);
    }

    StringBuilder modList = new StringBuilder();
    modList.append("None");

    if(!mods.isEmpty()) {
      modList.setLength(0);
    }

    if(mods.contains("mirror")) {
      modList.append("MR");

      if(mods.size() > 1) {
        modList.append(" + ");
      }

    }

    if(mods.contains("halftime")) {
      modList.append("HT");
    }

    if(mods.contains("doubletime")) {
      modList.append("DT");
    }

    g2d.setColor(Color.WHITE);
    g2d.drawString("Player: " + player, 30, 20);
    g2d.drawString("Title: " + title, 30, 40);
    g2d.drawString("Diff: [" + difficulty + "]", 30, 60);
    g2d.drawString("Mods: " + modList , 30, 80);


    g2d.drawString("MS: " + timeMS, 30, 100);
    g2d.drawString("MAX: " + countMax, 30, 120);
    g2d.drawString("300: " + count300, 30, 140);
    g2d.drawString("200: " + count200, 30, 160);
    g2d.drawString("100: " + count100, 30, 180);
    g2d.drawString("50: " + count50, 30, 200);
    g2d.drawString("Miss: " + countMiss, 30, 220);


    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.drawImage(imageGrabber.grabImage("x"), 20, 1100 - 600, null);

    int tempCombo = combo;

    if (tempCombo == 0) {
      g2d.drawImage(imageGrabber.grabImage("0"), 60, 1105 - 600, null);
    } else {

      nums.clear();

      while (tempCombo > 0) {
        nums.add(tempCombo % 10);
        tempCombo = tempCombo / 10;
      }

      int numOffset = 0;

      for (int i = 0; i < nums.size(); i++) {
        g2d.drawImage(imageGrabber.grabImage(Integer.toString(nums.get(nums.size() - 1 - i))), 60 + numOffset, 1105 - 600, null);
        numOffset += 40;
      }

    }

    StringBuilder base = new StringBuilder();

    boolean first = true;

    for (Note note : judgements) {

      if (first) {
        base.append(note.noteStringData());
        first = false;
      } else {
        base.append(" ").append(note.noteStringData());
      }
    }

    g2d.setColor(Color.CYAN);
    //g2d.drawString(base.toString(), 70, 180);

    if (!timings.isEmpty()) {

      String hitData = timings.getLast();

      if (hitData.contains("-")) {
        g2d.setColor(Color.CYAN);
      } else if (hitData.contains(" 0 ")) {
        g2d.setColor(Color.YELLOW);
      } else {
        g2d.setColor(Color.RED);
      }

      g2d.drawString(hitData, 30, 240);
    }

  }

  private void captureFrame() {

    if (timeMS >= this.finalMS) {
      this.stop();
    }

    if (timeUntilCapture >= captureInterval) {  // Capture every ~10ms (100 fps) high frame rate my beloved
      // captureG2d.drawImage(image, 0, 0, null);
      saveImageAsync(image, saveDirectory + "/frame" + String.format("%05d", renderNum) + ".jpg");
      renderNum++;
      timeUntilCapture = 0;
    }
  }

  // Runs a thread to ensure lag is minimized.
  private void saveImageAsync(BufferedImage image, String filename) {

    Thread saveThread = new Thread(() -> {
      try {
        ImageIO.write(image, "jpg", new File(filename));
      } catch (IOException e) {
        System.out.println("Error saving image: " + filename);
      }
    });

    saveThread.setPriority(Thread.MIN_PRIORITY);
    saveThread.start();
  }


//  @Override
//  public void run() {
//    timer = new Timer();
//    timer.scheduleAtFixedRate(new TimerTask() {
//      @Override
//      public void run() {
//        render();
//        checkEvents();
//        System.out.println(timeMS);
//        timeMS += timeMulti; // increment the timer
//        timeUntilCapture += timeMulti;
//      }
//    }, 0, 1); //1 ms delay
//  }




  @Override
  public void run() {
    long startTime = System.nanoTime();

    while (true) {
      long currentTime = System.nanoTime();
      if (currentTime > startTime) {

        if(timeMS % 1000 == 0) {
          System.out.println(timeMS);
          view.updateTimeRemaining(timeMS, finalMS + 2000);
        }

        render();
        checkEvents();
        timeMS += timeMulti; // increment the timer
        timeUntilCapture += timeMulti;
        startTime = currentTime;
      }
    }

  }
}