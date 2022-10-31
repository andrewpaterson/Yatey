package net.rectangletest;

import net.engine.GamePanel;
import net.engine.cel.Cel;
import net.engine.cel.CelHelper;
import net.engine.cel.CelStore;
import net.engine.picture.Picture;

import java.awt.*;

public class RectanglePanel extends GamePanel
{
  private GreenRect greenRect;
  private YellowRect yellowRect;
  private GrayRect grayRect;
  private Yellow2Rect yellow2Rect;
  private PinkRect pinkRect;

  public RectanglePanel()
  {
    super(true);
  }

  protected void initialise()
  {
    Picture green = new Picture(40, 40);
    green.setColor(0, Color.GREEN);
    green.rect(0, 0, 40, 40, 0);

    Picture yellow = new Picture(20, 20);
    yellow.setColor(0, Color.YELLOW);
    yellow.rect(0, 0, 20, 20, 0);

    Picture gray = new Picture(40, 40);
    gray.setColor(0, Color.GRAY);
    gray.rect(0, 0, 40, 40, 0);

    Picture yellow2 = new Picture(20, 40);
    yellow2.setColor(0, Color.YELLOW);
    yellow2.setColor(1, Color.RED);
    yellow2.rect(0, 0, 20, 20, 0);
    yellow2.rect(0, 20, 20, 40, 1);

    Picture pink = new Picture(10, 10);
    pink.setColor(0, Color.PINK);
    pink.rect(0, 0, 10, 10, 0);

    CelStore celStore = CelStore.getInstance();
    celStore.addCelHelper("GREEN 40x40", new CelHelper(green));
    celStore.addCelHelper("YELLOW 20x20", new CelHelper(yellow));
    celStore.addCelHelper("GRAY 40x40", new CelHelper(gray));
    celStore.addCelHelper("YELLOW2 20x20", new CelHelper(yellow2, 1, 2, false));
    celStore.addCelHelper("PINK 10x10", new CelHelper(pink));

    greenRect = new GreenRect(this, 320, 240);
    yellowRect = new YellowRect(this, 320, 240);
    grayRect = new GrayRect(this, 150, 100);
    yellow2Rect = new Yellow2Rect(this, 400, 300);
    pinkRect = new PinkRect(this, 200, 100);

    camera.setPosition(0, 0);
  }

  public void preRender()
  {
    backBuffer.setColor(Color.BLUE);
    backBuffer.fillRect(0, 0, 640, 480);
  }

    public void postRender()
  {
    Cel cel = yellowRect.getCel();
    float top = cel.getGraphicsTop();
    float left = cel.getGraphicsLeft();
    float bottom = cel.getGraphicsBottom();
    float right = cel.getGraphicsRight();

    String topLeft = "Left Top (" + left + ", " + top + ")";
    String bottomRight = "Right Bottom (" + right + ", " + bottom + ")";
    backBuffer.setColor(Color.YELLOW);
    backBuffer.drawString(topLeft, 10, 440);
    backBuffer.drawString(bottomRight, 10, 460);
  }

    public void resizedBuffer()
    {
    }
}
