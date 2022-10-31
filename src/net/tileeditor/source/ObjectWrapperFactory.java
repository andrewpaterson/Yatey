package net.tileeditor.source;

import net.tileeditor.SimpleBrush;
import net.tileeditor.layers.TileClassWrapper;

import java.awt.*;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

public class ObjectWrapperFactory
{
  public static ObjectWrapperFactory instance = null;

  public Map<Class, SimpleObjectWrapper> wrapperMap;

  public ObjectWrapperFactory()
  {
    wrapperMap = new LinkedHashMap<Class, SimpleObjectWrapper>();

    wrapperMap.put(Boolean.class, new BooleanWrapper());
    wrapperMap.put(SimpleBrush.class, new SpriteWrapper());
    wrapperMap.put(Color.class, new ColourWrapper());
    wrapperMap.put(Integer.class, new IntegerWrapper());
    wrapperMap.put(Character.class, new CharacterWrapper());
  }

  public SimpleObjectWrapper getObjectWrapper(Class aClass)
  {
    return wrapperMap.get(aClass);
  }

  public SimpleObjectWrapper getObjectWrapper(String className)
  {
    return getObjectWrapper(getClassForName(className));
  }

  public Object[] getAllowedClasses()
  {
    Set<Class> classSet = wrapperMap.keySet();
    Object[] vals = new Object[classSet.size()];
    int i = 0;
    for (Class aClass : classSet)
    {
      vals[i] = new TileClassWrapper(aClass);
      i++;
    }
    return vals;
  }

  public static ObjectWrapperFactory getInstance()
  {
    if (instance == null)
    {
      instance = new ObjectWrapperFactory();
    }
    return instance;
  }

  public Class getClassForName(String className)
  {
    Set<Class> classes = wrapperMap.keySet();
    for (Class aClass : classes)
    {
      if (aClass.getSimpleName().equals(className))
      {
        return aClass;
      }
    }
    return null;
  }
}
