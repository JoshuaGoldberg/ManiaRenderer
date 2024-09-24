import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;
import java.awt.Dimension;

import javax.swing.*;

public class ImprovedTransferHandler extends TransferHandler {

  JLabel text;
  JFrame frame;

  public ImprovedTransferHandler(JLabel text, JFrame frame) {
    this.text = text;
    this.frame = frame;
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
      List<?> droppedFiles = (List<?>) support.getTransferable()
              .getTransferData(DataFlavor.javaFileListFlavor);

      File file = (File) droppedFiles.getFirst();

      text.setText(file.getAbsolutePath());
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
