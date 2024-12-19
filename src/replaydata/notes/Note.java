package replaydata.notes;

import renderer.GameRenderer;
import replaydata.IManiaKeyEvent;
import replaydata.JudgementEvent;

import java.util.ArrayList;

public interface Note {

  String noteStringData();
  String getKey();
  int getTime();
  int getLength();
  void updateStatus(GameRenderer game, String[] keys, int time, ArrayList<String> heldDownPrev,
                    ArrayList<String> timings, IManiaKeyEvent prevFrame, ArrayList<Note> judges, ArrayList<JudgementEvent> judgementRenders);
  boolean isHit();
  void setHit();
  void setOffMap();
  boolean isLN();
  boolean isReleased();
  void setReleased();
  boolean isKeyDown();
  boolean doNotDelete();
  boolean shouldExclude();

}
