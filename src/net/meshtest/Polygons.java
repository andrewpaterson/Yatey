package net.meshtest;

import net.engine.file.chunk.ArrayDataConverter;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public class Polygons
{
  public static final int Name = 4;

  public static class Converter extends ArrayDataConverter<Polygons>
  {
    public Polygons convertToElement(byte[] data)
    {
      return new Polygons();
    }

    public byte[] convertFromElement(Polygons element)
    {
      return new byte[0];
    }

    public int elementSize()
    {
      return 0;
    }
  }
}
