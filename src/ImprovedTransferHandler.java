import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

import javax.swing.*;

public class ImprovedTransferHandler extends TransferHandler {

  JLabel text;

  public ImprovedTransferHandler(JLabel text) {
    this.text = text;
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }

    try {
      // Get the dropped file
      List<?> droppedFiles = (List<?>) support.getTransferable()
              .getTransferData(DataFlavor.javaFileListFlavor);

      // Get the first file (in case multiple are dropped)
      File file = (File) droppedFiles.getFirst();

      // Set the file path as the label's text
      text.setText(file.getAbsolutePath());

      return true;
    } catch (Exception e) {
      System.out.println("Can't Import File");
    }
    return false;
  }

}
