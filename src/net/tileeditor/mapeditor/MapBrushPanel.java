package net.tileeditor.mapeditor;

import net.tileeditor.Held;
import net.tileeditor.Settings;
import net.tileeditor.Source;
import net.tileeditor.TheHold;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.general.LineBorderPanel;
import net.tileeditor.general.TileEditor;
import net.tileeditor.keybindings.KeyBinding;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MapBrushPanel extends JPanel implements KeyListener, FocusListener
{
  private CompoundBrushViewPanel compoundBrushPanel;
  public static final int inputWidth = 130;
  public static final int labelWidth = 70;

  protected JTextField brushNameText;
  protected JTextField brushBindingText;
  protected TileEditor tileEditor;
  protected String initialBindingText;
  protected String initialNameText;

  public MapBrushPanel(TileEditor tileEditor)
  {
    this.tileEditor = tileEditor;
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(210, (int) getPreferredSize().getHeight()));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    compoundBrushPanel = new CompoundBrushViewPanel(tileEditor);
    add(new LineBorderPanel(compoundBrushPanel), BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridBagLayout());
    int row = 0;
    panel.add(label("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    brushNameText = textField("");
    panel.add(brushNameText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    panel.add(label("Binding:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    brushBindingText = textField("");
    panel.add(brushBindingText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    brushNameText.addKeyListener(this);
    brushBindingText.addKeyListener(this);
    brushNameText.addFocusListener(this);
    brushBindingText.addFocusListener(this);

    add(panel, BorderLayout.SOUTH);
  }

  public CompoundBrushViewPanel getBrushPanel()
  {
    return compoundBrushPanel;
  }

  protected JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.RIGHT);
    label.setPreferredSize(new Dimension(labelWidth, label.getPreferredSize().height));
    return label;
  }

  protected JTextField textField(String s)
  {
    JTextField field = new JTextField(s);
    field.setPreferredSize(new Dimension(inputWidth, field.getPreferredSize().height));
    return field;
  }

  public void updateValues()
  {
    CompoundBrush currentBrush = Source.getInstance().getCurrentBrush();
    if ((currentBrush != null) && (!currentBrush.isDefault()))
    {
      brushBindingText.setEnabled(true);
      brushNameText.setEnabled(true);

      KeyBinding binding = currentBrush.getKeyBinding();
      String asString;
      if (binding != null)
      {
        asString = binding.toString();
      }
      else
      {
        asString = "";
      }
      brushBindingText.setText(asString);
      brushNameText.setText(currentBrush.getName());
    }
    else
    {
      brushBindingText.setEnabled(false);
      KeyBinding binding = Settings.getInstance().getKeyBinding(Settings.DEFAULT_BRUSH);
      String asString;
      if (binding != null)
      {
        asString = binding.toString();
      }
      else
      {
        asString = "";
      }
      brushBindingText.setText(asString);
      brushNameText.setEnabled(false);
      brushNameText.setText("Default");
    }
  }

  public void keyTyped(KeyEvent e)
  {
  }

  public void keyPressed(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    if (e.getSource() == brushBindingText)
    {
      if (keyCode != KeyEvent.VK_ENTER)
      {
        brushBindingText.setText("");
      }
    }
  }

  public void keyReleased(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    CompoundBrush currentBrush = Source.getInstance().getCurrentBrush();
    if (currentBrush != null)
    {
      if (!currentBrush.isDefault())
      {
        if (e.getSource() == brushBindingText)
        {
          if (!((keyCode == KeyEvent.VK_CONTROL) || (keyCode == KeyEvent.VK_SHIFT) || (keyCode == KeyEvent.VK_ALT) || (keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_ENTER)))
          {
            int modifiers = e.getModifiers();
            String s = Settings.getInstance().getActionForKeypress(keyCode, modifiers);
            if (s != null)
            {
              tileEditor.setWarning("Already bound to action [" + s + "]");
              KeyBinding keyBinding = currentBrush.getKeyBinding();
              if (keyBinding != null)
              {
                brushBindingText.setText(keyBinding.toString());
              }
              return;
            }
            currentBrush.setKeyBinding(keyCode, modifiers);
            brushBindingText.setText(currentBrush.getKeyBinding().toString());
          }
          else if (keyCode == KeyEvent.VK_ESCAPE)
          {
            currentBrush.clearKeyBinding();
            tileEditor.getMapPanel().requestFocus();
          }
          else if (keyCode == KeyEvent.VK_ENTER)
          {
            tileEditor.getMapPanel().requestFocus();
          }
        }
        else if (e.getSource() == brushNameText)
        {
          if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_ENTER))
          {
            tileEditor.getMapPanel().requestFocus();
          }
          else
          {
            currentBrush.setName(brushNameText.getText());
          }
        }
      }
    }
  }

  public void focusGained(FocusEvent e)
  {
    initialBindingText = brushBindingText.getText();
    initialNameText = brushNameText.getText();
  }

  public void focusLost(FocusEvent e)
  {
    if ((!initialBindingText.equals(brushBindingText.getText())) || (!initialNameText.equals(brushNameText.getText())))
    {
      tileEditor.updateVales();
      TheHold.getInstance().hold(Held.BRUSH_NAME_CHANGED);
    }
  }
}
