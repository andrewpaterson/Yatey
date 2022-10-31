package net.meshtest;

import net.engine.file.chunk.ArrayDataConverter;
import net.engine.file.chunk.ChunkFile;
import net.engine.file.chunk.ExtendedFile;
import net.engine.file.chunk.SIndex;

import java.io.IOException;

public class Edge extends DataIndicies
{
  public static final int Name = 3;
  public static final int Num_Edge_Corners = 2;
  public static final int Max_Edge_Faces = 2;
  public static final int sizeof_this = DataIndicies.sizeof_this +
                                        SIndex.sizeof_this * Num_Edge_Corners +
                                        ExtendedFile.sizeof_int * Max_Edge_Faces +
                                        ExtendedFile.sizeof_int;

  public SIndex asCorner[];
  public int aiFace[];
  public int iSelected;

  public Edge()
  {
    asCorner = new SIndex[Num_Edge_Corners];
    for (int i = 0; i < Num_Edge_Corners; i++)
    {
      asCorner[i] = new SIndex();
    }
    aiFace = new int[Max_Edge_Faces];
  }

  public void load(ChunkFile chunkFile) throws IOException
  {
    super.load(chunkFile);
    for (int i = 0; i < Num_Edge_Corners; i++)
    {
      asCorner[i].load(chunkFile);
    }
    for (int i = 0; i < Max_Edge_Faces; i++)
    {
      aiFace[i] = chunkFile.readCInt();
    }
    iSelected = chunkFile.readDataTypeInt();
  }

  public static class Converter extends ArrayDataConverter<Edge>
  {
    public Edge convertToElement(byte[] data)
    {
      Edge element = new Edge();

      for (int i = 0; i < Max_Edge_Faces; i++)
      {
        element.aiFace[i] = ExtendedFile.fromCInt(data, SIndex.sizeof_this + i * ExtendedFile.sizeof_int);
      }
      element.iSelected = ExtendedFile.fromCInt(data, SIndex.sizeof_this + Max_Edge_Faces * ExtendedFile.sizeof_int);
      return element;
    }

    public byte[] convertFromElement(Edge element)
    {
      return new byte[0];
    }

    public int elementSize()
    {
      return Edge.sizeof_this;
    }
  }
}
