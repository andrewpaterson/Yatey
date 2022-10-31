package net.tileeditor.simpleobjectselector;

import net.engine.GamePanel;
import net.tileeditor.*;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.general.LineBorderPanel;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.source.ObjectWrapperFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.*;

public class SimpleObjectPropertiesPanel extends JPanel implements ActionListener, KeyListener, ItemListener
{
  protected TileEditor tileEditor;
  protected JButton addButton;
  protected JButton removeButton;
  protected JTextField nameText;
  protected SimpleObjectSelectedViewPanel brushPanel;
  protected JPanel propertiesPanel;
  protected SimpleObject selectedObject;
  protected boolean fakeEnabled;

  public SimpleObjectPropertiesPanel(TileEditor tileEditor)
  {
    super(new GridBagLayout());
    fakeEnabled = true;

    this.tileEditor = tileEditor;
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    setPreferredSize(new Dimension(234, (int) getPreferredSize().getHeight()));

    int row = 0;

    brushPanel = new SimpleObjectSelectedViewPanel(this);
    fakeEnabled = false;
    add(new LineBorderPanel(brushPanel), new GridBagConstraints(0, row, 2, 1, 0.0, 1.0, CENTER, BOTH, new Insets(10, 10, 10, 10), 0, 0));
    row++;

    nameText = textField("");
    add(label("Name:"), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 12, 2, 2), 0, 0));
    add(nameText, new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, EAST, HORIZONTAL, new Insets(2, 2, 2, 12), 0, 0));
    row++;

    propertiesPanel = new JPanel(new GridBagLayout());
    add(propertiesPanel, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, CENTER, BOTH, new Insets(10, 10, 10, 10), 0, 0));
    row++;

    JPanel buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.add(new JPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    addButton = button("Add...");
    removeButton = button("Remove");
    buttonPanel.add(addButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    buttonPanel.add(removeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    addButton.addActionListener(this);
    removeButton.addActionListener(this);

    nameText.addKeyListener(this);

    add(buttonPanel, new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
  }

  private JButton button(String s)
  {
    JButton button = new JButton(s);
    button.setPreferredSize(new Dimension(90, button.getPreferredSize().height));
    button.setMinimumSize(new Dimension(90, button.getPreferredSize().height));
    return button;
  }

  protected JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.RIGHT);
    label.setPreferredSize(new Dimension(60, label.getPreferredSize().height));
    return label;
  }

  protected JLabel labelLeft(String s)
  {
    return new JLabel(s, JLabel.LEFT);
  }

  protected JTextField textField(String s)
  {
    JTextField field = new JTextField(s);
    field.setPreferredSize(new Dimension(100, field.getPreferredSize().height));
    return field;
  }

  protected JTextField textField(Integer i)
  {
    String s = null;
    if (i != null)
    {
      s = i.toString();
    }
    return textField(s);
  }

  protected JComboBox comboBox(Object[] items, Object selectedItem)
  {
    JComboBox field = new JComboBox(items);
    field.setSelectedItem(selectedItem);
    field.setPreferredSize(new Dimension(100, field.getPreferredSize().height));
    return field;
  }

  public GamePanel getBrushPanel()
  {
    return brushPanel;
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == addButton)
    {
      addProperty();
    }
    else if (source == removeButton)
    {
      removeProperties();
    }
  }

  private void removeProperties()
  {
    boolean anyChanged = false;
    for (int i = 0; i < propertiesPanel.getComponentCount(); i++)
    {
      Component component = propertiesPanel.getComponent(i);
      if (component instanceof JCheckBox)
      {
        JCheckBox checkBox = (JCheckBox) component;
        if (checkBox.isSelected())
        {
          SimpleObjects simpleObjects = Source.getInstance().getSimpleObjects(selectedObject.getObjectClass());
          ObjectField field = simpleObjects.getFieldWithName(checkBox.getName());
          simpleObjects.removeField(field);
          anyChanged = true;
        }
      }
    }
    if (anyChanged)
    {
      updateValues(true);
      doLayout();
      propertiesPanel.doLayout();
      TheHold.getInstance().hold(Held.BRUSH_FIELDS_CHANGED);
    }
  }

  private void addProperty()
  {
    if (selectedObject != null)
    {
      AddPropertyDialog dialog = new AddPropertyDialog(tileEditor, selectedObject.getObjectClass());
      dialog.setVisible(true);

      if (dialog.isAccepted())
      {
        tileEditor.getSelectPanel().updateValues(true);
        doLayout();
        propertiesPanel.doLayout();
        TheHold.getInstance().hold(Held.BRUSH_FIELDS_CHANGED);
      }
    }
  }

  public void selectBrush()
  {
    if (fakeEnabled)
    {
      TheHold.getInstance().hold(Held.BRUSH_SELECTED);
      tileEditor.showMapPanel();
    }
  }

  public SimpleObject getSelectedObject()
  {
    return selectedObject;
  }

  public void setSelectedObject(SimpleObject selectedObject)
  {
    boolean layoutChanged = false;
    if (this.selectedObject == null)
    {
      layoutChanged = true;
    }
    brushPanel.clearSimpleObjects();
    if (selectedObject != null)
    {
      brushPanel.addSimpleObject(selectedObject);
    }
    this.selectedObject = selectedObject;
    fakeEnabled = true;
    updateValues(layoutChanged);
  }

  public void clearSelectedObject()
  {
    brushPanel.clearSimpleObjects();
    selectedObject = null;
    fakeEnabled = false;
    propertiesPanel.removeAll();
  }

  public void defaultValues()
  {
    Source source = Source.getInstance();
    TileMapWrapper map = source.getCurrentMap();
    clearSelectedObject();
    if (map != null)
    {
      TileArray layer = map.getCurrentLayer();
      if (layer != null)
      {
        Class tileClass = layer.getTileClass();
        brushPanel.setSimpleObjectWrapper(ObjectWrapperFactory.getInstance().getObjectWrapper(tileClass));
        brushPanel.setCelWidthAndHeight(map.celWidth, map.celHeight);

        CompoundBrush brush = source.getCurrentBrush();
        if ((brush != null) && (brush.getWidth() == 1) && (brush.getHeight() == 1))
        {
          setSelectedObject(brush.getObject(0, 0));
        }
        else
        {
          setSelectedObject(null);
        }
      }
    }
  }

  public TileEditor getTileEditor()
  {
    return tileEditor;
  }

  public void keyTyped(final KeyEvent e)
  {
    Object source = e.getSource();
    if (source == nameText)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          setSelectedObjectName(nameText.getText());
        }
      });
    }
    else
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          JTextField textField = (JTextField) e.getSource();
          SimpleObjects simpleObjects = Source.getInstance().getSimpleObjects(selectedObject.getObjectClass());
          ObjectField field = simpleObjects.getFieldWithName(textField.getName());
          if (field.getType() == ObjectField.TEXT)
          {
            selectedObject.setProperty(field, textField.getText());
          }
          else
          {
            String number = textField.getText();
            try
            {
              int value = Integer.parseInt(number);
              selectedObject.setProperty(field, value);
            }
            catch (NumberFormatException ex)
            {
              textField.setText(selectedObject.getProperty(field).toString());
            }
          }
        }
      });
    }
  }

  private void setSelectedObjectName(String text)
  {
    if (selectedObject != null)
    {
      selectedObject.setName(text);
    }
  }

  public void keyPressed(KeyEvent e)
  {
  }

  public void keyReleased(KeyEvent e)
  {
  }

  public void updateValues(boolean layoutChanged)
  {
    if (layoutChanged)
    {
      propertiesPanel.removeAll();
      if (selectedObject != null)
      {
        nameText.setText(selectedObject.getName());

        SimpleObjects simpleObjects = Source.getInstance().simpleObjects.get(selectedObject.getObjectClass());

        if (simpleObjects != null)
        {
          java.util.List<ObjectField> fields = simpleObjects.getFields();
          int i;
          for (i = 0; i < fields.size(); i++)
          {
            ObjectField objectField = fields.get(i);
            Object value = selectedObject.getProperty(objectField);
            JCheckBox checkBox = checkBox();
            propertiesPanel.add(checkBox, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, CENTER, HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            propertiesPanel.add(labelLeft(objectField.getName() + ":"), new GridBagConstraints(1, i, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(2, 2, 2, 2), 0, 0));

            JComponent field;
            switch (objectField.getType())
            {
            case ObjectField.TEXT:
              field = textField((String) value);
              field.addKeyListener(this);
              break;
            case ObjectField.NUMBER:
              field = textField((Integer) value);
              field.addKeyListener(this);
              break;
            case ObjectField.BRUSH:
              field = comboBox(simpleObjects.getAllNames(), value);
              ((JComboBox) field).addItemListener(this);
              break;
            default:
              field = new JPanel();
            }
            checkBox.setName(objectField.getName());
            field.setName(objectField.getName());
            if (value == null)
              field.setEnabled(false);
            else
              field.setEnabled(true);

            propertiesPanel.add(field, new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, EAST, HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
          }
          propertiesPanel.add(new JPanel(), new GridBagConstraints(0, i, 3, 1, 0.0, 0.0, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
      }
    }
    else
    {
      if (selectedObject != null)
      {
        nameText.setText(selectedObject.getName());

        SimpleObjects simpleObjects = Source.getInstance().simpleObjects.get(selectedObject.getObjectClass());
        if (simpleObjects != null)
        {
          java.util.List<ObjectField> fields = simpleObjects.getFields();
          int i;
          for (i = 0; i < fields.size(); i++)
          {
            ObjectField objectField = fields.get(i);
            Object value = selectedObject.getProperty(objectField);

            JComponent field = (JComponent) propertiesPanel.getComponent(i * 3 + 2);
            switch (objectField.getType())
            {
            case ObjectField.TEXT:
              ((JTextField) field).setText((String) value);
              break;
            case ObjectField.NUMBER:
              if (value == null)
                ((JTextField) field).setText("");
              else
                ((JTextField) field).setText(value.toString());
              field.addKeyListener(this);
              break;
            case ObjectField.BRUSH:
              ((JComboBox) field).setSelectedItem(value);
              break;
            }
            if (value == null)
              field.setEnabled(false);
            else
              field.setEnabled(true);
          }
        }
      }
    }
  }

  private JCheckBox checkBox()
  {
    JCheckBox field = new JCheckBox();
    field.setPreferredSize(new Dimension(18, field.getPreferredSize().height));
    return field;
  }

  public void itemStateChanged(ItemEvent e)
  {
    JComboBox comboBox = (JComboBox) e.getSource();
    SimpleObjects simpleObjects = Source.getInstance().getSimpleObjects(selectedObject.getObjectClass());
    ObjectField field = simpleObjects.getFieldWithName(comboBox.getName());
    selectedObject.setProperty(field, comboBox.getSelectedItem());
  }
}
