package net.engine.file.chunk;

import java.util.ArrayList;
import java.util.List;

public class ChunkStack
{
  public List<ChunkStackElement> stack;
  private ChunkFile chunkFile;

  public ChunkStack(ChunkFile chunkFile)
  {
    this.chunkFile = chunkFile;
    stack = new ArrayList<ChunkStackElement>();
  }

  public ChunkStackElement push()
  {
    ChunkStackElement element = new ChunkStackElement(chunkFile);
    stack.add(element);
    return element;
  }

  public ChunkStackElement tail()
  {
    if (stack.size() > 0)
    {
    return stack.get(stack.size()-1);
    }
    else
    {
      return null;
    }
  }

  public void pop()
  {
    stack.remove(stack.size()-1);
  }

  public int size()
  {
    return stack.size();
  }

  public ChunkStackElement get(int index)
  {
    return stack.get(index);
  }
}
