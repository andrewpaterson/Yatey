package net.meshtest;

import net.engine.file.chunk.ArrayDataConverter;
import net.engine.file.chunk.ChunkFile;
import net.engine.file.chunk.ExtendedFile;
import net.engine.file.chunk.SIndex;

import java.io.IOException;

public class Face extends DataIndicies
{
  public static final int Name = 5;
  public static final int Num_Face_Corners = 3;
  public static final int Num_Face_Edges = 3;
  public static final int sizeof_this = DataIndicies.sizeof_this +
                                        SIndex.sizeof_this * Num_Face_Corners +
                                        SIndex.sizeof_this * Num_Face_Edges +
                                        ExtendedFile.sizeof_int;

  public SIndex asCorner[];
  public SIndex asEdge[];
  public int iSelected;

  public Face()
  {
    asCorner = new SIndex[Num_Face_Corners];
    for (int i = 0; i < Num_Face_Corners; i++)
    {
      asCorner[i] = new SIndex();
    }
    asEdge = new SIndex[Num_Face_Edges];
    for (int i = 0; i < Num_Face_Edges; i++)
    {
      asEdge[i] = new SIndex();
    }
    iSelected = 0;
  }

  public void load(ChunkFile chunkFile) throws IOException
  {
    super.load(chunkFile);
    for (int i = 0; i < Num_Face_Corners; i++)
    {
      asCorner[i].load(chunkFile);
    }
    for (int i = 0; i < Num_Face_Edges; i++)
    {
      asEdge[i].load(chunkFile);
    }
    iSelected = chunkFile.readDataTypeInt();
  }

  public static class Converter extends ArrayDataConverter<Face>
  {
    public Face convertToElement(byte[] data)
    {
      Face element = new Face();
      element.iSelected = ExtendedFile.fromCInt(data, SIndex.sizeof_this * Num_Face_Corners + SIndex.sizeof_this * Num_Face_Edges);
      return element;
    }

    public byte[] convertFromElement(Face element)
    {
      return new byte[0];
    }

    public int elementSize()
    {
      return Face.sizeof_this;
    }
  }
}
