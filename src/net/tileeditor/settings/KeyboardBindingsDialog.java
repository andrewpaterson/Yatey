package net.tileeditor.settings;

import net.tileeditor.keybindings.KeyBinding;
import net.tileeditor.keybindings.ActionKeyBinding;
import net.tileeditor.Settings;
import net.tileeditor.general.AcceptCancelDialog;
import net.tileeditor.general.AcceptCancelPanel;
import net.tileeditor.general.TileEditor;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class KeyboardBindingsDialog extends AcceptCancelDialog implements KeyListener
{
  private Map<String, KeyBinding> keyBindings;

  public KeyboardBindingsDialog(TileEditor tileEditor)
  {
    super(tileEditor, "Key Bindings", true);
    setLayout(new GridBagLayout());
    int width = 230;
    int height = 560;
    setSize(width, height);

    Dimension dimension = tileEditor.getSize();
    Point point = tileEditor.getLocation();
    setLocation(dimension.width / 2 - width / 2 + point.x, dimension.height / 2 - height / 2 + point.y);

    copySettings();

    Set<String> strings = keyBindings.keySet();

    int row = 0;
    for (String action : strings)
    {
      add(label(action + ": "), new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, EAST, HORIZONTAL, new Insets(2, 12, 2, 2), 0, 0));
      KeyBinding binding = keyBindings.get(action);
      JTextField field;
      if (binding != null)
      {
        field = textField(binding.toString());
      }
      else
      {
        field = textField("");
      }
      field.addKeyListener(this);
      field.setName(action);
      add(field, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, WEST, new Insets(2, 2, 2, 12), 0, 0));
      row++;
    }

    add(new AcceptCancelPanel(this), new GridBagConstraints(0, row, 2, 1, 1.0, 0.0, CENTER, NONE, new Insets(2, 2, 12, 2), 0, 0));
    pack();
  }

  private void copySettings()
  {
    keyBindings = new LinkedHashMap<String, KeyBinding>();
    Settings settings = Settings.getInstance();
    Set<String> strings = settings.getActions();
    for (String action : strings)
    {
      ActionKeyBinding binding = (ActionKeyBinding) settings.getKeyBinding(action);
      if (binding != null)
      {
        keyBindings.put(action, new ActionKeyBinding(binding));
      }
      else
      {
        keyBindings.put(action, null);
      }
    }
  }

  public void cancelled()
  {
    super.cancelled();
  }

  public void accepted()
  {
    Settings settings = Settings.getInstance();
    settings.clearKeyBindings();
    Set<String> strings = keyBindings.keySet();
    for (String action : strings)
    {
      KeyBinding binding = keyBindings.get(action);
      settings.setKeyBinding(action, binding);
    }
    settings.save();
    super.accepted();
  }

  protected JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.RIGHT);
    label.setPreferredSize(new Dimension(110, label.getPreferredSize().height));
    return label;
  }

  public void keyTyped(KeyEvent e)
  {
  }

  public void keyPressed(KeyEvent e)
  {
    Object source = e.getSource();
    if (source instanceof JTextField)
    {
      JTextField textField = (JTextField) source;
      textField.setText("");
    }
  }

  public void keyReleased(KeyEvent e)
  {
    int keyCode = e.getKeyCode();
    if (!((keyCode == KeyEvent.VK_CONTROL) || (keyCode == KeyEvent.VK_SHIFT) || (keyCode == KeyEvent.VK_ALT) || (keyCode == KeyEvent.VK_ESCAPE)))
    {
      Object source = e.getSource();
      if (source instanceof JTextField)
      {
        JTextField textField = (JTextField) source;
        String action = textField.getName();
        int modifiers = e.getModifiers();
        KeyBinding keyBinding = new ActionKeyBinding(action, keyCode, modifiers);
        keyBindings.put(action, keyBinding);
        textField.setText(keyBinding.toString());
      }
    }
    else if (keyCode == KeyEvent.VK_ESCAPE)
    {
      Object source = e.getSource();
      if (source instanceof JTextField)
      {
        JTextField textField = (JTextField) source;
        String action = textField.getName();
        keyBindings.put(action, null);
      }
    }
  }
}
