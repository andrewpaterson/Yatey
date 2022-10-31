package net.engine.file.chunk;

import net.meshtest.array.ArrayInt;

import java.io.IOException;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public class SIndex
{
  public static final int sizeof_this = ExtendedFile.sizeof_int +
                                        ArraySimpleHeader.sizeof_this;

  public int typeIndex;  //The index into the type (eg: corner) array.
  public ArrayInt typeIndicies;  //The indicies into the CDataIndicies array.

  public void load(ChunkFile chunkFile) throws IOException
  {
    typeIndex = chunkFile.readDataTypeInt();
    typeIndicies = chunkFile.readArraySimpleInt();
  }
}
