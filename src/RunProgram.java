import gui.SwingController;
import gui.SwingView;

public class RunProgram {

  public static void main(String[] args) {

    SwingView view = new SwingView();
    SwingController controller = new SwingController(view);
    controller.execute();

    /************************************Extra Stuff*********************************************/

    //System.out.println(result);

    //System.out.println(data.numMax300);
    //System.out.println(Arrays.toString(data.notetypes.replayData));

    //System.out.println(events.size());
    //for(int[] keyEvent : keyEvents) {
      //System.out.println(Arrays.toString(keyEvent));
    //}
    //System.out.println(notetypes.replayData.decompressToText(data.notetypes.replayData));
  }
}