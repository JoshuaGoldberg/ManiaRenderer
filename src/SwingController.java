import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class SwingController implements ActionListener {

  SwingView view;

  public SwingController(SwingView view) {
    this.view = view;
  }

  RendererRunner runner;

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "replayFile": {
        final JFileChooser fchooser = new JFileChooser(".");
        int retvalue = fchooser.showSaveDialog(view);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          view.setReplayText(f.getAbsolutePath());
        }
        break;
      }
      case "audioFile": {
        final JFileChooser fchooser = new JFileChooser(".");
        int retvalue = fchooser.showSaveDialog(view);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          view.setAudioText(f.getAbsolutePath());
        }
        break;
      }
      case "osuFile": {
        final JFileChooser fchooser = new JFileChooser(".");
        int retvalue = fchooser.showSaveDialog(view);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File f = fchooser.getSelectedFile();
          view.setOsuText(f.getAbsolutePath());
        }
        break;
      }
      case "render": {

        if(!view.inProgress) {


          runner = new RendererRunner(view.getReplayText(), view.getAudioText(), view.getOsuText(), view);

          Thread saveThread = new Thread(() -> {
            try {
              runner.run();
            } catch (IOException ex) {
              view.fileError();
            }
          });

          saveThread.start();
          view.setProgress(true);
        } else {
          view.renderInProgress();
        }

        break;
      }
    }
  }

  public void execute() {

    view.setListeners(this);

  }
}
