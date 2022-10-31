package net.engine.file.chunk;

public class ArrayHeader
{
  public static final int sizeof_this = ExtendedFile.sizeof_int +
                                        ExtendedFile.sizeof_int +
                                        ExtendedFile.sizeof_int +
                                        ExtendedFile.sizeof_int +
                                        ExtendedFile.sizeof_void_ptr;

  public int elementSize;
  public int numElements;
  public int usedElements;
  public int chunkSize;

  public ArrayHeader()
  {
  }

  public ArrayHeader(int elementSize, int usedElements, int chunkSize)
  {
    this.elementSize = elementSize;
    this.usedElements = usedElements;
    this.chunkSize = chunkSize;
    int remainder = usedElements % chunkSize;
    if (remainder != 0)
    {
      remainder = chunkSize;
    }
    this.numElements = (usedElements / chunkSize) * chunkSize + remainder;
  }
}
