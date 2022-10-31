package net.tileeditor;

import net.engine.file.FileUtil;

public class AutoSaver
{
  public static AutoSaver instance = null;

  protected int maxSaves;

  public AutoSaver()
  {
    maxSaves = 200;
  }

  public static AutoSaver getInstance()
  {
    if (instance == null)
    {
      instance = new AutoSaver();
    }
    return instance;
  }

  public void save()
  {
    FileUtil.makeDirectory("autosave");
    long first = 0;
    int fileNumber = -1;
    for (int i = 0; i < maxSaves; i++)
    {
      String fileName = "autosave/" + Integer.toString(i) + ".xml";
      long l = FileUtil.lastModifiedTime(fileName);
      if (l == 0)
      {
        fileNumber = i;
        break;
      }
      else
      {
        if (first == 0)
        {
          first = l;
          fileNumber = i;
        }
        else if (l < first)
        {
          first = l;
          fileNumber = i;
        }
      }
    }
    String fileName = "autosave/" + Integer.toString(fileNumber) + ".xml";
    Source.getInstance()._save(fileName);
  }

  public void setMaxSaves(int maxSaves)
  {
    this.maxSaves = maxSaves;
  }

  public int getMaxSaves()
  {
    return maxSaves;
  }
}
