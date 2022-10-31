package net.tileeditor;

public class DefaultSimpleObject extends SimpleObject
{
  public DefaultSimpleObject(Object value)
  {
    super(value, "Default");
  }

  public boolean isDefault()
  {
    return true;
  }
}
