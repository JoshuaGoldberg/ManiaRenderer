import java.util.ArrayList;

public class RevisedLongNote implements Note {

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


  //to ensure rice notes/lns ahead of the current ln don't allow a misjudgement
  boolean allowedToAccept = false;



  int firstHit = 0;
  int maxAllowed = 0;
  String reason = "";

  //try some tactics using score v2!!!

  public RevisedLongNote(int xPos, int yPos, int hitTime, int releaseTime, int OD) {
    this.OD = OD;
    this.xPos = xPos;
    this.yPos = yPos;
    this.hitTime = hitTime;
    this.releaseTime = releaseTime;

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
    return "";
  }

  @Override
  public int getX() {
    return 0;
  }

  @Override
  public int getY() {
    return 0;
  }

  @Override
  public String getKey() {
    return "";
  }

  @Override
  public int getTime() {
    return 0;
  }

  @Override
  public int getLength() {
    return 0;
  }

  @Override
  public void updateStatus(GameRenderer game, String[] keys, int time, ArrayList<String> heldDownPrev, ArrayList<String> timings, IManiaKeyEvent prevFrame, ArrayList<Note> judges
  , ArrayList<JudgementEvent> judgementEvents) {

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

        if (valid && (hitTime - time) >= -(127 - (OD))) {
          hitBefore = true;
          heldDown = true;
          game.combo++;
          this.hit = true;
          int howOff = Math.abs(hitTime - time);
          firstHit = howOff;

          //OD related
          if (howOff <= (16)) {
            game.countMax++;
            timings.add("LN start MAX Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= (64 - (OD))) {
            game.count300 ++;
            timings.add("LN start 300 Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= (97 - (OD))) {
            game.count200 ++;
            timings.add("LN start 200 Margin : " + (hitTime - time) + " ms");
          } else if (howOff <= (127 - (OD))) {
            timings.add("LN start 100 Margin : " + (hitTime - time) + " ms");
            game.count100 ++;
          } else if(howOff <= 151 - (3 * (OD)))  {
            timings.add("LN start 50 Margin : " + (hitTime - time) + " ms");
            reason = "hit starting point late!";
            game.count50 ++;
          } else if(hitTime - time <= (188 - (OD)) && hitTime - time > 0) {
            game.countMiss++;
            game.combo = 0;
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
          hitBefore = true;
          game.combo++;


          heldDown = true;
        } else {

          if (!released) {

            //recent change from 188 top 151 here, maybe change?
            if ((releaseTime - time) <= (188 - (OD)) && (releaseTime - time) >= -(127 - (OD)) && heldDownPrev.contains(this.keyNeeded)) {
              released = true;

              int howOffSigned = releaseTime - time;
              int howOff = Math.abs(releaseTime - time);

              //OD related


              //lets figure out if they need the 1.1x and such bonuses
              if (howOff <= (16 * 1.5) && maxAllowed >= 320) {

                doNotDelete = true;

                timings.add("LN end MAX Margin : " + (releaseTime - time) + " ms");
                game.countMax++;
              } else if (howOff <= ((64 - (OD)) * 1.5) && maxAllowed >= 300) {

                timings.add("LN end 300 Margin : " + (releaseTime - time) + " ms");
                game.count300++;
              } else if (howOff <= ((97 - (OD)) * 1.5) && maxAllowed >= 200) {

                timings.add("LN end 200 Margin : " + (releaseTime - time) + " ms");
                game.count200++;
              } else if (howOff <= ((127 - (OD)) * 1.5) && maxAllowed >= 100) {

                timings.add("LN end 100 Margin : " + (releaseTime - time) + " ms");
                game.count100++;
              } else if(howOff <= 151 - (3 * (OD)) * 1.5) {
                timings.add("LN end 50 Margin : " + (releaseTime - time) + " ms");
                if (reason.isEmpty()) {
                  reason = "hit second point late!";
                }

                System.out.println("50 hit for reason: " + reason + " at time: " + time + " note at time: " + hitTime + " & " + releaseTime);
                System.out.println("max allowed = " + maxAllowed);
                System.out.println("first: " + firstHit + " second: " + howOff + " total: " + (firstHit + howOff) + ", second point: " + howOffSigned);
                game.count50++;
                reason = "";
              } else if(releaseTime - time <= (188 - (OD) * 1.5) && releaseTime - time > 0) {
                game.countMiss++;
                game.combo = 0;
                doNotDelete = true;
              }
            } else {

             if (hitBefore) {
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
    return false;
  }

  @Override
  public void setHit() {

  }

  @Override
  public boolean offMap() {
    return false;
  }

  @Override
  public void setOffMap() {

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
    return false;
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
  public boolean doNotDelete() {
    return false;
  }

  @Override
  public boolean shouldExclude() {
    return false;
  }
}
