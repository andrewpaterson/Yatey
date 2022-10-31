package net.tileeditor.mapeditor;

import net.tileeditor.*;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.general.BaseEditorPanel;
import net.tileeditor.general.GridExtents;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMap;
import net.tileeditor.layers.TileMapWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class MapViewPanel extends BaseEditorPanel implements ActionListener
{
  protected boolean painting;
  protected boolean selecting;
  protected Point startSelect;
  protected Point endSelect;
  protected boolean filling;
  protected Point startFill;
  protected Point endFill;
  protected MapPanel mapPanel;
  protected boolean brushSelected;
  protected boolean brushPainted;
  protected String warning;
  protected Timer timer;

  public MapViewPanel(MapPanel mapPanel)
  {
    super(false);
    this.mapPanel = mapPanel;
    this.warning = null;

    int delay = 2000;
    timer = new Timer(delay, this);
    timer.setRepeats(false);
  }

  protected void initialise()
  {
    resetCamera();
  }

  public void resizedBuffer()
  {
  }

  public void preRender()
  {
    super.preRender();
    if (Settings.getInstance().getGridStyle() == Settings.GRID_BENEATH)
    {
      renderGrid();
    }

    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      map.render(this);
    }
  }

  public void postRender()
  {
    renderMap();
    renderTheHold();
    renderWarning();
  }

  private void renderWarning()
  {
    if (warning != null)
    {
      renderCenteredText(Color.white, warning);
    }
  }

  private void renderMap()
  {
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();

    if (map != null)
    {
      if (Settings.getInstance().getGridStyle() == Settings.GRID_ONTOP)
      {
        renderGrid();
      }
      if (selecting)
      {
        renderSelection(Color.yellow, startSelect, endSelect);
      }
      if (filling)
      {
        renderSelection(Color.cyan, startFill, endFill);
      }

      if ((!selecting) && (!filling))
      {
        CompoundBrush currentBrush = source.getCurrentBrush();
        int width = 1;
        int height = 1;
        if (currentBrush != null)
        {
          width = currentBrush.getWidth();
          height = currentBrush.getHeight();
        }

        float x = mouseX - ((width - 1) * map.celWidth) / 2.0f;
        float y = mouseY - ((height - 1) * map.celHeight) / 2.0f;

        Point.Float startIndex = getCelIndexUnclipped((int) x, (int) y);
        Point.Float endIndex = new Point.Float(startIndex.x + width - 1, startIndex.y + height - 1);

        if (startIndex.x < 0)
        {
          startIndex.x = 0;
        }
        if (startIndex.y < 0)
        {
          startIndex.y = 0;
        }

        if (endIndex.x < 0)
        {
          return;
        }
        if (endIndex.y < 0)
        {
          return;
        }

        if (endIndex.x > map.getWidth() - 1)
        {
          endIndex.x = map.getWidth() - 1;
        }
        if (endIndex.y > map.getHeight() - 1)
        {
          endIndex.y = map.getHeight() - 1;
        }

        if (startIndex.x >= map.getWidth())
        {
          return;
        }
        if (startIndex.y >= map.getHeight())
        {
          return;
        }

        renderColouredRectangle(Color.yellow, new Point((int) startIndex.x, (int) startIndex.y), new Point((int) endIndex.x, (int) endIndex.y));
      }
    }
    else
    {
      renderCenteredText(Color.white, "No Current Map");
    }
  }

  private void renderCenteredText(Color colour, String data)
  {
    backBuffer.setColor(colour);
    Font font = backBuffer.getFont();
    font = new Font(font.getName(), Font.BOLD, 20);
    backBuffer.setFont(font);
    FontRenderContext context = backBuffer.getFontRenderContext();

    Rectangle2D bounds = font.getStringBounds(data, context);
    Dimension dimension = getSize();
    backBuffer.drawString(data, (int) ((dimension.getWidth() - bounds.getWidth()) / 2), (int) ((dimension.getHeight() + bounds.getHeight()) / 2));
  }

  private void renderTheHold()
  {
    if (Settings.getInstance().isHoldVisible())
    {
      int height = getHeight();
      TheHold theHold = TheHold.getInstance();
      java.util.List<Held> helds = theHold.getStack();
      for (int i = 0; i < helds.size(); i++)
      {
        Held held = helds.get(i);
        backBuffer.setColor(held.getColourForChange());
        backBuffer.fillRect(5 + i * 10, height - 15, 9, 9);
        if (theHold.getUndoPos() == i)
        {
          backBuffer.setColor(Color.white);
          backBuffer.drawRect(4 + i * 10, height - 16, 10, 10);
        }
      }
    }
  }

  private void renderSelection(Color color, Point start, Point end)
  {
    Point startIndex = getCelIndex(start.x, start.y);
    Point endIndex = getCelIndex(end.x, end.y);
    renderColouredRectangle(color, startIndex, endIndex);
  }

  private void renderColouredRectangle(Color color, Point startIndex, Point endIndex)
  {
    backBuffer.setColor(color);
    if ((startIndex != null) && (endIndex != null))
    {
      GridExtents gridExtents = new GridExtents(startIndex, endIndex);
      int left = getCelLeft(gridExtents.left);
      int right = getCelRight(gridExtents.right);
      int top = getCelTop(gridExtents.top);
      int bottom = getCelBottom(gridExtents.bottom);
      backBuffer.drawRect(left, top, right - left - 1, bottom - top - 1);
      backBuffer.drawRect(left - 1, top - 1, right - left + 1, bottom - top + 1);
    }
  }

  private int getCelLeft(int x)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return ((map.celWidth / 2) - map.celWidth) + (map.celWidth * (x)) - (int) camera.getPosition().x;
  }

  private int getCelTop(int y)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return ((map.celHeight / 2) - map.celHeight) + (map.celHeight * (y)) - (int) camera.getPosition().y;
  }

  private int getCelRight(int x)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return ((map.celWidth / 2)) + (map.celWidth * (x)) - (int) camera.getPosition().x;
  }

  private int getCelBottom(int y)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    return ((map.celHeight / 2)) + (map.celHeight * (y)) - (int) camera.getPosition().y;
  }

  private Point getCelIndex(int x, int y)
  {
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();
    if (map != null)
    {
      float column = (x + map.celWidth / 2 + (camera.getPosition().x)) / map.celWidth;
      float row = (y + (map.celHeight / 2) + camera.getPosition().y) / map.celHeight;

      if ((row < 0) || (row >= map.getHeight()))
      {
        return null;
      }

      if ((column < 0) || (column >= map.getWidth()))
      {
        return null;
      }

      return new Point((int) column, (int) row);
    }
    return null;
  }

  private Point.Float getCelIndexUnclipped(int x, int y)
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    float column = (x + map.celWidth / 2 + (camera.getPosition().x)) / map.celWidth;
    float row = (y + (map.celHeight / 2) + camera.getPosition().y) / map.celHeight;
    return new Point.Float(column, row);
  }

  private void renderGrid()
  {
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();
    if (map != null)
    {
      Graphics2D buffer = getBackBuffer();
      buffer.setColor(BORDER_COLOR);

      int top = getCelTop(0);
      int left = getCelLeft(0);
      buffer.drawRect(left - 1, top - 1, map.celWidth * map.getWidth() + 1, map.celHeight * map.getHeight() + 1);

      TileArray layer = map.getCurrentLayer();
      if (layer != null)
      {
        for (int y = 0; y <= map.getHeight(); y++)
        {
          top = getCelTop(y);
          for (int x = 0; x <= map.getWidth(); x++)
          {
            Object o = layer.getObject(x, y);
            if (o != null)
            {
              left = getCelLeft(x);
              buffer.drawRect(left, top, map.celWidth - 1, map.celHeight - 1);
            }
          }
        }
      }
    }
  }

  public void mouseMoved(MouseEvent e)
  {
    super.mouseMoved(e);
  }

  public void mouseClicked(MouseEvent e)
  {
  }

  public void mouseDragged(MouseEvent e)
  {
    super.mouseDragged(e);
    if (selecting)
    {
      endSelect = e.getPoint();
      brushSelected |= selectBrush();
    }
    if (filling)
    {
      endFill = e.getPoint();
    }
    if (painting)
    {
      Point point = e.getPoint();
      brushPainted |= paintBrush(point);
    }
  }

  public void mousePressed(MouseEvent e)
  {
    requestFocus();

    int modifiers = e.getModifiersEx();
    if (((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) && ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0))
    {
      startSelect = e.getPoint();
      endSelect = startSelect;
      selecting = true;
      brushSelected = selectBrush();
    }
    else if (((modifiers & InputEvent.ALT_DOWN_MASK) != 0) && ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0))
    {
      startFill = e.getPoint();
      endFill = startFill;
      filling = true;
    }
    else if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0)
    {
      Point point = e.getPoint();
      brushPainted = paintBrush(point);
      painting = true;
    }
  }

  private boolean paintBrush(Point point)
  {
    Source source = Source.getInstance();
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      TileArray layer = map.getCurrentLayer();
      if (layer == null)
      {
        return false;
      }

      if (layer.isVisible())
      {
        CompoundBrush currentBrush = source.getCurrentBrush();

        int width = 1;
        int height = 1;
        TileArray tileArray = null;
        if (currentBrush != null)
        {
          width = currentBrush.getWidth();
          height = currentBrush.getHeight();
          tileArray = currentBrush.getTileMap().getCurrentLayer();
        }

        Point.Float startIndex = getCelIndexUnclipped((int) (point.x - ((width - 1) * map.celWidth) / 2.0f), (int) (point.y - ((height - 1) * map.celHeight) / 2.0f));
        Point.Float endIndex = new Point.Float(startIndex.x + width - 1, startIndex.y + height - 1);

        if (endIndex.x < 0)
        {
          return false;
        }
        if (endIndex.y < 0)
        {
          return false;
        }
        if (startIndex.x > map.getWidth())
        {
          return false;
        }
        if (startIndex.y > map.getHeight())
        {
          return false;
        }

        for (int y = 0; y < height; y++)
        {
          for (int x = 0; x < width; x++)
          {
            int indexX = (int) (startIndex.x + x);
            int indexY = (int) (startIndex.y + y);

            paintCel(map.tileMap, layer, tileArray, x, y, indexX, indexY);
          }
        }
        return true;
      }
      else
      {
        setWarning("Can't paint hidden layer");
      }
    }
    return false;
  }

  private boolean selectBrush()
  {
    GridExtents gridExtents = new GridExtents(startSelect, endSelect);
    Point startIndex = getCelIndex(gridExtents.left, gridExtents.top);
    Point endIndex = getCelIndex(gridExtents.right, gridExtents.bottom);

    if ((startIndex == null) || (endIndex == null))
    {
      return false;
    }

    int width = endIndex.x - startIndex.x + 1;
    int height = endIndex.y - startIndex.y + 1;
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();
    if (map != null)
    {
      TileArray layer = map.getCurrentLayer();
      if (layer == null)
      {
        return false;
      }

      if (layer.isVisible())
      {
        if ((width == 1) && (height == 1) && (layer.getObject(startIndex.x, startIndex.y) == null))
        {
          source.setNullBrush();
        }
        else
        {
          Class tileClass = map.getCurrentLayer().getTileClass();

          CompoundBrush compoundBrush = new CompoundBrush(width, height, tileClass, source);
          TileArray tileArray = compoundBrush.getTileMap().getCurrentLayer();

          for (int y = 0; y < height; y++)
          {
            for (int x = 0; x < width; x++)
            {
              SimpleObject object = layer.getObject(startIndex.x + x, startIndex.y + y);
              tileArray.setObject(x, y, object);
            }
          }
          source.setCurrentBrush(compoundBrush, tileClass);
        }
        mapPanel.brushSelected();
        return true;
      }
    }
    return false;
  }

  private boolean paintFill()
  {
    Source source = Source.getInstance();
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      TileArray layer = map.getCurrentLayer();
      if (layer == null)
      {
        return false;
      }

      if (layer.isVisible())
      {
        CompoundBrush currentBrush = source.getCurrentBrush();

        int width = 1;
        int height = 1;
        TileArray brush = null;
        if (currentBrush != null)
        {
          width = currentBrush.getWidth();
          height = currentBrush.getHeight();
          brush = currentBrush.getTileMap().getCurrentLayer();
        }

        Point startIndex = getCelIndex(startFill.x, startFill.y);
        Point endIndex = getCelIndex(endFill.x, endFill.y);

        if ((startIndex == null) || (endIndex == null))
        {
          return false;
        }

        GridExtents gridExtents = new GridExtents(startIndex, endIndex);

        int brushIndexY = 0;
        for (int y = gridExtents.top; y <= gridExtents.bottom; y++)
        {
          int brushIndexX = 0;
          for (int x = gridExtents.left; x <= gridExtents.right; x++)
          {
            paintCel(map.tileMap, layer, brush, brushIndexX, brushIndexY, x, y);

            brushIndexX++;
            if (brushIndexX >= width)
            {
              brushIndexX = 0;
            }
          }
          brushIndexY++;
          if (brushIndexY >= height)
          {
            brushIndexY = 0;
          }
        }
        return true;
      }
    }
    return false;
  }

  private void paintCel(TileMap map, TileArray layer, TileArray brush, int sourceIndexX, int sourceIndexY, int destIndexX, int destIndexY)
  {
    if (brush != null)
    {
      SimpleObject sourceObject = brush.getObject(sourceIndexX, sourceIndexY);
      if (sourceObject != null)
      {
        SimpleObject destObject = layer.getObject(destIndexX, destIndexY);
        if (destObject == null)
        {
          map.clear(destIndexX, destIndexY);
        }
        layer.setObject(destIndexX, destIndexY, sourceObject);
      }
    }
    else
    {
      map.nullify(destIndexX, destIndexY);
    }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (filling)
    {
      if (paintFill())
      {
        TheHold.getInstance().hold(Held.MAP_PAINTED);
      }
    }
    if (painting)
    {
      if (brushPainted)
      {
        TheHold.getInstance().hold(Held.MAP_PAINTED);
      }
    }
    if (selecting)
    {
      if (brushSelected)
      {
        TheHold.getInstance().hold(Held.BRUSH_SELECTED);
      }
    }
    selecting = false;
    painting = false;
    filling = false;
  }

  public void setWarning(String warning)
  {
    this.warning = warning;
    timer.stop();
    timer.start();
  }

  public void resetCamera()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    camera.setPosition(-map.celWidth, -map.celHeight);
  }

  public void actionPerformed(ActionEvent e)
  {
    warning = null;
  }
}
