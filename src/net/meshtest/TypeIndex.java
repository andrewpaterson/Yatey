package net.meshtest;

import net.engine.file.chunk.ArrayDataConverter;
import net.engine.file.chunk.ExtendedFile;

public class TypeIndex
{
  public static final int sizeof_this = ExtendedFile.sizeof_int + ExtendedFile.sizeof_int + ExtendedFile.sizeof_int;
  
  public static final int TYPE_INDEX_GROUP_MASK = 0x7fffffff;
  public static final int TYPE_INDEX_EXTERNAL_FLAG = 0x80000000;

  public int type;  //The type of the data array used.
  public int index;  //The index into the above array to use.
  public int flags;  //A logical grouping of elements in the data array (also includes flags).

  public TypeIndex()
  {
  }

  public TypeIndex(int type, int index, int group, boolean external)
  {
    this.type = type;
    this.index = index;
    setGroup(group);
    setExternal(external);
  }

  int getGroup()
  {
    return flags & TYPE_INDEX_GROUP_MASK;
  }

  boolean isExternal()
  {
    return (flags & TYPE_INDEX_EXTERNAL_FLAG) != 0;
  }

  void setGroup(int iGroup)
  {
    flags = (flags & ~TYPE_INDEX_GROUP_MASK) | iGroup;
  }

  void setExternal(boolean bInternal)
  {
    flags = IntegerHelper.setFlag(flags, TYPE_INDEX_EXTERNAL_FLAG, bInternal);
  }

  public static class Converter extends ArrayDataConverter<TypeIndex>
  {
    public TypeIndex convertToElement(byte[] data)
    {
      TypeIndex element = new TypeIndex();
      element.type = ExtendedFile.fromCInt(data, 0);
      element.index = ExtendedFile.fromCInt(data, ExtendedFile.sizeof_int);
      element.flags = ExtendedFile.fromCInt(data, ExtendedFile.sizeof_int + ExtendedFile.sizeof_int);
      return element;
    }

    public byte[] convertFromElement(TypeIndex element)
    {
      byte[] data = new byte[sizeof_this];
      ExtendedFile.toCInt(data, 0, element.type);
      ExtendedFile.toCInt(data, ExtendedFile.sizeof_int, element.index);
      ExtendedFile.toCInt(data, ExtendedFile.sizeof_int + ExtendedFile.sizeof_int, element.flags);
      return data;
    }

    public int elementSize()
    {
      return TypeIndex.sizeof_this;
    }
  }
}
