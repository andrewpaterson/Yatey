package net.engine.shape;

import net.engine.math.Float2;

public class Circle extends Shape
{
  public Float2 center;
  public float radius;

  public Circle(Float2 center, float radius)
  {
    this.center = center;
    this.radius = radius;
  }

  public Float2 getAbsoluteCenter()
  {
    return center;
  }

  public float getRadius()
  {
    return radius;
  }
}
