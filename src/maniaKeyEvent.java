public class maniaKeyEvent implements IManiaKeyEvent{

  int eventTime;
  String[] activeKeys;

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
