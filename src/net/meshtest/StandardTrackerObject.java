package net.meshtest;

import net.engine.file.chunk.ChunkFile;
import net.engine.file.chunk.ExtendedFile;

import java.io.IOException;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public abstract class StandardTrackerObject
{
  public static final int Max_Header_Name = 48;
  public static final int sizeof_this = Max_Header_Name + ExtendedFile.sizeof_int + ExtendedFile.sizeof_int;

  public String name;
  public int flags;
  public int uniqueID;

  public StandardTrackerObject()
  {
    name = "";
    flags = 0;
    uniqueID = 0;
  }

  public void setName(String name)
  {
    if (name.length() >= Max_Header_Name - 1)
    {
      name = name.substring(0, Max_Header_Name - 1);
    }
    this.name = name;
  }

  public static void load(ChunkFile chunkFile, StandardTrackerObject object) throws IOException
  {
    byte[] data = chunkFile.checkReadData(sizeof_this);

    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < Max_Header_Name; i++)
    {
      char c = (char) data[i];
      if (c == 0)
      {
        break;
      }
      else
      {
        stringBuffer.append(c);
      }
    }
    object.name = stringBuffer.toString();
    object.flags = ChunkFile.fromCInt(data, Max_Header_Name);
    object.uniqueID = ChunkFile.fromCInt(data, Max_Header_Name + ExtendedFile.sizeof_int);
  }
}
