package net.tileeditor.mapeditor;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Vector;

public class MapLayerTable extends JTable
{
  public CheckBoxTableRenderer checkBoxTableRenderer;
  public DefaultCellEditor checkBoxTableEditor;
  private MapLayerPanel mapLayerPanel;
  private LayerSelectionListener layerSelectionListener;

  public MapLayerTable(MapLayerPanel mapLayerPanel, int numRows, int numColumns)
  {
    super(numRows, numColumns);
    this.mapLayerPanel = mapLayerPanel;
    checkBoxTableRenderer = null;
    checkBoxTableEditor = null;

    getColumnModel().getColumn(0).setHeaderValue(" ");
    getColumnModel().getColumn(0).setPreferredWidth(21);
    getColumnModel().getColumn(1).setHeaderValue("Name");
    getColumnModel().getColumn(2).setHeaderValue("Type");
    getColumnModel().getColumn(3).setHeaderValue("#");
    getColumnModel().getColumn(3).setPreferredWidth(14);
    getTableHeader().resizeAndRepaint();
    ListSelectionModel selectionModel = new SingleRowAlwaysSelectedSelectionModel();
    setSelectionModel(selectionModel);
    addSelectionListener();
  }

  public void tableChanged(TableModelEvent e)
  {
    super.tableChanged(e);
    DefaultTableModel tableModel = (DefaultTableModel) getModel();
    if (e.getType() == TableModelEvent.UPDATE)
    {
      if (mapLayerPanel != null)
      {
        int row = e.getFirstRow();
        Vector vector = (Vector) tableModel.getDataVector().get(row);
        Boolean checked = (Boolean) vector.get(0);
        mapLayerPanel.visibilityChanged(row, checked);
      }
    }
  }

  public boolean isCellEditable(int row, int column)
  {
    return column == 0;
  }

  public TableCellRenderer getCellRenderer(int row, int column)
  {
    if (column == 0)
    {
      if (checkBoxTableRenderer == null)
      {
        checkBoxTableRenderer = new CheckBoxTableRenderer();
      }
      return checkBoxTableRenderer;
    }
    else
    {
      return super.getCellRenderer(row, column);
    }
  }

  public TableCellEditor getCellEditor(int row, int column)
  {
    if (column == 0)
    {
      if (checkBoxTableEditor == null)
      {
        JCheckBox checkBox = new JCheckBox();
        checkBoxTableEditor = new DefaultCellEditor(checkBox);
      }
      return checkBoxTableEditor;
    }
    else
    {
      return super.getCellEditor(row, column);
    }
  }

  public void addSelectionListener()
  {
    layerSelectionListener = new LayerSelectionListener(mapLayerPanel);
    selectionModel.addListSelectionListener(layerSelectionListener);
  }

  public void removeSelectionListener()
  {
    selectionModel.removeListSelectionListener(layerSelectionListener);
    layerSelectionListener = null;
  }
}
