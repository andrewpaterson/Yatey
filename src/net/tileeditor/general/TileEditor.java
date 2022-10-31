package net.tileeditor.general;

import net.engine.GameRunnable;
import net.tileeditor.*;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.integerimporter.IntegerRangeDialog;
import net.tileeditor.paletteimporter.PaletteImporter;
import net.tileeditor.layers.TileArray;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.brushimporter.BrushImportPanel;
import net.tileeditor.mapeditor.MapPanel;
import net.tileeditor.mapeditor.AddLayerDialog;
import net.tileeditor.simpleobjectselector.SimpleObjectSelectPanel;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class TileEditor extends JFrame implements WindowListener, ContainerListener, KeyListener, ActionListener
{
  public static final String MAP_PANEL = "MapPanel";
  public static final String CEL_PANEL = "SimpleObjectSelectPanel";

  protected GameRunnable gameRunnable;
  protected MapPanel mapPanel;
  protected BrushImportPanel brushImportPanel;
  protected SimpleObjectSelectPanel selectPanel;
  protected final JPanel cardPanel;
  protected EditorMenuBar editorMenuBar;
  protected Timer timer;

  public static void main(String[] args)
  {
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    TileEditor tileEditor = new TileEditor();
    tileEditor.setSize(1024, 768);
    //tileEditor.setExtendedState(MAXIMIZED_BOTH);
    tileEditor.setVisible(true);
  }

  public TileEditor() throws HeadlessException
  {
    super("Yet Another Tile Editor (Yatey)");
    addKeyAndContainerListenerRecursively(this);
    addWindowListener(this);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    mapPanel = new MapPanel(this);
    gameRunnable = new GameRunnable(mapPanel.getViewPanel());
    gameRunnable.addGamePanel(mapPanel.getBrushPanel());

    brushImportPanel = new BrushImportPanel(this);
    gameRunnable.addGamePanel(brushImportPanel.getViewPanel());

    selectPanel = new SimpleObjectSelectPanel(this);
    gameRunnable.addGamePanel(selectPanel.getViewPanel());
    gameRunnable.addGamePanel(selectPanel.getBrushPanel());

    cardPanel = new JPanel(new BorderLayout());
    contentPane.add(cardPanel, BorderLayout.CENTER);

    editorMenuBar = new EditorMenuBar(this);
    contentPane.add(editorMenuBar, BorderLayout.NORTH);

    addDefaults();

    showMapPanel();

    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        updateVales();
      }
    });

    int delay = 30000;
    timer = new Timer(delay, this);
    timer.setRepeats(true);
    timer.start();

    if (!autoloadLastProject())
    {
      reset();
    }
  }

  private boolean autoloadLastProject()
  {
    Settings settings = Settings.getInstance();
    if (settings.isAutoloadLastProject())
    {
      String projectName = settings.getLastProjectName();
      if (projectName != null)
      {
        boolean result = Source.getInstance().load(new File(projectName));
        if (!result)
        {
          badLoad(settings, projectName);
          return false;
        }
        TheHold.getInstance().hold(Held.PROJECT_LOADED);
        updateVales();
        return true;
      }
    }
    return false;
  }

  protected void addDefaults()
  {
    Point size = Settings.getInstance().getDefaultMapSize();
    Point celSize = Settings.getInstance().getDefaultCelSize();
    Source.getInstance().addMap("Default", size.x, size.y, celSize.x, celSize.y);
  }

  public void showMapPanel()
  {
    mapPanel.setEnabled(true);
    selectPanel.setEnabled(false);
    brushImportPanel.setEnabled(false);

    cardPanel.removeAll();
    cardPanel.add(mapPanel, BorderLayout.CENTER);
    TileEditor.this.jerk();
    requestFocusInWindow();
  }

  public void showBrushImportPanel(String fileName)
  {
    brushImportPanel.defaultValues(fileName);

    mapPanel.setEnabled(false);
    selectPanel.setEnabled(false);
    brushImportPanel.setEnabled(true);

    cardPanel.removeAll();
    cardPanel.add(brushImportPanel, BorderLayout.CENTER);
    TileEditor.this.jerk();
    requestFocusInWindow();
  }

  private void showBrushImportPanel(List<String> fileNames)
  {
    brushImportPanel.defaultValues(fileNames);

    mapPanel.setEnabled(false);
    selectPanel.setEnabled(false);
    brushImportPanel.setEnabled(true);

    cardPanel.removeAll();
    cardPanel.add(brushImportPanel, BorderLayout.CENTER);
    TileEditor.this.jerk();
    requestFocusInWindow();
  }

  public void showSimpleObjectPanel()
  {
    selectPanel.defaultValues();

    mapPanel.setEnabled(false);
    brushImportPanel.setEnabled(false);
    selectPanel.setEnabled(true);

    cardPanel.removeAll();
    cardPanel.add(selectPanel, BorderLayout.CENTER);
    TileEditor.this.jerk();
    requestFocusInWindow();
  }

  public void updateVales()
  {
    editorMenuBar.updateVales();
    brushImportPanel.updateVales();
    mapPanel.updateValues();
  }

  private void jerk()
  {
    Dimension dimension = getSize();
    setSize(new Dimension(dimension.width - 1, dimension.height - 1));
    setSize(dimension);
  }

  public void windowOpened(WindowEvent e)
  {
  }

  public void windowClosing(WindowEvent e)
  {
    gameRunnable.stopGame();
  }

  public void windowClosed(WindowEvent e)
  {
  }

  public void windowIconified(WindowEvent e)
  {
  }

  public void windowDeiconified(WindowEvent e)
  {
  }

  public void windowActivated(WindowEvent e)
  {
  }

  public void windowDeactivated(WindowEvent e)
  {
  }

  public void stopGame()
  {
    gameRunnable.stopGame();
  }

  public void showMap(String text)
  {
    boolean result = Source.getInstance().setCurrentMap(text);
    if (result)
    {
      updateVales();
      TheHold.getInstance().hold(Held.MAP_SELECTED);
    }
  }

  public BrushImportPanel getCelPanel()
  {
    return brushImportPanel;
  }

  public MapPanel getMapPanel()
  {
    return mapPanel;
  }

  public SimpleObjectSelectPanel getSelectPanel()
  {
    return selectPanel;
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
    if (!((c instanceof JTextArea) || (c instanceof JTextField)))
    {
      c.addKeyListener(this);
    }
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
    int modifiers = e.getModifiers();

    if (keyCode == KeyEvent.VK_ESCAPE)
    {
      if (isSelectPanelVisibile() || isImportPanelVisibile())
      {
        showMapPanel();
      }
    }
    else if (keyCode == KeyEvent.VK_ENTER)
    {
      if (isSelectPanelVisibile())
      {
        selectPanel.getPropertiesPanel().selectBrush();
      }
      else if (isImportPanelVisibile())
      {
        brushImportPanel.getEditorPanel().accept();
      }
    }
    else
    {
      Settings settings = Settings.getInstance();
      String action = settings.getActionForKeypress(keyCode, modifiers);
      if (action != null)
      {
        act(action);
      }
      else
      {
        CompoundBrush compoundBrush = Source.getInstance().getCompoundBrush(keyCode, modifiers);
        if (compoundBrush != null)
        {
          selectBrush(compoundBrush);
          TheHold.getInstance().hold(Held.BRUSH_SELECTED);
        }
      }
    }
  }

  public void selectBrush(CompoundBrush compoundBrush)
  {
    if (compoundBrush != null)
    {
      Source.getInstance().setCurrentBrush(compoundBrush, compoundBrush.getTileMap().getCurrentLayer().getTileClass());
      brushSelected();
    }
  }

  public void brushSelected()
  {
    mapPanel.brushSelected();
  }

  private void act(String action)
  {
    if (action.equals(Settings.MAP_EDITOR))
    {
      showMapPanel();
    }
    else if (action.equals(Settings.BRUSH_SELECTOR))
    {
      showSimpleObjectPanel();
    }
    else if (action.equals(Settings.BRUSH_IMPORT))
    {
      importBrushes();
    }
    else if (action.equals(Settings.PALETTE_IMPORT))
    {
      importPalette();
    }
    else if (action.equals(Settings.INTEGER_IMPORT))
    {
      importIntegers();
    }
    else if (action.equals(Settings.NEXT_LAYER))
    {
      nextLayer();
    }
    else if (action.equals(Settings.PREVIOUS_LAYER))
    {
      previousLayer();
    }
    else if (action.equals(Settings.NEXT_MAP))
    {
      nextMap();
    }
    else if (action.equals(Settings.PREVIOUS_MAP))
    {
      previousMap();
    }
    else if (action.equals(Settings.CYCLE_GRID))
    {
      cycleGrid();
    }
    else if (action.equals(Settings.UNDO))
    {
      undo();
    }
    else if (action.equals(Settings.REDO))
    {
      redo();
    }
    else if (action.equals(Settings.LOAD))
    {
      load();
    }
    else if (action.equals(Settings.SAVE))
    {
      save();
    }
    else if (action.equals(Settings.ADD_LAYER))
    {
      addLayer();
    }
    else if (action.equals(Settings.REMOVE_LAYER))
    {
      removeLayer();
    }
    else if (action.equals(Settings.LAYER_VISIBILITY))
    {
      toggleLayerVisibility();
    }
    else if (action.equals(Settings.MOVE_LAYER_UP))
    {
      moveLayerUp();
    }
    else if (action.equals(Settings.MOVE_LAYER_DOWN))
    {
      moveLayerDown();
    }
    else if (action.equals(Settings.DEFAULT_BRUSH))
    {
      clearBrush();
    }
    else
    {
      throw new RuntimeException("Couldn't find action [" + action + "]");
    }
  }

  public void clearBrush()
  {
    Class tileClass = Source.getInstance().getCurrentMap().getCurrentLayer().getTileClass();
    Source.getInstance().setCurrentBrushObject(tileClass, null);
    getMapPanel().getBrushPanel().resizedBuffer();
    TheHold.getInstance().hold(Held.BRUSH_SELECTED);
  }

  private void toggleLayerVisibility()
  {
    if (isMapPanelVisibile())
    {
      TileMapWrapper map = Source.getInstance().getCurrentMap();
      if (map != null)
      {
        TileArray layer = map.getCurrentLayer();
        if (layer != null)
        {
          layer.toggleVisibility();
          updateVales();
        }
      }
    }
  }

  private void nextLayer()
  {
    if (isMapPanelVisibile())
    {
      TileMapWrapper map = Source.getInstance().getCurrentMap();
      if (map != null)
      {
        boolean result = map.nextLayer();
        if (result)
        {
          brushSelected();
          updateVales();
          TheHold.getInstance().hold(Held.LAYER_SELECTED);
        }
      }
    }
  }

  private void previousLayer()
  {
    if (isMapPanelVisibile())
    {
      TileMapWrapper map = Source.getInstance().getCurrentMap();
      if (map != null)
      {
        boolean result = map.previousLayer();
        if (result)
        {
          brushSelected();
          updateVales();
          TheHold.getInstance().hold(Held.LAYER_SELECTED);
        }
      }
    }
  }

  public void importBrushes()
  {
    final File file = FileChooser.show(this, "Import Brushes", Settings.getInstance().getImportDirectory(), false, "jpg", "gif", "png", "bmp");
    if (file != null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Settings.getInstance().setImportDirectory(file.getParent());
          showBrushImportPanel(file.getAbsolutePath());
        }
      });
    }
  }


  public void importBatchBrushes()
  {
    final File files[] = FileChooser.showMultiSelectLoad(this, "Import Brushes", Settings.getInstance().getImportDirectory(), "jpg", "gif", "png", "bmp");
    if (files != null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Settings.getInstance().setImportDirectory(files[0].getParent());
          java.util.List<String> fileNames = new ArrayList<String>();
          for (File file : files)
          {
            fileNames.add(file.getAbsolutePath());
          }
          showBrushImportPanel(fileNames);
        }
      });
    }
  }

  public void importPalette()
  {
    File file = FileChooser.show(this, "Import Palette", Settings.getInstance().getImportDirectory(), false, "jpg", "gif", "png", "bmp");
    importPalette(file);
  }

  private void importPalette(File file)
  {
    if (file != null)
    {
      Settings.getInstance().setImportDirectory(file.getParent());
      PaletteImporter paletteImporter = new PaletteImporter();
      paletteImporter.importFile(file);
    }
  }

  public void importIntegers()
  {
    IntegerRangeDialog dialog = new IntegerRangeDialog(this);
    dialog.setVisible(true);
  }

  private void nextMap()
  {
    if (isMapPanelVisibile())
    {
      boolean result = Source.getInstance().nextMap();
      if (result)
      {
        brushSelected();
        updateVales();
        TheHold.getInstance().hold(Held.MAP_SELECTED);
      }
    }
  }

  private void previousMap()
  {
    if (isMapPanelVisibile())
    {
      boolean result = Source.getInstance().previousMap();
      if (result)
      {
        brushSelected();
        updateVales();
        TheHold.getInstance().hold(Held.MAP_SELECTED);
      }
    }
  }

  private void cycleGrid()
  {
    if (isMapPanelVisibile())
    {
      Settings.getInstance().cycleGrid();
      Settings.getInstance().save();
    }
  }

  public void removeLayer()
  {
    if (isMapPanelVisibile())
    {
      Source source = Source.getInstance();
      TileMapWrapper map = source.getCurrentMap();
      if (map != null)
      {
        int selectedRow = mapPanel.getLayerPanel().getSelectedRow();
        map.removeLayer(selectedRow);
        source.fixCurrentBrush();
        brushSelected();
        updateVales();
        TheHold.getInstance().hold(Held.LAYERS_CHANGED);
      }
    }
  }

  public void addLayer()
  {
    if (isMapPanelVisibile())
    {
      AddLayerDialog addLayerDialog = new AddLayerDialog(this);
      addLayerDialog.setVisible(true);
      if (addLayerDialog.isAccepted())
      {
        brushSelected();
        updateVales();
        TheHold.getInstance().hold(Held.LAYERS_CHANGED);
      }
    }
  }

  public void moveLayerDown()
  {
    if (isMapPanelVisibile())
    {
      TileMapWrapper map = Source.getInstance().getCurrentMap();
      if (map != null)
      {
        if (map.moveCurrentLayerDown())
        {
          updateVales();
          TheHold.getInstance().hold(Held.LAYERS_CHANGED);
        }
      }
    }
  }

  public void moveLayerUp()
  {
    if (isMapPanelVisibile())
    {
      TileMapWrapper map = Source.getInstance().getCurrentMap();
      if (map != null)
      {
        if (map.moveCurrentLayerUp())
        {
          updateVales();
          TheHold.getInstance().hold(Held.LAYERS_CHANGED);
        }
      }
    }
  }

  public boolean isMapPanelVisibile()
  {
    return cardPanel.getComponent(0) == mapPanel;
  }

  public boolean isImportPanelVisibile()
  {
    return cardPanel.getComponent(0) == brushImportPanel;
  }

  public boolean isSelectPanelVisibile()
  {
    return cardPanel.getComponent(0) == selectPanel;
  }

  private void undo()
  {
    int result = TheHold.getInstance().undo();
    if ((result & Held.UPDATE_VALUES) != 0)
    {
      updateVales();
    }
    if ((result & Held.CENTER_BRUSH) != 0)
    {
      brushSelected();
    }
  }

  private void redo()
  {
    int result = TheHold.getInstance().redo();
    if ((result & Held.UPDATE_VALUES) != 0)
    {
      updateVales();
    }
    if ((result & Held.CENTER_BRUSH) != 0)
    {
      brushSelected();
    }
  }

  public void load()
  {
    final File file = FileChooser.show(this, "Load Project", Settings.getInstance().getProjectDirectory(), false, "xml");
    if (file != null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Settings settings = Settings.getInstance();
          boolean result = Source.getInstance().load(file);
          if (!result)
          {
            badLoad(settings, file.getName());
          }

          TheHold.getInstance().hold(Held.PROJECT_LOADED);
          settings.setLastProjectName(file.getAbsolutePath());
          Settings.getInstance().setSaveDirectory(file.getParent());
          Settings.getInstance().save();
          updateVales();
        }
      });
    }
  }

  private void badLoad(Settings settings, String name)
  {
    System.out.println("Couldn't load map [" + name + "]");
    
    setWarning("Couldn't load map ");
    settings.setLastProjectName(null);
    settings.save();
  }

  public void save()
  {
    Source source = Source.getInstance();
    if (source.getFileName() != null)
    {
      source.save();
    }
    else
    {
      saveAs();
    }
  }

  public void saveAs()
  {
    final File file = FileChooser.show(this, "Save Project", Settings.getInstance().getProjectDirectory(), true, "xml");
    if (file != null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          Source.getInstance().save(file);
          Settings.getInstance().setLastProjectName(file.getAbsolutePath());
          Settings.getInstance().setSaveDirectory(file.getParent());
          Settings.getInstance().save();
        }
      });
    }
  }

  public void resetCameras()
  {
    mapPanel.resetCamera();
  }

  public void actionPerformed(ActionEvent e)
  {
    AutoSaver.getInstance().save();
  }

  public void setWarning(String warning)
  {
    mapPanel.getViewPanel().setWarning(warning);
  }

  public void reset()
  {
    Source.reset();
    addDefaults();
    TheHold.getInstance().hold(Held.MAPS_CHANGED);
    updateVales();
    resetCameras();
    Settings settings = Settings.getInstance();
    settings.setLastProjectName(null);
    settings.save();
  }
}
