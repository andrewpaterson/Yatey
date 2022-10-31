package net.tileeditor;

import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;

public class ObjectField
{
  public static final int BRUSH = 1;
  public static final int TEXT = 2;
  public static final int NUMBER = 3;

  public static final String BRUSH_STRING = "Brush";
  public static final String TEXT_STRING = "Text";
  public static final String NUMBER_STRING = "Number";

  protected String name;
  protected int type;
  protected Object defaultValue;

  public ObjectField(String name, int type, Object defaultValue)
  {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public ObjectField(ObjectField source)
  {
    name = source.name;
    type = source.type;
    defaultValue = source.defaultValue;
  }

  public ObjectField(XMLTag xmlTag)
  {
    XMLBody fieldBody = (XMLBody) xmlTag.getData();

    name = (String) fieldBody.getData("Name");
    typeFromString((String) fieldBody.getData("Type"));
    defaultValue = valueFromString((String) fieldBody.getData("DefaultValue"));

    if (defaultValue == null)
    {
      //The only way this could have been null was if the empty string was saved.
      defaultValue = "";
    }
  }

  public Object valueFromString(String s)
  {
    Object value = null;
    if (type == TEXT)
    {
      value = s;
    }
    else if (type == BRUSH)
    {
      value = s;
    }
    else if (type == NUMBER)
    {
      value = Integer.parseInt(s);
    }
    return value;
  }

  private void typeFromString(String s)
  {
    if (s.equals(BRUSH_STRING))
    {
      type = BRUSH;
    }
    else if (s.equals(NUMBER_STRING))
    {
      type = NUMBER;
    }
    else if (s.equals(TEXT_STRING))
    {
      type = TEXT;
    }
  }

  public static String[] getAllowedFields()
  {
    return new String[]{BRUSH_STRING, TEXT_STRING, NUMBER_STRING};
  }

  public static int getFieldType(Object selectedItem)
  {
    if (selectedItem.equals(BRUSH_STRING))
    {
      return BRUSH;
    }
    else if (selectedItem.equals(TEXT_STRING))
    {
      return TEXT;
    }
    else if (selectedItem.equals(NUMBER_STRING))
    {
      return NUMBER;
    }
    return -1;
  }

  public int getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }

  public String typeAsString()
  {
    switch (type)
    {
    case BRUSH:
      return BRUSH_STRING;
    case TEXT:
      return TEXT_STRING;
    case NUMBER:
      return NUMBER_STRING;
    }
    return "";
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("Field");
    XMLBody fieldBody = tag.setData();

    fieldBody.put("Name", name);
    fieldBody.put("Type", typeAsString());
    fieldBody.put("DefaultValue", defaultValue.toString());
  }
}
