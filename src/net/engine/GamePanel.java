package net.engine;

import net.engine.graphics.Sprite;
import net.engine.shape.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static net.engine.GameRunnable.NANOS_IN_MILLI;

public abstract class GamePanel
    extends JPanel
    implements ComponentListener
{
  public int screenWidth;
  private GameRunnable runnable;
  public int screenHeight;

  private volatile boolean running = false;

  protected Image backBufferImage;
  protected Graphics2D backBuffer;
  protected Graphics2D abusedBuffer;

  private boolean[] keyDown;
  public boolean[] mouseDown;
  public Point mousePosition;
  public int mouseWheel;

  protected ArrayList<Sprite> activeObjects;
  protected Camera camera;
  protected Statistics statistics;

  protected Random random;
  private boolean showStatistics;

  protected boolean alreadyNotified;

  public GamePanel(boolean showStatistics)
  {
    statistics = new Statistics();
    this.showStatistics = showStatistics;
    activeObjects = new ArrayList<Sprite>();
    camera = new Camera(this, 0, 0);
    alreadyNotified = false;

    setBackground(Color.white);
    setPreferredSize(new Dimension(screenWidth, screenHeight));

    setupKeyInput();
    setupMouseInput();

    setFocusable(true);
    requestFocus();
    random = new Random(System.currentTimeMillis());
  }

  public void addNotify()
  {
    super.addNotify();
    if (!alreadyNotified)
    {
      initialise();
      alreadyNotified = true;
      this.addComponentListener(this);
    }
  }

  public void createBackBuffer()
  {
    screenWidth = getWidth();
    screenHeight = getHeight();
    if ((screenWidth > 0) && (screenHeight > 0))
    {
      backBufferImage = createImage(screenWidth, screenHeight);
      if (backBufferImage != null)
      {
        backBuffer = (Graphics2D) backBufferImage.getGraphics();
      }
      else
      {
        backBuffer = null;
      }
      resizedBuffer();
    }
    else
    {
      backBufferImage = null;
      backBuffer = null;
    }
    abusedBuffer = backBuffer;
  }

  public abstract void resizedBuffer();

  private void destroy()
  {
    System.exit(0);
  }

  public void paintScreen()
  {
    Graphics g;
    try
    {
      g = this.getGraphics();
      if ((g != null) && (backBufferImage != null))
      {
        paintStatistics(backBufferImage);
        g.drawImage(backBufferImage, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
      }
    }
    catch (Exception e)
    {
    }
  }

  private void paintStatistics(Image image)
  {
    if (showStatistics)
    {
      Graphics g = image.getGraphics();
      g.setColor(Color.WHITE);
      g.drawString("FrameTime: " + statistics.getAvgFrameTime() / NANOS_IN_MILLI + " (" + statistics.getImmFrameTime() / NANOS_IN_MILLI + ")", 10, 20);
      g.drawString("SleepTime: " + statistics.getAvgSleepTime() / NANOS_IN_MILLI + " (" + statistics.getImmSleepTime() / NANOS_IN_MILLI + ")", 10, 40);
      g.drawString("Ovetime: " + statistics.getAvgOvertime() / NANOS_IN_MILLI + " (" + statistics.getImmOvertime() / NANOS_IN_MILLI + ")", 10, 60);

      g.setColor(Color.yellow);
      g.drawString("Sprites: " + statistics.getAvgActiveObjects() + " (" + statistics.getImmActiveObjects() + ")", 10, 90);
    }
  }

  protected void update()
  {
    tick();
    collision();
  }

  private void collision()
  {
    ArrayList<Sprite> activeObjects = new ArrayList<Sprite>();
    for (Sprite sprite : this.activeObjects)
    {
      if (sprite.collision.size() > 0)
      {
        activeObjects.add(sprite);
      }
    }

    for (int i = 0; i < activeObjects.size(); i++)
    {
      for (int j = i + 1; j < activeObjects.size(); j++)
      {
        Sprite sprite1 = activeObjects.get(i);
        Sprite sprite2 = activeObjects.get(j);

        if ((sprite1.collisionBits & sprite2.collisionBits) != 0)
        {
          Shape shape1 = sprite1.getShape();
          Shape shape2 = sprite2.getShape();

          boolean result = Collision.collide(shape1, shape2);
          if (result)
          {
            sprite1.collidedWith(sprite2);
            sprite2.collidedWith(sprite1);
          }
          else
          {
            sprite1.noCollideWith(sprite2);
            sprite2.noCollideWith(sprite1);
          }
        }
      }
    }

    for (Sprite sprite : activeObjects)
    {
      LinkedHashMap<Sprite, Integer> collidedTicks = sprite.collidedTicks;
      if ((collidedTicks != null) && (collidedTicks.size() > 0))
      {
        Set<Map.Entry<Sprite, Integer>> entries = new LinkedHashSet<Map.Entry<Sprite, Integer>>(collidedTicks.entrySet());
        for (Map.Entry<Sprite, Integer> entry : entries)
        {
          Sprite collidedWith = entry.getKey();
          Integer ticks = entry.getValue();
          collidedWith.collide(sprite, ticks);
        }
      }
    }
  }

  private void tick()
  {
    ArrayList<Sprite> activeObjects = new ArrayList<Sprite>(this.activeObjects);

    statistics.setActiveObjects(activeObjects.size());

    for (Sprite sprite : activeObjects)
    {
      sprite.tick();
      sprite.prepositionTick();
    }

    boolean allTicked;
    do
    {
      allTicked = true;
      for (Sprite sprite : activeObjects)
      {
        sprite.updatePosition();
        if (!sprite.positionTicked)
        {
          allTicked = false;
        }
      }
    }
    while (!allTicked);

    for (Sprite sprite : activeObjects)
    {
      sprite.postpositionTick();
    }
  }

  public void render()
  {
    preRender();

    java.util.List layers[];
    layers = new java.util.List[50];
    for (int i = 0; i < layers.length; i++)
    {
      layers[i] = new ArrayList();
    }

    for (Sprite sprite : activeObjects)
    {
      if (sprite.isVisible())
      {
        layers[(sprite).layer].add(sprite);
      }
    }

    for (int i = layers.length - 1; i >= 0; i--)
    {
      for (Object o : layers[i])
      {
        renderSprite(((Sprite) o));
      }
    }

    postRender();
  }

  public void renderSprite(Sprite sprite)
  {
    int x = (int) (sprite.getLeft() - camera.getPosition().x);
    int y = (int) (sprite.getTop() - camera.getPosition().y);
    backBuffer.drawImage(sprite.getBufferedImage(), x, y, null);
  }

  public Point getTopLeftScreenPosition(Sprite sprite)
  {
    int x = (int) (sprite.getLeft() - camera.getPosition().x);
    int y = (int) (sprite.getTop() - camera.getPosition().y);
    return new Point(x, y);
  }

  public Point getBottomRightScreenPosition(Sprite sprite)
  {
    int x = (int) (sprite.getRight() - camera.getPosition().x);
    int y = (int) (sprite.getBottom() - camera.getPosition().y);
    return new Point(x, y);
  }

  public void postRender()
  {
  }

  public void preRender()
  {
  }

  protected abstract void initialise();

  private void setupMouseInput()
  {
    mouseDown = new boolean[4];

    addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        int button = e.getButton();
        mouseDown[button] = true;
      }

      public void mouseReleased(MouseEvent e)
      {
        int button = e.getButton();
        mouseDown[button] = false;
      }

      public void mouseWheelMoved(MouseWheelEvent e)
      {
        mouseWheel = e.getWheelRotation();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter()
    {
      public void mouseMoved(MouseEvent e)
      {
        mousePosition = e.getPoint();
      }
    });
  }

  private void setupKeyInput()
  {
    keyDown = new boolean[0xff];

    addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        int code = e.getKeyCode();
        if ((code >= 0) && (code <= 0xff))
        {
          keyDown[code] = true;
        }
      }

      public void keyReleased(KeyEvent e)
      {
        int code = e.getKeyCode();
        if ((code >= 0) && (code <= 0xff))
        {
          keyDown[code] = false;
        }
      }
    });
  }

  public void addSprite(Sprite sprite)
  {
    activeObjects.add(sprite);
  }

  public void removeSprite(Sprite sprite)
  {
    for (Sprite activeSprite : activeObjects)
    {
      if (activeSprite != sprite)
      {
        LinkedHashMap<Sprite, Integer> collidedTicks = activeSprite.collidedTicks;
        if (collidedTicks != null)
        {
          collidedTicks.remove(sprite);
        }
      }
    }
    activeObjects.remove(sprite);
  }

  public Camera getCamera()
  {
    return camera;
  }

  public void setCamera(Camera camera)
  {
    this.camera = camera;
  }

  public boolean keyDown(int code)
  {
    return keyDown[code];
  }

  public Random getRandom()
  {
    return random;
  }

  public void setRunnable(GameRunnable runnable)
  {
    this.runnable = runnable;
  }

  public void resizeBuffer()
  {
    abusedBuffer = null;
  }

  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    resizeBuffer();
  }

  public Graphics2D getBackBuffer()
  {
    return backBuffer;
  }

  public void componentResized(ComponentEvent e)
  {
    resizeBuffer();
  }

  public void componentMoved(ComponentEvent e)
  {
  }

  public void componentShown(ComponentEvent e)
  {
  }

  public void componentHidden(ComponentEvent e)
  {
  }

  public void clearActiveObjects(boolean keepCurrentCamera)
  {
    activeObjects = new ArrayList<Sprite>();
    if (keepCurrentCamera)
    {
      addSprite(camera);
    }
  }

  public ArrayList<Sprite> getActiveObjects()
  {
    return activeObjects;
  }
}
