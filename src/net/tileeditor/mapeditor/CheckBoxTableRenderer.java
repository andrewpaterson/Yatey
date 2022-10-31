package net.tileeditor.mapeditor;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CheckBoxTableRenderer extends JCheckBox implements TableCellRenderer
{
  public CheckBoxTableRenderer()
  {
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        boolean b = ((Boolean) value).booleanValue();
        this.setSelected(b);
        return this;
    }

  public void actionPerformed(ActionEvent e)
  {
    System.out.println("CheckBoxTableRenderer.actionPerformed");
  }
}
