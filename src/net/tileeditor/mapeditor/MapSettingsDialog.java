package net.tileeditor.mapeditor;

import net.tileeditor.general.AcceptCancelDialog;
import net.tileeditor.general.TileEditor;
import net.tileeditor.general.AcceptCancelPanel;
import net.tileeditor.Source;
import net.tileeditor.Settings;
import net.tileeditor.layers.TileMapWrapper;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

public class MapSettingsDialog extends AcceptCancelDialog
{
  public JTextField mapWidthText;
  public JTextField mapHeightText;
  public JTextField mapNameText;
  public JTextField celWidthText;
  public JTextField celHeightText;

  public MapSettingsDialog(TileEditor tileEditor, String title, boolean modal)
  {
    super(tileEditor, title, modal);
  }

  public MapSettingsDialog(TileEditor tileEditor)
  {
    this("Map Settings", tileEditor);
  }

  public MapSettingsDialog(String dialogName, TileEditor tileEditor)
  {
    super(tileEditor, dialogName, true);
    setLayout(new GridBagLayout());
    int width = 220;
    int height = 210;
    setSize(width, height);

    Dimension dimension = tileEditor.getSize();
    Point point = tileEditor.getLocation();
    setLocation(dimension.width / 2 - width / 2 + point.x, dimension.height / 2 - height / 2 + point.y);

    Point size = getMapSize();
    String mapWidth = Integer.toString(size.x);
    String mapHeight = Integer.toString(size.y);
    Point celSize = getCelSize();
    String celWidth = Integer.toString(celSize.x);
    String celHeight = Integer.toString(celSize.y);
    String name = getMapName();

    int row = 0;
    add(label("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    mapNameText = textField(name);
    add(mapNameText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Map Width:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    mapWidthText = textField(mapWidth);
    add(mapWidthText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Map Height:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    mapHeightText = textField(mapHeight);
    add(mapHeightText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Cel Width:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    celWidthText = textField(celWidth);
    add(celWidthText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Cel Height:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    celHeightText = textField(celHeight);
    add(celHeightText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(new AcceptCancelPanel(this), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
  }

  protected String getMapName()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return map.name;
  }

  protected Point getCelSize()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return new Point(map.celWidth, map.celHeight);
  }

  protected Point getMapSize()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return new Point(map.getWidth(), map.getHeight());
  }

  public void accepted()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    boolean valid = map.set(mapNameText.getText(), mapWidthText.getText(), mapHeightText.getText(), celWidthText.getText(), celHeightText.getText());
    if (valid)
    {
      Settings.getInstance().setMapSize(map.celWidth, map.celHeight, map.tileMap.width, map.tileMap.height);
      Settings.getInstance().save();
      super.accepted();
      tileEditor.updateVales();
    }
  }
}
