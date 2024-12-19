package videocreator;

import gui.SwingView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AudioOverlayer {

  private final ArrayList<String> mods;

  public AudioOverlayer(ArrayList<String> mods) {
    this.mods = mods;
  }

  public void overlayAudio(String audioTrack, String videoFilePath, String outputFilePath, int offSet, SwingView view) {
    System.out.println("Starting audio overlaying!");
    view.overlayingAudio();


    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-i");
    command.add(videoFilePath);
    command.add("-itsoffset");
    command.add(String.valueOf(offSet));
    command.add("-i");
    command.add(audioTrack);
    command.add("-c:v");
    command.add("copy");
    if(mods.contains("halftime")) {
      command.add("-filter:a");
      command.add("atempo=0.75");
    } else if(mods.contains("doubletime")) {
      command.add("-filter:a");
      command.add("atempo=1.5");
    } else {
      command.add("-c:a");
      command.add("aac");
    }

    command.add("-strict");
    command.add("experimental");
    command.add("-map");
    command.add("0:v:0");
    command.add("-map");
    command.add("1:a:0");
    command.add("-shortest");
    command.add(outputFilePath);

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);

      Process process = processBuilder.start();

      Thread outputThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            System.out.println(line);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      Thread errorThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            System.err.println(line);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      outputThread.start();
      errorThread.start();

      int exitCode = process.waitFor();
      outputThread.join();
      errorThread.join();

      if (exitCode != 0) {
        System.out.println("FFmpeg process failed with exit code " + exitCode);
      } else {

        view.cleaningFiles();
        File folder = new File("renderedImages");
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

        System.out.println("Audio overlay completed successfully");
        view.renderComplete(outputFilePath);
        view.setProgress(false);
      }

    } catch (IOException | InterruptedException e) {
      System.err.println(e.getMessage());
    }
  }
}