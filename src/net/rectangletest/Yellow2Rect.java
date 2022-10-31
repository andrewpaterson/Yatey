package net.rectangletest;

import net.engine.GamePanel;
import net.engine.cel.CelStore;
import net.engine.graphics.Sprite;

public class Yellow2Rect extends Sprite
{
  public Yellow2Rect(GamePanel gamePanel, float x, float y)
  {
    super(gamePanel, x, y);
    setName("Yellow");
    addCelsFromCelHelper(CelStore.getInstance().get("YELLOW2 20x20"));
    addBoundingBoxes(0);
    setCollisionBit(0, true);
    setLayer(2);
    setControl(new Yellow2RectControl(this));
  }
}
