package net.tileeditor;

import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.engine.picture.ColorUtil;

import java.awt.*;

public class BrushImportNumbers
{
  public int celWidth;
  public int celHeight;
  public int columnCount;
  public int rowCount;
  public int leftOffset;
  public int topOffset;
  public int verticalSpacing;
  public int horizontalSpacing;
  public Color transparent;
  public boolean leftToRightFirst;
  public boolean trim;

  public BrushImportNumbers()
  {
    celWidth = -1;
    celHeight = -1;
    columnCount = 1;
    rowCount = 1;
    leftOffset = 0;
    topOffset = 0;
    verticalSpacing = 0;
    horizontalSpacing = 0;
    transparent = null;
    leftToRightFirst = true;
    trim = false;

  }

  public BrushImportNumbers(int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color transparent)
  {
    set(celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trim, transparent);
  }

  private void set(int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color transparent)
  {
    this.celWidth = celWidth;
    this.celHeight = celHeight;
    this.columnCount = columnCount;
    this.rowCount = rowCount;
    this.leftOffset = leftOffset;
    this.topOffset = topOffset;
    this.verticalSpacing = verticalSpacing;
    this.horizontalSpacing = horizontalSpacing;
    this.leftToRightFirst = leftToRightFirst;
    this.trim = trim;
    this.transparent = transparent;
  }

  public BrushImportNumbers(BrushImportNumbers source)
  {
    copy(source);
  }

  public BrushImportNumbers(XMLTag tag)
  {
    if (tag != null)
    {
      XMLBody numbersBody = (XMLBody) tag.getData();
      celWidth = Integer.parseInt((String) numbersBody.getData("CelWidth"));
      celHeight = Integer.parseInt((String) numbersBody.getData("CelHeight"));

      columnCount = Integer.parseInt((String) numbersBody.getData("ColumnCount"));
      rowCount = Integer.parseInt((String) numbersBody.getData("RowCount"));
      leftOffset = Integer.parseInt((String) numbersBody.getData("LeftOffset"));
      topOffset = Integer.parseInt((String) numbersBody.getData("TopOffset"));
      verticalSpacing = Integer.parseInt((String) numbersBody.getData("VerticalSpacing"));
      horizontalSpacing = Integer.parseInt((String) numbersBody.getData("HorizontalSpacing"));
      XMLBody transparentBody = (XMLBody) numbersBody.getData("Transparent");
      transparent = null;
      if (transparentBody != null)
      {
        transparent = ColorUtil.fromXML(transparentBody);
      }

      String leftToRightFirstString = (String) numbersBody.getData("LeftToRightFirst");
      leftToRightFirst = true;
      if (leftToRightFirstString != null)
      {
        leftToRightFirst = Boolean.parseBoolean(leftToRightFirstString);
      }

      String trimString = (String) numbersBody.getData("Trim");
      trim = false;
      if (trimString != null)
      {
        trim = Boolean.parseBoolean(trimString);
      }

    }
  }

  public void save(XMLTag tag)
  {
    XMLBody numbersBody = tag.setData();
    numbersBody.put("CelWidth", Integer.toString(celWidth));
    numbersBody.put("CelHeight", Integer.toString(celHeight));
    numbersBody.put("ColumnCount", Integer.toString(columnCount));
    numbersBody.put("RowCount", Integer.toString(rowCount));
    numbersBody.put("LeftOffset", Integer.toString(leftOffset));
    numbersBody.put("TopOffset", Integer.toString(topOffset));
    numbersBody.put("VerticalSpacing", Integer.toString(verticalSpacing));
    numbersBody.put("HorizontalSpacing", Integer.toString(horizontalSpacing));
    XMLBody transparentBody = numbersBody.put("Transparent").setData();
    if (transparent != null)
    {
      ColorUtil.toXML(transparentBody, transparent);
    }
    numbersBody.put("LeftToRightFirst", Boolean.toString(leftToRightFirst));
    numbersBody.put("Trim", Boolean.toString(trim));
  }

  public boolean update(String celWidthText, String celHeightText, String columnCountText, String rowCountText,
                        String leftOffsetText, String topOffsetText, String verticalSpacingText, String horizontalSpacingText,
                        boolean leftToRightFirst, boolean trimEmptyEdges, boolean useEmptyColor, Color emptyColor)
  {
    try
    {
      int celWidth = Integer.parseInt(celWidthText);
      int celHeight = Integer.parseInt(celHeightText);
      int columnCount = Integer.parseInt(columnCountText);
      int rowCount = Integer.parseInt(rowCountText);
      int leftOffset = Integer.parseInt(leftOffsetText);
      int topOffset = Integer.parseInt(topOffsetText);
      int verticalSpacing = Integer.parseInt(verticalSpacingText);
      int horizontalSpacing = Integer.parseInt(horizontalSpacingText);
      if (!useEmptyColor)
      {
        emptyColor = null;
      }
      set(celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trimEmptyEdges, emptyColor);
    }
    catch (NumberFormatException e)
    {
      return false;
    }
    return true;
  }

  public void copy(BrushImportNumbers source)
  {
    celWidth = source.celWidth;
    celHeight = source.celHeight;
    columnCount = source.columnCount;
    rowCount = source.rowCount;
    leftOffset = source.leftOffset;
    topOffset = source.topOffset;
    verticalSpacing = source.verticalSpacing;
    horizontalSpacing = source.horizontalSpacing;
    transparent = ColorUtil.copy(source.transparent);
    leftToRightFirst = source.leftToRightFirst;
    trim = source.trim;
  }

  public void setTransparent(Color transparent)
  {
    this.transparent = transparent;
  }
}
