package net.tileeditor.simpleobjectselector;

import net.tileeditor.ObjectField;
import net.tileeditor.SimpleObject;
import net.tileeditor.SimpleObjects;
import net.tileeditor.Source;
import net.tileeditor.general.AcceptCancelDialog;
import net.tileeditor.general.AcceptCancelPanel;
import net.tileeditor.general.TileEditor;

import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;

public class AddPropertyDialog extends AcceptCancelDialog
{
  public JComboBox typeComboBox;
  public JTextField fieldNameText;
  public JTextField defaultValueText;
  private Class tileClass;

  public AddPropertyDialog(TileEditor tileEditor, Class tileClass)
  {
    super(tileEditor, "Add Property", true);
    this.tileClass = tileClass;
    setLayout(new GridBagLayout());
    int width = 230;
    int height = 150;
    setSize(width, height);

    Dimension dimension = tileEditor.getSize();
    Point point = tileEditor.getLocation();
    setLocation(dimension.width / 2 - width / 2 + point.x, dimension.height / 2 - height / 2 + point.y);

    int row = 0;
    add(label("Type:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    typeComboBox = comboBox(ObjectField.getAllowedFields());
    typeComboBox.setSelectedItem("Text");
    add(typeComboBox, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Field Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    fieldNameText = textField("");
    add(fieldNameText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(label("Default value:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    defaultValueText = textField("");
    add(defaultValueText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    row++;

    add(new AcceptCancelPanel(this), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
  }

  public void accepted()
  {
    int type = ObjectField.getFieldType(typeComboBox.getSelectedItem());
    String fieldName = fieldNameText.getText();

    SimpleObjects objects = Source.getInstance().getSimpleObjects(tileClass);
    if (doesFieldExist(fieldName, objects))
    {
      return;
    }

    if ((type != -1) && (fieldName.length() > 1))
    {
      String text = defaultValueText.getText();
      if (type == ObjectField.NUMBER)
      {
        Integer number = isNumberValid(text);
        if (number == null)
        {
          return;
        }
        objects.addField(fieldName, type, number);
      }
      else if (type == ObjectField.BRUSH)
      {
        String brush = isBrushValid(text, objects);
        if (brush == null)
        {
          return;
        }
        objects.addField(fieldName, type, brush);

      }
      else
      {
        objects.addField(fieldName, type, text);
      }
      super.accepted();
    }
  }

  private String isBrushValid(String text, SimpleObjects objects)
  {
    for (SimpleObject simpleObject : objects.getSimpleObjects())
    {
      if (simpleObject.getName().equals(text))
      {
        return text;
      }
    }
    return null;
  }

  private Integer isNumberValid(String text)
  {
    Integer number = 0;
    try
    {
      number = Integer.parseInt(text);
      return number;
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }

  private boolean doesFieldExist(String fieldName, SimpleObjects objects)
  {
    for (ObjectField objectField : objects.getFields())
    {
      if (objectField.getName().equals(fieldName))
      {
        return true;
      }
    }
    return false;
  }
}
