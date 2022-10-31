package net.rectangletest;

import net.engine.graphics.Sprite;
import net.engine.GamePanel;
import net.engine.cel.CelStore;

public class GreenRect extends Sprite
{
  public GreenRect(GamePanel gamePanel, float x, float y)
  {
    super(gamePanel, x, y);
    addCelsFromCelHelper(CelStore.getInstance().get("GREEN 40x40"));
    setLayer(1);
    setControl(new RectControl(this));
  }
}
