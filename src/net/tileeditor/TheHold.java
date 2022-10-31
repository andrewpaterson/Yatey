package net.tileeditor;

import java.util.List;
import java.util.ArrayList;

public class TheHold
{
  public static TheHold instance = null;
  public int maxStack;

  private List<Held> stack;
  private int undoPos;

  public TheHold()
  {
    stack = new ArrayList<Held>();
    undoPos = -1;
    maxStack = 50;
  }

  public static TheHold getInstance()
  {
    if (instance == null)
    {
      instance = new TheHold();
    }
    return instance;
  }

  public void hold(int reason)
  {
    Source source = new Source(Source.getInstance());
    if (undoPos < stack.size() - 1)
    {
      for (int i = stack.size() - 1; i > undoPos; i--)
      {
        stack.remove(i);
      }
    }
    stack.add(new Held(source, reason));
    undoPos = stack.size() - 1;

    if (stack.size() > maxStack)
    {
      shiftLeft();
    }
  }

  private void shiftLeft()
  {
    undoPos--;
    stack.remove(0);
  }

  public int undo()
  {
    if (undoPos != 0)
    {
      undoPos--;
      Held held = stack.get(undoPos);
      Source.setInstance(held.source);
      return held.updatability();
    }
    return 0;
  }

  public int redo()
  {
    if (undoPos < stack.size() - 1)
    {
      undoPos++;
      Held held = stack.get(undoPos);
      Source.setInstance(held.source);
      return held.updatability();
    }
    return 0;
  }

  public List<Held> getStack()
  {
    return stack;
  }

  public int getUndoPos()
  {
    return undoPos;
  }

  public void setMaxStack(int maxStack)
  {
    this.maxStack = maxStack;
  }

  public int getMaxStack()
  {
    return maxStack;
  }
}
