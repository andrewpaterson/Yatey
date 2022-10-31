package net.tileeditor.layers;

import net.tileeditor.Source;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.engine.GamePanel;

import java.util.List;
import java.awt.*;

public class TileMapWrapper
{
  public TileMap tileMap;

  public int celWidth;
  public int celHeight;
  public String name;

  public TileMapWrapper(TileMap tileMap, String name, int celWidth, int celHeight)
  {
    this.tileMap = tileMap;
    this.name = name;
    this.celWidth = celWidth;
    this.celHeight = celHeight;
  }

  public TileMapWrapper(TileMapWrapper source, Source sourceSource)
  {
    tileMap = new TileMap(source.tileMap, sourceSource);
    celWidth = source.celWidth;
    celHeight = source.celHeight;
    name = source.name;
  }

  public TileMapWrapper(XMLTag compoundBrushTag, Source sourceSource)
  {
    XMLBody compoundBrushBody = (XMLBody) compoundBrushTag.getData();
    XMLTag tileMapTag = compoundBrushBody.getTag("TileMap");
    tileMap = new TileMap(tileMapTag, sourceSource);

    celWidth = Integer.parseInt((String) compoundBrushBody.getData("CelWidth"));
    celHeight = Integer.parseInt((String) compoundBrushBody.getData("CelHeight"));
    name = (String) compoundBrushBody.getData("Name");
  }

  public void save(XMLBody body)
  {
    XMLTag compoundBrushTag = body.put("TileMapWrapper");
    XMLBody compoundBrushBody = compoundBrushTag.setData();
    tileMap.save(compoundBrushBody);

    compoundBrushBody.put("Name", this.name);
    compoundBrushBody.put("CelWidth", Integer.toString(celWidth));
    compoundBrushBody.put("CelHeight", Integer.toString(celHeight));
  }

  public TileArray getCurrentLayer()
  {
    return tileMap.getCurrentLayer();
  }

  public List<TileArray> getLayers()
  {
    return tileMap.layers;
  }

  public void setCurrentLayer(int index)
  {
    tileMap.setCurrentLayer(index);
  }

  public Point getSize()
  {
    return tileMap.getSize();
  }

  public boolean nextLayer()
  {
    return tileMap.nextLayer();
  }

  public boolean previousLayer()
  {
    return tileMap.previousLayer();
  }

  public void removeLayer(int selectedRow)
  {
    tileMap.removeLayer(selectedRow);
  }

  public boolean moveCurrentLayerUp()
  {
    return tileMap.moveCurrentLayerUp();
  }

  public boolean moveCurrentLayerDown()
  {
    return tileMap.moveCurrentLayerDown();
  }

  public int getCurrentLayerIndex()
  {
    return tileMap.currentLayer;
  }

  public void setVisibility(int layer, boolean visible)
  {
    tileMap.setVisibility(layer, visible);
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

  public void addLayer(String name, Class aClass)
  {
    tileMap.addLayer(name, aClass, true);
  }

  public boolean set(String name, String mapWidthText, String mapHeightText, String celWidthText, String celHeightText)
  {
    try
    {
      int widthInt = Integer.parseInt(mapWidthText);
      int heightInt = Integer.parseInt(mapHeightText);
      int celWidthInt = Integer.parseInt(celWidthText);
      int celHeightInt = Integer.parseInt(celHeightText);

      this.name = name;
      tileMap.setSize(widthInt, heightInt);
      celWidth = celWidthInt;
      celHeight = celHeightInt;
      return true;
    }
    catch (NumberFormatException e)
    {
      return false;
    }
  }
}
