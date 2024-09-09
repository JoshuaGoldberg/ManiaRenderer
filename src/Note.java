import java.util.ArrayList;

public interface Note {

  String noteStringData();
  int getX();
  int getY();
  String getKey();
  int getTime();
  int getLength();
  void updateStatus(GameRenderer game, String[] keys, int time, ArrayList<String> heldDownPrev,
                    ArrayList<String> timings, IManiaKeyEvent prevFrame, ArrayList<Note> judges, ArrayList<JudgementEvent> judgementRenders);
  boolean isHit();
  void setHit();
  boolean offMap();
  void setOffMap();
  boolean isLN();
  void setLNFirstMiss();
  boolean isReleased();
  void setReleased();
  boolean isKeyDown();
  void calcOnFullHold(GameRenderer game);
  boolean doNotDelete();
  boolean shouldExclude();

}
