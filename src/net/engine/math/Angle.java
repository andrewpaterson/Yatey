package net.engine.math;

public class Angle
{
  public static double LEFT = -(Math.PI / 2);
  public static double RIGHT = (Math.PI / 2);
  public static double UP = 0;
  public static double DOWN = Math.PI;

  public static double directionToAngle(Float2 fireDirection)
  {
    if (fireDirection.isZero())
    {
      return 0.0f;
    }

    double angle = Math.asin(fireDirection.x);
    if (fireDirection.y > 0)
    {
      if (fireDirection.x < 0)
      {
        angle = -(angle + Math.PI);
      }
      else
      {
        angle = Math.PI - angle;
      }
    }

    return angle;
  }

  public static Float2 directionFrom(boolean right, boolean left, boolean up, boolean down)
  {
    Float2 direction = new Float2();
    if (right)
    {
      direction.x += 1.0f;
    }
    if (left)
    {
      direction.x -= 1.0f;
    }
    if (up)
    {
      direction.y -= 1.0f;
    }
    if (down)
    {
      direction.y += 1.0f;
    }

    direction.normalize();
    return direction;
  }

  public static double degToRad(final double degrees)
  {
    return (Math.PI * degrees) / 180.0;
  }

  public static double radToDeg(final double radians)
  {
    return (180.0 * radians) / Math.PI;
  }

  public static double rotateTowards(double requiredAngle, double currentAngle, double maxChange)
  {
    double angleBetween = angleBetween(requiredAngle, currentAngle);

    if (angleBetween > maxChange)
    {
      return currentAngle + maxChange;
    }
    else if (angleBetween < -maxChange)
    {
      return currentAngle - maxChange;
    }
    else
    {
      return requiredAngle;
    }
  }

  private static double angleBetween(double requiredAngle, double currentAngle)
  {
    requiredAngle = normaliseAngle(requiredAngle);
    currentAngle = normaliseAngle(currentAngle);

    return normaliseAngle(requiredAngle - currentAngle);
  }

  public static double normaliseAngle(double angle)
  {
    if (angle < -Math.PI)
    {
      int _180s = ((int) (-angle / (Math.PI)));
      if (_180s % 2 == 1)
      {
        _180s++;
      }
      double remove = _180s * (Math.PI);
      return angle + remove;
    }
    else if (angle > Math.PI)
    {
      int _180s = ((int) (angle / (Math.PI)));
      if (_180s % 2 == 1)
      {
        _180s++;
      }
      double remove = _180s * (Math.PI);
      return angle - remove;
    }
    else
    {
      return angle;
    }
  }

  public static Float2 angleToDirection(double angle, float speed)
  {
    return new Float2((float) (Math.sin(angle) * speed), (float) -(Math.cos(angle) * speed));
  }

  public static boolean isLeftHalf(double angle)
  {
    angle = normaliseAngle(angle);
    if (angle < 0)
    {
      return true;
    }
    return false;
  }

  public static boolean isRightHalf(double angle)
  {
    angle = normaliseAngle(angle);
    if ((angle > 0) && (angle < DOWN))
    {
      return true;
    }
    return false;
  }

  public static boolean isTopHalf(double angle)
  {
    angle = normaliseAngle(angle);
    if ((angle > LEFT) && (angle < RIGHT))
    {
      return true;
    }
    return false;
  }

  public static boolean isBottomHalf(double angle)
  {
    angle = normaliseAngle(angle);
    if ((angle > RIGHT) && (angle <= DOWN))
    {
      return true;
    }
    if ((angle > LEFT) && (angle <= -DOWN))
    {
      return true;
    }
    return false;
  }

  public static int getFrameForAngle(double angle, int firstFrame, int lastFrame, int upFrame, boolean clockwise)
  {
    angle = normaliseAngle(angle);
    if (angle < 0)
    {
      angle = (Math.PI * 2) + angle;
    }
    float unit = (float) (angle / (Math.PI * 2));
    int frames = (lastFrame - firstFrame) + 1;

    unit += (0.5f / frames);
    int frame = (int) (frames * unit);
    frame += upFrame;
    frame += firstFrame;
    if (frame >= frames)
    {
      frame -= frames;
    }
    return frame;
  }
}
