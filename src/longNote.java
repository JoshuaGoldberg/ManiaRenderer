import java.util.ArrayList;

public class longNote implements Note {

  ArrayList<String> keyInput = new ArrayList<>();

  int xPos;
  int yPos;
  int hitTime;
  int releaseTime;
  String keyNeeded;
  boolean hit = false;
  boolean released = false;
  boolean offMap = false;
  boolean releaseOffMap = false;
  boolean heldDown = false;
  boolean hitBefore = false;
  boolean excludeFromJudges = false;
  boolean doNotDelete = false;
  int OD;
  ImageGrabber imageGrabber;

  //to ensure rice notes/lns ahead of the current ln don't allow a misjudgement
  boolean allowedToAccept = false;
  ArrayList<String> mods;


  int firstHit = 0;
  int maxAllowed = 0;
  String reason = "";
  double rawOD;

  //try some tactics using score v2!!!

  public longNote(int xPos, int yPos, int hitTime, int releaseTime, int OD, double rawOD, ImageGrabber imageGrabber, ArrayList<String> mods) {
    this.rawOD = rawOD;
    this.mods = mods;
    this.OD = OD;
    this.xPos = xPos;
    this.yPos = yPos;
    this.hitTime = hitTime;
    this.releaseTime = releaseTime;

    this.imageGrabber = imageGrabber;

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
    return "Long note at: " + hitTime + " milliseconds, release time: " + releaseTime + " milliseconds, requires " + keyNeeded;
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
    return releaseTime - hitTime;
  }

  @Override
  public void updateStatus(GameRenderer game, String[] keys, int time, ArrayList<String> heldDownPrev, ArrayList<String> timings, IManiaKeyEvent prevFrame, ArrayList<Note> judges
          , ArrayList<JudgementEvent> judgementRenders) {

    if(!heldDownPrev.contains(this.keyNeeded)) {
      allowedToAccept = true;
    }

    if(allowedToAccept) {

      if (!hit) {

        boolean valid = false;
        for (String k : keys) {
          if (k.equals(this.keyNeeded) && !heldDownPrev.contains(this.keyNeeded)) {
            valid = true;
            break;
          }
        }

        if (valid && (hitTime - time) >= -((151 - (OD))) && (hitTime - time) <= 188 - (OD)) {
          hitBefore = true;
          heldDown = true;
          game.combo++;
          this.hit = true;
          int howOff = Math.abs(hitTime - time);
          firstHit = howOff;

          //OD related
          if (howOff <= (int) ((24.9 - (rawOD * 1.1)))) {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("max"), 500));

            game.countMax++;
            timings.add("LN start MAX Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= ((64 - (OD)))) {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("300"), 500));

            game.count300 ++;
            timings.add("LN start 300 Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= ((97 - (OD)))) {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("200"), 500));

            game.count200 ++;
            timings.add("LN start 200 Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= ((127 - (OD)))) {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("100"), 500));

            timings.add("LN start 100 Margin : " + (hitTime - time) + " ms");
            game.count100 ++;
          } else if(howOff <= (151 - ((OD))))  {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("50"), 500));

            timings.add("LN start 50 Margin : " + (hitTime - time) + " ms");
            reason = "hit starting point late!";
            game.count50 ++;
          } else if(howOff <= (188 - (OD))) {
            judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));

            game.countMiss++;
            game.combo = 0;
            System.out.println("ln start miss :skullemoji: (ln hit but within error timeframe)");
          } else {
            System.out.println("no start judgement given! not good. HowOff: " + howOff + " , hitTime - time =  " + (hitTime - time));
          }
        }
      } else {

        boolean valid = false;

        for (String k : keys) {
          if (k.equals(this.keyNeeded)) {
            valid = true;
            break;
          }
        }

        if (valid && !released) {

          if(!heldDownPrev.contains(this.keyNeeded) && releaseTime - time < 0 && game.nextValidLNExist(keyNeeded, hitTime)) {
            excludeFromJudges = true;
              Note replaced = game.getNextValidLN(keyNeeded);
              replaced.updateStatus(game, keys, time, heldDownPrev, timings, prevFrame, judges, judgementRenders);
          }

          hitBefore = true;
         // game.combo++;
          heldDown = true;
        } else {

          if (!released) {

            //may want this one deleted?
            if(!heldDownPrev.contains(this.keyNeeded) && releaseTime - time < 0 && game.nextValidLNExist(keyNeeded, hitTime)) {
              excludeFromJudges = true;
              Note replaced = game.getNextValidLN(keyNeeded);
              replaced.updateStatus(game, keys, time, heldDownPrev, timings, prevFrame, judges, judgementRenders);
            }

            //recent change from 188 to 151 here, maybe change?
            if ((releaseTime - time) <= (int) (188 - ((rawOD * 3))) * 1.5 && (releaseTime - time) >= (int) (-(151 - (rawOD * 3)) * 1.5) && heldDownPrev.contains(this.keyNeeded)) {
              released = true;
              game.combo++;
              int howOffSigned = releaseTime - time;
              int howOff = Math.abs(releaseTime - time);

              //OD related
              //lets figure out if they need the 1.1x and such bonuses
              if (howOff <= ((int) ((24.9 - (rawOD * 1.1)) * 1.5)) && maxAllowed != 50) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("max"), 500));

                timings.add("LN end MAX Margin : " + (releaseTime - time) + " ms");
                game.countMax++;
              } else if (howOff <= (int) ((64 - (rawOD * 3)) * 1.5) && maxAllowed != 50) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("300"), 500));

                timings.add("LN end 300 Margin : " + (releaseTime - time) + " ms");
                game.count300++;
              } else if (howOff <= (int) ((97 - (rawOD * 3)) * 1.5) && maxAllowed != 50) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("200"), 500));

                timings.add("LN end 200 Margin : " + (releaseTime - time) + " ms");
                game.count200++;
              } else if (howOff <= (int) ((127 - (rawOD * 3)) * 1.5) && maxAllowed != 50) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("100"), 500));

                timings.add("LN end 100 Margin : " + (releaseTime - time) + " ms");
                game.count100++;
              } else if(howOff <= (int) ((151 - ((rawOD * 3))) * 1.5)) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("50"), 500));


                timings.add("LN end 50 Margin : " + (releaseTime - time) + " ms");
                if (reason.isEmpty()) {
                  reason = "hit second point late!";
                }

                System.out.println("50 hit for reason: " + reason + " at time: " + time + " note at time: " + hitTime + " & " + releaseTime);
                System.out.println("max allowed = " + maxAllowed);
                System.out.println("first: " + firstHit + " second: " + howOff + " total: " + (firstHit + howOff) + ", second point: " + howOffSigned);
                game.count50++;
                reason = "";
              } else if(howOff <= (int) (188 - ((rawOD * 3))) * 1.5) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));

                game.countMiss++;
                game.combo = 0;
                System.out.println("ln release miss :skullemoji: at time " + time);
              } else {
                System.out.println("no release judgement given! not good.");
              }
            } else {

              if (hitBefore) {
                judgementRenders.add(new JudgementEvent(imageGrabber.grabImage("miss"), 500));

                reason = "lifted mid ln at time " + time + " ms, note start at " + hitTime + " ms, needed key " + keyNeeded + " , held down prev = " + heldDownPrev + " released : " + released;
                maxAllowed = 50;
                game.combo = 0;
              }

            }

            heldDown = false;
            for (String k : keys) {
              if (k.equals(this.keyNeeded)) {
                heldDown = true;
                break;
              }
            }

          }
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
    hit = true;
  }

  @Override
  public void setOffMap() {
    offMap = true;
  }

  @Override
  public boolean isLN() {
    return true;
  }

  @Override
  public boolean isReleased() {
    return released;
  }

  @Override
  public void setReleased() {
    released = true;
  }

  @Override
  public boolean isKeyDown() {
    return heldDown;
  }

// Currently not in use
//  public void calcOnFullHold(GameRenderer game) {
//
//    //temp adjustment, may not be accurate
//    //System.out.println("held too long! 50 awarded. First timing: " + firstHit + " ms");
//    //game.count50++;
//
//    //mess with the value here
//    int secondTime = (127 - (OD));
//
//    //OD related
//    if (secondTime + firstHit <= (16 * 2.4) && maxAllowed >= 320) {
//      game.countMax++;
//    } else if (secondTime + firstHit <= ((64 - (OD)) * 2.2) && maxAllowed >= 300) {
//      game.count300++;
//    } else if (secondTime + firstHit <= ((97 - (OD)) * 2) && maxAllowed >= 200) {
//      game.count200++;
//    } else if (secondTime + firstHit <= ((127 - (OD)) * 2) && maxAllowed >= 100) {
//      game.count100++;
//    } else {
//      System.out.println("held too long! 50 awarded. Requires key " + keyNeeded + " First timing: " + firstHit + " ms, second (static): " + secondTime + " ms");
//      game.count50++;
//    }
//
//  }

  @Override
  public boolean doNotDelete() {
    return doNotDelete;
  }

  @Override
  public boolean shouldExclude() {
    return excludeFromJudges;
  }
}
