package net.engine.math;

/**
 *
 */
public class Float4
{
  float x, y, z, w;

  public Float4()
  {
    this(0, 0, 0, 1);
  }

  public Float4(float x, float y, float z, float w)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }
}
