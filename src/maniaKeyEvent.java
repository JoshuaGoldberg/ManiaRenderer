import java.util.Arrays;

public class maniaKeyEvent implements IManiaKeyEvent{

  int eventTime;
  String[] activeKeys;

  public maniaKeyEvent(int eventTime, String[] activeKeys) {
    this.eventTime = eventTime;
    this.activeKeys = activeKeys;
  }

  @Override
  public String eventStringData() {
    return "At " + eventTime + " ms. " + Arrays.toString(activeKeys) + " pressed.";
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
