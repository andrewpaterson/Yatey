package net.tileeditor.general;

import net.tileeditor.Source;
import net.tileeditor.mapeditor.MapSettingsDialog;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

public class AddMapDialog extends MapSettingsDialog
{
  public AddMapDialog(TileEditor tileEditor, String title, boolean modal)
  {
    super(tileEditor, title, modal);
  }

  public AddMapDialog(TileEditor tileEditor)
  {
    super("Add New Map", tileEditor);
  }

  protected Point getCelSize()
  {
    return Source.getInstance().getDefaultCelSize();
  }

  protected Point getMapSize()
  {
    return Source.getInstance().getDefaultMapSize();
  }


  public String getMapName()
  {
    return "";
  }

  public void accepted()
  {
    try
    {
      Integer width = Integer.valueOf(mapWidthText.getText());
      Integer height = Integer.valueOf(mapHeightText.getText());
      Integer celWidth = Integer.valueOf(celWidthText.getText());
      Integer celHeight = Integer.valueOf(celHeightText.getText());
      String name = mapNameText.getText();
      if ((name.length() > 0) && ((width > 0) && (height > 0)))
      {
        Source.getInstance().addMap(name, width, height, celWidth, celHeight);
        super.accepted();
      }
    }
    catch (NumberFormatException e)
    {
    }
  }
}
