package net.tileeditor;

import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.tileeditor.source.SimpleObjectWrapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SimpleObject
{
  public Object value;
  public Map<ObjectField, Object> properties;
  public String name;
  public SimpleObjects simpleObjects;

  public SimpleObject(Object value)
  {
    this(value, "");
  }

  public SimpleObject(Object value, String name)
  {
    this.value = value;
    this.name = name;
    properties = null;
    this.simpleObjects = null;
  }

  public SimpleObject(SimpleObject source)
  {
    value = source.value;
    name = source.name;
    if (source.properties == null)
    {
      properties = null;
    }
    else
    {
      properties = new LinkedHashMap<ObjectField, Object>();
      for (ObjectField objectField : source.properties.keySet())
      {
        Object object = source.properties.get(objectField);
        properties.put(objectField, object);  //As long as these objects are immutable they will not need to be copied
      }
    }
    simpleObjects = source.simpleObjects;
  }

  public SimpleObject(XMLBody body, Object value, SimpleObjects simpleObjects)
  {
    this.simpleObjects = simpleObjects;
    name = (String) body.getData("Name");
    this.value = value;

    XMLBody propertiesBody = (XMLBody) body.getData("Properties");
    properties = null;
    if (propertiesBody != null)
    {
      List<XMLTag> tags = propertiesBody.getTags();
      for (XMLTag xmlTag : tags)
      {
        XMLBody propertyBody = (XMLBody) xmlTag.getData();
        String field = (String) propertyBody.getData("Field");
        ObjectField objectField = simpleObjects.getFieldWithName(field);
        String fieldValue = (String) propertyBody.getData("Value");
        if (fieldValue == null)
        {
          fieldValue = "";
        }
        Object objectValue = objectField.valueFromString(fieldValue);
        setProperty(objectField, objectValue);
      }
    }
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setProperty(ObjectField field, Object value)
  {
    if (properties == null)
    {
      properties = new LinkedHashMap<ObjectField, Object>();
    }
    properties.put(field, value);
  }

  public Class getObjectClass()
  {
    return value.getClass();
  }

  public Object getProperty(ObjectField objectField)
  {
    if (properties != null)
    {
      return properties.get(objectField);
    }
    else
    {
      return null;
    }
  }

  public void removeProperty(ObjectField field)
  {
    properties.remove(field);
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("Object");
    XMLBody objectBody = tag.setData();

    objectBody.put("Name", name);
    SimpleObjectWrapper wrapper = simpleObjects.getObjectWrapper();
    wrapper.toXML(objectBody, value);

    XMLTag propetiesTag = objectBody.put("Properties");
    XMLBody propertiesBody = propetiesTag.setData();

    if (properties != null)
    {
      for (ObjectField objectField : properties.keySet())
      {
        Object value = properties.get(objectField);
        XMLTag propertyTag = propertiesBody.put("Property");
        XMLBody propertyBody = propertyTag.setData();
        propertyBody.put("Field", objectField.getName());
        propertyBody.put("Value", value.toString());
      }
    }
  }

  public boolean isDefault()
  {
    return false;
  }

  public void setSimpleObjects(SimpleObjects simpleObjects)
  {
    this.simpleObjects = simpleObjects;
  }

  public Object getValue()
  {
    return value;
  }

  public boolean sameAs(SimpleObject other)
  {
    return value == other.value;
  }
}
