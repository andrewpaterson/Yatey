package net.tileeditor;

import java.awt.*;
import java.util.*;


public class LayerColour
{
  public static LayerColour instance = null;

  private ArrayList<Color> layerColours;

  public LayerColour()
  {
    layerColours = new ArrayList<Color>();
    layerColours.add(Color.lightGray);
    layerColours.add(Color.green);
    layerColours.add(Color.red);
    layerColours.add(Color.magenta);
    layerColours.add(Color.orange);
    layerColours.add(Color.pink);
  }

  public static LayerColour getInstance()
  {
    if (instance == null)
    {
      instance = new LayerColour();
    }
    return instance;
  }

  public Color getColour(int layer)
  {
    if (layer < layerColours.size())
    {
      return layerColours.get(layer);
    }
    return Color.BLACK;
  }
}
