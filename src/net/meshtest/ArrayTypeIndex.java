package net.meshtest;

import net.meshtest.array.ArrayInt;
import net.engine.file.chunk.ChunkFile;
import net.engine.file.chunk.ArraySimpleHeader;

import java.util.List;
import java.io.IOException;

public class ArrayTypeIndex
{
  public List<TypeIndex> array;
  public static final int sizeof_this = ArraySimpleHeader.sizeof_this;

  public int getIndexForTypeInGroup(int type, int iGroup, int typeNum)
  {
    int i;
    int iCount;
    TypeIndex psIndex;

    iCount = 0;
    for (i = 0; i < array.size(); i++)
    {
      psIndex = array.get(i);
      if ((psIndex.type == type) && (psIndex.getGroup() == iGroup))
      {
        if (iCount == typeNum)
        {
          return array.get(i).index;
        }
        iCount++;
      }
    }
    return -1;
  }

  public ArrayInt getIndiciesForTypeInGroup(int type, int iGroup)
  {
    int i;
    TypeIndex psIndex;
    int iCount;
    ArrayInt arrayInt = new ArrayInt(8);

    iCount = 0;
    for (i = 0; i < array.size(); i++)
    {
      psIndex = array.get(i);
      if ((psIndex.type == type) && (psIndex.getGroup() == iGroup))
      {
        if (arrayInt.length() > iCount)
        {
          arrayInt.set(iCount, array.get(i).index);
        }
        else
        {
          arrayInt.add(array.get(i).index);
        }
        iCount++;
      }
    }

    return arrayInt;
  }

  public ArrayTypeIndex getIndiciesForType(int type)
  {
    int i;
    TypeIndex index;
    int iCount;
    ArrayTypeIndex typeIndicies = new ArrayTypeIndex();

    //This assumes that typeIndicies is setup reasonably but does not have to be empty.
    iCount = 0;
    for (i = 0; i < array.size(); i++)
    {
      index = array.get(i);
      if (index.type == type)
      {
        if (typeIndicies.array.size() > iCount)
        {
          typeIndicies.set(iCount, index);
        }
        else
        {
          typeIndicies.add(index);
        }
        iCount++;
      }
    }
    return typeIndicies;
  }

  private void add(TypeIndex typeIndex)
  {
    array.add(typeIndex);
  }

  private void set(int index, TypeIndex typeIndex)
  {
    array.set(index, typeIndex);
  }

  public int addIndexForType(int type, int index, int iGroup, boolean bExternal)
  {
    TypeIndex psTypeIndex = new TypeIndex(type, index, iGroup, bExternal);
    int iReturn = array.size();
    add(psTypeIndex);
    return iReturn;
  }

  public int addIndexForTypeIfUnique(int type, int index, int iGroup, boolean bExternal)
  {
    int i;

    i = GetPosition(type, index, iGroup);
    if (i == -1)
    {
      addIndexForType(type, index, iGroup, bExternal);

      //-1 implies that a new one was added at position (array.size() - 1).
      return -1;
    }
    return i;
  }

  public ArrayInt getUniqueTypes()
  {
    int i;
    TypeIndex psType;
    ArrayInt arrayInt = new ArrayInt(8);

    for (i = 0; i < array.size(); i++)
    {
      psType = array.get(i);
      arrayInt.addIfUnique(psType.type);
    }

    arrayInt.sort();
    return arrayInt;
  }

  public ArrayInt getTypes()
  {
    int i;
    TypeIndex psType;
    ArrayInt arrayInt = new ArrayInt(8);

    for (i = 0; i < array.size(); i++)
    {
      psType = array.get(i);
      arrayInt.add(psType.type);
    }
    return arrayInt;
  }

  public void getSortedTypes()
  {
    int i;
    TypeIndex psType;
    ArrayInt arrayInt = new ArrayInt(8);

    for (i = 0; i < array.size(); i++)
    {
      psType = array.get(i);
      arrayInt.add(psType.type);
    }
    arrayInt.sort();
  }

  public int GetPosition(int type, int typeIndex, int iGroup)
  {
    int i;
    TypeIndex psType;

    for (i = 0; i < array.size(); i++)
    {
      psType = array.get(i);
      if ((psType.type == type) && (psType.index == typeIndex) && (psType.getGroup() == iGroup))
      {
        return i;
      }
    }
    return -1;
  }

  public void RemoveIndexForTypeInGroup(int type, int iGroup)
  {
    int i;
    TypeIndex psIndex;

    for (i = 0; i < array.size(); i++)
    {
      psIndex = array.get(i);
      if ((psIndex.type == type) && (psIndex.getGroup() == iGroup))
      {
        removeAt(i);
        i--;
      }
    }
  }

  private void removeAt(int index)
  {
    array.remove(index);
  }

  public void Dump()
  {
    int i;
    TypeIndex psIndex;
    StringBuffer sz;

    sz = new StringBuffer();
    for (i = 0; i < array.size(); i++)
    {
      psIndex = array.get(i);
      sz.append("[");
      sz.append(psIndex.type);
      sz.append(", ");
      sz.append(psIndex.index);
      sz.append("], ");
    }
    sz.deleteCharAt(sz.length()-1);
    sz.deleteCharAt(sz.length()-1);
    sz.append("\n");
    System.out.println(sz);
  }

  public void load(ChunkFile chunkFile) throws IOException
  {
    array = chunkFile.readArraySimple(new TypeIndex.Converter());
  }
}
