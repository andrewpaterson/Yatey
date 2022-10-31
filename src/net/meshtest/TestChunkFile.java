package net.meshtest;

import net.engine.file.chunk.ChunkFile;

import java.io.IOException;

public class TestChunkFile
{
  public static void main(String[] args) throws IOException
  {
    ChunkFile chunkFile = new ChunkFile();
    chunkFile.readOpen("/home/r2d2/Brick.DRG");

    loadTracker(chunkFile);

    chunkFile.readClose();
  }

  static int loadTracker(ChunkFile chunkFile) throws IOException
  {
    int chunkNum;
    int iFirstIndex;
    int iIndex;

    iFirstIndex = 0;
    chunkNum = chunkFile.findFirstChunkWithName(Mesh.Name);

    iIndex = 0;
    while (chunkNum != -1)
    {
      Mesh mesh = new Mesh();
      mesh.load(chunkFile, chunkNum);

      chunkNum = chunkFile.findNextChunkWithName();
      iIndex++;
    }
    System.out.println(iIndex);
    return iFirstIndex;
  }
}
