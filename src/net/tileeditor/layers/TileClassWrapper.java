package net.tileeditor.layers;

import net.tileeditor.SimpleBrush;

public class TileClassWrapper
{
  public Class aClass;

  public TileClassWrapper(Class aClass)
  {
    this.aClass = aClass;
  }

  public String toString()
  {
    if (aClass.equals(SimpleBrush.class))
    {
      return "Brush";
    }
    return aClass.getSimpleName();
  }

  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    TileClassWrapper that = (TileClassWrapper) o;

    if (aClass != null ? !aClass.equals(that.aClass) : that.aClass != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    return (aClass != null ? aClass.hashCode() : 0);
  }
}
