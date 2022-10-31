package net.rectangletest;

import net.engine.cel.Cel;
import net.engine.graphics.Sprite;

import java.awt.event.KeyEvent;

public class YellowRectControl extends RectControl
{
  public YellowRectControl(Sprite sprite)
  {
    super(sprite);
  }

  public void control()
  {
    super.control();
    Cel cel = sprite.getCel();

    if (gamePanel.keyDown(KeyEvent.VK_O))
    {
      cel.setOffset(0, 0, 0, 0);
    }
    if (gamePanel.keyDown(KeyEvent.VK_I))
    {
      cel.setOffset(10, 10, 10, 10);
    }
  }
}
