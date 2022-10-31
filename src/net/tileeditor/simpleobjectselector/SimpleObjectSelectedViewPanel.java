package net.tileeditor.simpleobjectselector;

import net.tileeditor.general.SimpleObjectViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

public class SimpleObjectSelectedViewPanel extends SimpleObjectViewPanel
{
  private SimpleObjectPropertiesPanel propertiesPanel;

  public SimpleObjectSelectedViewPanel(SimpleObjectPropertiesPanel propertiesPanel)
  {
    super();
    this.propertiesPanel = propertiesPanel;
    setBorder(BorderFactory.createLineBorder(Color.RED));
  }

  public Color getBorderColour(int i, boolean hovering)
  {
    return Color.white;
  }

  public void mouseReleased(MouseEvent e)
  {
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
    {
      propertiesPanel.selectBrush();
    }
  }

  public void postRender()
  {
    if (mouseIn)
    {
      backBuffer.setColor(new Color(184, 207, 229));
      backBuffer.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
      backBuffer.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
    }
  }

  public void renderObjects()
  {
    super.renderObjects();
  }

  protected void renderBorder(int xPos, int yPos, Color color)
  {
  }
}
