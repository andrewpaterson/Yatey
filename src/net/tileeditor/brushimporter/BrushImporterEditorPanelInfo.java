package net.tileeditor.brushimporter;

import net.engine.cel.CelHelper;
import net.engine.cel.CelSource;
import net.tileeditor.*;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.general.TileEditor;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.source.ObjectWrapperFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BrushImporterEditorPanelInfo extends JPanel implements ActionListener
{
  protected JTextField celWidthText;
  protected JTextField celHeightText;
  protected List<BrushSource> brushSources;
  protected JTextField columns;
  protected JTextField rows;
  protected JTextField leftOffset;
  protected JTextField topOffset;
  protected JTextField verticalSpacing;
  protected JTextField horizontalSpacing;
  protected TileEditor tileEditor;
  protected BrushImportViewPanel brushImportViewPanel;
  protected boolean useSource;
  protected JButton update;
  protected JButton toggle;
  protected JButton accept;
  protected JButton cancel;
  protected List<List<Point>> lastGrids;
  protected ColourButton emptyColourButton;
  protected JCheckBox useEmptyColorCheck;
  protected JCheckBox trimEmptyEdgesCheck;
  protected JCheckBox leftToRightFirstCheck;
  protected boolean selectingColor;
  private final String SELECT_COLOUR = "Set Empty Colour";
  private final String CANCEL_COLOUR = "Cancel Set Colour";

  public BrushImporterEditorPanelInfo(TileEditor tileEditor, BrushImportViewPanel brushImportViewPanel)
  {
    this.tileEditor = tileEditor;
    this.brushImportViewPanel = brushImportViewPanel;
    brushSources = new ArrayList<BrushSource>();
    lastGrids = null;
    selectingColor = false;

    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    Settings settings = Settings.getInstance();
    BrushImportNumbers numbers = settings.getBrushImportNumbers();
    celWidthText = textField(Integer.toString(numbers.celWidth));
    celHeightText = textField(Integer.toString(numbers.celHeight));
    columns = textField(Integer.toString(numbers.columnCount));
    rows = textField(Integer.toString(numbers.rowCount));
    verticalSpacing = textField(Integer.toString(numbers.verticalSpacing));
    horizontalSpacing = textField(Integer.toString(numbers.horizontalSpacing));
    topOffset = textField(Integer.toString(numbers.topOffset));
    leftOffset = textField(Integer.toString(numbers.leftOffset));
    leftToRightFirstCheck = new JCheckBox();
    trimEmptyEdgesCheck = new JCheckBox();
    useEmptyColorCheck = new JCheckBox();
    emptyColourButton = new ColourButton(SELECT_COLOUR, numbers.transparent);

    add(new JPanel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

    int row = 0;
    update = new JButton("Update");
    toggle = new JButton("Toggle");
    add(update, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(toggle, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Cel Width:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(celWidthText, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Cel Height:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(celHeightText, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Left to Right First:"), new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(leftToRightFirstCheck, new GridBagConstraints(6, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Columns:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(columns, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Rows:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(rows, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Trim Empty Edges:"), new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(trimEmptyEdgesCheck, new GridBagConstraints(6, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Left Offset:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(leftOffset, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Top Offset:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(topOffset, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Use Empty Colour:"), new GridBagConstraints(5, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(useEmptyColorCheck, new GridBagConstraints(6, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    row++;
    add(label("Vertical Spacing:"), new GridBagConstraints(1, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(verticalSpacing, new GridBagConstraints(2, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(label("Horizontal Spacing:"), new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(horizontalSpacing, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));
    add(emptyColourButton, new GridBagConstraints(5, row, 2, 1, 0.0, 0.0, EAST, HORIZONTAL, new Insets(2, 10, 2, 2), 0, 0));

    add(new JPanel(), new GridBagConstraints(7, 0, 1, 1, 1.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0));

    accept = new JButton("Accept");
    cancel = new JButton("Cancel");
    add(accept, new GridBagConstraints(8, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 10, 2, 2), 0, 0));
    add(cancel, new GridBagConstraints(9, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(2, 2, 2, 2), 0, 0));

    accept.addActionListener(this);
    cancel.addActionListener(this);
    update.addActionListener(this);
    toggle.addActionListener(this);
    emptyColourButton.addActionListener(this);
    useEmptyColorCheck.addActionListener(this);
  }

  private JTextField textField(String s)
  {
    JTextField field = new JTextField(s);
    field.setPreferredSize(new Dimension(70, field.getPreferredSize().height));

    return field;
  }

  private JLabel label(String s)
  {
    JLabel label = new JLabel(s, JLabel.RIGHT);
    label.setPreferredSize(new Dimension(130, label.getPreferredSize().height));
    return label;
  }

  public void defaultValues(String filename)
  {
    brushSources = new ArrayList<BrushSource>();

    TileMapWrapper map = Source.getInstance().getCurrentMap();
    BrushSource brushSource = new BrushSource(map.celWidth, map.celHeight,
                                              -1, -1, 0, 0, 0, 0, filename, true, false, null);
    brushSources.add(brushSource);

    lastGrids = null;
    updateVales();
    source();
  }

  public void defaultValues(List<String> filenames)
  {
    brushSources = new ArrayList<BrushSource>();

    TileMapWrapper map = Source.getInstance().getCurrentMap();
    for (String filename : filenames)
    {
      BrushSource brushSource = new BrushSource(map.celWidth, map.celHeight, -1, -1, 0, 0, 0, 0, filename, true, false, null);
      brushSources.add(brushSource);
    }

    lastGrids = null;
    updateVales();
    source();
  }

  public void updateVales()
  {
    BrushImportNumbers numbers = Settings.getInstance().getBrushImportNumbers();
    celWidthText.setText(Integer.toString(numbers.celWidth));
    celHeightText.setText(Integer.toString(numbers.celHeight));
    columns.setText(Integer.toString(numbers.columnCount));
    rows.setText(Integer.toString(numbers.rowCount));
    leftOffset.setText(Integer.toString(numbers.leftOffset));
    topOffset.setText(Integer.toString(numbers.topOffset));
    verticalSpacing.setText(Integer.toString(numbers.verticalSpacing));
    horizontalSpacing.setText(Integer.toString(numbers.horizontalSpacing));
    emptyColourButton.setVisible(numbers.transparent != null);
    emptyColourButton.repaint();
    useEmptyColorCheck.setSelected(numbers.transparent != null);
    leftToRightFirstCheck.setSelected(numbers.leftToRightFirst);
    trimEmptyEdgesCheck.setSelected(numbers.trim);
  }

  private void source()
  {
    useSource = true;

    brushImportViewPanel.setSimpleObjectWrapper(ObjectWrapperFactory.getInstance().getObjectWrapper(SimpleBrush.class));
    brushImportViewPanel.clearSimpleObjects();

    int maxWidth = 0;
    int maxHeight = 0;
    for (BrushSource brushSource : brushSources)
    {
      CelHelper celHelper = new CelHelper(brushSource.getFileName());
      CelSource cel = celHelper.getCels().get(0);
      SimpleBrush brush = new SimpleBrush(cel.cel, brushSource, -1);
      brushImportViewPanel.addSimpleObject(brush);

      int width = cel.cel.bufferedImage.getWidth();
      if (width > maxWidth)
      {
        maxWidth = width;
      }
      int height = cel.cel.bufferedImage.getHeight();
      if (height > maxHeight)
      {
        maxHeight = height;
      }
    }

    brushImportViewPanel.setCelWidthAndHeight(maxWidth, maxHeight);
  }

  private void spritefy()
  {
    useSource = false;

    brushImportViewPanel.setSimpleObjectWrapper(ObjectWrapperFactory.getInstance().getObjectWrapper(SimpleBrush.class));
    brushImportViewPanel.clearSimpleObjects();

    int maxWidth = 0;
    int maxHeight = 0;
    lastGrids = new ArrayList<List<Point>>();
    for (BrushSource brushSource : brushSources)
    {
      List<Point> lastGrid = brushSource.convert();
      lastGrids.add(lastGrid);

      for (CelSource cel : brushSource.celSources)
      {
        SimpleBrush brush = new SimpleBrush(cel.cel, brushSource, -1);
        brushImportViewPanel.addSimpleObject(brush);

        int width = cel.cel.bufferedImage.getWidth();
        if (width > maxWidth)
        {
          maxWidth = width;
        }
        int height = cel.cel.bufferedImage.getHeight();
        if (height > maxHeight)
        {
          maxHeight = height;
        }
      }
    }

    brushImportViewPanel.setCelWidthAndHeight(maxWidth, maxHeight);
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == cancel)
    {
      tileEditor.showMapPanel();
    }
    else if (source == accept)
    {
      accept();
    }
    else if (source == toggle)
    {
      scrapeValues();
      toggle();
    }
    else if (source == update)
    {
      scrapeValues();
      spritefy();
    }
    else if (source == emptyColourButton)
    {
      selectingColor = !selectingColor;
      if (selectingColor)
      {
        emptyColourButton.setText(CANCEL_COLOUR);
      }
      else
      {
        emptyColourButton.setText(SELECT_COLOUR);
      }
    }
    else if (source == useEmptyColorCheck)
    {
      if (useEmptyColorCheck.isSelected())
      {
        emptyColourButton.setVisible(true);
      }
      else
      {
        emptyColourButton.setVisible(false);
      }
    }
  }

  private void scrapeValues()
  {
    BrushImportNumbers numbers = Settings.getInstance().getBrushImportNumbers();
    boolean result = numbers.update(celWidthText.getText(), celHeightText.getText(), columns.getText(), rows.getText(), leftOffset.getText(),
                                    topOffset.getText(), verticalSpacing.getText(), horizontalSpacing.getText(), leftToRightFirstCheck.isSelected(),
                                    trimEmptyEdgesCheck.isSelected(), useEmptyColorCheck.isSelected(), emptyColourButton.getColour());
    if (result)
    {
      for (BrushSource brushSource : brushSources)
      {
        brushSource.getNumbers().copy(numbers);
      }
      tileEditor.updateVales();  //Why?
    }
  }

  private void toggle()
  {
    if (useSource)
    {
      spritefy();
    }
    else
    {
      source();
    }
  }

  public void accept()
  {
    scrapeValues();
    Settings.getInstance().save();

    if (lastGrids == null)
    {
      spritefy();
    }
    addBrushes();
    TheHold.getInstance().hold(Held.BRUSH_IMPORTED);
    BrushImporterEditorPanelInfo.this.tileEditor.showMapPanel();
    tileEditor.updateVales();
  }

  private void addBrushes()
  {
    BrushImportNumbers numbers = Settings.getInstance().getBrushImportNumbers();
    for (BrushSource brushSource : brushSources)
    {
      brushSource.getNumbers().setTransparent(numbers.transparent);
    }

    List<CompoundBrush> compoundBrushes = new ArrayList<CompoundBrush>();
    boolean alphabetise = (lastGrids.size() > 40);

    for (int i = 0; i < lastGrids.size(); i++)
    {
      List<Point> lastGrid = lastGrids.get(i);
      int width = 0;
      int height = 0;
      for (Point point : lastGrid)
      {
        if (point.x > width)
        {
          width = point.x;
        }
        if (point.y > height)
        {
          height = point.y;
        }
      }

      width++;
      height++;
      CompoundBrush compoundBrush = new CompoundBrush(width, height, SimpleBrush.class, Source.getInstance());
      compoundBrushes.add(compoundBrush);
      BrushSource brushSource = brushSources.get(i);
      compoundBrush.setName(name(alphabetise, brushSource.getFileName()));
      Source.getInstance().addNoCheckExistingCompoundBrush(compoundBrush);
    }

    Source source = Source.getInstance();
    for (BrushSource brushSource : brushSources)
    {
      source.addBrushSource(brushSource);
    }

    ArrayList<SimpleObject> simpleObjects = brushImportViewPanel.getSimpleObjects();
    ArrayList<Boolean> ignoredObjects = brushImportViewPanel.getIgnoredObjects();

    BrushSource oldBrushSource = null;
    int start = 0;
    for (int i = 0; i < simpleObjects.size(); i++)
    {
      SimpleObject simpleObject = simpleObjects.get(i);
      SimpleBrush simpleBrush = (SimpleBrush) simpleObject.getValue();
      BrushSource brushSource = simpleBrush.getBrushSource();
      if (oldBrushSource != brushSource)
      {
        start = i;
        oldBrushSource = brushSource;
      }
      int index = brushSources.indexOf(brushSource);
      Boolean bool = ignoredObjects.get(i);
      Point point = lastGrids.get(index).get(i - start);
      CompoundBrush compoundBrush = compoundBrushes.get(index);
      TileArray brushTileArray = compoundBrush.getTileMap().getCurrentLayer();
      if (!bool)
      {
        simpleObject.setName(name(i, simpleObjects.size(), brushSource.getFileName()));
        simpleBrush.setId(Source.getInstance().nextSimpleBrushId());
        source.addSimpleObject(simpleObject);

        brushTileArray.setObject(point.x, point.y, simpleObject);
      }
    }

    CompoundBrush compoundBrush = compoundBrushes.get(0);
    if (source.isLayerValid())
    {
      Class brushClass = SimpleBrush.class;
      if (source.getCurrentMap().getCurrentLayer().getTileClass() == brushClass)
      {
        source.setCurrentBrush(compoundBrush, brushClass);
      }
      else
      {
        source.setPreviousBrush(compoundBrush, brushClass);
      }
    }
  }

  private String name(int index, int total, String pathName)
  {
    String fileName = name(pathName);
    String totalString = Integer.toString(total);

    StringBuffer stringBuffer = new StringBuffer(fileName);
    stringBuffer.append(" ");
    String indexString = Integer.toString(index + 1);
    for (int i = 0; i < totalString.length() - indexString.length(); i++)
    {
      stringBuffer.append('0');
    }
    stringBuffer.append(indexString);
    return stringBuffer.toString();
  }

  private String name(boolean alphabetise, String pathName)
  {
    String name = name(pathName);
    if (alphabetise)
    {
      char c = name.charAt(0);
      c = Character.toUpperCase(c);
      name = new StringBuilder().append("Import.").append(c).append(".").append(name).toString();
    }
    else
    {
      name = "Import." + name;
    }
    return name;
  }

  private String name(String pathName)
  {
    File file = new File(pathName);

    String fileName = file.getName();
    int lastIndex = fileName.lastIndexOf('.');
    if (lastIndex != -1)
    {
      fileName = fileName.substring(0, lastIndex);
    }
    return fileName;
  }

  public boolean isSelectingColor()
  {
    return selectingColor;
  }

  public void setEmptyColor(int rgb)
  {
    Color color = new Color(rgb);
    emptyColourButton.setColor(color);
    emptyColourButton.setText(SELECT_COLOUR);
    emptyColourButton.repaint();
    selectingColor = false;
  }
}
