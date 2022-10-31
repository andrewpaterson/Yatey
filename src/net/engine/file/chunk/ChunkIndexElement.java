package net.engine.file.chunk;

public class ChunkIndexElement
{
  public static final int sizeof_this = ExtendedFile.sizeof_int + ExtendedFile.sizeof_int + ExtendedFile.sizeof_int;

  public int name;
  public int chunkSize;
  public int chunkDataPos;

  public static class Converter extends ArrayDataConverter<ChunkIndexElement>
  {
    public ChunkIndexElement convertToElement(byte[] data)
    {
      ChunkIndexElement element = new ChunkIndexElement();
      element.name = ExtendedFile.fromCInt(data, 0);
      element.chunkSize = ExtendedFile.fromCInt(data, ExtendedFile.sizeof_int);
      element.chunkDataPos = ExtendedFile.fromCInt(data, ExtendedFile.sizeof_int + ExtendedFile.sizeof_int);
      return element;
    }

    public byte[] convertFromElement(ChunkIndexElement element)
    {
      byte[] data = new byte[sizeof_this];
      ExtendedFile.toCInt(data, 0, element.name);
      ExtendedFile.toCInt(data, ExtendedFile.sizeof_int, element.chunkSize);
      ExtendedFile.toCInt(data, ExtendedFile.sizeof_int + ExtendedFile.sizeof_int, element.chunkDataPos);
      return data;
    }

    public int elementSize()
    {
      return ChunkIndexElement.sizeof_this;
    }
  }
}
