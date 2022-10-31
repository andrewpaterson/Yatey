package net.engine;

import net.engine.math.Float2;
import net.engine.shape.Circle;
import net.engine.shape.Rectangle;
import net.engine.shape.Shape;

public class Collision
{
  private static Collision instance = null;

  public boolean circleCircle(Circle circle1, Circle circle12)
  {
    float distance = (float) circle1.getAbsoluteCenter().distance(circle12.getAbsoluteCenter());
    return distance <= circle1.getRadius() + circle12.getRadius();
  }

  public boolean rectangleRectangle(Rectangle rectangle1, Rectangle rectangle2)
  {
    Float2 topLeft2 = rectangle2.getAbsoluteTopLeft();
    Float2 bottomRight2 = rectangle2.getAbsoluteBottomRight();
    Float2 topLeft1 = rectangle1.getAbsoluteTopLeft();
    Float2 bottomRight1 = rectangle1.getAbsoluteBottomRight();

    return !((topLeft2.x >= bottomRight1.x)
              || (bottomRight2.x <= topLeft1.x)
              || (topLeft2.y >= bottomRight1.y)
              || (bottomRight2.y <= topLeft1.y));
  }

  private boolean circleRectangle(Circle circle, Rectangle circle1)
  {
    return false;
  }

  private boolean testCollision(Shape shape1, Shape shape2)
  {
    if (shape1 instanceof Rectangle)
    {
      if (shape2 instanceof Rectangle)
      {
        return rectangleRectangle((Rectangle) shape1, (Rectangle) shape2);
      }
      if (shape2 instanceof Circle)
      {
        return circleRectangle((Circle) shape2, (Rectangle) shape1);
      }
    }
    if (shape1 instanceof Circle)
    {
      if (shape2 instanceof Circle)
      {
        return circleCircle((Circle) shape1, (Circle) shape2);
      }
      if (shape2 instanceof Rectangle)
      {
        return circleRectangle((Circle) shape1, (Rectangle) shape2);
      }
    }
    return false;
  }

  public static boolean collide(Shape shape1, Shape shape2)
  {
    if (instance == null)
    {
      instance = new Collision();
    }

    return instance.testCollision(shape1, shape2);
  }
}
