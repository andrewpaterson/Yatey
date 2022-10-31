package net.tileeditor.layers;

import net.engine.GamePanel;
import net.engine.file.csv.CSVFile;
import net.engine.file.csv.CSVLine;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.tileeditor.SimpleObject;
import net.tileeditor.Source;
import net.tileeditor.source.ObjectWrapperFactory;
import net.tileeditor.source.SimpleObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class TileArray
{
  public String name;
  public TileMap tileMap;
  public boolean visible;

  private SimpleObject[] tiles;
  private boolean gridBased;
  private SimpleObjectWrapper objectWrapper;

  public TileArray(String name, TileMap tileMap, SimpleObjectWrapper objectWrapper, int maxWidth, int maxHeight)
  {
    this.name = name;
    this.tileMap = tileMap;
    visible = true;

    this.objectWrapper = objectWrapper;
    tiles = new SimpleObject[maxWidth * maxHeight];
    gridBased = true;
  }

  public TileArray(TileArray tileArray, TileMap tileMap)
  {
    name = tileArray.name;
    this.tileMap = tileMap;
    visible = tileArray.visible;
    gridBased = tileArray.gridBased;
    objectWrapper = tileArray.objectWrapper;
    tiles = new SimpleObject[tileArray.tiles.length];
    for (int i = 0; i < tileArray.tiles.length; i++)
    {
      SimpleObject tile = tileArray.tiles[i];
      SimpleObject copiedTile = null;
      if (tile != null)
      {
        if (tile.isDefault())
        {
          copiedTile = tile;
        }
        else
        {
          int index = tileArray.getSource().indexOf(tile);
          if (index != -1)
          {
            copiedTile = getSource().getSimpleObject(tile.getObjectClass(), index + 1);
          }
        }
      }
      tiles[i] = copiedTile;
    }
  }

  private Source getSource()
  {
    return tileMap.getSource();
  }

  public TileArray(XMLTag xmlTag, TileMap tileMap)
  {
    XMLBody layerBody = (XMLBody) xmlTag.getData();

    this.tileMap = tileMap;
    name = (String) layerBody.getData("Name");
    gridBased = Boolean.parseBoolean((String) layerBody.getData("GridBased"));
    visible = Boolean.parseBoolean((String) layerBody.getData("Visible"));
    objectWrapper = ObjectWrapperFactory.getInstance().getObjectWrapper((String) layerBody.getData("ObjectClass"));
    int maxTiles = Integer.parseInt((String) layerBody.getData("MaxTiles"));
    tiles = new SimpleObject[maxTiles];
    clear();

    loadTiles(layerBody);
  }

  private void loadTiles(XMLBody layerBody)
  {
    Object data = layerBody.getData("Tiles");
    List<String> rows;
    if (data == null)
    {
      return;
    }
    else if (data instanceof List)
    {
      rows = (List<String>) data;
    }
    else if (data instanceof String)
    {
      rows = new ArrayList<String>();
      rows.add((String) data);
    }
    else
    {
      throw new RuntimeException("Tiles must be strings");
    }

    CSVFile csvFile = new CSVFile(',');
    csvFile.addRows(rows);

    if (rows.size() != tileMap.height)
    {
      throw new RuntimeException("Rows and height don't match");
    }

    for (int y = 0; y < tileMap.height; y++)
    {
      CSVLine csvLine = csvFile.getRow(y);
      List<String> cells = csvLine.getCells();
      if (cells.size() != tileMap.width)
      {
        throw new RuntimeException("Columns and width don't match");
      }

      for (int x = 0; x < tileMap.width; x++)
      {
        String s = cells.get(x);
        s = s.trim();

        SimpleObject simpleObject;
        if (s.isEmpty())
        {
          simpleObject = null;
        }
        else if (s.equals("0"))
        {
          simpleObject = objectWrapper.getClearValue();
        }
        else
        {
          Integer index;
          index = Integer.parseInt(s);
          simpleObject = getSource().getSimpleObject(objectWrapper.getTileClass(), index);
        }
        tiles[y * tileMap.maxWidth + x] = simpleObject;
      }
    }
  }

  public Class getTileClass()
  {
    return objectWrapper.getTileClass();
  }

  public SimpleObject getClearValue()
  {
    return objectWrapper.getClearValue();
  }

  public void clear()
  {
    SimpleObject value = objectWrapper.getClearValue();
    for (int i = 0; i < tiles.length; i++)
    {
      tiles[i] = value;
    }
  }

  public boolean isGridBased()
  {
    return gridBased;
  }

  private boolean isValid(int x, int y)
  {
    return tileMap.isValid(x, y);
  }

  public void setObject(int x, int y, SimpleObject simpleObject)
  {
    if (isValid(x, y))
    {
      tiles[y * tileMap.maxWidth + x] = simpleObject;
    }
  }

  public SimpleObject getObject(int x, int y)
  {
    if (isValid(x, y))
    {
      return tiles[y * tileMap.maxWidth + x];
    }
    return null;
  }

  public void render(GamePanel gamePanel, int layer)
  {
    int width = tileMap.width;
    int height = tileMap.height;
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    int celWidth = map.celWidth;
    int celHeight = map.celHeight;

    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        SimpleObject object = getObject(x, y);
        if (object != null)
        {
          objectWrapper.render(gamePanel, object.value, x * celWidth, y * celHeight, celWidth, celHeight, layer);
        }
      }
    }
  }

  public boolean isVisible()
  {
    return visible;
  }

  public void toggleVisibility()
  {
    visible = !visible;
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("Layer");
    XMLBody layerBody = tag.setData();

    layerBody.put("Name", name);
    layerBody.put("GridBased", Boolean.toString(gridBased));
    layerBody.put("Visible", Boolean.toString(visible));
    layerBody.put("ObjectClass", objectWrapper.getTileClass().getSimpleName());
    layerBody.put("MaxTiles", Integer.toString(tiles.length));

    saveTiles(layerBody);
  }

  private void saveTiles(XMLBody body)
  {
    CSVFile csvFile = new CSVFile(',');

    for (int y = 0; y < tileMap.height; y++)
    {
      CSVLine csvLine = csvFile.addRow();
      for (int x = 0; x < tileMap.width; x++)
      {
        SimpleObject simpleObject = tiles[y * tileMap.maxWidth + x];
        String s = "";
        if (simpleObject != null)
        {
          if (simpleObject.isDefault())
          {
            s = "0";
          }
          else
          {
            int index = Source.getInstance().indexOf(simpleObject);
            if (index != -1)
            {
              s = Integer.toString(index + 1);
            }
          }
        }
        csvLine.add(s);
      }
    }

    ArrayList rows = new ArrayList();
    for (int i = 0; i < csvFile.size(); i++)
    {
      CSVLine csvLine = csvFile.getRow(i);
      rows.add(csvLine.toString());
    }
    body.put("Tiles", rows);
  }

  public boolean sameAs(TileArray tileArrayOther)
  {
    if (objectWrapper != tileArrayOther.objectWrapper)
    {
      return false;
    }

    for (int y = 0; y < tileMap.height; y++)
    {
      for (int x = 0; x < tileMap.width; x++)
      {
        SimpleObject simpleObject = getObject(x, y);
        SimpleObject simpleObjectOther = tileArrayOther.getObject(x, y);

        if ((simpleObject != null) && (simpleObjectOther != null))
        {
          if (!simpleObject.sameAs(simpleObjectOther))
          {
            return false;
          }
        }
        if (((simpleObject == null) && (simpleObjectOther != null)) || ((simpleObject != null) && (simpleObjectOther == null)))
        {
          return false;
        }
      }
    }
    return true;
  }

  public void nullify()
  {
    for (int i = 0; i < tiles.length; i++)
    {
      tiles[i] = null;
    }
  }
}
