package net.engine.shape;

import net.engine.math.Float2;

public class Shape
{
  public static boolean lineLineIntersect(LineSegment line1, LineSegment line2)
  {
    float d = line1.direction.x * line2.direction.y - line1.direction.y * line2.direction.x;
    if (d == 0)
    {
      return false;
    }

    float x = ((line2.start.x - line2.end.x) * (line1.start.x * line1.end.y - line1.start.y * line1.end.x)
            - (line1.start.x - line1.end.x) * (line2.start.x * line2.end.y - line2.start.y * line2.end.x))
            / d;
    float y = ((line2.start.y - line2.end.y) * (line1.start.x * line1.end.y - line1.start.y * line1.end.x)
            - (line1.start.y - line1.end.y) * (line2.start.x * line2.end.y - line2.start.y * line2.end.x))
            / d;

    if (((x >= line1.start.x) && (x <= line1.end.x)) || (x >= line1.end.x) && (x <= line1.start.x))
    {
      if (((y >= line1.start.y) && (y <= line1.end.y)) || ((y >= line1.end.y) && (y <= line1.start.y)))
      {
        return true;
      }
    }
    return false;

  }

  public static boolean lineCircleIntersect(LineSegment line, Circle circle)
  {
    Float2 v1;
    float f;
    float radiusSquare;
    float sphereAlongRay;

    v1 = (new Float2(circle.center)).subtract(line.start);
    sphereAlongRay = v1.dot(line.direction);
    radiusSquare = circle.radius * circle.radius;

    if ((sphereAlongRay >= 0) && (sphereAlongRay <= line.length))
    {
      f = (new Float2(v1)).cross(line.direction);
      return f < radiusSquare;
    }
    else if (sphereAlongRay < 0)
    {
      return pointCircleIntersect(line.start, circle);
    }
    else
    {
      return pointCircleIntersect(line.end, circle);
    }
  }

  public static boolean lineHullIntersect(LineSegment line, ConvexHull hull)
  {
    return false;
  }

  public static boolean circleCircleIntersect(Circle circle1, Circle circle2)
  {
    Float2 dir;
    float dist;

    dir = new Float2(circle2.center);
    dir.subtract(circle1.center);
    dist = dir.squareMagnitude();

    return dist <= (circle1.radius + circle2.radius) * (circle1.radius + circle2.radius);
  }

  public static boolean circleHullIntersect(Circle circle, ConvexHull hull)
  {
    return false;
  }

  public static boolean hullHullIntersect(ConvexHull hull1, ConvexHull hull2)
  {
    return false;
  }

  public static boolean pointHullIntersect(Float2 point, ConvexHull hull2)
  {
    return false;
  }

  public static boolean pointCircleIntersect(Float2 point, Circle circle)
  {
    Float2 dir;
    float dist;

    dir = new Float2(point);
    dir.subtract(circle.center);
    dist = dir.squareMagnitude();

    return dist <= circle.radius * circle.radius;
  }
}
