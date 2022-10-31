package net.tileeditor.mapeditor;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LayerSelectionListener implements ListSelectionListener
{
  private MapLayerPanel mapLayerPanel;

  public LayerSelectionListener(MapLayerPanel mapLayerPanel)
  {
    this.mapLayerPanel = mapLayerPanel;
  }

  public void valueChanged(ListSelectionEvent e)
  {
    if (!e.getValueIsAdjusting())
    {
      SingleRowAlwaysSelectedSelectionModel source = (SingleRowAlwaysSelectedSelectionModel) e.getSource();
      int selectedRow = source.getMinSelectionIndex();
      if (selectedRow != -1)
      {
        mapLayerPanel.selectionChanged(selectedRow);
      }
    }
  }
}
