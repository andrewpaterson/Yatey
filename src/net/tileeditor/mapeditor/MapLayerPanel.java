package net.tileeditor.mapeditor;

import net.tileeditor.Held;
import net.tileeditor.Source;
import net.tileeditor.TheHold;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileClassWrapper;
import net.tileeditor.layers.TileMapWrapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.*;

public class MapLayerPanel extends JPanel implements ActionListener
{
  protected MapLayerTable table;
  protected JButton addButton;
  protected JButton removeButton;
  protected TileEditor tileEditor;
  protected JButton upButton;
  protected JButton downButton;

  public MapLayerPanel(TileEditor tileEditor)
  {
    this.tileEditor = tileEditor;
    setLayout(new GridBagLayout());

    setPreferredSize(new Dimension(230, (int) getPreferredSize().getHeight()));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JPanel panel = new JPanel(new BorderLayout());
    int rows = 3;
    int cols = 4;
    table = new MapLayerTable(this, rows, cols);
    JTableHeader header = table.getTableHeader();

    panel.add(header, BorderLayout.NORTH);
    panel.add(table, BorderLayout.CENTER);

    add(new Label("Layers"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));
    add(panel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

    JPanel buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.add(new JPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    addButton = button("Add...");
    removeButton = button("Remove");
    buttonPanel.add(addButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    buttonPanel.add(removeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(buttonPanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.add(new JPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    upButton = button("Up");
    downButton = button("Down");
    buttonPanel.add(upButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    buttonPanel.add(downButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(buttonPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    addButton.addActionListener(this);
    removeButton.addActionListener(this);
    upButton.addActionListener(this);
    downButton.addActionListener(this);
  }

  private JButton button(String s)
  {
    JButton button = new JButton(s);
    button.setPreferredSize(new Dimension(90, button.getPreferredSize().height));
    button.setMinimumSize(new Dimension(90, button.getPreferredSize().height));
    return button;
  }

  public void updateValues()
  {
    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      int rows = map.getLayers().size();
      tableModel.setRowCount(rows);
      for (int i = 0; i < rows; i++)
      {
        TileArray tileArray = map.getLayers().get(i);
        tableModel.setValueAt(new Boolean(tileArray.visible), i, 0);
        tableModel.setValueAt(tileArray.name, i, 1);
        tableModel.setValueAt(new TileClassWrapper(tileArray.getTileClass()), i, 2);
        tableModel.setValueAt(Integer.toString(i), i, 3);
      }

      table.removeSelectionListener();
      table.getSelectionModel().setSelectionInterval(map.getCurrentLayerIndex(), map.getCurrentLayerIndex());
      table.addSelectionListener();
    }
    else
    {
      tableModel.setRowCount(0);
    }
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == addButton)
    {
      tileEditor.addLayer();
    }
    else if (source == removeButton)
    {
      tileEditor.removeLayer();
    }
    else if (source == upButton)
    {
      tileEditor.moveLayerUp();
    }
    else if (source == downButton)
    {
      tileEditor.moveLayerDown();
    }
  }

  public void selectionChanged(int selectedRow)
  {
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();
    if (map != null)
    {
      source.setCurrentLayer(selectedRow);
      map.setCurrentLayer(selectedRow);
      tileEditor.brushSelected();
      TheHold.getInstance().hold(Held.LAYER_SELECTED);
    }
  }

  public void visibilityChanged(int layer, boolean visible)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      map.setVisibility(layer, visible);
    }
  }

  public int getSelectedRow()
  {
    return table.getSelectedRow();
  }
}
