package net.engine.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFile
{
  protected List<String> rows;

  public TextFile()
  {
    clearLines();
  }

  public void clearLines()
  {
    rows = new ArrayList<String>();
  }

  public List<String> read(InputStream inputStream)
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    readRows(reader);
    return rows;
  }

  public List<String> read(File file)
  {
    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new FileReader(file));
    }
    catch (FileNotFoundException e)
    {
      return null;
    }
    readRows(reader);
    return rows;
  }

  private void readRows(BufferedReader reader)
  {
    try
    {
      String line = reader.readLine();

      while (line != null)
      {
        rows.add(line);
        line = reader.readLine();
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public List<String> getLines()
  {
    return rows;
  }

  public void addLine(String text)
  {
    rows.add(text);
  }

  public void addLines(List<String> lines)
  {
    rows.addAll(lines);
  }


  public void write(File file)
  {
    Writer output = null;
    try
    {
      if (!file.exists())
      {
        file.createNewFile();
      }
      output = new BufferedWriter(new FileWriter(file));
      for (String line : rows)
      {
        output.write(line);
        output.write("\n");
      }

    }
    catch (IOException e)
    {
    }
    finally
    {
      if (output != null)
      {
        try
        {
          output.close();
        }
        catch (IOException e)
        {
        }
      }
    }
  }
}
