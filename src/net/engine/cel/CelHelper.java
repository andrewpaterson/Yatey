package net.engine.cel;

import net.engine.IntegerRange;
import net.engine.math.Float2;
import net.engine.picture.Picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CelHelper
{
  private static GraphicsConfiguration graphicsConfiguration = null;
  private List<CelSource> cels;

  private void createGraphicsConfiguration()
  {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (graphicsConfiguration == null)
    {
      graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
    }
  }

  public CelHelper()
  {
    createGraphicsConfiguration();
    cels = new ArrayList<CelSource>();
  }

  public CelHelper(String fileName)
  {
    this(new File(fileName));
  }

  public CelHelper(String fileName, List<CelSource> tempCelSources, Color transparentColor)
  {
    this();
    BufferedImage image = loadBufferedImage(new File(fileName));
    for (CelSource source : tempCelSources)
    {
      CelSource dest = addCelSource(image, source.source.x, source.source.y, source.source.width, source.source.height, false, transparentColor, (int)source.cel.offsetTopLeft.x, (int)source.cel.offsetTopLeft.y, (int)source.cel.offsetBottomRight.x, (int)source.cel.offsetBottomRight.y);
      dest.cel.offsetTopLeft = new Float2(source.cel.offsetTopLeft);
      dest.cel.offsetBottomRight = new Float2(source.cel.offsetBottomRight);
      dest.cel.horizontalAlignment = source.cel.horizontalAlignment;
      dest.cel.verticalAlignment = source.cel.verticalAlignment;

      source.cel.bufferedImage = dest.cel.bufferedImage;
      source.cel.celHelper = this;
    }
  }

  public CelHelper(File file)
  {
    this();
    BufferedImage image = loadBufferedImage(file);
    addCelSource(image, true);
  }

  public CelHelper(String fileName, boolean trim)
  {
    this();
    BufferedImage image = loadBufferedImage(new File(fileName));
    addCelSource(image, trim);
  }

  public CelHelper(String fileName, int cellCountX, int cellCountY, boolean leftToRightFirst)
  {
    this();
    BufferedImage image = loadBufferedImage(new File(fileName));
    breakIntoFrames(image, cellCountX, cellCountY, leftToRightFirst);
  }

  public CelHelper(Image image, int cellCountX, int cellCountY, boolean leftToRightFirst)
  {
    this();
    breakIntoFrames(image, cellCountX, cellCountY, leftToRightFirst);
  }

  public CelHelper(Picture picture, int cellCountX, int cellCountY, boolean leftToRightFirst)
  {
    this();
    BufferedImage image = convertFromPicture(picture);
    breakIntoFrames(image, cellCountX, cellCountY, leftToRightFirst);
  }

  public CelHelper(Picture picture)
  {
    this();
    BufferedImage image = convertFromPicture(picture);
    addCelSource(image, true);
  }

  public CelHelper(String fileName, int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color replaceColour)
  {
    this();
    BufferedImage image = loadBufferedImage(new File(fileName));
    breakIntoFrames(image, celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trim, replaceColour);
  }

  public CelHelper(String fileName, int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color replaceColour, List<Point> grid)
  {
    this();
    BufferedImage image = loadBufferedImage(new File(fileName));
    List<Point> list = breakIntoFrames(image, celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trim, replaceColour);
    grid.addAll(list);
  }

  public List<CelSource> getCels()
  {
    return cels;
  }

  private BufferedImage convertFromPicture(Picture picture)
  {
    BufferedImage image = new BufferedImage(picture.width, picture.height, BufferedImage.TYPE_INT_ARGB);

    int colour[] = new int[4];
    WritableRaster raster = image.getWritableTile(0, 0);
    for (int y = 0; y < picture.height; y++)
    {
      for (int x = 0; x < picture.width; x++)
      {
        int index = picture.getPixel(x, y);
        Color color = picture.getColour(index);

        if (color != null)
        {
          colour[3] = color.getAlpha();
          colour[0] = color.getRed();
          colour[1] = color.getGreen();
          colour[2] = color.getBlue();
          raster.setPixel(x, y, colour);
        }
      }
    }
    return image;
  }

  private BufferedImage loadBufferedImage(File file)
  {
    try
    {
      BufferedImage source = ImageIO.read(file);
      if (source != null)
      {
        return source;
      }
      throw new RuntimeException("Couldn't load image [" + file.getName() + "]");
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public List<Point> loadBufferedImage(String fileName, int celWidth, int celHeight, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color replaceColour)
  {
    try
    {
      BufferedImage source = ImageIO.read(new File(fileName));
      return breakIntoFrames(source, celWidth, celHeight, columnCount, rowCount, leftOffset, topOffset, verticalSpacing, horizontalSpacing, leftToRightFirst, trim, replaceColour);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private List<Point> breakIntoFrames(Image source, int width, int height, int columnCount, int rowCount, int leftOffset, int topOffset, int verticalSpacing, int horizontalSpacing, boolean leftToRightFirst, boolean trim, Color replaceColour)
  {
    if ((columnCount == -1) && (width == -1))
    {
      width = source.getWidth(null);
      columnCount = 1;
    }
    else
    {
      if (columnCount == -1)
      {
        int right = source.getWidth(null) - leftOffset;
        int totalWidth = width + verticalSpacing;
        columnCount = right / totalWidth;

        int remainder = right % totalWidth;
        if (remainder >= width)
        {
          columnCount++;
        }
        
      }
      if (width == -1)
      {
        width = (source.getWidth(null)) / columnCount;
      }
    }
    if ((rowCount == -1) && (height == -1))
    {
      height = source.getHeight(null);
      rowCount = 1;
    }
    else
    {
      if (rowCount == -1)
      {
        int bottom = source.getHeight(null) - topOffset;
        int totalHeight = height + horizontalSpacing;
        rowCount = bottom / totalHeight;
        int remainder = bottom % totalHeight;
        if (remainder >= height)
        {
          rowCount++;
        }

      }
      if (height == -1)
      {
        height = (source.getHeight(null)) / rowCount;
      }
    }

    List<Point> grid = new ArrayList<Point>();
    if (leftToRightFirst)
    {
      for (int y = 0; y < rowCount; y++)
      {
        int sy = topOffset + y * (height + horizontalSpacing);
        for (int x = 0; x < columnCount; x++)
        {
          int sx = leftOffset + x * (width + verticalSpacing);
          CelSource celSource = addCelSource(source, sx, sy, width, height, trim, replaceColour);
          if (celSource != null)
          {
            grid.add(new Point(x, y));
          }
        }
      }
    }
    else
    {
      for (int x = 0; x < columnCount; x++)
      {
        int sx = leftOffset + x * (width + verticalSpacing);
        for (int y = 0; y < rowCount; y++)
        {
          int sy = topOffset + y * (height + horizontalSpacing);
          CelSource celSource = addCelSource(source, sx, sy, width, height, trim, replaceColour);
          if (celSource != null)
          {
            grid.add(new Point(x, y));
          }
        }
      }
    }
    return grid;
  }

  public CelSource addCelSource(Image source, int sx, int sy, int width, int height, boolean trim, Color replaceColour)
  {
    return addCelSource(source, sx, sy, width, height, trim, replaceColour, 0, 0, 0, 0);
  }

  public CelSource addCelSource(Image source, int sx, int sy, int width, int height, boolean trim, Color replaceColour, int leftOffset, int topOffset, int rightOffset, int bottomOffset)
  {
    Cel cel = newCel(convertToBufferedImage(source, 0, 0, sx + leftOffset, sy + topOffset, width - (leftOffset + rightOffset), height - (topOffset + bottomOffset), Transparency.TRANSLUCENT), trim, replaceColour);
    if (cel != null)
    {
      cel.setOffset(leftOffset, topOffset, rightOffset, bottomOffset);
      Rectangle rectangle = new Rectangle(sx, sy, width, height);
      CelSource celSource = new CelSource(cel, rectangle, false, false);
      cels.add(celSource);
      return celSource;
    }
    return null;
  }

  private void breakIntoFrames(Image source, int cellCountX, int cellCountY, boolean leftToRightFirst)
  {
    int width = source.getWidth(null) / cellCountX;
    int height = source.getHeight(null) / cellCountY;

    breakIntoFrames(source, width, height, cellCountX, cellCountY, 0, 0, 0, 0, leftToRightFirst, true, null);
  }

  private void addCelSource(Image image, boolean trim)
  {
    int height = image.getHeight(null);
    int width = image.getWidth(null);
    addCelSource(image, 0, 0, width, height, trim, null);
  }

  private BufferedImage convertToBufferedImage(Image image, int dx, int dy, int sx, int sy, int width, int height, int transparency)
  {
    BufferedImage copy = graphicsConfiguration.createCompatibleImage(width, height, transparency);
    Graphics2D g2d = copy.createGraphics();
    g2d.drawImage(image, dx, dy, dx + width, dy + height, sx, sy, sx + width, sy + height, null);
    g2d.dispose();
    return copy;
  }

  public Cel get(int frame)
  {
    if ((frame < 0) || (frame > cels.size()))
    {
      return null;
    }
    CelSource celSource = cels.get(frame);
    if (celSource != null)
    {
      return celSource.cel;
    }
    return null;
  }

  public int numFrames()
  {
    return cels.size();
  }

  public int addFlippedFrames(boolean horizontal, boolean vertical, Object... frames)
  {
    if (frames.length == 0)
    {
      return -1;
    }

    int first = cels.size();
    for (Object object : frames)
    {
      if (object instanceof Integer)
      {
        addFlippedFrame((Integer) object, horizontal, vertical);
      }
      else if (object instanceof IntegerRange)
      {
        IntegerRange integerRange = (IntegerRange) object;
        for (int frame = integerRange.getMin(); frame <= integerRange.getMax(); frame++)
        {
          addFlippedFrame(frame, horizontal, vertical);
        }
      }
    }
    return first;
  }

  public int addFlippedFrame(int sourceFrame, boolean horizontal, boolean vertical)
  {
    CelSource celSource = cels.get(sourceFrame);
    if (celSource == null)
    {
      cels.add(null);
      return cels.size() - 1;
    }
    Cel cel = celSource.cel;

    int imageWidth = cel.bufferedImage.getWidth();
    int imageHeight = cel.bufferedImage.getHeight();

    BufferedImage newFrame = graphicsConfiguration.createCompatibleImage(imageWidth, imageHeight, cel.bufferedImage.getType());
    Graphics2D g2d = newFrame.createGraphics();

    int dx1, dy1, dx2, dy2;
    float top, left, bottom, right;
    if (horizontal)
    {
      dx1 = imageWidth;
      dx2 = 0;
      left = cel.offsetBottomRight.x;
      right = cel.offsetTopLeft.x;
    }
    else
    {
      dx1 = 0;
      dx2 = imageWidth;
      left = cel.offsetTopLeft.x;
      right = cel.offsetBottomRight.x;
    }

    if (vertical)
    {
      dy1 = imageHeight;
      dy2 = 0;
      top = cel.offsetBottomRight.y;
      bottom = cel.offsetTopLeft.y;
    }
    else
    {
      dy1 = 0;
      dy2 = imageHeight;
      top = cel.offsetTopLeft.y;
      bottom = cel.offsetBottomRight.y;
    }

    g2d.drawImage(cel.bufferedImage, dx1, dy1, dx2, dy2, 0, 0, imageWidth, imageHeight, null);

    Cel newCel = new Cel(this, newFrame, cel.horizontalAlignment, cel.verticalAlignment, new Float2(left, top), new Float2(right, bottom));
    Rectangle rectangle = new Rectangle(celSource.source);
    cels.add(new CelSource(newCel, rectangle, horizontal, vertical));
    return cels.size() - 1;
  }

  public void offsetFrames(float left, float top, float right, float bottom)
  {
    //I don't think this takes celSource into account with rectangle.
    for (CelSource celSource : cels)
    {
      if (celSource != null)
      {
        celSource.cel.offset(left, top, right, bottom);
      }
    }
  }

  public Cel newCel(BufferedImage bufferedImage, boolean trim, Color replaceColour)
  {
    return newCel(bufferedImage, Cel.CENTERED, Cel.CENTERED, trim, replaceColour);
  }

  public Cel newCel(BufferedImage bufferedImage, int horizontalAlignment, int verticalAlignment, boolean trim, Color replaceColour)
  {
    if (replaceColour != null)
    {
      replaceColour(bufferedImage, new Color(0, 0, 0, 0), replaceColour);
    }

    if (trim)
    {
      CelHelper.TrimReturn trimReturn = trim(bufferedImage);
      if (trimReturn != null)
      {
        return new Cel(this, trimReturn.bufferedImage, horizontalAlignment, verticalAlignment, trimReturn.offsetTopLeft, trimReturn.offsetBottomRight);
      }
      else
      {
        return null;
      }
    }
    else
    {
      return new Cel(this, bufferedImage, horizontalAlignment, verticalAlignment, new Float2(0, 0), new Float2(0, 0));
    }
  }

  class TrimReturn
  {
    public BufferedImage bufferedImage;
    public Float2 offsetTopLeft;
    public Float2 offsetBottomRight;

    public TrimReturn(BufferedImage bufferedImage, Float2 offsetTopLeft, Float2 offsetBottomRight)
    {
      this.bufferedImage = bufferedImage;
      this.offsetTopLeft = offsetTopLeft;
      this.offsetBottomRight = offsetBottomRight;
    }
  }

  private void replaceColour(BufferedImage bufferedImage, Color dest, Color source)
  {
    if ((dest == null) || (source == null))
    {
      return;
    }
    WritableRaster raster = bufferedImage.getRaster();

    int colour[] = new int[4];
    for (int y = 0; y < raster.getHeight(); y++)
    {
      for (int x = 0; x < raster.getWidth(); x++)
      {
        raster.getPixel(x, y, colour);
        if ((colour[3] == source.getAlpha()) && (colour[0] == source.getRed()) && (colour[1] == source.getGreen()) && (colour[2] == source.getBlue()))
        {
          colour[3] = dest.getAlpha();
          colour[0] = dest.getRed();
          colour[1] = dest.getGreen();
          colour[2] = dest.getBlue();
          raster.setPixel(x, y, colour);
        }
      }
    }
  }

  private TrimReturn trim(BufferedImage bufferedImage)
  {
    WritableRaster raster = bufferedImage.getRaster();

    int top;
    for (top = 0; top < raster.getHeight(); top++)
    {
      if (!isRowTransparent(raster, 0, raster.getWidth(), top))
      {
        break;
      }
    }

    if (top == raster.getHeight())
    {
      return null;
    }

    int bottom;
    for (bottom = raster.getHeight() - 1; bottom >= 0; bottom--)
    {
      if (!isRowTransparent(raster, 0, raster.getWidth(), bottom))
      {
        break;
      }
    }

    int left;
    for (left = 0; left < raster.getWidth(); left++)
    {
      if (!isColumnTransparent(raster, left, top, bottom + 1))
      {
        break;
      }
    }

    int right;
    for (right = raster.getWidth() - 1; right >= 0; right--)
    {
      if (!isColumnTransparent(raster, right, top, bottom + 1))
      {
        break;
      }
    }

    int width = right - left + 1;
    int height = bottom - top + 1;

    right = raster.getWidth() - (right + 1);
    bottom = raster.getHeight() - (bottom + 1);

    if ((top == 0) && (bottom == 0) && (left == 0) && (right == 0))
    {
      return new TrimReturn(bufferedImage, new Float2(0, 0), new Float2(0, 0));
    }
    else
    {
      bufferedImage = convertToBufferedImage(bufferedImage, 0, 0, left, top, width, height, Transparency.TRANSLUCENT);
      return new TrimReturn(bufferedImage, new Float2(left, top), new Float2(right, bottom));
    }
  }

  private boolean isRowTransparent(WritableRaster raster, int x1, int x2, int y)
  {
    for (int x = x1; x < x2; x++)
    {
      if (!isTransparent(raster, x, y))
      {
        return false;
      }
    }
    return true;
  }

  private boolean isColumnTransparent(WritableRaster raster, int x, int y1, int y2)
  {
    for (int y = y1; y < y2; y++)
    {
      if (!isTransparent(raster, x, y))
      {
        return false;
      }
    }
    return true;
  }

  private boolean isTransparent(WritableRaster raster, int x, int y)
  {
    int colour[] = new int[4];
    raster.getPixel(x, y, colour);
    return colour[3] == 0;
  }
}
