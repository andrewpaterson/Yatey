package net.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class GameFrame<T extends GamePanel> extends JFrame
{
    public T gamePanel;
    public GameRunnable runnable;
    public GraphicsDevice graphicsDevice;

    protected GameFrame() throws HeadlessException
    {
        this("Game");
    }

    protected GameFrame(String title) throws HeadlessException
    {
        super(title);
        gamePanel = buildGamePanel();
        runnable = new GameRunnable(gamePanel);
        add(gamePanel);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                runnable.stopGame();
            }
        });
        gamePanel.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ESCAPE)
                {
                    runnable.stopGame();
                }
            }
        });
    }

    protected void initFullScreen()
    {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);
        DisplayMode displayMode = getDisplayMode(640, 480);
        graphicsDevice.setFullScreenWindow(this);
        graphicsDevice.setDisplayMode(displayMode);
    }

    protected DisplayMode getDisplayMode(int width, int height)
    {
        DisplayMode[] displayModes = graphicsDevice.getDisplayModes();
        int lastBitDepth = -1;
        DisplayMode lastDisplayMode = null;

        for (DisplayMode displayMode : displayModes)
        {
            if (displayMode.getHeight() == height)
            {
                if (displayMode.getWidth() == width)
                {
                    if (displayMode.getBitDepth() >= lastBitDepth)
                    {
                        lastBitDepth = displayMode.getBitDepth();
                        lastDisplayMode = displayMode;
                    }
                }
            }
        }
        return lastDisplayMode;
    }

    protected abstract T buildGamePanel();
}
