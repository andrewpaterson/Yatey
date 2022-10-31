package net.engine.file.chunk;

import java.io.IOException;

public class ChunkHeader
{
  public static final int sizeof_this = ExtendedFile.sizeof_int + ExtendedFile.sizeof_int + ExtendedFile.sizeof_int;;

  public ChunkFile chunkFile;

  public int name;
  public int chunkSize;
  public int chunkIndexPos;

  public ChunkHeader(ChunkFile chunkFile)
  {
    this.chunkFile = chunkFile;
  }

  public void read() throws IOException
  {
    name = chunkFile.readCInt();
    chunkSize = chunkFile.readCInt();
    chunkIndexPos = chunkFile.readCInt();
  }

  public void write() throws IOException
  {
    chunkFile.writeCInt(name);
    chunkFile.writeCInt(chunkSize);
    chunkFile.writeCInt(chunkIndexPos);
  }
}

