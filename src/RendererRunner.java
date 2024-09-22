import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class RendererRunner {

  Scanner scanner = new Scanner(System.in);

  GameRenderer game;

  String osrPath;
  String audioPath;
  String osuPath;
  SwingView view;
  boolean lowerQuality;
  boolean lowFPS;

  public RendererRunner(String osrPath, String audioPath, String osuPath, SwingView view, boolean lowerQuality, boolean lowFPS) {
    this.osrPath = osrPath;
    this.audioPath = audioPath;
    this.osuPath = osuPath;
    this.view = view;
    this.lowerQuality = lowerQuality;
    this.lowFPS = lowFPS;
  }

  public void run() throws IOException {

    String inputPattern = "renderedImages\\frame%05d.jpg";

    ImageToVideoConverter converter = new ImageToVideoConverter();
    replayData data = new replayData();
    String result = data.parseOsrFile(new File(osrPath));
    File sourceOsuFile = new File(osuPath);
    ImageGrabber imageGrabber;
    imageGrabber = new ImageGrabber();

    ModConverter modConverter = new ModConverter();

    AudioOverlayer audio = new AudioOverlayer(modConverter.translate(data.mods));
    osuFileData map = new osuFileData(imageGrabber, modConverter.translate(data.mods));
    int OD = (int) ((Double.parseDouble(map.extractOD(sourceOsuFile)) * 10));

    OD = OD * 3;

    if(OD % 10 != 0) {
      OD = OD + (10 - (OD % 10));
    }

    OD = OD/10;

    String title = map.extractSongName(sourceOsuFile);
    String diff = map.extractDifficulty(sourceOsuFile);
    String player = data.playerName;


    int[][] keyEvents = replayData.replayDataConversion(replayData.decompressToText(data.replayData));
    ArrayList<IManiaKeyEvent> events = data.convertToEvents(keyEvents, modConverter.translate(data.mods));
    map.parseHitObjects(sourceOsuFile);

    int fps = 100;

    if(lowFPS) {
      fps = 50;
    }

    //System.out.println(modConverter.translate(data.mods));
    game = new GameRenderer(map.getNotes(), events, OD, player, title, diff, converter, "ffmpeg", inputPattern,
            "replay.mp4", fps, audio,
            audioPath, "replay.mp4", "completeReplay.mp4", 2, imageGrabber, false, modConverter.translate(data.mods), view, lowerQuality, lowFPS);

    game.run();

  }

}
