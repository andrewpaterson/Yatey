package net.tileeditor.source;

import net.engine.GamePanel;
import net.engine.file.xml.XMLTag;
import net.engine.file.xml.XMLBody;
import net.tileeditor.SimpleObject;
import net.tileeditor.SimpleObjects;

public abstract class SimpleObjectWrapper
{
  protected SimpleObject defaultValue;

  protected SimpleObjectWrapper()
  {
  }

  public abstract void render(GamePanel gamePanel, Object object, int x, int y, int width, int height, int layer);

  public abstract Class getTileClass();

  public abstract SimpleObject getClearValue();

  public abstract SimpleObject fromXML(XMLBody body, SimpleObjects simpleObjects);

  public abstract void toXML(XMLBody body, Object value);
}
