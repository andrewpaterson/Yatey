package net.rectangletest;

import net.engine.GamePanel;
import net.engine.cel.CelStore;
import net.engine.graphics.Sprite;

public class YellowRect extends Sprite
{
  public YellowRect(GamePanel gamePanel, float x, float y)
  {
    super(gamePanel, x, y);
    addCelsFromCelHelper(CelStore.getInstance().get("YELLOW 20x20"));
    setLayer(0);
    setControl(new YellowRectControl(this));
  }
}
