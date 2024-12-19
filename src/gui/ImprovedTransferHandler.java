package gui;

import gui.panels.InformationPanel;

import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.awt.Dimension;

import javax.swing.*;

public class ImprovedTransferHandler extends TransferHandler {
  private final JFrame frame;
  private final InformationPanel panel;
  private InformationPanel otherPanel;

  public ImprovedTransferHandler(InformationPanel text, JFrame frame) {
    this.panel = text;
    this.frame = frame;
  }

  public ImprovedTransferHandler(InformationPanel text, InformationPanel otherPanel, JFrame frame) {
    this.panel = text;
    this.frame = frame;
    this.otherPanel = otherPanel;
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }

  private String extractAudioFileName(String file) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("AudioFilename")) {
          // Split the line at ':' to get the OD value
          String[] parts = line.split(":");
          if (parts.length > 1) {
            return parts[1].trim();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Return null if OD value is not found
    return otherPanel.getLabel().getText();
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }

    try {
      List<?> droppedFiles = (List<?>) support.getTransferable()
              .getTransferData(DataFlavor.javaFileListFlavor);

      File file = (File) droppedFiles.getFirst();
      String path = file.getAbsolutePath();

      panel.changeText(file.getAbsolutePath());

      if (otherPanel != null) {
        String targetChar = "\\";

        int lastIndex = path.lastIndexOf(targetChar);

        if (lastIndex != -1) {
          String result = path.substring(0, lastIndex);
          String audioFile = extractAudioFileName(path);

          otherPanel.changeText(result + "\\" + audioFile);
        }
      }

      Dimension currentSize = frame.getSize();
      frame.pack();
      Dimension newSize = frame.getSize();

      if (newSize.width < currentSize.width) {
        frame.setSize(currentSize);
      } else {
        frame.setSize(new Dimension(newSize.width, currentSize.height));
      }

      return true;
    } catch (Exception e) {
      System.out.println("Can't Import File");
    }
    return false;
  }

}
