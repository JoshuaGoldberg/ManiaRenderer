public class RunProgram {

  public static void main(String[] args) {

    SwingView view = new SwingView();
    SwingController controller = new SwingController(view);
    controller.execute();
  }
}