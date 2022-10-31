package net.meshtest;

import net.engine.file.chunk.ChunkFile;

import java.io.IOException;

public class DataIndicies
{
  public ArrayTypeIndex type;
  public static final int sizeof_this = ArrayTypeIndex.sizeof_this;

  public DataIndicies()
  {
    type = new ArrayTypeIndex();
  }

  public void load(ChunkFile chunkFile) throws IOException
  {
    type.load(chunkFile);
  }
}
