package net.tileeditor.simpleobjectselector;

import net.tileeditor.SimpleObject;
import net.tileeditor.SimpleObjects;
import net.tileeditor.Source;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.source.ObjectWrapperFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleObjectSelectPanelInfo extends JPanel implements ActionListener
{
  private TileEditor tileEditor;
  private SimpleObjectSelectViewPanel viewPanel;
  private JButton emptyBrush;
  private JButton cancel;
  private JButton nullBrush;

  public SimpleObjectSelectPanelInfo(TileEditor tileEditor, SimpleObjectSelectViewPanel viewPanel)
  {
    this.tileEditor = tileEditor;
    this.viewPanel = viewPanel;
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    int col = 0;
    nullBrush = new JButton("Null Brush");
    add(nullBrush, new GridBagConstraints(col++, 0, 1, 1, 0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label(" affects ALL layers - make sure you know what you're doing."), new GridBagConstraints(col++, 0, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(new JPanel(), new GridBagConstraints(col++, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    emptyBrush = new JButton("Clear Brush");
    add(emptyBrush, new GridBagConstraints(col++, 0, 1, 1, 0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    cancel = new JButton("Cancel");
    add(cancel, new GridBagConstraints(col, 0, 1, 1, 0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    emptyBrush.addActionListener(this);
    cancel.addActionListener(this);
    nullBrush.addActionListener(this);
  }

  private JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.LEFT);
    label.setPreferredSize(new Dimension(400, label.getPreferredSize().height));
    return label;

  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == cancel)
    {
      tileEditor.showMapPanel();
    }
    else if (source == emptyBrush)
    {
      tileEditor.clearBrush();
      tileEditor.showMapPanel();
    }
    else if (source == nullBrush)
    {
      Source.getInstance().setNullBrush();
      tileEditor.showMapPanel();
    }
  }

  public boolean defaultValues()
  {
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    if (map != null)
    {
      TileArray layer = map.getCurrentLayer();
      if (layer != null)
      {
        Class tileClass = layer.getTileClass();
        if (tileClass != null)
        {
          viewPanel.defaultValues(tileClass);
          spritefy();
          return true;
        }
      }
    }
    return false;
  }

  private void spritefy()
  {
    Class tileClass = Source.getInstance().getCurrentMap().getCurrentLayer().getTileClass();
    viewPanel.setSimpleObjectWrapper(ObjectWrapperFactory.getInstance().getObjectWrapper(tileClass));
    viewPanel.clearSimpleObjects();
    TileMapWrapper map = Source.getInstance().getCurrentMap();
    viewPanel.setCelWidthAndHeight(map.celWidth, map.celHeight);

    SimpleObjects objects = Source.getInstance().getSimpleObjects(tileClass);
    if (objects != null)
    {
      for (SimpleObject object : objects.getSimpleObjects())
      {
        viewPanel.addSimpleObject(object);
      }
    }
  }
}
