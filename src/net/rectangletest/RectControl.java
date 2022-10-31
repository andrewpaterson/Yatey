package net.rectangletest;

import net.engine.Control;
import net.engine.graphics.Sprite;
import net.engine.cel.Cel;

import java.awt.event.KeyEvent;

public class RectControl extends Control
{
  protected Sprite sprite;
  protected RectanglePanel gamePanel;

  public RectControl(Sprite sprite)
  {
    super();
    this.sprite = sprite;
    gamePanel = (RectanglePanel) sprite.gamePanel;
  }

  public void control()
  {
    Cel cel = sprite.getCel();

    if (gamePanel.keyDown(KeyEvent.VK_T))
    {
      cel.setVerticalAlignment(Cel.TOP_ALIGNED);
    }
    if (gamePanel.keyDown(KeyEvent.VK_B))
    {
      cel.setVerticalAlignment(Cel.BOTTOM_ALIGNED);
    }
    if (gamePanel.keyDown(KeyEvent.VK_C))
    {
      cel.setAlignment(Cel.CENTERED, Cel.CENTERED);
    }
    if (gamePanel.keyDown(KeyEvent.VK_L))
    {
      cel.setHorizontalAlignment(Cel.LEFT_ALIGNED);
    }
    if (gamePanel.keyDown(KeyEvent.VK_R))
    {
      cel.setHorizontalAlignment(Cel.RIGHT_ALIGNED);
    }
  }
}
