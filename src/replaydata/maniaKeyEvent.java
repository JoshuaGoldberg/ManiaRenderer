package replaydata;

public class maniaKeyEvent implements IManiaKeyEvent{

  private final int eventTime;
  private final String[] activeKeys;

  public maniaKeyEvent(int eventTime, String[] activeKeys) {
    this.eventTime = eventTime;
    this.activeKeys = activeKeys;
  }

  @Override
  public int getEventTime() {
    return eventTime;
  }

  @Override
  public String[] getActiveKeys() {
    return activeKeys;
  }
}
