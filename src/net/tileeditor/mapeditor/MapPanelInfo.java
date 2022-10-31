package net.tileeditor.mapeditor;

import net.tileeditor.Settings;
import net.tileeditor.Source;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileMap;
import net.tileeditor.layers.TileMapWrapper;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapPanelInfo extends JPanel implements ActionListener
{
  public TileEditor tileEditor;
  private JButton ontop;
  private JButton beneath;
  private JButton noGrid;
  private JButton mapSettings;
  public JLabel mapName;

  public MapPanelInfo(final TileEditor tileEditor)
  {
    this.tileEditor = tileEditor;

    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    JPanel topPanel = new JPanel(new GridBagLayout());
    int col = 0;
    ontop = new JButton("Grid Ontop");
    beneath = new JButton("Grid Beneath");
    noGrid = new JButton("No Grid");
    mapSettings = new JButton("Map Settings");
    mapName = new JLabel("");
    topPanel.add(ontop, new GridBagConstraints(++col, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    topPanel.add(beneath, new GridBagConstraints(++col, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    topPanel.add(noGrid, new GridBagConstraints(++col, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    topPanel.add(new JPanel(), new GridBagConstraints(++col, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    topPanel.add(mapName, new GridBagConstraints(++col, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    topPanel.add(mapSettings, new GridBagConstraints(++col, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    add(topPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, WIDTH, new Insets(0, 0, 0, 0), 0, 0));

    ontop.addActionListener(this);
    beneath.addActionListener(this);
    noGrid.addActionListener(this);
    mapSettings.addActionListener(this);
  }

  public void updateVales()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      mapName.setText(map.name+ "  ");
    }
    else
    {
      mapName.setText("");
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == ontop)
    {
      Settings.getInstance().setGridStyle(Settings.GRID_ONTOP);
    }
    else if (source == beneath)
    {
      Settings.getInstance().setGridStyle(Settings.GRID_BENEATH);
    }
    else if (source == noGrid)
    {
      Settings.getInstance().setGridStyle(Settings.GRID_NONE);
    }
    else if (source == mapSettings)
    {
      if (Source.getInstance().getCurrentMap() != null)
      {
        MapSettingsDialog dialog = new MapSettingsDialog(tileEditor);
        dialog.setVisible(true);
      }
    }
  }
}

