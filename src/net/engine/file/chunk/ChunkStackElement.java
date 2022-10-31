package net.engine.file.chunk;

import net.engine.md5.MD5Context;

import java.io.IOException;

public class ChunkStackElement
{
  public ChunkHeader header;
  public int chunkHeaderPos;
  public boolean containsChunks;
  public ChunkIndex chunkIndex;
  public MD5Context md5Context;

  public ChunkStackElement(ChunkFile chunkFile)
  {
    header = new ChunkHeader(chunkFile);
    chunkHeaderPos = -1;
    containsChunks = false;
    chunkIndex = null;
  }

  public void readInit() throws IOException
  {
    chunkHeaderPos = (int) header.chunkFile.getFilePos();
    header.read();
    chunkIndex = null;
  }

  public void writeInit(int chunkHeaderPos) throws IOException
  {
    containsChunks = false;
    this.chunkHeaderPos = chunkHeaderPos;
    header.chunkIndexPos = -1;
    header.chunkSize = 0;
    header.name = -1;
    header.write();
    chunkIndex = null;
  }
}
