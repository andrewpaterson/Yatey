package net.tileeditor.layers;

import net.engine.GamePanel;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.tileeditor.SimpleBrush;
import net.tileeditor.SimpleObject;
import net.tileeditor.Source;
import net.tileeditor.source.ObjectWrapperFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileMap
{
  public static final int MAX_WIDTH = 500;
  public static final int MAX_HEIGHT = 500;

  public List<TileArray> layers;
  public int width;
  public int height;
  public int maxWidth;
  public int maxHeight;
  public int currentLayer;
  public Source source;

  public TileMap(int width, int height, Source source)
  {
    this(width, height, MAX_WIDTH, MAX_HEIGHT, source);
  }

  public TileMap(int width, int height, int maxWidth, int maxHeight, Source source)
  {
    this.width = width;
    this.height = height;
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    layers = new ArrayList<TileArray>();
    currentLayer = 0;
    this.source = source;
  }

  public TileMap(TileMap source, Source sourceSource)
  {
    width = source.width;
    height = source.height;
    maxWidth = source.maxWidth;
    maxHeight = source.maxHeight;
    this.source = sourceSource;

    currentLayer = source.currentLayer;
    layers = new ArrayList<TileArray>();

    for (TileArray layer : source.layers)
    {
      layers.add(new TileArray(layer, this));
    }
  }

  public TileMap(XMLTag tag, Source source)
  {
    this.source = source;

    XMLBody mapBody = (XMLBody) tag.getData();

    width = Integer.parseInt((String) mapBody.getData("Width"));
    height = Integer.parseInt((String) mapBody.getData("Height"));
    maxWidth = Integer.parseInt((String) mapBody.getData("MaxWidth"));
    maxHeight = Integer.parseInt((String) mapBody.getData("MaxHeight"));
    currentLayer = Integer.parseInt((String) mapBody.getData("CurrentLayer"));

    XMLBody layersBody = (XMLBody) mapBody.getData("Layers");
    layers = new ArrayList<TileArray>();
    if (layersBody != null)
    {
      List<XMLTag> tags = layersBody.getTags();
      for (XMLTag xmlTag : tags)
      {
        TileArray tileArray = new TileArray(xmlTag, this);
        layers.add(tileArray);
      }
    }
  }

  public void addDefaultLayers()
  {
    addLayer("Graphics", SimpleBrush.class, true);
    currentLayer = 0;
  }

  public TileArray addLayer(String name, Class aClass, boolean clear)
  {
    TileArray tileArray = new TileArray(name, this, ObjectWrapperFactory.getInstance().getObjectWrapper(aClass), maxWidth, maxHeight);
    if (clear)
    {
      tileArray.clear();
    }
    else
    {
      tileArray.nullify();
    }
    layers.add(tileArray);
    if (currentLayer == -1)
    {
      currentLayer = 0;
      return tileArray;
    }
    else
    {
      makeNulls(tileArray, getCurrentLayer());
      currentLayer = layers.size() - 1;
      return tileArray;
    }
  }

  private void makeNulls(TileArray dest, TileArray source)
  {
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        Object value = source.getObject(x, y);
        if (value == null)
        {
          dest.setObject(x, y, null);
        }
      }
    }
  }

  public boolean setSize(int width, int height)
  {
    if ((width > 0) && (width < MAX_WIDTH) && ((height > 0) && (height < MAX_HEIGHT)))
    {
      this.width = width;
      this.height = height;
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean isValid(int x, int y)
  {
    return (x >= 0) && (x < width) &&
           (y >= 0) && (y < height);
  }

  public void render(GamePanel gamePanel)
  {

    for (int i = 0; i < layers.size(); i++)
    {
      TileArray layer = layers.get(i);
      if (layer.visible)
      {
        layer.render(gamePanel, i);
      }
    }
  }

  public TileArray getCurrentLayer()
  {
    if ((layers.size() != 0) && (layers.size() > currentLayer))
    {
      return layers.get(currentLayer);
    }
    return null;
  }

  public void setCurrentLayer(int index)
  {
    if ((index >= 0) && (index < layers.size()))
    {
      currentLayer = index;
    }
    else
    {
      currentLayer = -1;
    }
  }

  public boolean removeLayer(int layer)
  {
    if (layers.size() > 0)
    {
      if (layer < layers.size())
      {
        layers.remove(layer);
        if (currentLayer >= layers.size())
        {
          currentLayer--;
          return true;
        }
      }
    }
    return false;
  }

  public void setVisibility(int layer, boolean visible)
  {
    TileArray tileArray = layers.get(layer);
    tileArray.visible = visible;
  }

  public boolean moveCurrentLayerUp()
  {
    if (currentLayer > 0)
    {
      TileArray tileArray1 = layers.get(currentLayer - 1);
      TileArray tileArray2 = layers.get(currentLayer);
      layers.set(currentLayer, tileArray1);
      layers.set(currentLayer - 1, tileArray2);
      currentLayer--;
      return true;
    }
    return false;
  }

  public boolean moveCurrentLayerDown()
  {
    if (currentLayer < layers.size() - 1)
    {
      TileArray tileArray1 = layers.get(currentLayer);
      TileArray tileArray2 = layers.get(currentLayer + 1);
      layers.set(currentLayer, tileArray2);
      layers.set(currentLayer + 1, tileArray1);
      currentLayer++;
      return true;
    }
    return false;
  }

  public Point getSize()
  {
    return new Point(width, height);
  }

  public void nullify(int x, int y)
  {
    for (TileArray layer : layers)
    {
      layer.setObject(x, y, null);
    }
  }

  public void clear(int x, int y)
  {
    for (TileArray layer : layers)
    {
      SimpleObject value = layer.getClearValue();
      layer.setObject(x, y, value);
    }
  }

  public boolean nextLayer()
  {
    if (layers.size() != 0)
    {
      for (int i = 0; i < layers.size(); i++)
      {
        if (currentLayer < layers.size() - 1)
        {
          currentLayer++;
        }
        else
        {
          currentLayer = 0;
        }

        if (getCurrentLayer().isVisible())
        {
          return true;
        }
      }
    }
    return false;
  }

  public boolean previousLayer()
  {
    if (layers.size() != 0)
    {
      for (int i = 0; i < layers.size(); i++)
      {
        if (currentLayer > 0)
        {
          currentLayer--;
        }
        else
        {
          currentLayer = layers.size() - 1;
        }

        if (getCurrentLayer().isVisible())
        {
          return true;
        }
      }
    }
    return false;
  }

  public void selectLayerWithClassOrFirstLayer(Class oldClass)
  {
    if (layers.size() == 0)
    {
      currentLayer = -1;
      return;
    }

    for (int i = 0; i < layers.size(); i++)
    {
      TileArray layer = layers.get(i);
      if (layer.getTileClass() == oldClass)
      {
        currentLayer = i;
        return;
      }
    }
    currentLayer = 0;
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("TileMap");
    XMLBody mapBody = tag.setData();

    mapBody.put("Width", Integer.toString(width));
    mapBody.put("Height", Integer.toString(height));
    mapBody.put("MaxWidth", Integer.toString(maxWidth));
    mapBody.put("MaxHeight", Integer.toString(maxHeight));
    mapBody.put("CurrentLayer", Integer.toString(currentLayer));

    XMLTag layersTag = mapBody.put("Layers");
    XMLBody layersBody = layersTag.setData();
    for (TileArray layer : layers)
    {
      layer.save(layersBody);
    }
  }

  public Source getSource()
  {
    return source;
  }

  public boolean sameAs(TileMap other)
  {
    if ((width != other.width) || (height != other.height) || (maxWidth != other.maxWidth) || (maxHeight != other.maxHeight))
    {
      return false;
    }

    if (layers.size() != other.layers.size())
    {
      return false;
    }

    for (int i = 0; i < layers.size(); i++)
    {
      TileArray tileArray = layers.get(i);
      TileArray tileArrayOther = other.layers.get(i);
      if (!tileArray.sameAs(tileArrayOther))
      {
        return false;
      }
    }
    return true;
  }
}
