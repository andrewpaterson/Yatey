package net.tileeditor.mapeditor;

import net.tileeditor.general.TileEditor;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;

public class MapPanel extends JPanel
{
  private MapViewPanel viewPanel;
  private MapBrushPanel brushPanel;
  private MapLayerPanel layerPanel;
  private MapPanelInfo infoPanel;

  public MapPanel(TileEditor tileEditor)
  {
    viewPanel = new MapViewPanel(this);
    setLayout(new BorderLayout());
    add(viewPanel, BorderLayout.CENTER);
    brushPanel = new MapBrushPanel(tileEditor);
    layerPanel = new MapLayerPanel(tileEditor);

    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    panel.setLayout(new GridBagLayout());
    panel.add(brushPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
    panel.add(layerPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));

    add(panel, BorderLayout.EAST);

    infoPanel = new MapPanelInfo(tileEditor);
    add(infoPanel, BorderLayout.SOUTH);
  }

  public MapViewPanel getViewPanel()
  {
    return viewPanel;
  }

  public CompoundBrushViewPanel getBrushPanel()
  {
    return brushPanel.getBrushPanel();
  }

  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    viewPanel.setEnabled(enabled);
    brushPanel.getBrushPanel().setEnabled(enabled);
  }

  public void updateValues()
  {
    layerPanel.updateValues();
    infoPanel.updateVales();
    brushPanel.updateValues();
  }

  public MapPanelInfo getInfoPanel()
  {
    return infoPanel;
  }

  public MapLayerPanel getLayerPanel()
  {
    return layerPanel;
  }

  public void resetCamera()
  {
    viewPanel.resetCamera();
  }

  public void brushSelected()
  {
    getBrushPanel().centerBrush();
    brushPanel.updateValues();
  }
}
