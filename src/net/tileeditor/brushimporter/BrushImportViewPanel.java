package net.tileeditor.brushimporter;

import net.tileeditor.SimpleObject;
import net.tileeditor.general.SimpleObjectViewPanel;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BrushImportViewPanel extends SimpleObjectViewPanel
{
  private ArrayList<Boolean> ignoredObjects;
  private BrushImporterEditorPanelInfo editorPanelInfo;
  private int rgb;

  public BrushImportViewPanel()
  {
    super();
    ignoredObjects = new ArrayList<Boolean>();
  }

  public void mouseReleased(MouseEvent e)
  {
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
    {
      if (editorPanelInfo.isSelectingColor())
      {
        int x = e.getX();
        int y = e.getY();
        int rgb = ((BufferedImage)(backBufferImage)).getRGB(x, y);
        editorPanelInfo.setEmptyColor(rgb);
      }
      else
      {
        SimpleObject object = pick(e.getX(), e.getY());

        if (object != null)
        {
          int i = simpleObjects.indexOf(object);
          ignoredObjects.set(i, !ignoredObjects.get(i));
        }
      }
    }
  }

  public Color getBorderColour(int i, boolean hovering)
  {
    if (i < ignoredObjects.size())
    {
      Boolean ignored = ignoredObjects.get(i);
      Color color;
      if (ignored)
      {
        color = Color.red;
      }
      else
      {
        color = Color.white;
      }

      if (hovering)
      {
        color = Color.yellow;
      }
      return color;
    }
    return Color.black;
  }

  public void postRender()
  {
    if (editorPanelInfo.isSelectingColor())
    {
      backBuffer.setColor(Color.WHITE);
      backBuffer.drawRect(getWidth() - 31, getHeight() - 31, 20, 20);
      backBuffer.setColor(new Color(rgb));
      backBuffer.fillRect(getWidth() - 30, getHeight() - 30, 19, 19);
    }
  }

  public void mouseMoved(MouseEvent e)
  {
    super.mouseMoved(e);
    if (editorPanelInfo.isSelectingColor())
    {
      int x = e.getX();
      int y = e.getY();
      rgb = ((BufferedImage)(backBufferImage)).getRGB(x, y);
    }
  }

  protected void pickHoveringObject(MouseEvent e)
  {
    if (!editorPanelInfo.isSelectingColor())
    {
      super.pickHoveringObject(e);
    }
  }

  public void clearSimpleObjects()
  {
    super.clearSimpleObjects();
    ignoredObjects = new ArrayList<Boolean>();
  }

  public void addSimpleObject(Object object)
  {
    super.addSimpleObject(new SimpleObject(object));
    ignoredObjects.add(false);
  }

  public ArrayList<Boolean> getIgnoredObjects()
  {
    return ignoredObjects;
  }

  public BrushImporterEditorPanelInfo getEditorPanelInfo()
  {
    return editorPanelInfo;
  }

  public void setEditorPanelInfo(BrushImporterEditorPanelInfo editorPanelInfo)
  {
    this.editorPanelInfo = editorPanelInfo;
  }
}
