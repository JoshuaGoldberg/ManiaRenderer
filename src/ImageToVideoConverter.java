import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImageToVideoConverter {

  boolean done = false;

  public void createVideoFromImages(String ffmpegPath, String inputPattern, String outputVideo, int framerate,
                                    AudioOverlayer audioOverlay, String audioFile, String videoFile, String outputFile, int offset, SwingView view) {

    System.out.println("Starting video conversion!");
    if(!done) {
      view.creatingVideo();
    }

    List<String> command = new ArrayList<>();
    command.add(ffmpegPath);
    command.add("-framerate");
    command.add(String.valueOf(framerate));
    command.add("-i");
    command.add(inputPattern);
    command.add("-c:v");
    command.add("h264_nvenc");
    command.add("-preset");
    command.add("fast");
    command.add("-pix_fmt");
    command.add("yuv420p");
    command.add("-vf");
    command.add("scale=2000:1200");
    command.add(outputVideo);
    ProcessBuilder processBuilder = new ProcessBuilder(command);

    try {
      Process process = processBuilder.start();

      // Separate threads to consume both stdout and stderr
      Thread outputThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            System.out.println(line);
          }
        } catch (IOException e) {
          System.out.println("Error: " + e);
        }
      });

      Thread errorThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            System.err.println(line);
          }
        } catch (IOException e) {
          System.out.println("Error: " + e);
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
        System.out.println("Video created successfully: " + outputVideo);
        done = true;

        process.destroy();
        audioOverlay.overlayAudio(audioFile, videoFile, outputFile, offset, view);
      }
    } catch (IOException | InterruptedException e) {
      System.out.println("Video creation failed: " + e.getMessage());
      view.ffmpegError();
    }
  }
}