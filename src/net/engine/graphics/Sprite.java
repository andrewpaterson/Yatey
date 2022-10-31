package net.engine.graphics;

import net.engine.Control;
import net.engine.GamePanel;
import net.engine.SafeArrayList;
import net.engine.cel.Cel;
import net.engine.cel.CelHelper;
import net.engine.cel.CelSource;
import net.engine.links.Links;
import net.engine.math.Float2;
import net.engine.shape.Rectangle;
import net.engine.shape.Shape;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

public class Sprite
{
  Float2 position;
  public Float2[] prevPositions;

  public Float2 velocity;
  public Float2 perceivedVelocity;
  public GamePanel gamePanel;
  public boolean positionTicked;
  public String name;
  public int layer;
  public int collisionBits;

  public SafeArrayList<Cel> cels;
  public SafeArrayList<Shape> collision;
  public SafeArrayList<Links> links;
  public int celFrame;
  public int collisionFrame;
  public int linkFrame;

  public Sprite parent;
  public int linkIndexOnParent;
  public SafeArrayList<Sprite> children;

  public Control control;
  public int numPrevPositions;

  public LinkedHashMap<Sprite, Integer> collidedTicks;

  public Sprite(GamePanel gamePanel)
  {
    this.gamePanel = gamePanel;
    position = null;
    numPrevPositions = 3;
    prevPositions = initPrevPositions(numPrevPositions);
    velocity = new Float2();
    perceivedVelocity = new Float2();
    parent = null;
    name = "";
    collisionBits = 0;
    celFrame = 0;
    collisionFrame = 0;
    linkFrame = 0;
    linkIndexOnParent = -1;
    children = new SafeArrayList<Sprite>();
    cels = new SafeArrayList<Cel>();
    collision = new SafeArrayList<Shape>();
    links = new SafeArrayList<Links>();
    control = null;
    collidedTicks = null;
  }

  public Sprite(GamePanel gamePanel, float x, float y)
  {
    this(gamePanel);
    position = new Float2(x, y);
    gamePanel.addSprite(this);
  }

  public Sprite(GamePanel gamePanel, CelHelper celHelper, int frame, float x, float y)
  {
    this(gamePanel, x, y);
    addCelsFromCelHelper(celHelper);
    celFrame = frame;
  }

  public Sprite(GamePanel gamePanel, Cel cel, float x, float y)
  {
    this(gamePanel, x, y);
    cels.add(cel);
  }

  private Float2[] initPrevPositions(int numPrevPositions)
  {
    Float2[] positons = new Float2[numPrevPositions];
    if (position == null)
    {
      for (int i = 0; i < numPrevPositions; i++)
      {
        positons[i] = null;
      }
    }
    else
    {
      for (int i = 0; i < numPrevPositions; i++)
      {
        positons[i] = new Float2(position);
      }
    }
    return positons;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setPosition(float x, float y)
  {
    if (position != null)
    {
      position.x = x;
      position.y = y;
    }
    else
    {
      position = new Float2(x, y);
    }
  }

  public void setPosition(Point2D.Float position)
  {
    this.position.x = position.x;
    this.position.y = position.y;
  }

  public void setStaticPosition(float x, float y)
  {
    position.x = x;
    position.y = y;
    updatePrevPositions();
  }

  public void setStaticPosition(Point2D.Float position)
  {
    this.position.x = position.x;
    this.position.y = position.y;
    updatePrevPositions();
  }

  public void setPositionX(float x)
  {
    this.position.x = x;
  }

  public void setPositionY(float y)
  {
    this.position.y = y;
  }

  private boolean isLinked()
  {
    return parent != null;
  }

  public void linkTo(Sprite parent, int linkIndexOnParent)
  {
    parent.children.add(this);
    this.parent = parent;
    this.linkIndexOnParent = linkIndexOnParent;

    position = new Float2();
    velocity = new Float2();
  }

  public void unlink()
  {
    if (isLinked())
    {
      velocity = new Float2(parent.velocity);

      parent.children.remove(this);
      parent = null;
      linkIndexOnParent = -1;
    }
  }

  public void updatePosition()
  {
    if (isLinked())
    {
      if (parent.positionTicked)
      {
        updatePrevPositions();
        position = parent.getLinkPointInWorldSpace(linkIndexOnParent);
        if (position == null)
        {
          position = new Float2();
        }
        positionTicked = true;
      }
    }
    else
    {
      updatePrevPositions();
      position.x += velocity.x;
      position.y += velocity.y;
      positionTicked = true;
    }
  }

  public void perceivedVelocity()
  {
    if (numPrevPositions > 0)
    {
      perceivedVelocity = new Float2(position);
      perceivedVelocity.subtract(prevPositions[0]);
    }
  }

  private void updatePrevPositions()
  {
    if (numPrevPositions >= 1)
    {
      for (int i = numPrevPositions - 2; i >= 0; i--)
      {
        prevPositions[i + 1] = prevPositions[i];
      }
      prevPositions[0] = new Float2(position);
    }
  }

  public Float2 getLinkPoint(int linkIndex)
  {
    Links links = getLink();
    if (links == null)
    {
      return null;
    }
    return links.linkPoints.get(linkIndex);
  }

  protected Float2 getLinkPointInWorldSpace(int linkIndex)
  {
    Links link = getLink();
    if (link == null)
    {
      return null;
    }

    Float2 linkPoint = link.getLinkPoint(linkIndex);
    if (linkPoint == null)
    {
      return null;
    }
    else
    {
      return new Float2(linkPoint).add(position);
    }
  }

  public void tick()
  {
    if (control != null)
    {
      control.control();
    }
  }

  public String toString()
  {
    return name;
  }

  public Cel getCel()
  {
    return cels.get(celFrame);
  }

  public Shape getCollision()
  {
    return collision.get(collisionFrame);
  }

  public float getLeft()
  {
    return getCel().getGraphicsLeft() + position.x;
  }

  public float getTop()
  {
    return getCel().getGraphicsTop() + position.y;
  }

  public float getRight()
  {
    return getCel().getGraphicsRight() + position.x;
  }

  public float getBottom()
  {
    return getCel().getGraphicsBottom() + position.y;
  }

  public BufferedImage getBufferedImage()
  {
    return getCel().bufferedImage;
  }

  public void setLayer(int layer)
  {
    this.layer = layer;
  }

  public void addCelsFromCelHelper(CelHelper celHelper)
  {
    for (CelSource cel : celHelper.getCels())
    {
      cels.add(cel.cel);
    }
  }

  public void addCelsFromCelHelper(CelHelper celHelper, Integer... celOrder)
  {
    for (int i = 0; i < celOrder.length; i++)
    {
      int index = celOrder[i];
      Cel cel = celHelper.get(index);
      cels.add(cel);
    }
  }

  public void setCel(int frame, Cel cel)
  {
    cels.set(frame, cel);
  }

  public void setCollsion(int frame, Shape shape)
  {
    collision.set(frame, shape);
  }

  public void setLinks(int frame, Links links)
  {
    this.links.set(frame, links);
  }

  public void setLinks(int frame, int linkIndex, float x, float y)
  {
    Links links = this.links.get(frame);
    if (links == null)
    {
      links = new Links(this);
      setLinks(frame, links);
    }
    links.setLinkPoint(linkIndex, x, y);
  }

  public boolean isVisible()
  {
    if (isLinked())
    {
      Float2 float2 = parent.getLinkPoint(linkIndexOnParent);
      if (float2 == null)
      {
        return false;
      }
    }
    return getCel() != null;
  }

  public boolean isCollidable()
  {
    return getCollision() != null;
  }

  public Control getControl()
  {
    return control;
  }

  public void setControl(Control control)
  {
    this.control = control;
  }

  private Links getLink()
  {
    return links.get(linkFrame);
  }

  public void remove()
  {
    gamePanel.removeSprite(this);
  }

  public int numCels()
  {
    return cels.size();
  }

  public Float2 getPosition()
  {
    return position;
  }

  public void prepositionTick()
  {
    positionTicked = false;
    updatePosition();
  }

  public void postpositionTick()
  {
    perceivedVelocity();
  }

  public void addBoundingBoxes(float inset)
  {
    for (int i = 0; i < cels.size(); i++)
    {
      Cel cel = cels.get(i);
      if (cel != null)
      {
        Float2 topLeft = new Float2();
        Float2 bottomRight = new Float2();

        topLeft.y = cel.getGraphicsTop() + inset;
        topLeft.x = cel.getGraphicsLeft() + inset;
        bottomRight.y = cel.getGraphicsBottom() - inset;
        bottomRight.x = cel.getGraphicsRight() - inset;

        setCollsion(i, new Rectangle(topLeft, bottomRight, position));
      }
      else
      {
        setCollsion(i, null);
      }
    }
  }

  public Shape getShape()
  {
    return collision.get(collisionFrame);
  }

  public void collide(Sprite sprite, int ticks)
  {
    if (control != null)
    {
      if (ticks > 0)
      {
        control.startCollision(sprite);
        control.collide(sprite, ticks);
      }
      else
      {
        control.endCollision(sprite);
      }
    }
  }

  public void setCollisionBit(int bit, boolean state)
  {
    if (state)
    {
      this.collisionBits |= 1 << bit;
    }
    else
    {
      this.collisionBits &= ~(1 << bit);
    }
  }

  public boolean getCollisionBit(int bit)
  {
    return ((collisionBits & (1 << bit)) != 0);
  }

  public void collidedWith(Sprite sprite)
  {
    if (collidedTicks == null)
    {
      collidedTicks = new LinkedHashMap<Sprite, Integer>();
    }
    Integer integer = collidedTicks.get(sprite);
    if (integer == null)
    {
      integer = 1;
    }
    else
    {
      integer++;
    }
    collidedTicks.put(sprite, integer);
  }

  public void noCollideWith(Sprite sprite)
  {
    if (collidedTicks == null)
    {
      return;
    }

    Integer integer = collidedTicks.get(sprite);
    if (integer != null)
    {
      if (integer == 0)
      {
        collidedTicks.remove(sprite);
      }
      else
      {
        integer = 0;
        collidedTicks.put(sprite, integer);
      }
    }
  }

  public void move(int xDiff, int yDiff)
  {
    Float2 position = getPosition();
    setPosition(position.x + xDiff, position.y + yDiff);
  }
}
