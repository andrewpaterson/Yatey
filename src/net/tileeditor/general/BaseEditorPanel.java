package net.tileeditor.general;

import net.engine.GamePanel;
import net.engine.graphics.Sprite;
import net.tileeditor.SimpleBrush;
import net.tileeditor.source.SimpleObjectWrapper;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public abstract class BaseEditorPanel extends GamePanel implements MouseMotionListener, MouseListener
{
  public static final Color BORDER_COLOR = new Color(228, 228, 228);

  public int mouseX;
  public int mouseY;

  public boolean mouseIn;

  public BaseEditorPanel(boolean showStatistics)
  {
    super(showStatistics);
    addMouseMotionListener(this);
    addMouseListener(this);
    mouseIn = false;
  }

  public void resizedBuffer()
  {
    camera.setPosition((-screenWidth) / 2, (-screenHeight) / 2);
  }

  public void preRender()
  {
    backBuffer.setColor(Color.gray);
    backBuffer.fillRect(0, 0, screenWidth, screenHeight);
  }

  protected void initialise()
  {
  }

  public void mouseDragged(MouseEvent e)
  {
    int modifiers = e.getModifiersEx();

    if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0)
    {
      int xDiff = e.getX() - mouseX;
      int yDiff = e.getY() - mouseY;

      camera.move(-xDiff, -yDiff);
    }

    mouseX = e.getX();
    mouseY = e.getY();
  }

  public void mouseMoved(MouseEvent e)
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  public void mouseClicked(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent e)
  {
  }

  public void mouseEntered(MouseEvent e)
  {
    mouseIn = true;
  }

  public void mouseExited(MouseEvent e)
  {
    mouseIn = false;
  }
}
