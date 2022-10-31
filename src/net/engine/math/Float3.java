package net.engine.math;

/**
 *
 */
public class Float3
{
  public float x, y, z;

  public Float3()
  {
    this(0, 0, 0);
  }

  public Float3(float x, float y, float z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Float3(Float4 float4)
  {
    this.x = float4.x;
    this.y = float4.y;
    this.z = float4.z;
  }
}
