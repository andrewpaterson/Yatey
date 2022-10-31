package net.tileeditor;

import net.engine.file.TextFile;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLFile;
import net.engine.file.xml.XMLReader;
import net.engine.file.xml.XMLTag;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.keybindings.KeyBinding;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMap;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.source.ObjectWrapperFactory;
import net.tileeditor.source.SimpleObjectWrapper;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Source
{
  public static Source instance = null;

  public List<BrushSource> brushSources;
  public Map<Class, SimpleObjects> simpleObjects;
  public List<TileMapWrapper> mapWrappers;
  protected int currentMap;
  protected CompoundBrush currentBrush;
  protected Map<Class, CompoundBrush> lastLayerBrushClass;
  protected List<CompoundBrush> compoundBrushes;
  protected boolean nullBrush;
  protected String fileName;

  public Source()
  {
    fileName = null;
    simpleObjects = new LinkedHashMap<Class, SimpleObjects>();
    compoundBrushes = new ArrayList<CompoundBrush>();
    mapWrappers = new ArrayList<TileMapWrapper>();
    brushSources = new ArrayList<BrushSource>();
    nullBrush = false;
    lastLayerBrushClass = new LinkedHashMap<Class, CompoundBrush>();
    addDefaultObjects();
  }

  public Source(Source source)
  {
    brushSources = new ArrayList<BrushSource>();
    for (BrushSource brushSource : source.brushSources)
    {
      brushSources.add(new BrushSource(brushSource));
    }

    simpleObjects = new LinkedHashMap<Class, SimpleObjects>();
    for (Class aClass : source.simpleObjects.keySet())
    {
      SimpleObjects objects = source.simpleObjects.get(aClass);
      simpleObjects.put(aClass, new SimpleObjects(objects));
    }

    mapWrappers = new ArrayList<TileMapWrapper>();
    for (TileMapWrapper mapWrapper : source.mapWrappers)
    {
      mapWrappers.add(new TileMapWrapper(mapWrapper, this));
    }

    currentMap = source.currentMap;
    if (source.currentBrush != null)
    {
      currentBrush = new CompoundBrush(source.currentBrush, this);
    }
    else
    {
      currentBrush = null;
    }

    compoundBrushes = new ArrayList<CompoundBrush>();
    for (CompoundBrush compoundBrush : source.compoundBrushes)
    {
      compoundBrushes.add(new CompoundBrush(compoundBrush, this));
    }

    lastLayerBrushClass = new LinkedHashMap<Class, CompoundBrush>();
    for (Class aClass : source.lastLayerBrushClass.keySet())
    {
      CompoundBrush compoundBrush = source.lastLayerBrushClass.get(aClass);
      int index = source.indexOf(compoundBrush);
      if (index != -1)
      {
        lastLayerBrushClass.put(aClass, getCompoundBrush(index));
      }
    }

    nullBrush = source.nullBrush;
    fileName = source.fileName;
  }

  private void addDefaultObjects()
  {
    addSimpleObject(new SimpleObject(false, "Bool " + "False"));
//    for (int i = 33; i < 127; i++)
//    {
//      Character value = new Character((char) i);
//      addSimpleObject(new SimpleObject(value, "Char " + value));
//    }
  }

  public void addMap(String name, int width, int height, int celWidth, int celHeight)
  {
    TileMap tileMap = new TileMap(width, height, this);
    TileMapWrapper mapWrapper = new TileMapWrapper(tileMap, name, celWidth, celHeight);
    tileMap.addDefaultLayers();
    mapWrappers.add(mapWrapper);
    currentMap = mapWrappers.size() - 1;
  }

  public static Source getInstance()
  {
    if (instance == null)
    {
      reset();
    }
    return instance;
  }

  public TileMapWrapper getCurrentMap()
  {
    if ((currentMap < 0) || (currentMap >= mapWrappers.size()))
    {
      return null;
    }
    return mapWrappers.get(currentMap);
  }

  public boolean setCurrentMap(String text)
  {
    for (int i = 0; i < mapWrappers.size(); i++)
    {
      TileMapWrapper map = mapWrappers.get(i);
      if (map.name.equals(text))
      {
        currentMap = i;
        return true;
      }
    }
    currentMap = -1;
    return false;
  }

  public void addSimpleObject(SimpleObject object)
  {
    Class aClass = object.getObjectClass();
    SimpleObjects simpleObjects = this.simpleObjects.get(aClass);
    if (simpleObjects == null)
    {
      SimpleObjectWrapper wrapper = ObjectWrapperFactory.getInstance().getObjectWrapper(aClass);
      simpleObjects = new SimpleObjects(wrapper);
      this.simpleObjects.put(aClass, simpleObjects);
    }
    object.setSimpleObjects(simpleObjects);
    simpleObjects.add(object);
  }

  public void addBrushSource(BrushSource brushSource)
  {
    brushSources.add(brushSource);
  }

  public void setCurrentBrushObject(Class aClass, SimpleObject brush)
  {
    nullBrush = false;
    if (brush != null)
    {
      currentBrush = new CompoundBrush(brush, this);
      currentBrush = addCompoundBrush(currentBrush);
    }
    else
    {
      currentBrush = clearBrush(aClass);
    }
    setPreviousBrush(currentBrush, aClass);
  }

  private CompoundBrush clearBrush(Class aClass)
  {
    SimpleObjectWrapper wrapper = ObjectWrapperFactory.getInstance().getObjectWrapper(aClass);
    SimpleObject clear = wrapper.getClearValue();
    return new CompoundBrush(clear, this);
  }

  public CompoundBrush getCurrentBrush()
  {
    if (nullBrush)
    {
      return null;
    }
    if (currentBrush == null)
    {
      TileMapWrapper map = getCurrentMap();
      if (map != null)
      {
        TileArray layer = map.getCurrentLayer();
        if (layer != null)
        {
          Class tileClass = layer.getTileClass();
          currentBrush = clearBrush(tileClass);
          setPreviousBrush(currentBrush, tileClass);
        }
      }
    }
    return currentBrush;
  }

  public void setCurrentBrush(CompoundBrush compoundBrush, Class aClass)
  {
    compoundBrush = addCompoundBrush(compoundBrush);
    nullBrush = false;
    currentBrush = compoundBrush;
    setPreviousBrush(currentBrush, aClass);
  }

  public void setCurrentLayer(int index)
  {
    TileMapWrapper map = getCurrentMap();
    if (map != null)
    {
      if ((index < map.getLayers().size()) && (index >= 0))
      {
        map.setCurrentLayer(index);
        fixCurrentBrush();
      }
    }
  }

  public boolean setCurrentBrush(Integer integer)
  {
    CompoundBrush compoundBrush = compoundBrushes.get(integer);
    if (compoundBrush != null)
    {
      nullBrush = false;
      Class brushTileClass = compoundBrush.getTileMap().getCurrentLayer().getTileClass();
      if (brushTileClass == getCurrentMap().getCurrentLayer().getTileClass())
      {
        setCurrentBrush(compoundBrush, brushTileClass);
        return true;
      }
    }
    return false;
  }

  public SimpleObjects getSimpleObjects(Class tileClass)
  {
    return simpleObjects.get(tileClass);
  }

  public Point getDefaultMapSize()
  {
    TileMapWrapper map = getCurrentMap();
    if (map == null)
    {
      return new Point(8, 6);
    }
    else
    {
      return map.getSize();
    }
  }

  public Point getDefaultCelSize()
  {
    TileMapWrapper map = getCurrentMap();
    if (map == null)
    {
      return new Point(64, 64);
    }
    else
    {
      return new Point(map.celWidth, map.celHeight);
    }
  }

  public void deleteCurrentMap()
  {
    TileMapWrapper map = getCurrentMap();
    if (map != null)
    {
      mapWrappers.remove(currentMap);
      currentMap--;
      if ((currentMap == -1) && (mapWrappers.size() > 0))
      {
        currentMap = 0;
      }
    }
  }

  public void setNullBrush()
  {
    nullBrush = true;
  }

  public static void reset()
  {
    instance = new Source();
  }

  public boolean nextMap()
  {
    if (mapWrappers.size() > 0)
    {
      if (currentMap < mapWrappers.size() - 1)
      {
        currentMap++;
      }
      else
      {
        currentMap = 0;
      }
      fixCurrentLayer(getCurrentMap().getCurrentLayer());
      fixCurrentBrush();
      return true;
    }
    return false;
  }

  public boolean previousMap()
  {
    if (mapWrappers.size() > 0)
    {
      if (currentMap > 0)
      {
        currentMap--;
      }
      else
      {
        currentMap = mapWrappers.size() - 1;
      }
      fixCurrentLayer(getCurrentMap().getCurrentLayer());
      fixCurrentBrush();
      return true;
    }
    return false;
  }

  private void fixCurrentLayer(TileArray oldLayer)
  {
    Class oldClass = oldLayer.getTileClass();

    TileMapWrapper map = getCurrentMap();
    TileArray newLayer = map.getCurrentLayer();
    if ((newLayer == null) || (newLayer.getTileClass() != oldClass))
    {
      map.tileMap.selectLayerWithClassOrFirstLayer(oldClass);
    }
  }

  public boolean fixCurrentBrush()
  {
    if (currentBrush != null)
    {
      Class brushClass = currentBrush.getTileMap().getCurrentLayer().getTileClass();

      TileArray layer = getCurrentMap().getCurrentLayer();
      if (layer != null)
      {
        Class layerClass = layer.getTileClass();
        if (brushClass != layerClass)
        {
          CompoundBrush compoundBrush = lastLayerBrushClass.get(layerClass);
          if (compoundBrush == null)
          {
            currentBrush = clearBrush(layerClass);
            return true;
          }
          else
          {
            currentBrush = compoundBrush;
            return true;
          }
        }
        else
        {
          return false;
        }
      }
      else
      {
        currentBrush = null;
        return true;
      }
    }
    else
    {
      return false;
    }
  }

  public boolean isLayerValid()
  {
    TileMapWrapper map = getCurrentMap();
    if (map != null)
    {
      if (map.getCurrentLayer() != null)
      {
        return true;
      }
    }
    return false;
  }

  public void setPreviousBrush(CompoundBrush compoundBrush, Class brushClass)
  {
    lastLayerBrushClass.put(brushClass, compoundBrush);
  }

  public static void setInstance(Source source)
  {
    instance = new Source(source);
  }

  public boolean load(File file)
  {
    fileName = file.getAbsolutePath();

    List<String> lines = new TextFile().read(file);
    if (lines == null)
    {
      return false;
    }

    XMLFile xmlFile = XMLReader.read(lines);
    XMLBody body = xmlFile.getBody();
    XMLBody sourceBody = (XMLBody) body.getData("Source");

    String version = (String) sourceBody.getData("Version");
    if ((version == null) || (!version.equals(Settings.getInstance().getVersion())))
    {
      return false;
    }

    XMLBody brushSourcesBody = (XMLBody) sourceBody.getData("BrushSources");
    if (brushSourcesBody != null)
    {
      brushSources = new ArrayList<BrushSource>();
      for (XMLTag tag : brushSourcesBody.getTags())
      {
        try
        {
          brushSources.add(new BrushSource(tag));
        }
        catch (RuntimeException e)
        {
          System.out.println(e.getCause());
        }
      }
    }

    XMLBody objectClassesBody = (XMLBody) sourceBody.getData("ObjectClasses");
    simpleObjects = new LinkedHashMap<Class, SimpleObjects>();
    for (XMLTag tag : objectClassesBody.getTags())
    {
      SimpleObjects objects = new SimpleObjects(tag);
      simpleObjects.put(objects.getObjectWrapper().getTileClass(), objects);
    }

    XMLBody mapsBody = (XMLBody) sourceBody.getData("Maps");
    List<XMLTag> mapsTags = mapsBody.getTags();
    mapWrappers = new ArrayList<TileMapWrapper>();
    for (XMLTag mapsTag : mapsTags)
    {
      TileMapWrapper tileMap = new TileMapWrapper(mapsTag, this);
      mapWrappers.add(tileMap);
    }

    loadCurrentMap(sourceBody);

    XMLBody boundBrushesBody = (XMLBody) sourceBody.getData("CompoundBrushes");
    compoundBrushes = new ArrayList<CompoundBrush>();
    if (boundBrushesBody != null)
    {
      List<XMLTag> boundBrushesTags = boundBrushesBody.getTags();
      for (XMLTag boundBrushesTag : boundBrushesTags)
      {
        compoundBrushes.add(new CompoundBrush(boundBrushesTag));
      }
    }

    fixCurrentBrush();
    return true;
  }

  public void save(File file)
  {
    fileName = file.getAbsolutePath();
    if (!fileName.endsWith(".xml"))
    {
      fileName = fileName + ".xml";
    }
    _save(fileName);
  }

  public void _save(String fileName)
  {
    XMLFile xmlFile = new XMLFile();
    XMLTag tag = xmlFile.getBody().put("Source");

    XMLBody sourceBody = tag.setData();

    sourceBody.put("Version", Settings.getInstance().getVersion());

    XMLTag brushSourcesTag = sourceBody.put("BrushSources");
    XMLBody body = brushSourcesTag.setData();
    for (BrushSource brushSource : brushSources)
    {
      brushSource.save(body);
    }

    XMLTag objectClassesTag = sourceBody.put("ObjectClasses");
    XMLBody objectClassesBody = objectClassesTag.setData();
    for (Class aClass : simpleObjects.keySet())
    {
      SimpleObjects objects = this.simpleObjects.get(aClass);
      objects.save(objectClassesBody);
    }

    XMLTag mapsTag = sourceBody.put("Maps");
    body = mapsTag.setData();
    for (TileMapWrapper map : mapWrappers)
    {
      map.save(body);
    }

    saveCurrentMap(sourceBody);

    XMLTag boundBrushes = sourceBody.put("CompoundBrushes");
    XMLBody compoundBrushBody = boundBrushes.setData();
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      compoundBrush.save(compoundBrushBody);
    }

    TextFile textFile = new TextFile();
    List<String> lines = xmlFile.toLines();
    textFile.addLines(lines);
    textFile.write(new File(fileName));
  }

  private void loadCurrentMap(XMLBody sourceBody)
  {
    String mapName = (String) sourceBody.getData("currentMap");
    setCurrentMap(mapName);
  }

  private void saveCurrentMap(XMLBody sourceBody)
  {
    TileMapWrapper map = getCurrentMap();
    String mapName = "";
    if (map != null)
    {
      mapName = map.name;
    }
    sourceBody.put("currentMap", mapName);
  }

  public void save()
  {
    save(new File(fileName));
  }

  public String getFileName()
  {
    return fileName;
  }

  public int indexOf(SimpleObject simpleObject)
  {
    if (simpleObject == null)
    {
      return -1;
    }
    Class objectClass = simpleObject.getObjectClass();
    SimpleObjects objects = simpleObjects.get(objectClass);
    if (objects == null)
    {
      return -1;
    }
    return objects.getSimpleObjects().indexOf(simpleObject);
  }

  public SimpleObject getSimpleObject(Class objectClass, int index)
  {
    SimpleObjects objects = simpleObjects.get(objectClass);
    if (objects == null)
    {
      return null;
    }
    else
    {
      return objects.getWithIndex(index - 1);
    }
  }

  public int nextSimpleBrushId()
  {
    SimpleObjects objects = simpleObjects.get(SimpleBrush.class);
    if (objects == null)
    {
      return 1;
    }
    return objects.getSimpleObjects().size() + 1;
  }

  public BrushSource getBrushSource(String fileName)
  {
    for (BrushSource brushSource : brushSources)
    {
      if (brushSource.getFileName().equals(fileName))
      {
        return brushSource;
      }
    }
    return null;
  }

  public CompoundBrush getCompoundBrush(int index)
  {
    return compoundBrushes.get(index);
  }

  public int indexOf(CompoundBrush brush)
  {
    return compoundBrushes.indexOf(brush);
  }

  protected CompoundBrush addCompoundBrush(CompoundBrush compoundBrush)
  {
    if ((compoundBrush == null) || (compoundBrush.isDefault()))
    {
      return compoundBrush;
    }

    for (CompoundBrush brush : compoundBrushes)
    {
      if (brush.sameAs(compoundBrush))
      {
        return brush;
      }
    }
    compoundBrushes.add(compoundBrush);
    return compoundBrush;
  }

  public CompoundBrush getCompoundBrush(int keyCode, int modifiers)
  {
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      if (compoundBrush.isKeyBound(keyCode, modifiers))
      {
        return compoundBrush;
      }
    }
    return null;
  }

  public void removeAllBound(int keyCode, int modifiers)
  {
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      if (compoundBrush.isKeyBound(keyCode, modifiers))
      {
        compoundBrush.clearKeyBinding();
      }
    }
  }

  public List<String> getCompoundBrushNames()
  {
    List<String> names = new ArrayList<String>();
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      String name = compoundBrush.getName();
      if (name.length() > 0)
      {
        names.add(name);
      }
    }
    Collections.sort(names);
    return names;
  }

  public List<String> getCompoundBrushBindngs()
  {
    List<String> bindings = new ArrayList<String>();
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      KeyBinding binding = compoundBrush.getKeyBinding();
      if (binding != null)
      {
        bindings.add(binding.toString());
      }
    }
    Collections.sort(bindings);
    return bindings;
  }

  public CompoundBrush getCompoundBrush(String name)
  {
    for (CompoundBrush compoundBrush : compoundBrushes)
    {
      if (name.equals(compoundBrush.getName()))
      {
        return compoundBrush;
      }
    }
    return null;
  }

  public void addNoCheckExistingCompoundBrush(CompoundBrush compoundBrush)
  {
    compoundBrushes.add(compoundBrush);
  }
}
