package net.tileeditor.mapeditor;

import net.tileeditor.Source;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.general.BaseEditorPanel;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileMapWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class CompoundBrushViewPanel extends BaseEditorPanel
{
  private TileEditor tileEditor;

  public CompoundBrushViewPanel(final TileEditor tileEditor)
  {
    super(false);
    this.tileEditor = tileEditor;
    setBorder(BorderFactory.createLineBorder(Color.WHITE));
  }

  protected void initialise()
  {
  }

  public void preRender()
  {
    if (Source.getInstance().isLayerValid())
    {
      CompoundBrush compoundBrush = Source.getInstance().getCurrentBrush();
      if (compoundBrush != null)
      {
        backBuffer.setColor(Color.gray);
        backBuffer.fillRect(0, 0, screenWidth, screenHeight);
        compoundBrush.render(this);
      }
      else
      {
        backBuffer.setColor(Color.black);
        backBuffer.fillRect(0, 0, screenWidth, screenHeight);

        renderBrushMessage("Null brush");
      }
    }
    else
    {
      backBuffer.setColor(Color.lightGray);
      backBuffer.fillRect(0, 0, screenWidth, screenHeight);

      renderBrushMessage("No brush");
    }
  }

  private void renderBrushMessage(String data)
  {
    Font font = backBuffer.getFont();
    font = new Font(font.getName(), Font.BOLD, 14);
    backBuffer.setFont(font);
    backBuffer.setColor(Color.white);
    FontRenderContext context = backBuffer.getFontRenderContext();

    Rectangle2D bounds = font.getStringBounds(data, context);
    Dimension dimension = getSize();
    backBuffer.drawString(data, (int) ((dimension.getWidth() - bounds.getWidth()) / 2), (int) ((dimension.getHeight() + bounds.getHeight()) / 2));
  }

  public void resizedBuffer()
  {
    centerBrush();
  }

  public void centerBrush()
  {
    CompoundBrush compoundBrush = Source.getInstance().getCurrentBrush();
    int x = 0;
    int y = 0;
    if (compoundBrush != null)
    {
      int width = compoundBrush.getWidth();
      int height = compoundBrush.getHeight();

      TileMapWrapper map = Source.getInstance().getCurrentMap();
      x = (map.celWidth * (width - 1)) / 2;
      y = (map.celHeight * (height - 1)) / 2;
    }
    camera.setPosition(((-screenWidth) / 2) + x, ((-screenHeight) / 2) + y);
  }

  public void postRender()
  {
    if (mouseIn)
    {
      if (Source.getInstance().isLayerValid())
      {
        backBuffer.setColor(new Color(184, 207, 229));
        backBuffer.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        backBuffer.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
      }
    }
  }

  public void mouseReleased(MouseEvent e)
  {
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.BUTTON1_MASK) != 0)
    {
      if (Source.getInstance().isLayerValid())
      {
        tileEditor.showSimpleObjectPanel();
      }
    }
  }
}
