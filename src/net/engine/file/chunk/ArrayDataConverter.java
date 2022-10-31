package net.engine.file.chunk;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class ArrayDataConverter<E>
{
  public abstract E convertToElement(byte[] data);

  public abstract byte[] convertFromElement(E element);

  public abstract int elementSize();

  public List<E> convertToList(byte[] data)
  {
    ArrayList<E> list = new ArrayList<E>();
    int numElements = data.length / elementSize();
    for (int i = 0; i < numElements; i++)
    {
      byte[] elementData = Arrays.copyOfRange(data, i * elementSize(), (i + 1) * elementSize());
      list.add(convertToElement(elementData));
    }
    return list;
  }

  public byte[] convertFromList(List<E> list)
  {
    int dataLength = list.size() * elementSize();
    byte[] data = new byte[dataLength];

    for (int i = 0; i < list.size(); i++)
    {
      byte[] element = convertFromElement(list.get(i));
      System.arraycopy(element, 0, data, i * elementSize(), elementSize());
    }
    return data;
  }
}
