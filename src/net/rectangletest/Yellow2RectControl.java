package net.rectangletest;

import net.engine.Control;
import net.engine.graphics.Sprite;

import java.awt.*;

public class Yellow2RectControl extends Control
{
  private Yellow2Rect yellow2Rect;

  public Yellow2RectControl(Yellow2Rect yellow2Rect)
  {
    super();
    this.yellow2Rect = yellow2Rect;
  }

  public void control()
  {
    if (yellow2Rect != null)
    {
      RectanglePanel gamePanel = (RectanglePanel) yellow2Rect.gamePanel;
      Point mousePosition = gamePanel.mousePosition;
      if (mousePosition != null)
      {
        yellow2Rect.setPosition(mousePosition.x, mousePosition.y);
      }
    }
  }

  public void startCollision(Sprite sprite)
  {
    yellow2Rect.celFrame = 1;
  }

  public void endCollision(Sprite sprite)
  {
    yellow2Rect.celFrame = 0;
  }
}