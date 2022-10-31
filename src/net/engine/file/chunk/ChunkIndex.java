package net.engine.file.chunk;

import java.util.List;

public class ChunkIndex
{
  public int searchName;
  public int currChunkNum;
  public List<ChunkIndexElement> chunkIndicies;

  public ChunkIndex(List<ChunkIndexElement> chunkIndicies)
  {
    this.chunkIndicies = chunkIndicies;
    searchName = -1;
    currChunkNum = -1;
  }

  public int byteSize()
  {
    ChunkIndexElement.Converter converter = new ChunkIndexElement.Converter();
    return chunkIndicies.size() * converter.elementSize();
  }

  int findFirstChunkWithName(int name)
  {
    int i;
    int testName;

    currChunkNum = 0;
    searchName = name;

    for (i = 0; i < chunkIndicies.size(); i++)
    {
      testName = chunkIndicies.get(i).name;
      if (testName == name)
      {
        currChunkNum = i + 1;
        return i;
      }
    }
    return -1;
  }

  int findNextChunkWithName()
  {
    int i;

    for (i = currChunkNum; i < chunkIndicies.size(); i++)
    {
      if (chunkIndicies.get(i).name == searchName)
      {
        currChunkNum = i + 1;
        return i;
      }
    }
    return -1;
  }

  int getNumChunksWithName(int name)
  {
    int i;
    int numWithName;

    numWithName = 0;

    for (i = 0; i < chunkIndicies.size(); i++)
    {
      if (chunkIndicies.get(i).name == name)
      {
        numWithName++;
      }
    }
    return numWithName;
  }
}
