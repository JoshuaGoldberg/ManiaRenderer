package gui;

import renderer.RendererRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class SwingController implements ActionListener {

  private final SwingView view;
  private RendererRunner runner;

  public SwingController(SwingView view) {
    this.view = view;
  }

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

        boolean setRender = false;

        if(!view.inProgress) {

          if(view.getAudioText().equals("Audio File Path") ||
             view.getReplayText().equals(".osr File Path") ||
             view.getOsuText().equals(".osu File Path")) {
            view.filesSubmittedError();
          }else if(!view.getAudioText().contains(".mp3") && !view.getAudioText().contains(".ogg")) {
            view.audioError();
          } else {

            runner = new RendererRunner(
                    view.getReplayText(),
                    view.getAudioText(),
                    view.getOsuText(),
                    view,
                    !view.getQualityCheck(),
                    view.getFPSCheck(),
                    view.getIsNvidiaCheck()
            );

            Thread saveThread = new Thread(() -> {
              try {
                view.setProgress(true);
                runner.run();
              } catch (IOException | NullPointerException ex) {
                view.fileError();
                view.setProgress(false);
              }
            });

            saveThread.start();
          }
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
