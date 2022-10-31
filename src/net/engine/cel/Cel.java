package net.engine.cel;

import net.engine.math.Float2;

import java.awt.image.BufferedImage;

public class Cel
{
  public static final int LEFT_ALIGNED = 0;
  public static final int CENTERED = 1;
  public static final int RIGHT_ALIGNED = 2;
  public static final int TOP_ALIGNED = 3;
  public static final int BOTTOM_ALIGNED = 4;

  public static final String LEFT_ALIGNED_TEXT = "LeftAligned";
  public static final String CENTERED_TEXT = "Centered";
  public static final String RIGHT_ALIGNED_TEXT = "RightAligned";
  public static final String TOP_ALIGNED_TEXT = "TopAligned";
  public static final String BOTTOM_ALIGNED_TEXT = "BottomAligned";

  public BufferedImage bufferedImage;
  public CelHelper celHelper;
  public Float2 offsetTopLeft;
  public Float2 offsetBottomRight;

  public int horizontalAlignment;
  public int verticalAlignment;

  public Cel()
  {
    offsetTopLeft = new Float2();
    offsetBottomRight = new Float2();
  }

  public Cel(CelHelper celHelper, BufferedImage bufferedImage, int horizontalAlignment, int verticalAlignment, Float2 offsetTopLeft, Float2 offsetBottomRight)
  {
    this.celHelper = celHelper;
    this.bufferedImage = bufferedImage;
    this.horizontalAlignment = horizontalAlignment;
    this.verticalAlignment = verticalAlignment;
    this.offsetTopLeft = offsetTopLeft;
    this.offsetBottomRight = offsetBottomRight;
  }

  public Cel(Cel source)
  {
    bufferedImage = source.bufferedImage;
    celHelper = source.celHelper;
    offsetTopLeft = source.offsetTopLeft;
    offsetBottomRight = source.offsetBottomRight;

    horizontalAlignment = source.horizontalAlignment;
    verticalAlignment = source.verticalAlignment;
  }

  public void setHorizontalAlignment(int horizontalAlignment)
  {
    this.horizontalAlignment = horizontalAlignment;
  }

  public void setVerticalAlignment(int verticalAlignment)
  {
    this.verticalAlignment = verticalAlignment;
  }

  public float getGraphicsLeft()
  {
    int width = bufferedImage.getWidth();
    if (horizontalAlignment == LEFT_ALIGNED)
    {
      return offsetTopLeft.x;
    }
    else if (horizontalAlignment == CENTERED)
    {
      return ((offsetTopLeft.x + width + offsetBottomRight.x) / 2) - width - offsetBottomRight.x;
    }
    else if (horizontalAlignment == RIGHT_ALIGNED)
    {
      return -offsetBottomRight.x - width;
    }
    return 0;
  }

  public float getGraphicsTop()
  {
    int height = bufferedImage.getHeight();
    if (verticalAlignment == TOP_ALIGNED)
    {
      return offsetTopLeft.y;
    }
    else if (verticalAlignment == CENTERED)
    {
      return ((offsetTopLeft.y + height + offsetBottomRight.y) / 2) - height - offsetBottomRight.y;
    }
    else if (verticalAlignment == BOTTOM_ALIGNED)
    {
      return -offsetTopLeft.y - height;
    }
    return 0;
  }

  public float getGraphicsBottom()
  {
    int height = bufferedImage.getHeight();
    if (verticalAlignment == TOP_ALIGNED)
    {
      return offsetBottomRight.y + height;
    }
    else if (verticalAlignment == CENTERED)
    {
      return ((offsetBottomRight.y + height + offsetTopLeft.y) / 2) - offsetBottomRight.y;
    }
    else if (verticalAlignment == BOTTOM_ALIGNED)
    {
      return offsetBottomRight.y;
    }
    return 0;
  }

  public float getGraphicsRight()
  {
    int width = bufferedImage.getWidth();
    if (horizontalAlignment == LEFT_ALIGNED)
    {
      return offsetBottomRight.x + width;
    }
    if (horizontalAlignment == CENTERED)
    {
      return ((offsetBottomRight.x + width + offsetTopLeft.x) / 2) - offsetBottomRight.x;
    }
    if (horizontalAlignment == RIGHT_ALIGNED)
    {
      return offsetBottomRight.x;
    }
    return 0;
  }

  public float getRelativeLeft()
  {
    int width = bufferedImage.getWidth();
    if (horizontalAlignment == LEFT_ALIGNED)
    {
      return 0;
    }
    else if (horizontalAlignment == CENTERED)
    {
      return (offsetTopLeft.x + width + offsetBottomRight.x) / 2;
    }
    else if (horizontalAlignment == RIGHT_ALIGNED)
    {
      return offsetTopLeft.x + width + offsetBottomRight.x;
    }
    return 0;
  }

  public float getRelativeTop()
  {
    int height = bufferedImage.getHeight();
    if (verticalAlignment == TOP_ALIGNED)
    {
      return 0;
    }
    else if (verticalAlignment == CENTERED)
    {
      return (offsetTopLeft.y + height + offsetBottomRight.y) / 2;
    }
    else if (verticalAlignment == BOTTOM_ALIGNED)
    {
      return offsetTopLeft.y + height + offsetBottomRight.y;
    }
    return 0;
  }

  public void setAlignment(int horizontalAlignment, int verticalAlignment)
  {
    setHorizontalAlignment(horizontalAlignment);
    setVerticalAlignment(verticalAlignment);
  }

  public void setOffset(float left, float top, float right, float bottom)
  {
    offsetTopLeft.x = left;
    offsetTopLeft.y = top;
    offsetBottomRight.x = right;
    offsetBottomRight.y = bottom;
  }

  public void offset(float left, float top, float right, float bottom)
  {
    offsetTopLeft.x += left;
    offsetTopLeft.y += top;
    offsetBottomRight.x += right;
    offsetBottomRight.y += bottom;
  }

  public String horizontalAlignmentAsString()
  {
    switch (horizontalAlignment)
    {
    case LEFT_ALIGNED:
      return LEFT_ALIGNED_TEXT;
    case CENTERED:
      return CENTERED_TEXT;
    case RIGHT_ALIGNED:
      return RIGHT_ALIGNED_TEXT;
    }
    return "";
  }

  public String verticalAlignmentAsString()
  {
    switch (verticalAlignment)
    {
    case TOP_ALIGNED:
      return "TopAligned";
    case CENTERED:
      return "Centered";
    case BOTTOM_ALIGNED:
      return "BottomAligned";
    }
    return "";
  }

  public void setHorizontalAlignmentFromString(String string)
  {
    if (string.equals(LEFT_ALIGNED_TEXT))
    {
      horizontalAlignment = LEFT_ALIGNED;
    }
    else if (string.equals(CENTERED_TEXT))
    {
      horizontalAlignment = CENTERED;
    }
    else if (string.equals(RIGHT_ALIGNED_TEXT))
    {
      horizontalAlignment = RIGHT_ALIGNED;
    }
  }

  public void setVerticalAlignmentFromString(String string)
  {
    if (string.equals(TOP_ALIGNED_TEXT))
    {
      verticalAlignment = TOP_ALIGNED;
    }
    else if (string.equals(CENTERED_TEXT))
    {
      verticalAlignment = CENTERED;
    }
    else if (string.equals(BOTTOM_ALIGNED_TEXT))
    {
      verticalAlignment = BOTTOM_ALIGNED;
    }
  }
}
