import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AudioOverlayer {

  ArrayList<String> mods;

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
    command.add("atempo=0.75");

    if(mods.contains("halftime")) {
      command.add("-filter:a");
      command.add("atempo=0.75");
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
      // Execute the command
      ProcessBuilder processBuilder = new ProcessBuilder(command);

      Process process = processBuilder.start();

      // Separate threads to consume both stdout and stderr
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

      // Start the threads
      outputThread.start();
      errorThread.start();

      // Wait for the process to complete
      int exitCode = process.waitFor();

      // Ensure the threads finish
      outputThread.join();
      errorThread.join();

      if (exitCode != 0) {
        System.out.println("FFmpeg process failed with exit code " + exitCode);
      } else {
        System.out.println("Audio overlay completed successfully");
        view.renderComplete(outputFilePath);
        view.setProgress(false);
    }

    } catch (IOException | InterruptedException e) {
      System.err.println(e.getMessage());
    }
  }
}