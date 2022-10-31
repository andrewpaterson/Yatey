package net.tileeditor.layers;

public class Tileenator
{
  public int width;
  public int height;

  public Tileenator(int width, int height)
  {
    this.width = width;
    this.height = height;
  }

  public Object[] create()
  {
    return new Object[width * height];
  }
  
  void setObject(int x, int y)
  {

  }
}
