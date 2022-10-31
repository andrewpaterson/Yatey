package net.tileeditor.mapeditor;

import net.tileeditor.general.TileEditor;
import net.tileeditor.general.AcceptCancelPanel;
import net.tileeditor.general.AcceptCancelDialog;
import net.tileeditor.source.ObjectWrapperFactory;
import net.tileeditor.Source;
import net.tileeditor.layers.TileClassWrapper;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

public class AddLayerDialog extends AcceptCancelDialog
{
  private JTextField nameText;
  private JComboBox typeComboBox;

  public AddLayerDialog(TileEditor tileEditor)
  {
    super(tileEditor, "Add New Layer", true);
    setLayout(new GridBagLayout());
    int width = 220;
    int height = 120;
    setSize(width, height);

    Dimension dimension = tileEditor.getSize();
    Point point = tileEditor.getLocation();
    setLocation(dimension.width / 2 - width / 2 + point.x, dimension.height / 2 - height / 2 + point.y);

    add(label("Name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    nameText = textField("");
    add(nameText, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Type:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    typeComboBox = comboBox(ObjectWrapperFactory.getInstance().getAllowedClasses());
    add(typeComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(new AcceptCancelPanel(this), new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
  }

  public void accepted()
  {
    String name = nameText.getText();
    TileClassWrapper tileClassWrapper = (TileClassWrapper) typeComboBox.getSelectedItem();
    if ((tileClassWrapper != null) && (name.length() > 0))
    {
      Source source = Source.getInstance();
      source.getCurrentMap().addLayer(name, tileClassWrapper.aClass);
      source.fixCurrentBrush();
      super.accepted();
    }
  }

  public boolean isAccepted()
  {
    return accepted;
  }
}
