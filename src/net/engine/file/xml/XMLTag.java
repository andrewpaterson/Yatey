package net.engine.file.xml;

import java.util.List;

public class XMLTag implements Comparable
{
  private String name;
  private Object data;
  private boolean open;
  private XMLBody parentBody;

  public XMLTag(String tag, Object data, XMLBody parentBody)
  {
    this.name = tag;
    this.data = data;
    this.parentBody = parentBody;
    open = false;

    if ((data != null) && (!((data instanceof String) || (data instanceof XMLFile) || (data instanceof List))))
    {
      throw new RuntimeException("Can't add [" + data.getClass().getSimpleName() + "] to XML");
    }
  }

  public String getName()
  {
    return name;
  }

  public Object getData()
  {
    return data;
  }

  public void setData(Object data)
  {
    this.data = data;
  }

  public XMLBody setData()
  {
    this.data = new XMLBody(this);
    return (XMLBody) this.data;
  }

  public int compareTo(Object o)
  {
    XMLTag xmlTag = (XMLTag) o;
    if ((data instanceof XMLFile) && (!(xmlTag.data instanceof XMLFile)))
    {
      return 1;
    }
    else if ((!(data instanceof XMLFile)) && (xmlTag.data instanceof XMLFile))
    {
      return -1;
    }
    else
    {
      return name.compareTo(((XMLTag) o).name);
    }
  }

  public XMLBody getParentBody()
  {
    return parentBody;
  }

  public boolean isOpen()
  {
    return open;
  }

  public void setOpen(boolean open)
  {
    this.open = open;
  }
}
