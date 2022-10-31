package net.tileeditor;

import net.engine.cel.Cel;
import net.engine.cel.CelHelper;
import net.engine.cel.CelSource;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.engine.math.Float2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BrushSource
{
  protected BrushImportNumbers numbers;
  protected String fileName;

  public List<CelSource> celSources;

  public BrushSource()
  {
    numbers = new BrushImportNumbers();
    celSources = new ArrayList<CelSource>();
  }

  public BrushSource(int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, String fileName, boolean leftToRightFirst, boolean trim, Color transparent)
  {
    numbers = new BrushImportNumbers(celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trim, transparent);
    this.fileName = fileName;
    celSources = new ArrayList<CelSource>();
  }

  public BrushSource(BrushSource brushSource)
  {
    numbers = new BrushImportNumbers(brushSource.numbers);
    fileName = brushSource.fileName;

    celSources = new ArrayList<CelSource>();
    if (brushSource.celSources != null)
    {
      for (CelSource celSource : brushSource.celSources)
      {
        celSources.add(new CelSource(celSource));
      }
    }
  }

  public BrushSource(XMLTag tag)
  {
    XMLBody brushSourceBody = (XMLBody) tag.getData();

    XMLTag numbersTag = brushSourceBody.getTag("Numbers");
    numbers = new BrushImportNumbers(numbersTag);
    fileName = (String) brushSourceBody.getData("FileName");

    XMLBody celBody = (XMLBody) brushSourceBody.getData("Cels");
    if (celBody != null)
    {
      List<XMLTag> tags = celBody.getTags();
      celSources = new ArrayList<CelSource>();
      for (XMLTag celTag : tags)
      {
        CelSource celSource = loadCelSource(celTag);
        celSources.add(celSource);
      }
      new CelHelper(fileName, celSources, numbers.transparent);
    }
  }

  private CelSource loadCelSource(XMLTag celTag)
  {
    XMLBody celBody = (XMLBody) celTag.getData();
    CelSource celSource = new CelSource();

    loadFloat2("OffsetTopLeft", celBody, celSource.cel.offsetTopLeft);
    loadFloat2("OffsetBottomRight", celBody, celSource.cel.offsetBottomRight);
    celSource.cel.setHorizontalAlignmentFromString((String) celBody.getData("HorizontalAlignment"));
    celSource.cel.setVerticalAlignmentFromString((String) celBody.getData("VerticalAlignment"));
    celSource.flippedVertical = Boolean.parseBoolean((String) celBody.getData("FlippedVertical"));
    celSource.flippedHorizontal = Boolean.parseBoolean((String) celBody.getData("FlippedHorizontal"));
    loadRectangle("SourceRectangle", celBody, celSource.source);
    return celSource;
  }

  private void loadRectangle(String tagName, XMLBody body, Rectangle rectangle)
  {
    XMLBody rectangleBody = (XMLBody) body.getData(tagName);
    rectangle.x = Integer.parseInt((String) rectangleBody.getData("X"));
    rectangle.y = Integer.parseInt((String) rectangleBody.getData("Y"));
    rectangle.width = Integer.parseInt((String) rectangleBody.getData("Width"));
    rectangle.height = Integer.parseInt((String) rectangleBody.getData("Height"));
  }

  private void loadFloat2(String tagName, XMLBody celBody, Float2 float2)
  {
    XMLBody float2Body = (XMLBody) celBody.getData(tagName);
    float2.x = Float.parseFloat((String) float2Body.getData("X"));
    float2.y = Float.parseFloat((String) float2Body.getData("Y"));
  }

  public List<Point> convert()
  {
    List<Point> grid = new ArrayList<Point>();
    CelHelper celHelper = new CelHelper(fileName, numbers.celWidth, numbers.celHeight, numbers.columnCount,
                                        numbers.rowCount, numbers.leftOffset, numbers.topOffset, numbers.verticalSpacing,
                                        numbers.horizontalSpacing, numbers.leftToRightFirst, numbers.trim, numbers.transparent,
                                        grid);

    celSources = new ArrayList<CelSource>();
    for (CelSource cel : celHelper.getCels())
    {
      celSources.add(cel);
    }
    return grid;
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("BrushSource");
    XMLBody brushSourceBody = tag.setData();
    XMLTag numbersTag = brushSourceBody.put("Numbers");
    numbers.save(numbersTag);

    brushSourceBody.put("FileName", fileName);

    XMLTag celsTag = brushSourceBody.put("Cels");
    XMLBody celBody = celsTag.setData();
    if (celSources != null)
    {
      for (CelSource celSource : celSources)
      {
        saveCelSource(celSource, celBody);
      }
    }
  }

  public void saveCelSource(CelSource celSource, XMLBody body)
  {
    XMLTag tag = body.put("Cel");
    XMLBody celBody = tag.setData();

    saveFloat2("OffsetTopLeft", celBody, celSource.cel.offsetTopLeft);
    saveFloat2("OffsetBottomRight", celBody, celSource.cel.offsetBottomRight);
    celBody.put("HorizontalAlignment", celSource.cel.horizontalAlignmentAsString());
    celBody.put("VerticalAlignment", celSource.cel.verticalAlignmentAsString());
    celBody.put("FlippedVertical", Boolean.toString(celSource.flippedVertical));
    celBody.put("FlippedHorizontal", Boolean.toString(celSource.flippedHorizontal));
    saveRectangle("SourceRectangle", celBody, celSource.source);
  }

  private void saveFloat2(String tagName, XMLBody celBody, Float2 float2)
  {
    XMLTag float2Tag = celBody.put(tagName);
    XMLBody float2Body = float2Tag.setData();
    float2Body.put("X", Float.toString(float2.x));
    float2Body.put("Y", Float.toString(float2.y));
  }

  private void saveRectangle(String tagName, XMLBody celBody, Rectangle rectangle)
  {
    XMLTag rectangleTag = celBody.put(tagName);
    XMLBody rectangleBody = rectangleTag.setData();
    rectangleBody.put("X", Integer.toString(rectangle.x));
    rectangleBody.put("Y", Integer.toString(rectangle.y));
    rectangleBody.put("Width", Integer.toString(rectangle.width));
    rectangleBody.put("Height", Integer.toString(rectangle.height));
  }

  public String getFileName()
  {
    return fileName;
  }

  public Cel getCel(int index)
  {
    if (index < celSources.size())
    {
      return celSources.get(index).cel;
    }
    return null;
  }

  public int getCelIndex(Cel cel)
  {
    for (int i = 0; i < celSources.size(); i++)
    {
      CelSource celSource = celSources.get(i);
      if (celSource.cel == cel)
      {
        return i;
      }
    }
    return -1;
  }

  public BrushImportNumbers getNumbers()
  {
    return numbers;
  }
}
