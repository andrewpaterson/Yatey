package net.meshtest.array;

import net.engine.file.chunk.ExtendedFile;

import java.util.Arrays;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public class ArrayInt
{
  public int numElements;
  public int usedElements;
  public int[] array;
  public int chunkSize;

  public ArrayInt(int chunkSize)
  {
    this.chunkSize = chunkSize;
    numElements = 0;
    usedElements = 0;
    array = null;
  }

  public void add(int value)
  {
    usedElements++;
    if (usedElements > numElements)
    {
      setArraySize(numElements + chunkSize);
    }
    array[usedElements - 1] = value;
  }

  private void setArraySize(int newNumElements)
  {
    if (newNumElements == 0)
    {
      array = null;
      numElements = 0;
    }
    else
    {
      if (numElements != newNumElements)
      {
        int[] newArray = new int[newNumElements];
        if (array != null)
        {
          System.arraycopy(array, 0, newArray, 0, numElements);
        }
        array = newArray;
        numElements = newNumElements;
      }
    }
  }

  public void sort()
  {
    Arrays.sort(array, 0, usedElements);
  }

  public int length()
  {
    return usedElements;
  }

  public int[] subArray(int startIndex, int endIndex)
  {
    int length = endIndex - startIndex;
    int[] subArray = new int[length];
    System.arraycopy(array, startIndex, subArray, 0, length);
    return subArray;
  }

  public void set(int value, int index)
  {
    array[index] = value;
  }

  public void addIfUnique(int value)
  {
    for (int existing : array)
    {
      if (existing == value)
      {
        return;
      }
    }
    add(value);
  }
}
