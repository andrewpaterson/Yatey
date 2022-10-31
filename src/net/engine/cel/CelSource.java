package net.engine.cel;

import net.engine.cel.Cel;

import java.awt.*;

public class CelSource
{
  public Cel cel;
  public Rectangle source;
  public boolean flippedHorizontal;
  public boolean flippedVertical;

  public CelSource()
  {
    cel = new Cel();
    source = new Rectangle();
  }

  public CelSource(Cel cel, Rectangle source, boolean flippedHorizontal, boolean flippedVertical)
  {
    this.cel = cel;
    this.source = source;
    this.flippedHorizontal = flippedHorizontal;
    this.flippedVertical = flippedVertical;
  }

  public CelSource(CelSource celSource)
  {
    cel = new Cel(celSource.cel);
    source = new Rectangle(celSource.source);
    flippedHorizontal = celSource.flippedHorizontal;
    flippedVertical = celSource.flippedVertical;
  }
}
