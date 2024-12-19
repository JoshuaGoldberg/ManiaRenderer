package videocreator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageGrabber {

  File maxFile = new File("assets\\max.png");
  BufferedImage maxImage = ImageIO.read(maxFile);

  File file300 = new File("assets\\300.png");
  BufferedImage image300 = ImageIO.read(file300);

  File file200 = new File("assets\\200.png");
  BufferedImage image200 = ImageIO.read(file200);

  File file100 = new File("assets\\100.png");
  BufferedImage image100 = ImageIO.read(file100);

  File file50 = new File("assets\\50.png");
  BufferedImage image50 = ImageIO.read(file50);

  File fileMiss = new File("assets\\miss.png");
  BufferedImage imageMiss = ImageIO.read(fileMiss);

  File fileX = new File("assets\\x.png");
  BufferedImage imageX = ImageIO.read(fileX);

  File file0 = new File("assets\\0.png");
  BufferedImage image0 = ImageIO.read(file0);

  File file1 = new File("assets\\1.png");
  BufferedImage image1 = ImageIO.read(file1);

  File file2 = new File("assets\\2.png");
  BufferedImage image2 = ImageIO.read(file2);

  File file3 = new File("assets\\3.png");
  BufferedImage image3 = ImageIO.read(file3);

  File file4 = new File("assets\\4.png");
  BufferedImage image4 = ImageIO.read(file4);

  File file5 = new File("assets\\5.png");
  BufferedImage image5 = ImageIO.read(file5);

  File file6 = new File("assets\\6.png");
  BufferedImage image6 = ImageIO.read(file6);

  File file7 = new File("assets\\7.png");
  BufferedImage image7 = ImageIO.read(file7);

  File file8 = new File("assets\\8.png");
  BufferedImage image8 = ImageIO.read(file8);

  File file9 = new File("assets\\9.png");
  BufferedImage image9 = ImageIO.read(file9);

  File fileDot = new File("assets\\..png");
  BufferedImage imageDot = ImageIO.read(fileDot);

  File filePercent = new File("assets\\%.png");
  BufferedImage imagePercent = ImageIO.read(filePercent);

  HashMap<String, BufferedImage> images = new HashMap<>();

  public ImageGrabber() throws IOException {
    images.put("max", maxImage);
    images.put("300", image300);
    images.put("200", image200);
    images.put("100", image100);
    images.put("50", image50);
    images.put("miss", imageMiss);
    images.put("x", imageX);
    images.put("0", image0);
    images.put("1", image1);
    images.put("2", image2);
    images.put("3", image3);
    images.put("4", image4);
    images.put("5", image5);
    images.put("6", image6);
    images.put("7", image7);
    images.put("8", image8);
    images.put("9", image9);
    images.put(".", imageDot);
    images.put("%", imagePercent);
  }

  public BufferedImage grabImage(String index) {
    return images.get(index);
  }


}
