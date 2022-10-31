package net.tileeditor.general;

import net.tileeditor.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileChooser
{
  public static File show(TileEditor tileEditor, String title, String directory, boolean save, String... extensions)
  {
    JFileChooser fileChooser = create(title, directory);
    if (save)
    {
      fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    }
    else
    {
      fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    }

    extensions(fileChooser, extensions);

    int returnVal = fileChooser.showOpenDialog(tileEditor);
    File file;
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      file = fileChooser.getSelectedFile();
    }
    else
    {
      file = null;
    }
    return file;
  }

  public static File[] showMultiSelectLoad(TileEditor tileEditor, String title, String directory, String... extensions)
  {
    JFileChooser fileChooser = create(title, directory);
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    fileChooser.setMultiSelectionEnabled(true);
    extensions(fileChooser, extensions);
    int returnVal = fileChooser.showOpenDialog(tileEditor);
    File files[];
    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      files = fileChooser.getSelectedFiles();
      if (files.length == 0)
      {
        files = null;
      }
    }
    else
    {
      files = null;
    }
    return files;
  }

  private static void extensions(JFileChooser fileChooser, String... extensions)
  {
    if (extensions.length > 0)
    {
      StringBuffer buffer = new StringBuffer();
      buffer.append("Files of type: ");
      for (String extension : extensions)
      {
        buffer.append(extension);
        buffer.append(", ");
      }
      buffer.delete(buffer.length() - 2, buffer.length());
      FileNameExtensionFilter filter = new FileNameExtensionFilter(buffer.toString(), extensions);
      fileChooser.setFileFilter(filter);
    }
  }

  private static JFileChooser create(String title, String directory)
  {
    JFileChooser fileChooser;
    if (directory == null)
    {
      fileChooser = new JFileChooser();
    }
    else
    {
      fileChooser = new JFileChooser(directory);
    }
    fileChooser.setDialogTitle(title);
    return fileChooser;
  }

}
