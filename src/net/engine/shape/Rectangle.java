package net.engine.shape;

import net.engine.math.Float2;

public class Rectangle extends Shape
{
  public Float2 topLeft;
  public Float2 bottomRight;
  public Float2 center;

  public Rectangle(Float2 topLeft, Float2 bottomRight, Float2 center)
  {
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
    this.center = center;
  }

  public Float2 getTopLeft()
  {
    return topLeft;
  }

  public Float2 getBottomRight()
  {
    return bottomRight;
  }

  public Float2 getAbsoluteTopLeft()
  {
    return new Float2(topLeft).add(center);
  }

  public Float2 getAbsoluteBottomRight()
  {
    return new Float2(bottomRight).add(center);
  }
}
