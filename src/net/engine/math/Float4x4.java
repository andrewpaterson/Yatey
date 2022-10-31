package net.engine.math;

/**
 *
 */
public class Float4x4
{
  public Float4 x;
  public Float4 y;
  public Float4 z;
  public Float4 pos;

  public Float4x4()
  {
    Identity();
  }

  public Float4x4(Float4 x, Float4 y, Float4 z, Float4 pos)
  {
    this.x = x;
    this.y = y;
    this.z = z;
    this.pos = pos;
  }

  public void Identity()
  {
    x = new Float4(1, 0, 0, 0);
    y = new Float4(0, 1, 0, 0);
    z = new Float4(0, 0, 1, 0);
    pos = new Float4(0, 0, 0, 1);
  }

  public Float4x4 multiply(Float4x4 right)
  {
    Float4x4 C = new Float4x4();

    C.x.x = x.x * right.x.x + x.y * right.y.x + x.z * right.z.x + x.w * right.pos.x;
    C.x.y = x.x * right.x.y + x.y * right.y.y + x.z * right.z.y + x.w * right.pos.y;
    C.x.z = x.x * right.x.z + x.y * right.y.z + x.z * right.z.z + x.w * right.pos.z;
    C.x.w = x.x * right.x.w + x.y * right.y.w + x.z * right.z.w + x.w * right.pos.w;
    C.y.x = y.x * right.x.x + y.y * right.y.x + y.z * right.z.x + y.w * right.pos.x;
    C.y.y = y.x * right.x.y + y.y * right.y.y + y.z * right.z.y + y.w * right.pos.y;
    C.y.z = y.x * right.x.z + y.y * right.y.z + y.z * right.z.z + y.w * right.pos.z;
    C.y.w = y.x * right.x.w + y.y * right.y.w + y.z * right.z.w + y.w * right.pos.w;
    C.z.x = z.x * right.x.x + z.y * right.y.x + z.z * right.z.x + z.w * right.pos.x;
    C.z.y = z.x * right.x.y + z.y * right.y.y + z.z * right.z.y + z.w * right.pos.y;
    C.z.z = z.x * right.x.z + z.y * right.y.z + z.z * right.z.z + z.w * right.pos.z;
    C.z.w = z.x * right.x.w + z.y * right.y.w + z.z * right.z.w + z.w * right.pos.w;
    C.pos.x = pos.x * right.x.x + pos.y * right.y.x + pos.z * right.z.x + pos.w * right.pos.x;
    C.pos.y = pos.x * right.x.y + pos.y * right.y.y + pos.z * right.z.y + pos.w * right.pos.y;
    C.pos.z = pos.x * right.x.z + pos.y * right.y.z + pos.z * right.z.z + pos.w * right.pos.z;
    C.pos.w = pos.x * right.x.w + pos.y * right.y.w + pos.z * right.z.w + pos.w * right.pos.w;

    return C;
  }

  public Float4 multiply(Float4 right)
  {
    Float4 C = new Float4();

    C.x = x.x * right.x + x.y * right.y + x.z * right.z + x.w * right.w;
    C.y = y.x * right.x + y.y * right.y + y.z * right.z + y.w * right.w;
    C.z = z.x * right.x + z.y * right.y + z.z * right.z + z.w * right.w;
    C.w = pos.x * right.x + pos.y * right.y + pos.z * right.z + pos.w * right.w;
    return C;
  }

  public Float4 multiply(Float3 right)
  {
    Float4 C = new Float4();

    C.x = x.x * right.x + x.y * right.y + x.z * right.z + x.w * 1.0f;
    C.y = y.x * right.x + y.y * right.y + y.z * right.z + y.w * 1.0f;
    C.z = z.x * right.x + z.y * right.y + z.z * right.z + z.w * 1.0f;
    C.w = pos.x * right.x + pos.y * right.y + pos.z * right.z + pos.w * 1.0f;
    return C;
  }

  public static Float4x4 rotationX(double rad)
  {
    return new Float4x4(new Float4(1, 0, 0, 0),
                        new Float4(0, (float) Math.cos(rad), -(float) Math.sin(rad), 0),
                        new Float4(0, (float) Math.sin(rad), (float) Math.cos(rad), 0),
                        new Float4(0, 0, 0, 1));
  }

  public static Float4x4 rotationY(double rad)
  {
    return new Float4x4(new Float4((float) Math.cos(rad), 0, (float) Math.sin(rad), 0),
                        new Float4(0, 1, 0, 0),
                        new Float4(-(float) Math.sin(rad), 0, (float) Math.cos(rad), 0),
                        new Float4(0, 0, 0, 1));
  }
}
