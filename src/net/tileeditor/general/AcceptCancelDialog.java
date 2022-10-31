package net.tileeditor.general;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerListener;
import java.awt.event.KeyListener;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyEvent;

public abstract class AcceptCancelDialog extends JDialog implements AcceptedListener, ContainerListener, KeyListener
{
  public static final int inputWidth = 160;
  public static final int labelWidth = 120;

  public boolean accepted;
  public TileEditor tileEditor;

  public AcceptCancelDialog(TileEditor tileEditor, String title, boolean modal)
  {
    super(tileEditor, title, modal);
    this.tileEditor = tileEditor;
    addKeyAndContainerListenerRecursively(this);
    accepted = false;
  }

  protected JTextField textField(String s)
  {
    JTextField field = new JTextField(s);
    field.setPreferredSize(new Dimension(inputWidth, field.getPreferredSize().height));
    return field;
  }

  protected JTextField textField(String s, int width)
  {
    JTextField field = new JTextField(s);
    field.setPreferredSize(new Dimension(width, field.getPreferredSize().height));
    return field;
  }

  protected JComboBox comboBox(Object[] items)
  {
    JComboBox box = new JComboBox(items);
    box.setPreferredSize(new Dimension(inputWidth, box.getPreferredSize().height));
    return box;
  }

  protected JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.RIGHT);
    label.setPreferredSize(new Dimension(labelWidth, label.getPreferredSize().height));
    return label;
  }

  protected JCheckBox checkBox(boolean checked)
  {
    JCheckBox field = new JCheckBox();
    field.setPreferredSize(new Dimension(18, field.getPreferredSize().height));
    field.setSelected(checked);
    return field;
  }

  public void accepted()
  {
    this.setVisible(false);
    this.dispose();
    accepted = true;
  }

  public void cancelled()
  {
    this.setVisible(false);
    this.dispose();
  }

  public boolean isAccepted()
  {
    return accepted;
  }

  public void componentAdded(ContainerEvent e)
  {
    addKeyAndContainerListenerRecursively(e.getChild());
  }

  public void componentRemoved(ContainerEvent e)
  {
    removeKeyAndContainerListenerRecursively(e.getChild());
  }

  private void addKeyAndContainerListenerRecursively(Component c)
  {
    c.addKeyListener(this);
    if (c instanceof Container)
    {
      Container cont = (Container) c;
      cont.addContainerListener(this);
      Component[] children = cont.getComponents();
      for (Component aChildren : children)
      {
        addKeyAndContainerListenerRecursively(aChildren);
      }
    }
  }

  private void removeKeyAndContainerListenerRecursively(Component c)
  {
    c.removeKeyListener(this);
    if (c instanceof Container)
    {
      Container cont = (Container) c;
      cont.removeContainerListener(this);
      Component[] children = cont.getComponents();
      for (Component aChildren : children)
      {
        removeKeyAndContainerListenerRecursively(aChildren);
      }
    }
  }

  public void keyTyped(KeyEvent e)
  {
  }

  public void keyPressed(KeyEvent e)
  {
  }

  public void keyReleased(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_ESCAPE)
    {
      cancelled();
    }
    else if (keyCode == KeyEvent.VK_ENTER)
    {
      accepted();
    }
  }
}
