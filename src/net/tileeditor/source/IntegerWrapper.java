package net.tileeditor.source;

import net.engine.GamePanel;
import net.engine.Camera;
import net.engine.cel.Cel;
import net.engine.file.xml.XMLTag;
import net.engine.file.xml.XMLBody;
import net.tileeditor.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;

public class IntegerWrapper extends SimpleObjectWrapper
{
  public IntegerWrapper()
  {
    defaultValue = new DefaultSimpleObject(new Integer(-1));
  }

  public Class getTileClass()
  {
    return Integer.class;
  }

  public SimpleObject getClearValue()
  {
    return defaultValue;
  }

  public void render(GamePanel gamePanel, Object object, int x, int y, int width, int height, int layer)
  {
    Integer integer = (Integer) object;
    if (integer >= 0)
    {
      Graphics2D buffer = gamePanel.getBackBuffer();
      buffer.setColor(LayerColour.getInstance().getColour(layer));
      Camera camera = gamePanel.getCamera();

      int halfWidth = width / 2;
      int halfHeight = height / 2;

      x -= camera.getPosition().x;
      y -= camera.getPosition().y;

      Font font = buffer.getFont();
      font = new Font(font.getName(), Font.BOLD, (halfWidth + halfHeight) / 2);
      buffer.setFont(font);
      FontRenderContext context = buffer.getFontRenderContext();

      String data = integer.toString();

      Rectangle2D bounds = font.getStringBounds(data, context);
      buffer.drawString(data, x - (int) (bounds.getWidth() / 2), y + (int) (bounds.getHeight() / 3));
    }
  }

  public SimpleObject fromXML(XMLBody body, SimpleObjects simpleObjects)
  {
    String value = (String) body.getData("Value");

    int integer;
    try
    {
      integer = Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      integer = -1;
    }
    return new SimpleObject(body, integer, simpleObjects);
  }

  public void toXML(XMLBody body, Object value)
  {
    body.put("Value", ((Integer) value).toString());
  }
}
