package net.engine.file.chunk;

import net.meshtest.array.ArrayInt;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ExtendedFile
{
  public static final int sizeof_int = 4;
  public static final int sizeof_void_ptr = 4;

  protected RandomAccessFile randomAccessFile;

  public ExtendedFile()
  {
    randomAccessFile = null;
  }

  protected void writeCInt(int value) throws IOException
  {
    byte[] bytes = new byte[sizeof_int];
    bytes[0] = (byte) (value & 0x000000ff);
    bytes[1] = (byte) ((value & 0x0000ff00) >> 8);
    bytes[2] = (byte) ((value & 0x00ff0000) >> 16);
    bytes[3] = (byte) ((value & 0xff000000) >> 24);

    write(bytes);
  }

  public int readCInt() throws IOException
  {
    byte[] bytes = new byte[sizeof_int];
    read(bytes);

    return fromCInt(bytes, 0);
  }

  //You know these aren't going to work because java will skrew up the sign...
  public static int fromCInt(byte[] bytes, int offset)
  {
    return (int) ((((bytes[offset + 3] & 0xff) << 24) | ((bytes[offset + 2] & 0xff) << 16) | ((bytes[offset + 1] & 0xff) << 8) | ((bytes[offset] & 0xff))));
  }

  public static void toCInt(byte[] bytes, int offset, int integer)
  {
    bytes[offset] = (byte) (integer & 0xff);
    bytes[offset + 1] = (byte) ((integer & 0xff00) >> 8);
    bytes[offset + 2] = (byte) ((integer & 0xff0000) >> 16);
    bytes[offset + 3] = (byte) ((integer & 0xff0000) >> 24);
  }

  public void write(byte[] bytes) throws IOException
  {
    randomAccessFile.write(bytes);
  }

  public void read(byte[] bytes) throws IOException
  {
    randomAccessFile.read(bytes);
  }

  public void open(String fileName, FileMode eMode) throws IOException
  {
    if (randomAccessFile != null)
    {
      close();
    }
    File file = new File(fileName);
    if (eMode == FileMode.Read)
    {
      randomAccessFile = new RandomAccessFile(file, "r");
    }
    else if (eMode == FileMode.Read_Create)
    {
      if (!file.exists())
      {
        file.createNewFile();
      }
      randomAccessFile = new RandomAccessFile(file, "r");
    }
    else if (eMode == FileMode.Write_Create)
    {
      randomAccessFile = new RandomAccessFile(file, "rw");
    }
    else if (eMode == FileMode.ReadWrite)
    {
      randomAccessFile = new RandomAccessFile(file, "rw");
    }
    else if (eMode == FileMode.ReadWrite_Create)
    {
      if (!file.exists())
      {
        file.createNewFile();
      }
      randomAccessFile = new RandomAccessFile(file, "rw");
    }
  }

  public void close() throws IOException
  {
    if (randomAccessFile != null)
    {
      randomAccessFile.close();
    }
    randomAccessFile = null;
  }

  public void seek(long offset, FileSeekOrigin origin) throws IOException
  {
    if (origin != FileSeekOrigin.Start)
    {
      throw new IOException("Only Start is valid");
    }
    randomAccessFile.seek(offset);
  }

  public void seek(long offset) throws IOException
  {
    randomAccessFile.seek(offset);
  }

  public void flush()
  {
  }

  public boolean isEndOfFile() throws IOException
  {
    return getFilePos() >= getFileLength();
  }

  public boolean isOpen()
  {
    return randomAccessFile != null;
  }

  public long getFileLength() throws IOException
  {
    return randomAccessFile.length();
  }

  public long getFilePos() throws IOException
  {
    return randomAccessFile.getFilePointer();
  }

  public long getFileSize() throws IOException
  {
    return getFileLength();
  }

  public List readArray(ArrayDataConverter converter) throws IOException
  {
    checkReadType(ChunkDataTypes.DT_ArrayTemplate);
    int elementSize = readCInt();
    if (elementSize != converter.elementSize())
    {
      throw new IOException();
    }
    ArrayHeader header = readArrayHeader();

    if (header.usedElements != 0)
    {
      int byteSize = header.usedElements * header.elementSize;
      byte[] bytes = checkReadData(byteSize);
      return converter.convertToList(bytes);
    }
    else
    {
      return new ArrayList();
    }
  }

  private ArrayHeader readArrayHeader() throws IOException
  {
    ArrayHeader header = new ArrayHeader();
    header.elementSize = readCInt();
    header.numElements = readCInt();
    header.usedElements = readCInt();
    header.chunkSize = readCInt();
    readCPointer();
    return header;
  }

  public ArrayInt readArraySimpleInt() throws IOException
  {
    checkReadType(ChunkDataTypes.DT_ArraySimple);
    ArraySimpleHeader header = readArraySimpleHeader();
    if (header.usedElements != 0)
    {
      ArrayInt arrayInt = new ArrayInt(1);
      int byteSize = header.usedElements * sizeof_int;
      byte[] bytes = checkReadData(byteSize);
      for (int i = 0; i < header.usedElements; i++)
      {
        int value = fromCInt(bytes, i * sizeof_int);
        arrayInt.add(value);
      }
      return arrayInt;
    }
    else
    {
      return new ArrayInt(1);
    }
  }

  private ArraySimpleHeader readArraySimpleHeader() throws IOException
  {
    ArraySimpleHeader header = new ArraySimpleHeader();
    readCPointer();
    header.usedElements = readCInt();
    return header;
  }

  public List readArraySimple(ArrayDataConverter converter) throws IOException
  {
    checkReadType(ChunkDataTypes.DT_ArraySimple);
    ArraySimpleHeader header = readArraySimpleHeader();

    if (header.usedElements != 0)
    {
      int byteSize = header.usedElements * converter.elementSize();
      byte[] bytes = checkReadData(byteSize);
      return converter.convertToList(bytes);
    }
    else
    {
      return new ArrayList();
    }
  }

  public void writeArrayTemplate(ArrayDataConverter converter, List list, int chunkSize) throws IOException
  {
    int iElementSize = converter.elementSize();

    writeType(ChunkDataTypes.DT_ArrayTemplate);
    writeCInt(iElementSize);
    writeArrayTemplateHeader(new ArrayHeader(iElementSize, list.size(), chunkSize));

    if (list.size() > 0)
    {
      byte[] bytes = converter.convertFromList(list);
      writeData(bytes);
    }
  }

  private void writeArrayTemplateHeader(ArrayHeader header) throws IOException
  {
    writeCInt(header.elementSize);
    writeCInt(header.numElements);
    writeCInt(header.usedElements);
    writeCInt(header.chunkSize);
    writeCPointer();
  }

  private void writeCPointer() throws IOException
  {
    byte[] bytes = new byte[sizeof_void_ptr];
    write(bytes);
  }

  private int readCPointer() throws IOException
  {
    byte[] bytes = new byte[sizeof_void_ptr];
    read(bytes);
    return 0;
  }

  public byte[] checkReadData(int size) throws IOException
  {
    byte[] bytes = new byte[size];
    read(bytes);
    return bytes;
  }

  private void checkReadType(int checkType) throws IOException
  {
    int dataType = privateReadDataType();
    if (dataType != checkType)
    {
      throw new IOException();
    }
  }

  int privateReadDataType() throws IOException
  {
    return readCInt();
  }

  private void writeData(byte[] bytes) throws IOException
  {
    write(bytes);
  }

  void writeType(int type) throws IOException
  {
    writeCInt(type);
  }

  public int readDataTypeInt() throws IOException
  {
    checkReadType(ChunkDataTypes.DT_Int);
    return readCInt();
  }
}
