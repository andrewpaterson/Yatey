package net.meshtest;

import net.engine.file.chunk.ChunkFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public class Mesh extends StandardTrackerObject
{
  public static final int Name = 1;

  public List<Corner> corners;
  public List<Edge> edges;
  public List<Face> faces;
  public List<Polygons> polygons;

  public Mesh()
  {
    corners = new ArrayList<Corner>();
    edges = new ArrayList<Edge>();
    faces = new ArrayList<Face>();
    polygons = new ArrayList<Polygons>();
  }

  public void load(ChunkFile chunkFile, int chunkNum) throws IOException
  {
    if (!chunkFile.verifyChunkName(chunkNum, Name))
    {
      throw new IOException("Could not load Mesh from chunk, not a Mesh.");
    }

    chunkFile.readChunkBegin(chunkNum);
    StandardTrackerObject.load(chunkFile, this);

    //Read in the corners array.  Most of it will be garbage pointers on load...
    chunkNum = chunkFile.findFirstChunkWithName(Corner.Name);
    chunkFile.readChunkBegin(chunkNum);

    corners = chunkFile.readArray(new Corner.Converter());
    //Read in each corner... they contain arrays themselves.
    for (int i = 0; i < corners.size(); i++)
    {
      Corner corner = corners.get(i);
      corner.load(chunkFile);
    }

    //Read in the edges array.  Most of it will be garbage pointers on load...
    chunkNum = chunkFile.findFirstChunkWithName(Edge.Name);
    chunkFile.readChunkBegin(chunkNum);

    edges = chunkFile.readArray(new Edge.Converter());
    for (int i = 0; i < edges.size(); i++)
    {
      Edge edge = edges.get(i);
      edge.load(chunkFile);
    }

    //Read in the polygon groups.
    chunkNum = chunkFile.findFirstChunkWithName(Polygons.Name);
    chunkFile.readChunkBegin(chunkNum);

    polygons = new ArrayList<Polygons>();

    //Read in the faces array.  Most of it will be garbage pointers on load...
    chunkNum = chunkFile.findFirstChunkWithName(Face.Name);
    chunkFile.readChunkBegin(chunkNum);

    faces = chunkFile.readArray(new Face.Converter());

    //Read in each face... they contain arrays themselves.
    for (int i = 0; i < faces.size(); i++)
    {
      Face face = faces.get(i);
      face.load(chunkFile);
    }

    //Load the data elements.
//    if (!CDataArray::Load(chunkFile))
//    {
//      return FALSE;
//    }

    chunkFile.readIndexEnd();
  }
}
