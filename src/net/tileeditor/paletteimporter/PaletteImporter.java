package net.tileeditor.paletteimporter;

import net.engine.cel.Cel;
import net.engine.cel.CelHelper;
import net.tileeditor.SimpleObject;
import net.tileeditor.Source;
import net.tileeditor.TheHold;
import net.tileeditor.Held;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.LinkedHashMap;

public class PaletteImporter
{
  public PaletteImporter()
  {
  }

  public void importFile(File file)
  {
    CelHelper celHelper = new CelHelper(file);
    Cel cel = celHelper.get(0);
    BufferedImage image = cel.bufferedImage;
    WritableRaster raster = image.getRaster();
    int colour[] = new int[4];
    LinkedHashMap<Color, Boolean> hashMap = new LinkedHashMap<Color, Boolean>(5000);
    int uniqueColours = 0;

    for (int y = 0; y < image.getHeight(); y++)
    {
      for (int x = 0; x < image.getWidth(); x++)
      {
        raster.getPixel(x, y, colour);
        if (!((colour[0] == 0) && (colour[1] == 0) && (colour[2] == 0) && (colour[3] == 0)))
        {
          Color color = new Color(colour[0], colour[1], colour[2]);
          Boolean bool = hashMap.put(color, true);
          if (bool == null)
          {
            uniqueColours++;
          }
        }
      }
    }

    Source source = Source.getInstance();
    for (Color color : hashMap.keySet())
    {
      SimpleObject simpleObject = new SimpleObject(color);
      simpleObject.setName(name(color));
      source.addSimpleObject(simpleObject);
    }

    TheHold.getInstance().hold(Held.BRUSH_IMPORTED);
  }

  private String name(Color color)
  {
    StringBuffer stringBuffer = new StringBuffer("Colour ");
    pad(stringBuffer, Integer.toString(color.getRed(), 16));
    pad(stringBuffer, Integer.toString(color.getGreen(), 16));
    pad(stringBuffer, Integer.toString(color.getBlue(), 16));
    return stringBuffer.toString();
  }

  private void pad(StringBuffer stringBuffer, String digits)
  {
    if (digits.length() == 1)
    {
      stringBuffer.append('0');
    }
    stringBuffer.append(digits);
  }
}
