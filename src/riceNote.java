import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class riceNote implements Note {

  int xPos;
  int yPos;
  int hitTime;
  String keyNeeded;
  boolean offMap = false;
  int OD;
  boolean doNotDelete = false;
  double rawOD;
  boolean hit = false;
  ImageGrabber imageGrabber;
  ArrayList<String> mods;

  public riceNote(int xPos, int yPos, int hitTime, int OD, double rawOD, ImageGrabber imageGrabber, ArrayList<String> mods) {
    this.imageGrabber = imageGrabber;
    this.mods = mods;
    this.rawOD = rawOD;
    this.OD = OD;
    this.xPos = xPos;
    this.yPos = yPos;
    this.hitTime = hitTime;
    if(xPos < 128) {
      this.keyNeeded = "key1";
    } else if(xPos < 256) {
      this.keyNeeded = "key2";
    } else if(xPos < 384) {
      this.keyNeeded = "key3";
    } else if(xPos < 512) {
      this.keyNeeded = "key4";
    }

  }

  @Override
  public String noteStringData() {
    return "Rice note at: " + hitTime + " milliseconds, requires " + keyNeeded;
  }

  @Override
  public int getX() {
    return xPos;
  }

  @Override
  public int getY() {
    return yPos;
  }

  @Override
  public String getKey() {
    return keyNeeded;
  }

  @Override
  public int getTime() {
    return hitTime;
  }

  @Override
  public int getLength() {
    return 0;
  }


  @Override
  public void updateStatus(GameRenderer game, String[] keys, int time, ArrayList<String> heldDownPrev, ArrayList<String> timings, IManiaKeyEvent prevFrame, ArrayList<Note> judges,
                           ArrayList<JudgementEvent> judgementRenders) {

    if(!hit) {
      boolean valid = false;
      for (String k : keys) {
        if (k.equals(this.keyNeeded) && !heldDownPrev.contains(this.keyNeeded)) {
          valid = true;
        }
      }

      if (valid && (hitTime - time) > -(151 - (OD))) {
        game.combo++;
        this.hit = true;
        int howOff = Math.abs(hitTime - time);


        //impacted by OD

        int maxRange;

        if(rawOD - 9.0 <= 0.0001) {
          maxRange = 15;
        } else {
          maxRange = (int) (24.9 - (rawOD * 1.1));
        }

        if (howOff <= maxRange) {
          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("max"), 500));

          game.countMax++;
          timings.add("Rice MAX Margin : " + (hitTime - time) + " ms");
        } else if (howOff <= (64 - (OD))) {

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("300"), 500));


          game.count300++;
          timings.add("Rice 300 Margin : " + (hitTime - time) + " ms");
        } else if (howOff <= (97 - (OD))) {

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("200"), 500));

          game.count200++;
          timings.add("Rice 200 Margin : " + (hitTime - time) + " ms");
        } else if (howOff <= (127 - (OD))) {

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("100"), 500));

          game.count100++;
          timings.add("Rice 100 Margin : " + (hitTime - time) + " ms");
        } else if (howOff <= (151 - (OD))) {

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("50"), 500));


          //  doNotDelete = true;
          game.count50++;
          timings.add("Rice 50 Margin : " + (hitTime - time) + " ms at " + hitTime + " ms");
          System.out.println("rice note 50 hit at " + hitTime + " ms");
        } else if (howOff <= (188 - (OD))) {

          judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));
          System.out.println("miss! combo broke : ( at time " + time + " ms, proper timing at " + hitTime + " ms");
          System.out.println("prevFrame info : " + Arrays.toString(prevFrame.getActiveKeys()) + " , time: " + prevFrame.getEventTime() + " ms");
          System.out.println("OD: " + OD);
          // doNotDelete = true;
          game.countMiss ++;
          game.combo = 0;


          //potential code if needed to correct between frame note judgements
//          boolean validError = false;
//
//          for(String k : prevFrame.getActiveKeys()) {
//            if (k.equals(this.keyNeeded)) {
//              validError = true;
//            }
//          }
//
//          //may need to adjust the marginal misses here
//          if (!validError && Math.abs(prevFrame.getEventTime() - hitTime) > (188 - (OD))) {
//            this.hit = false;
//            System.out.println("miss avoided... at time " + time + " ms, proper timing at " + hitTime + " ms\"");
//            System.out.println("prevFrame info : " + Arrays.toString(prevFrame.getActiveKeys()) + " , time: " + prevFrame.getEventTime() + " ms");
//            System.out.println("OD: " + OD);
//          } else {
//            System.out.println("miss! combo broke : ( at time " + time + " ms, proper timing at " + hitTime + " ms");
//            System.out.println("prevFrame info : " + Arrays.toString(prevFrame.getActiveKeys()) + " , time: " + prevFrame.getEventTime() + " ms");
//            System.out.println("OD: " + OD);
//            doNotDelete = true;
//            game.countMiss ++;
//            game.combo = 0;
//          }

        }
      }
    }

  }

  @Override
  public boolean isHit() {
    return hit;
  }

  @Override
  public void setHit() {
    this.hit = true;
  }

  @Override
  public boolean offMap() {
    return offMap;
  }

  @Override
  public void setOffMap() {
    offMap = true;
  }

  @Override
  public boolean isLN() {
    return false;
  }

  @Override
  public void setLNFirstMiss() {

  }

  @Override
  public boolean isReleased() {
    return true;
  }

  @Override
  public void setReleased() {

  }

  @Override
  public boolean isKeyDown() {
    return false;
  }

  @Override
  public void calcOnFullHold(GameRenderer game) {

  }

  @Override
  public boolean shouldExclude() {
    return false;
  }

  @Override
  public boolean doNotDelete() {
    return doNotDelete;
  }
}
