package net.rectangletest;

import net.engine.GameFrame;

import java.awt.*;

public class RectangleTest extends GameFrame<RectanglePanel>
{
    public RectangleTest() throws HeadlessException
    {
        super("Rectange Test");
    }

    public static void main(String[] args)
    {
        RectangleTest rectangleTest = new RectangleTest();
        rectangleTest.setSize(640, 480);
        rectangleTest.setVisible(true);
    }

    public RectanglePanel buildGamePanel()
    {
        return new RectanglePanel();
    }
}
