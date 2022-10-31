package net.engine.math;

import java.awt.geom.Point2D;

public class Float2 extends Point2D.Float
{
  public Float2()
  {
    super();
  }

  public Float2(float x, float y)
  {
    super(x, y);
  }

  public Float2(Float2 float2)
  {
    x = float2.x;
    y = float2.y;
  }

  public Float2 add(Float2 float2)
  {
    x += float2.x;
    y += float2.y;
    return this;
  }

  public Float2 subtract(Float2 float2)
  {
    x -= float2.x;
    y -= float2.y;
    return this;
  }

  public Float2 multiply(float f)
  {
    x *= f;
    y *= f;
    return this;
  }

  public Float2 divide(float f)
  {
    f = 1.0f / f;
    x *= f;
    y *= f;
    return this;
  }

  public Float2 set(float x, float y)
  {
    this.x = x;
    this.y = y;
    return this;
  }

  public float magnitude()
  {
    return (float) Math.sqrt(squareMagnitude());
  }

  public float squareMagnitude()
  {
    return x * x + y * y;
  }

  public void normalize()
  {
    float f;

    f = magnitude();
    if (f != 0.0f)
    {
      f = 1.0f / f;
      x *= f;
      y *= f;
    }
  }

  public float dot(Float2 float2)
  {
    return x * float2.x + y * float2.y;
  }

  public float cross(Float2 float2)
  {
    return x * float2.y - y * float2.x;
  }

  public String toString()
  {
    return x + ", " + y;
  }

  public boolean isZero()
  {
    return (x == 0.0f) && (y == 0.0f);
  }
}
