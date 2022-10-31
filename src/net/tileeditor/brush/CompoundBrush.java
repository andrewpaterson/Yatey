package net.tileeditor.brush;

import net.engine.GamePanel;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.tileeditor.SimpleObject;
import net.tileeditor.Source;
import net.tileeditor.keybindings.KeyBinding;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMap;

public class CompoundBrush
{
  protected TileMap tileMap;
  protected String name;
  protected KeyBinding keyBinding;

  public CompoundBrush(SimpleObject singleObject, Source source)
  {
    tileMap = new TileMap(1, 1, 1, 1, source);
    keyBinding = null;
    name = "";
    TileArray tileArray = tileMap.addLayer("", singleObject.getObjectClass(), false);
    tileArray.setObject(0, 0, singleObject);
  }

  public CompoundBrush(int width, int height, Class aClass, Source source)
  {
    tileMap = new TileMap(width, height, width, height, source);
    tileMap.addLayer("", aClass, false);
    keyBinding = null;
    name = "";
  }

  public CompoundBrush(CompoundBrush compoundBrush, Source source)
  {
    TileMap map = compoundBrush.tileMap;
    tileMap = new TileMap(map, source);
    if (compoundBrush.keyBinding != null)
    {
      keyBinding = new KeyBinding(compoundBrush.keyBinding);
    }
    else
    {
      keyBinding = null;
    }
    name = compoundBrush.name;
  }

  public CompoundBrush(XMLTag compoundBrushTag)
  {
    XMLBody compoundBrushBody = (XMLBody) compoundBrushTag.getData();
    XMLTag tileMapTag = compoundBrushBody.getTag("TileMap");
    tileMap = new TileMap(tileMapTag, Source.getInstance());

    XMLTag tag = compoundBrushBody.getTag("KeyBinding");
    if (tag != null)
    {
      keyBinding = new KeyBinding(tag);
    }
    else
    {
      keyBinding = null;
    }

    name = (String) compoundBrushBody.getData("Name");
    if (name == null)
    {
      name = "";
    }
  }

  public void render(GamePanel gamePanel)
  {
    tileMap.render(gamePanel);
  }

  public int getWidth()
  {
    return tileMap.width;
  }

  public int getHeight()
  {
    return tileMap.height;
  }

  public SimpleObject getObject(int x, int y)
  {
    return tileMap.getCurrentLayer().getObject(x, y);
  }

  public void save(XMLBody body)
  {
    XMLTag compoundBrushTag = body.put("CompoundBrush");
    XMLBody compoundBrushBody = compoundBrushTag.setData();
    tileMap.save(compoundBrushBody);
    if (keyBinding != null)
    {
      keyBinding.save(compoundBrushBody);
    }
    compoundBrushBody.put("Name", name);
  }

  public Source getSource()
  {
    return tileMap.getSource();
  }

  public TileMap getTileMap()
  {
    return tileMap;
  }

  public void setKeyBinding(int keyCode, int modifiers)
  {
    Source source = getSource();
    source.removeAllBound(keyCode, modifiers);
    this.keyBinding = new KeyBinding(keyCode, modifiers);
  }

  public boolean isDefault()
  {
    if ((tileMap.width == 1) && (tileMap.height == 1))
    {
      SimpleObject simpleObject = tileMap.getCurrentLayer().getObject(0, 0);
      if (simpleObject != null)
      {
        return simpleObject.isDefault();
      }
    }
    return false;
  }

  public void setName(String name)
  {
    if (name != null)
    {
      this.name = name;
    }
    else
    {
      this.name = "";
    }
  }

  public KeyBinding getKeyBinding()
  {
    return keyBinding;
  }

  public String getName()
  {
    return name;
  }

  public boolean sameAs(CompoundBrush other)
  {
    return tileMap.sameAs(other.tileMap);
  }

  public boolean isKeyBound(int keyCode, int modifiers)
  {
    if (keyBinding != null)
    {
      if (keyBinding.equals(keyCode, modifiers))
      {
        return true;
      }
    }
    return false;
  }

  public void clearKeyBinding()
  {
    keyBinding = null;
  }
}
