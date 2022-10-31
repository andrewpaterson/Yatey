package net.engine.file.chunk;

import java.io.IOException;
import java.util.List;

public class ChunkFile extends ExtendedFile
{
  ChunkStack chunkStack;

  public ChunkFile()
  {
    chunkStack = null;
  }

  private void __privateReadChunkBegin() throws IOException
  {
    ChunkStackElement element = chunkStack.push();
    element.readInit();

    if (element.header.chunkIndexPos != -1)
    {
      element.containsChunks = true;
    }
    else
    {
      element.containsChunks = false;
    }
    if (element.containsChunks)
    {
      element.chunkIndex = __privateReadChunkIndex(element.header.chunkIndexPos);
      seek(element.chunkHeaderPos + sizeof_SChunkHeader(), FileSeekOrigin.Start);
    }
  }

  private int sizeof_SChunkHeader()
  {
    return sizeof_int + sizeof_int + sizeof_int;
  }

  private ChunkIndex __privateReadChunkIndex(int chunkIndexPos) throws IOException
  {
    seek(chunkIndexPos, FileSeekOrigin.Start);

    List<ChunkIndexElement> chunkIndexElements = readArray(new ChunkIndexElement.Converter());
    return new ChunkIndex(chunkIndexElements);
  }

  public void readOpen(String fileName) throws IOException
  {
    chunkStack = new ChunkStack(this);
    open(fileName, FileMode.Read);
    __privateReadChunkBegin();
  }

  public void readClose() throws IOException
  {
    chunkStack = null;
    randomAccessFile.close();
  }

  public void writeOpen(String fileName) throws IOException
  {
    chunkStack = new ChunkStack(this);
    open(fileName, FileMode.Write_Create);
    writeChunkBegin();
  }

  public void writeClose() throws IOException
  {
    writeChunkEnd(ChunkFileStatics.chunk);
    close();
  }

  public void readChunkBegin(int chunkNum) throws IOException
  {
    ChunkStackElement element;
    ChunkIndexElement index;

    element = chunkStack.tail();
    if (element != null)
    {
      index = element.chunkIndex.chunkIndicies.get(chunkNum);
      seek(index.chunkDataPos, FileSeekOrigin.Start);
    }
  }

  public void readIndexEnd() throws IOException
  {
    ChunkStackElement element;
    int iSeekPos;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        iSeekPos = element.header.chunkIndexPos;
        iSeekPos += element.chunkIndex.byteSize() + +sizeof_int * 2 + sizeof_int * 3 + sizeof_void_ptr;  //Seriously.  I haven't the foggiest why I did this.
      }
      else
      {
        iSeekPos = element.chunkHeaderPos;
        iSeekPos += element.header.chunkSize;
      }
      seek(iSeekPos, FileSeekOrigin.Start);
      chunkStack.pop();
    }
  }

  public boolean verifyChunkName(int iChunkNum, int iChunkName)
  {
    ChunkStackElement element;
    ChunkIndexElement index;

    element = chunkStack.tail();
    if (element != null)
    {
      index = element.chunkIndex.chunkIndicies.get(iChunkNum);
      if (index != null)
      {
        if (index.name == iChunkName)
        {
          return true;
        }
      }
    }
    return false;
  }

  public int findFirstChunkWithName(int name)
  {
    ChunkStackElement element;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        return element.chunkIndex.findFirstChunkWithName(name);
      }
    }
    return -1;
  }

  public int findNextChunkWithName()
  {
    ChunkStackElement element;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        return element.chunkIndex.findNextChunkWithName();
      }
    }
    return -1;
  }

  public int getNumChunksWithName(int name)
  {
    ChunkStackElement element;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        return element.chunkIndex.getNumChunksWithName(name);
      }
    }
    return 0;
  }

  public int getNumChunks()
  {
    ChunkStackElement element;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        return element.chunkIndex.chunkIndicies.size();
      }
    }
    return ChunkFileStatics.error;
  }

  public int getChunkName(int iChunkNum)
  {
    ChunkIndexElement index;
    ChunkStackElement element;

    element = chunkStack.tail();
    if (element != null)
    {
      if (element.containsChunks)
      {
        index = element.chunkIndex.chunkIndicies.get(iChunkNum);
        if (index != null)
        {
          return index.name;
        }
      }
    }
    return ChunkFileStatics.error;
  }

  public void writeChunkBegin() throws IOException
  {
    long filePos;
    ChunkStackElement element;

    filePos = getFilePos();
    element = chunkStack.tail();
    if (element != null)
    {
      element.containsChunks = true;
    }

    element = chunkStack.push();
    element.writeInit((int) filePos);
  }

  public void writeChunkEnd(int iChunkName) throws IOException
  {
    long filePos;
    ChunkStackElement element;
    ChunkStackElement parent;
    ChunkIndexElement indexParent;

    filePos = getFilePos();
    element = chunkStack.tail();
    parent = chunkStack.get(chunkStack.size() - 2);
    if (element != null)
    {
      element.header.chunkSize = (int) filePos - element.chunkHeaderPos - ChunkHeader.sizeof_this;
      element.header.name = iChunkName;
      if (element.containsChunks)
      {
        element.header.chunkIndexPos = (int) filePos;
      }

      if (parent != null)
      {
        indexParent = new ChunkIndexElement();
        parent.chunkIndex.chunkIndicies.add(indexParent);
        indexParent.chunkDataPos = element.chunkHeaderPos + ChunkHeader.sizeof_this;
        indexParent.name = iChunkName;
        indexParent.chunkSize = element.header.chunkSize;
      }

      seek(element.chunkHeaderPos, FileSeekOrigin.Start);
      element.header.write();
      seek(0, FileSeekOrigin.End);

      if (element.containsChunks)
      {
        writeArrayTemplate(new ChunkIndexElement.Converter(), element.chunkIndex.chunkIndicies, 1024);
      }
      chunkStack.pop();
    }
  }
}


