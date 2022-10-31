package net.tileeditor.settings;

import net.tileeditor.*;
import net.tileeditor.general.AcceptCancelDialog;
import net.tileeditor.general.AcceptCancelPanel;
import net.tileeditor.general.TileEditor;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.*;

public class ProjectSettingsDialog extends AcceptCancelDialog
{
  public JTextField maxUndoText;
  public JTextField maxAutosaveText;
  public JTextField texturesPathText;
  public JCheckBox autoloadCheck;

  public ProjectSettingsDialog(TileEditor tileEditor)
  {
    super(tileEditor, "Project Settings", true);
    setLayout(new GridBagLayout());
    int width = 400;
    int height = 170;
    setSize(width, height);

    Dimension dimension = tileEditor.getSize();
    Point point = tileEditor.getLocation();
    setLocation(dimension.width / 2 - width / 2 + point.x, dimension.height / 2 - height / 2 + point.y);

    String maxUndo = Integer.toString(TheHold.getInstance().getMaxStack());
    String maxAutosave = Integer.toString(AutoSaver.getInstance().getMaxSaves());
    String texturesPath = Settings.getInstance().getTextureDirectory();
    boolean autoload = Settings.getInstance().isAutoloadLastProject();

    int row = 0;
    add(label("Max Undo:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(2, 2, 2, 2), 0, 0));
    maxUndoText = textField(maxUndo);
    add(maxUndoText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Textures Path:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(2, 2, 2, 2), 0, 0));
    texturesPathText = textField(texturesPath, 200);
    add(texturesPathText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Max Autosave:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(2, 2, 2, 2), 0, 0));
    maxAutosaveText = textField(maxAutosave);
    add(maxAutosaveText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Autoload Last:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(2, 2, 2, 2), 0, 0));
    autoloadCheck = checkBox(autoload);
    add(autoloadCheck, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(new AcceptCancelPanel(this), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
  }

  public void accepted()
  {
    boolean canAccept = true;
    try
    {
      String newTexturesPath = texturesPathText.getText();
      if (!newTexturesPath.equals(Settings.getInstance().getTextureDirectory()))
      {
        SimpleObjects simpleObjects = Source.getInstance().getSimpleObjects(SimpleBrush.class);
        boolean canChangeTexturePath = true;
        if ((simpleObjects != null) && (simpleObjects.getSimpleObjects() != null))
        {
          if (simpleObjects.getSimpleObjects().size() > 0)
          {
            canChangeTexturePath = false;
          }
        }
        if (canChangeTexturePath)
        {
          Settings.getInstance().setTextureDirectory(newTexturesPath);
        }
        else
        {
          canAccept = false;
        }
      }

      if (canAccept)
      {
        Integer maxUndo = Integer.valueOf(maxUndoText.getText());
        Integer maxAutoSave = Integer.valueOf(maxAutosaveText.getText());

        TheHold.getInstance().setMaxStack(maxUndo);
        AutoSaver.getInstance().setMaxSaves(maxAutoSave);
        Settings.getInstance().setAutoloadLastProject(autoloadCheck.isSelected());

        Settings.getInstance().save();
        super.accepted();
      }
    }
    catch (NumberFormatException e)
    {
    }
  }
}