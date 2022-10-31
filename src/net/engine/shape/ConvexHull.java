package net.engine.shape;

import net.engine.math.Float2;

import java.util.ArrayList;

public class ConvexHull extends Shape
{
  ArrayList<LineSegment> segments;
  boolean closed;

  public ConvexHull()
  {
    segments = new ArrayList<LineSegment>();
    closed = false;
  }

  public void addPoint(Float2 point)
  {
    segments.add(new LineSegment(point));
    if (segments.size() > 1)
    {
      LineSegment prev = segments.get(segments.size() - 2);
      prev.set(prev.start, point);
      closed = false;
    }
  }

  void close()
  {
    if (segments.size() > 2)
    {
      LineSegment last = segments.get(segments.size() - 1);
      LineSegment first = segments.get(0);
      last.set(last.start, first.start);
      closed = true;
    }
  }
}
