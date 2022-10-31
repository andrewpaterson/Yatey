package net.engine.shape;

import net.engine.math.Float2;

public class LineSegment extends Shape
{
  Float2 start;
  Float2 direction;
  float length;
  Float2 end;

  public LineSegment(Float2 start)
  {
    this.length = 0;
    this.start = start;
    this.direction = null;
    this.end = start;
  }

  public LineSegment(Float2 start, Float2 direction, float length)
  {
    this.length = length;
    this.start = start;
    this.direction = direction;
    this.end = calculateEnd();
  }

  private Float2 calculateEnd()
  {
    return new Float2(this.start).add(new Float2(this.direction).multiply(this.length));
  }

  public boolean set(Float2 start, Float2 end)
  {
    this.start = start;
    direction = new Float2(end.x - start.x, end.y - start.y);
    if (!((direction.x == 0) && (direction.y == 0)))
    {
      length = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
      direction.x /= length;
      direction.y /= length;
      this.end = calculateEnd();
      return true;
    }
    else
    {
      this.length = 0;
      this.direction = null;
      this.end = start;
      return false;
    }
  }
}
