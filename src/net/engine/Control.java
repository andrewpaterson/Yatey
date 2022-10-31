package net.engine;

import net.engine.graphics.Sprite;

public abstract class Control
{
  public abstract void control();

  public void collide(Sprite sprite, int ticks)
  {
  }

  public void startCollision(Sprite sprite)
  {
  }

  public void endCollision(Sprite sprite)
  {
  }
}
