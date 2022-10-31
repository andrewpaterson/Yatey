package net.tileeditor.general;

import net.tileeditor.Source;

import java.awt.*;

public class GridExtents
{
  public int left;
  public int right;
  public int top;
  public int bottom;

  public GridExtents(Point startIndex, Point endIndex)
  {
    if (startIndex.x <= endIndex.x)
    {
      left = startIndex.x;
      right = endIndex.x;
    }
    else
    {
      left = endIndex.x;
      right = startIndex.x;
    }

    if (startIndex.y <= endIndex.y)
    {
      top = startIndex.y;
      bottom = endIndex.y;
    }
    else
    {
      top = endIndex.y;
      bottom = startIndex.y;
    }
  }
}
