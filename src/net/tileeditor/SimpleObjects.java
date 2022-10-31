package net.tileeditor;

import net.tileeditor.source.SimpleObjectWrapper;
import net.tileeditor.source.ObjectWrapperFactory;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;

import java.util.List;
import java.util.ArrayList;

public class SimpleObjects
{
  private SimpleObjectWrapper objectWrapper;
  private List<SimpleObject> simpleObjects;
  private List<ObjectField> fields;

  public SimpleObjects(SimpleObjectWrapper objectWrapper)
  {
    this.objectWrapper = objectWrapper;
    this.simpleObjects = new ArrayList<SimpleObject>();
    fields = new ArrayList<ObjectField>();
  }

  public SimpleObjects(SimpleObjects source)
  {
    objectWrapper = source.objectWrapper;
    simpleObjects = new ArrayList<SimpleObject>();
    for (SimpleObject simpleObject : source.simpleObjects)
    {
      simpleObjects.add(new SimpleObject(simpleObject));
    }
    fields = new ArrayList<ObjectField>();
    for (ObjectField field : source.fields)
    {
      fields.add(new ObjectField(field));
    }
  }

  public SimpleObjects(XMLTag tag)
  {
    XMLBody body = (XMLBody) tag.getData();
    String className = (String) body.getData("Class");
    Class aClass = ObjectWrapperFactory.getInstance().getClassForName(className);
    objectWrapper = ObjectWrapperFactory.getInstance().getObjectWrapper(aClass);

    XMLBody fieldsBody = (XMLBody) body.getData("Fields");
    fields = new ArrayList<ObjectField>();
    if (fieldsBody != null)
    {
      for (XMLTag xmlTag : fieldsBody.getTags())
      {
        ObjectField field = new ObjectField(xmlTag);
        fields.add(field);
      }
    }

    XMLBody objectsBody = (XMLBody) body.getData("Objects");
    simpleObjects = new ArrayList<SimpleObject>();
    if (objectsBody != null)
    {
      List<XMLTag> tags = objectsBody.getTags();
      for (XMLTag xmlTag : tags)
      {
        XMLBody objectBody = (XMLBody) xmlTag.getData();
        SimpleObject simpleObject = objectWrapper.fromXML(objectBody, this);
        if (simpleObject != null)
        {
          simpleObjects.add(simpleObject);
        }
      }
    }
  }


  public void add(SimpleObject object)
  {
    setDefaultProperties(object);
    simpleObjects.add(object);
  }

  private void setDefaultProperties(SimpleObject object)
  {
    for (ObjectField field : fields)
    {
      object.setProperty(field, field.defaultValue);
    }
  }

  public void addField(String name, int type, Object defaultValue)
  {
    ObjectField field = new ObjectField(name, type, defaultValue);
    fields.add(field);
    for (SimpleObject simpleObject : simpleObjects)
    {
      simpleObject.setProperty(field, defaultValue);
    }
  }

  public List<SimpleObject> getSimpleObjects()
  {
    return simpleObjects;
  }

  public SimpleObjectWrapper getObjectWrapper()
  {
    return objectWrapper;
  }

  public List<ObjectField> getFields()
  {
    return fields;
  }

  public ObjectField getFieldWithName(String s)
  {
    for (ObjectField field : fields)
    {
      if (field.getName().equals(s))
      {
        return field;
      }
    }
    return null;
  }

  public String[] getAllNames()
  {
    String[] strings = new String[simpleObjects.size()];
    for (int i = 0; i < simpleObjects.size(); i++)
    {
      strings[i] = simpleObjects.get(i).getName();
    }
    return strings;
  }

  public SimpleObject getWithName(String s)
  {
    for (SimpleObject simpleObject : simpleObjects)
    {
      if (simpleObject.getName().equals(s))
      {
        return simpleObject;
      }
    }
    return null;
  }

  public void removeField(ObjectField field)
  {
    fields.remove(field);
    for (SimpleObject simpleObject : simpleObjects)
    {
      simpleObject.removeProperty(field);
    }
  }

  public void save(XMLBody body)
  {
    XMLTag simpleObjectsTag = body.put("ObjectClass");
    body = simpleObjectsTag.setData();
    body.put("Class", objectWrapper.getTileClass().getSimpleName());

    XMLTag fieldsTag = body.put("Fields");
    XMLBody fieldsBody = fieldsTag.setData();
    for (ObjectField field : fields)
    {
      field.save(fieldsBody);
    }

    XMLTag objectsTag = body.put("Objects");
    XMLBody objectsBody = objectsTag.setData();
    for (SimpleObject simpleObject : simpleObjects)
    {
      simpleObject.save(objectsBody);
    }
  }

  public SimpleObject getWithIndex(int index)
  {
    if ((index >= 0) && (index < simpleObjects.size()))
    {
      return simpleObjects.get(index);
    }
    return null;
  }
}
