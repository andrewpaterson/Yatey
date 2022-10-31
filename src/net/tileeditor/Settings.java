package net.tileeditor;

import net.engine.file.TextFile;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLFile;
import net.engine.file.xml.XMLReader;
import net.engine.file.xml.XMLTag;
import net.tileeditor.keybindings.ActionKeyBinding;
import net.tileeditor.keybindings.KeyBinding;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Settings
{
  public static Settings instance = null;

  public static final int GRID_ONTOP = 0;
  public static final int GRID_BENEATH = 1;
  public static final int GRID_NONE = 2;

  public static final String GRID_ONTOP_TEXT = "Ontop";
  public static final String GRID_BENEATH_TEXT = "Beneath";
  public static final String GRID_NONE_TEXT = "None";

  public static final String MAP_EDITOR = "Map Editor";
  public static final String BRUSH_SELECTOR = "Brush Selector";
  public static final String BRUSH_IMPORT = "Import Brush";
  public static final String PALETTE_IMPORT = "Import Palette";
  public static final String INTEGER_IMPORT = "Import Integers";
  public static final String NEXT_LAYER = "Next Layer";
  public static final String PREVIOUS_LAYER = "Previous Layer";
  public static final String NEXT_MAP = "Next Map";
  public static final String PREVIOUS_MAP = "Previous Map";
  public static final String CYCLE_GRID = "Cycle Grid";
  public static final String UNDO = "Undo";
  public static final String REDO = "Redo";
  public static final String LAYER_VISIBILITY = "Layer Visibility";
  public static final String ADD_LAYER = "Add Layer";
  public static final String REMOVE_LAYER = "Remove Layer";
  public static final String MOVE_LAYER_UP = "Move Layer Up";
  public static final String MOVE_LAYER_DOWN = "Move Layer Down";
  public static final String LOAD = "Load";
  public static final String SAVE = "Save";
  public static final String DEFAULT_BRUSH = "Default Brush";

  public static final String[] actionStrings = new String[]{MAP_EDITOR, BRUSH_SELECTOR, BRUSH_IMPORT, PALETTE_IMPORT, INTEGER_IMPORT, NEXT_LAYER, PREVIOUS_LAYER,
      NEXT_MAP, PREVIOUS_MAP, CYCLE_GRID, UNDO, REDO, LAYER_VISIBILITY, ADD_LAYER, REMOVE_LAYER, MOVE_LAYER_UP, MOVE_LAYER_DOWN, LOAD, SAVE, DEFAULT_BRUSH};

  public static final String settingsFileName = "Settings.xml";

  protected String importDirectory;
  protected String saveDirectory;
  protected int gridStyle;
  protected boolean holdVisible;
  protected String version;
  protected Map<String, KeyBinding> keyBindings;
  protected String lastProjectName;
  protected boolean autoloadLastProject;
  protected BrushImportNumbers brushImportNumbers;
  protected int mapCelWidth;
  protected int mapCelHeight;
  protected int mapWidth;
  protected int mapHeight;
  protected String textureDirectory;

  public Settings()
  {
    importDirectory = null;
    saveDirectory = null;
    gridStyle = GRID_ONTOP;
    holdVisible = true;
    version = "1.1";
    brushImportNumbers = new BrushImportNumbers();
    autoloadLastProject = true;
    Point defaultMapSize = Source.getInstance().getDefaultMapSize();
    mapWidth = defaultMapSize.x;
    mapHeight = defaultMapSize.y;
    Point defaultCelSize = Source.getInstance().getDefaultCelSize();
    mapCelWidth = defaultCelSize.x;
    mapCelHeight = defaultCelSize.y;
    textureDirectory = System.getProperty("user.dir");
    defaultKeybindings();

    if (!load())
    {
      save();
    }
  }

  private void defaultKeybindings()
  {
    clearKeyBindings();

    setKeyBinding(MAP_EDITOR, KeyEvent.VK_F1, 0);
    setKeyBinding(BRUSH_SELECTOR, KeyEvent.VK_SPACE, 0);
    setKeyBinding(BRUSH_IMPORT, KeyEvent.VK_F2, 0);
    setKeyBinding(PALETTE_IMPORT, KeyEvent.VK_F3, 0);
    setKeyBinding(INTEGER_IMPORT, KeyEvent.VK_F4, 0);
    setKeyBinding(NEXT_LAYER, KeyEvent.VK_2, 0);
    setKeyBinding(PREVIOUS_LAYER, KeyEvent.VK_1, 0);
    setKeyBinding(NEXT_MAP, KeyEvent.VK_4, 0);
    setKeyBinding(PREVIOUS_MAP, KeyEvent.VK_3, 0);
    setKeyBinding(CYCLE_GRID, KeyEvent.VK_G, 0);
    setKeyBinding(UNDO, KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
    setKeyBinding(REDO, KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
    setKeyBinding(ADD_LAYER, KeyEvent.VK_EQUALS, KeyEvent.CTRL_MASK);
    setKeyBinding(REMOVE_LAYER, KeyEvent.VK_MINUS, KeyEvent.CTRL_MASK);
    setKeyBinding(LAYER_VISIBILITY, KeyEvent.VK_BACK_QUOTE, 0);
    setKeyBinding(MOVE_LAYER_UP, KeyEvent.VK_OPEN_BRACKET, KeyEvent.CTRL_MASK);
    setKeyBinding(MOVE_LAYER_DOWN, KeyEvent.VK_CLOSE_BRACKET, KeyEvent.CTRL_MASK);
    setKeyBinding(LOAD, KeyEvent.VK_L, KeyEvent.CTRL_MASK);
    setKeyBinding(SAVE, KeyEvent.VK_S, KeyEvent.CTRL_MASK);
    setKeyBinding(DEFAULT_BRUSH, KeyEvent.VK_E, 0);

    getActions();
  }

  public void setKeyBinding(String action, int keyCode, int modifiers)
  {
    keyBindings.put(action, new ActionKeyBinding(action, keyCode, modifiers));
  }

  private boolean load()
  {
    List<String> lines = new TextFile().read(new File(settingsFileName));
    if (lines == null)
    {
      return false;
    }

    XMLFile xmlFile = XMLReader.read(lines);
    XMLBody body = xmlFile.getBody();
    XMLBody settingsBody = (XMLBody) body.getData("Settings");
    version = (String) settingsBody.getData("Version");
    lastProjectName = (String) settingsBody.getData("ProjectName");
    importDirectory = (String) settingsBody.getData("ImportDirectory");
    saveDirectory = (String) settingsBody.getData("SaveDirectory");
    gridStyle = gridStyleFromString((String) settingsBody.getData("gridStyle"));
    holdVisible = Boolean.parseBoolean((String) settingsBody.getData("HoldVisible"));
    autoloadLastProject = Boolean.parseBoolean((String) settingsBody.getData("AutoloadProject"));
    XMLTag numbersTag = settingsBody.getTag("Numbers");
    brushImportNumbers = new BrushImportNumbers(numbersTag);

    mapCelWidth = Integer.parseInt((String) settingsBody.getData("MapCelWidth"));
    mapCelHeight = Integer.parseInt((String) settingsBody.getData("MapCelHeight"));
    mapWidth = Integer.parseInt((String) settingsBody.getData("MapWidth"));
    mapHeight = Integer.parseInt((String) settingsBody.getData("MapHeight"));
    textureDirectory = (String) settingsBody.getData("TexturePath");

    loadKeyBindings(settingsBody);

    return true;
  }

  public void save()
  {
    XMLFile xmlFile = new XMLFile();
    XMLTag tag = xmlFile.getBody().put("Settings");

    XMLBody settingsBody = tag.setData();
    settingsBody.put("Version", version);
    settingsBody.put("ProjectName", lastProjectName);
    settingsBody.put("ImportDirectory", importDirectory);
    settingsBody.put("SaveDirectory", saveDirectory);
    settingsBody.put("gridStyle", gridStyleAsString());
    settingsBody.put("HoldVisible", Boolean.toString(holdVisible));
    settingsBody.put("AutoloadProject", Boolean.toString(autoloadLastProject));
    settingsBody.put("MapCelWidth", Integer.toString(mapCelWidth));
    settingsBody.put("MapCelHeight", Integer.toString(mapCelHeight));
    settingsBody.put("MapWidth", Integer.toString(mapWidth));
    settingsBody.put("MapHeight", Integer.toString(mapHeight));
    settingsBody.put("TexturePath", textureDirectory);

    XMLTag numbersTag = settingsBody.put("Numbers");
    brushImportNumbers.save(numbersTag);

    saveKeybindings(settingsBody);

    File file = new File(settingsFileName);
    TextFile textFile = new TextFile();
    List<String> lines = xmlFile.toLines();
    textFile.addLines(lines);
    textFile.write(file);
  }

  private void loadKeyBindings(XMLBody body)
  {
    XMLBody keyBindingBody = (XMLBody) body.getData("KeyBindings");
    List<XMLTag> tags = keyBindingBody.getTags("KeyBinding");

    clearKeyBindings();
    for (XMLTag tag : tags)
    {
      try
      {
        ActionKeyBinding binding = new ActionKeyBinding(tag);
        keyBindings.put(binding.getAction(), binding);
      }
      catch (RuntimeException e)
      {

      }
    }
  }

  private void saveKeybindings(XMLBody body)
  {
    XMLTag tag = body.put("KeyBindings");
    XMLBody keyBindingBody = tag.setData();

    for (String action : keyBindings.keySet())
    {
      KeyBinding binding = keyBindings.get(action);
      if (binding != null)
      {
        binding.save(keyBindingBody);
      }
    }
  }

  private int gridStyleFromString(String s)
  {
    if (s.equals(GRID_ONTOP_TEXT))
    {
      return GRID_ONTOP;
    }
    if (s.equals(GRID_BENEATH_TEXT))
    {
      return GRID_BENEATH;
    }
    if (s.equals(GRID_NONE_TEXT))
    {
      return GRID_NONE;
    }
    throw new RuntimeException("Unknown grid style");
  }

  public static Settings getInstance()
  {
    if (instance == null)
    {
      instance = new Settings();
    }
    return instance;
  }

  public String getImportDirectory()
  {
    return importDirectory;
  }

  public void setImportDirectory(String importDirectory)
  {
    this.importDirectory = importDirectory;
    save();
  }

  public String getProjectDirectory()
  {
    return saveDirectory;
  }

  public void setSaveDirectory(String saveDirectory)
  {
    this.saveDirectory = saveDirectory;
  }

  public int getGridStyle()
  {
    return gridStyle;
  }

  public void setGridStyle(int gridStyle)
  {
    this.gridStyle = gridStyle;
    save();
  }

  public KeyBinding getKeyBinding(String action)
  {
    return keyBindings.get(action);
  }

  public Set<String> getActions()
  {
    return keyBindings.keySet();
  }

  public void clearKeyBindings()
  {
    keyBindings = new LinkedHashMap<String, KeyBinding>();
    for (String actionString : actionStrings)
    {
      keyBindings.put(actionString, null);
    }
  }

  public void setKeyBinding(String action, KeyBinding binding)
  {
    keyBindings.put(action, binding);
  }

  public String getActionForKeypress(int keyCode, int modifiers)
  {
    for (String action : keyBindings.keySet())
    {
      KeyBinding binding = keyBindings.get(action);
      if (binding != null)
      {
        if (binding.equals(keyCode, modifiers))
        {
          return action;
        }
      }
    }
    return null;
  }

  public void cycleGrid()
  {
    gridStyle++;
    if (gridStyle > 2)
    {
      gridStyle = 0;
    }
  }

  public boolean isHoldVisible()
  {
    return holdVisible;
  }

  public void toggleHoldVisibility()
  {
    holdVisible = !holdVisible;
  }

  public String gridStyleAsString()
  {
    switch (gridStyle)
    {
    case GRID_ONTOP:
      return GRID_ONTOP_TEXT;
    case GRID_BENEATH:
      return GRID_BENEATH_TEXT;
    case GRID_NONE:
      return GRID_NONE_TEXT;
    }
    return "";
  }

  public BrushImportNumbers getBrushImportNumbers()
  {
    return brushImportNumbers;
  }

  public void setLastProjectName(String lastProjectName)
  {
    this.lastProjectName = lastProjectName;
  }

  public void setAutoloadLastProject(boolean autoloadLastProject)
  {
    this.autoloadLastProject = autoloadLastProject;
  }

  public boolean isAutoloadLastProject()
  {
    return autoloadLastProject;
  }

  public String getLastProjectName()
  {
    return lastProjectName;
  }

  public String getVersion()
  {
    return version;
  }

  public void setMapSize(int celWidth, int celHeight, int width, int height)
  {
    mapCelWidth = celWidth;
    mapCelHeight = celHeight;
    mapWidth = width;
    mapHeight = height;
  }

  public Point getDefaultMapSize()
  {
    return new Point(mapWidth, mapHeight);
  }

  public Point getDefaultCelSize()
  {
    return new Point(mapCelWidth, mapCelHeight);
  }

  public String getTextureDirectory()
  {
    return textureDirectory;
  }

  public void setTextureDirectory(String textureDirectory)
  {
    this.textureDirectory = textureDirectory;
  }
}
