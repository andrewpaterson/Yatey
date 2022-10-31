package net.meshtest;

import net.engine.file.chunk.ArrayDataConverter;
import net.engine.file.chunk.ChunkFile;
import net.engine.file.chunk.ExtendedFile;
import net.engine.file.chunk.ArraySimpleHeader;
import net.meshtest.array.ArrayInt;

import java.io.IOException;

public class Corner extends DataIndicies
{
  public static final int Name = 2;
  public static final int sizeof_this = DataIndicies.sizeof_this +
                                        ArraySimpleHeader.sizeof_this +
                                        ArraySimpleHeader.sizeof_this +
                                        ExtendedFile.sizeof_int;

  public ArrayInt faceIndicies;
  public ArrayInt edgeIndicies;
  public int selected;

  public Corner()
  {
    faceIndicies = new ArrayInt(1);
    edgeIndicies = new ArrayInt(1);
    selected = 0;
  }

  public void load(ChunkFile chunkFile) throws IOException
  {
    super.load(chunkFile);
    edgeIndicies = chunkFile.readArraySimpleInt();
    faceIndicies = chunkFile.readArraySimpleInt();
    selected = chunkFile.readDataTypeInt();
  }

  public static class Converter extends ArrayDataConverter<Corner>
  {
    public Corner convertToElement(byte[] data)
    {
      Corner element = new Corner();
      element.selected = ExtendedFile.fromCInt(data, ArraySimpleHeader.sizeof_this + ArraySimpleHeader.sizeof_this);
      return element;
    }

    public byte[] convertFromElement(Corner element)
    {
      return new byte[0];
    }

    public int elementSize()
    {
      return Corner.sizeof_this;
    }
  }
}
