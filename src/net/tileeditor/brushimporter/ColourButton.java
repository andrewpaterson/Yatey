package net.tileeditor.brushimporter;

import javax.swing.*;
import java.awt.*;

public class ColourButton extends JButton
{
  public Color color;

  public ColourButton(String text, Color color)
  {
    super(text);
    if (color == null)
    {
      color = Color.black;
    }
    this.color = color;
  }

  protected void paintComponent(Graphics g)
  {
    g.setColor(color);
    g.fillRect(0, 0, getWidth(), getHeight());
    if ((color.getRed() + color.getGreen() + color.getBlue()) < 192)
    {
      g.setColor(Color.white);
    }
    else
    {
      g.setColor(Color.black);
    }
    g.drawString(getText(), 20, getHeight() / 2 + 5);
  }

  public void setColor(Color color)
  {
    this.color = color;
  }

  public Color getColour()
  {
    return color;
  }
}
