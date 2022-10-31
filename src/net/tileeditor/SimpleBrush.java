package net.tileeditor;

import net.engine.cel.Cel;

public class SimpleBrush
{
  protected Cel cel;
  protected BrushSource source;
  protected Integer id;

  public SimpleBrush(Cel cel, BrushSource source, Integer id)
  {
    this.cel = cel;
    this.source = source;
    this.id = id;
  }

  public Cel getCel()
  {
    return cel;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public BrushSource getBrushSource()
  {
    return source;
  }

  public Integer getId()
  {
    return id;
  }
}
