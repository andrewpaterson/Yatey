package net.tileeditor.simpleobjectselector;

import net.tileeditor.general.SimpleObjectViewPanel;
import net.tileeditor.general.TileEditor;
import net.tileeditor.source.SimpleObjectWrapper;
import net.tileeditor.source.ObjectWrapperFactory;
import net.tileeditor.Source;
import net.tileeditor.SimpleObject;
import net.tileeditor.TheHold;
import net.tileeditor.Held;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class SimpleObjectSelectViewPanel extends SimpleObjectViewPanel
{
  private SimpleObjectPropertiesPanel propertiesPanel;
  private TileEditor tileEditor;

  public SimpleObjectSelectViewPanel(SimpleObjectPropertiesPanel propertiesPanel, TileEditor tileEditor)
  {
    super();
    this.propertiesPanel = propertiesPanel;
    this.tileEditor = tileEditor;
  }

  public Color getBorderColour(int i, boolean hovering)
  {
    Color color;
    color = Color.white;

    if (hovering)
    {
      color = Color.yellow;
    }
    return color;
  }

  public void mouseReleased(MouseEvent e)
  {
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
    {
      SimpleObject object = pick(e.getX(), e.getY());
      if (object != null)
      {
        Source.getInstance().setCurrentBrushObject(object.getObjectClass(), object);
        propertiesPanel.setSelectedObject(object);
        tileEditor.brushSelected();
      }
    }
  }

  public void mousePressed(MouseEvent e)
  {
    super.mousePressed(e);
    int modifiers = e.getModifiersEx();
    if (((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) && ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0))
    {
      SimpleObject object = pick(e.getX(), e.getY());
      if (object != null)
      {
        Source.getInstance().setCurrentBrushObject(object.getObjectClass(), object);
        tileEditor.brushSelected();
        propertiesPanel.getTileEditor().showMapPanel();
        TheHold.getInstance().hold(Held.BRUSH_SELECTED);
      }
    }
  }
}

