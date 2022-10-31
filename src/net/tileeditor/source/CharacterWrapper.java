package net.tileeditor.source;

import net.engine.Camera;
import net.engine.GamePanel;
import net.engine.file.xml.XMLTag;
import net.engine.file.xml.XMLBody;
import net.tileeditor.LayerColour;
import net.tileeditor.SimpleObject;
import net.tileeditor.DefaultSimpleObject;
import net.tileeditor.SimpleObjects;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class CharacterWrapper extends SimpleObjectWrapper
{
  public CharacterWrapper()
  {
    defaultValue = new DefaultSimpleObject(new Character(' '));
  }

  public Class getTileClass()
  {
    return Character.class;
  }

  public SimpleObject getClearValue()
  {
    return defaultValue;
  }

  public void render(GamePanel gamePanel, Object object, int x, int y, int width, int height, int layer)
  {
    Character character = (Character) object;
    if (character.charValue() != ' ')
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

      char[] data = {character.charValue()};
      Rectangle2D bounds = font.getStringBounds(data, 0, 1, context);
      buffer.drawChars(data, 0, 1, x - (int) (bounds.getWidth() / 2), y + (int) (bounds.getHeight() / 3));
    }
  }

  public SimpleObject fromXML(XMLBody body, SimpleObjects simpleObjects)
  {
    String value = (String) body.getData("Value");
    return new SimpleObject(body, new Character(value.charAt(0)), simpleObjects);
  }

  public void toXML(XMLBody body, Object value)
  {
    body.put("Value", ((Character)value).toString());
  }
}
