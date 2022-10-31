package net.tileeditor.general;

import net.engine.string.StringUtil;
import net.tileeditor.Held;
import net.tileeditor.Settings;
import net.tileeditor.Source;
import net.tileeditor.TheHold;
import net.tileeditor.brush.CompoundBrush;
import net.tileeditor.keybindings.KeyBinding;
import net.tileeditor.layers.TileMapWrapper;
import net.tileeditor.settings.KeyboardBindingsDialog;
import net.tileeditor.settings.ProjectSettingsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditorMenuBar extends JMenuBar
{
  private TileEditor tileEditor;
  public JMenu selectMapMenu;
  public JMenu boundBrush;
  public JMenu namedBrush;

  public EditorMenuBar(TileEditor tileEditor)
  {
    this.tileEditor = tileEditor;
    add(createFileMenu());
    add(createImportMenu());
    add(createMapMenu());
    add(createBrushMenu());
    add(createSettingsMenu());
  }

  private JMenu createBrushMenu()
  {
    JMenu mapMenu = new JMenu("Brush");
    namedBrush = new JMenu("Select named");
    mapMenu.add(namedBrush);
    boundBrush = new JMenu("Select bound");
    mapMenu.add(boundBrush);
    return mapMenu;
  }

  private JMenu createMapMenu()
  {
    JMenu mapMenu = new JMenu("Maps");
    selectMapMenu = new JMenu("Select");
    mapMenu.add(selectMapMenu);
    JMenuItem addItem = new JMenuItem("Add...");
    addItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        addMap();
      }
    });
    mapMenu.add(addItem);
    JMenuItem deleteCurrentItem = new JMenuItem("Delete Current");
    deleteCurrentItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        deleteCurrentMap();
      }
    });
    mapMenu.add(deleteCurrentItem);
//    JMenuItem writePng = new JMenuItem("Write PNG...");
//    writePng.addActionListener(new ActionListener()
//    {
//      public void actionPerformed(ActionEvent e)
//      {
//        deleteCurrentMap();
//      }
//    });
//    mapMenu.add(writePng);
    return mapMenu;
  }

  private void deleteCurrentMap()
  {
    Source.getInstance().deleteCurrentMap();
    TheHold.getInstance().hold(Held.MAPS_CHANGED);
    tileEditor.updateVales();
  }

  private void addMap()
  {
    AddMapDialog dialog = new AddMapDialog(tileEditor);
    dialog.setVisible(true);
    if (dialog.isAccepted())
    {
      TheHold.getInstance().hold(Held.MAPS_CHANGED);
      tileEditor.updateVales();
    }
  }

  private JMenu createSettingsMenu()
  {
    JMenu settingsMenu = new JMenu("Settings");
    JMenuItem cellSizeItem = new JMenuItem("Project...");
    cellSizeItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ProjectSettingsDialog dialog = new ProjectSettingsDialog(tileEditor);
        dialog.setVisible(true);
      }
    });
    settingsMenu.add(cellSizeItem);
    JMenuItem keyboardItem = new JMenuItem("Keyboard...");
    keyboardItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        KeyboardBindingsDialog dialog = new KeyboardBindingsDialog(tileEditor);
        dialog.setVisible(true);
      }
    });
    settingsMenu.add(keyboardItem);
    JMenuItem holdItem = new JMenuItem("The Hold Visibility");
    holdItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        Settings instance = Settings.getInstance();
        instance.toggleHoldVisibility();
        instance.save();
      }
    });
    settingsMenu.add(holdItem);
    return settingsMenu;
  }

  private JMenu createImportMenu()
  {
    JMenu importMenu = new JMenu("Import");
    JMenuItem batchBrushesItem = new JMenuItem("Brushes...");
    importMenu.add(batchBrushesItem);
    batchBrushesItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.importBatchBrushes();
      }
    });

    JMenuItem paletteItem = new JMenuItem("Palette...");
    importMenu.add(paletteItem);
    paletteItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.importPalette();
      }
    });

    JMenuItem integerItem = new JMenuItem("Integers...");
    importMenu.add(integerItem);
    integerItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.importIntegers();
      }
    });

    return importMenu;
  }

  private JMenu createFileMenu()
  {
    JMenu fileMenu = new JMenu("File");
    JMenuItem newItem = new JMenuItem("New");
    fileMenu.add(newItem);
    newItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.reset();
      }
    });
    JMenuItem openItem = new JMenuItem("Open...");
    fileMenu.add(openItem);
    openItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.load();
      }
    });
    JMenuItem saveItem = new JMenuItem("Save");
    fileMenu.add(saveItem);
    saveItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.save();
      }
    });
    JMenuItem saveAsItem = new JMenuItem("Save As...");
    fileMenu.add(saveAsItem);
    saveAsItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.saveAs();
      }
    });
    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        tileEditor.stopGame();
      }
    });
    fileMenu.add(exitItem);
    return fileMenu;
  }

  public void updateVales()
  {
    Source source = Source.getInstance();

    selectMapMenu.removeAll();
    List<TileMapWrapper> tileMaps = source.mapWrappers;
    for (TileMapWrapper tileMap : tileMaps)
    {
      JMenuItem menuItem = new JMenuItem(tileMap.name);
      selectMapMenu.add(menuItem);
      menuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JMenuItem source = (JMenuItem) e.getSource();
          tileEditor.showMap(source.getText());
        }
      });
    }

    namedBrush.removeAll();
    List<String> brushNames = source.getCompoundBrushNames();
    Map menuMap = mapify(brushNames);
    JMenu menu = namedBrush;
    recurse(menuMap, menu, "");

    boundBrush.removeAll();
    List<String> brushBindings = source.getCompoundBrushBindngs();
    for (String name : brushBindings)
    {
      JMenuItem menuItem = new JMenuItem(name);
      boundBrush.add(menuItem);
      menuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JMenuItem source = (JMenuItem) e.getSource();
          String bindingText = source.getText();
          KeyBinding binding = new KeyBinding(bindingText);
          CompoundBrush compoundBrush = Source.getInstance().getCompoundBrush(binding.getKeyCode(), binding.getModifiers());
          tileEditor.selectBrush(compoundBrush);
          TheHold.getInstance().hold(Held.BRUSH_SELECTED);
        }
      });
    }
  }

  private void recurse(Map map, JMenu menu, String path)
  {
    for (Object o : map.keySet())
    {
      String name = (String) o;
      String fullPath;
      if (path.length() == 0)
      {
        fullPath = name;
      }
      else
      {
        fullPath = path + "." + name;
      }

      Map childMap = (Map) map.get(name);
      if (childMap.size() == 0)
      {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setName(fullPath);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            JMenuItem source = (JMenuItem) e.getSource();
            tileEditor.selectBrush(Source.getInstance().getCompoundBrush(source.getName()));
            TheHold.getInstance().hold(Held.BRUSH_SELECTED);
          }
        });
      }
      else
      {
        JMenu childMenu = new JMenu(name);
        menu.add(childMenu);
        recurse(childMap, childMenu, fullPath);
      }
    }
  }

  private Map mapify(List<String> brushNames)
  {
    Map menuMap = new LinkedHashMap();
    for (String name : brushNames)
    {
      Map map = menuMap;
      List<String> names = StringUtil.split(name, ".");
      for (String singleName : names)
      {
        Map childMap = (Map) map.get(singleName);
        if (childMap == null)
        {
          childMap = new LinkedHashMap();
          map.put(singleName, childMap);
        }
        map = childMap;
      }
    }
    return menuMap;
  }
}
