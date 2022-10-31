package net.tileeditor.simpleobjectselector;

import net.engine.GamePanel;
import net.tileeditor.general.TileEditor;

import javax.swing.*;
import java.awt.*;

public class SimpleObjectSelectPanel extends JPanel
{
  public SimpleObjectSelectViewPanel viewPanel;
  public SimpleObjectSelectPanelInfo editorPanel;
  public SimpleObjectPropertiesPanel propertiesPanel;

  public SimpleObjectSelectPanel(TileEditor tileEditor)
  {
    setLayout(new BorderLayout());
    propertiesPanel = new SimpleObjectPropertiesPanel(tileEditor);
    add(propertiesPanel, BorderLayout.EAST);
    viewPanel = new SimpleObjectSelectViewPanel(propertiesPanel, tileEditor);
    add(viewPanel, BorderLayout.CENTER);
    editorPanel = new SimpleObjectSelectPanelInfo(tileEditor, viewPanel);
    add(editorPanel, BorderLayout.SOUTH);
  }

  public GamePanel getViewPanel()
  {
    return viewPanel;
  }

  public boolean defaultValues()
  {
    propertiesPanel.defaultValues();
    return editorPanel.defaultValues();
  }

  public GamePanel getBrushPanel()
  {
    return propertiesPanel.getBrushPanel();
  }

  public void updateValues(boolean layoutChanged)
  {
    propertiesPanel.updateValues(layoutChanged);
  }

  public SimpleObjectPropertiesPanel getPropertiesPanel()
  {
    return propertiesPanel;
  }
}
