package net.engine.file.csv;

import java.util.ArrayList;
import java.util.List;

public class CSVLine
{
  protected List<String> cells;
  protected CSVFile csvFile;

  public CSVLine(CSVFile csvFile)
  {
    this.csvFile = csvFile;
    cells = new ArrayList<String>(40);
  }

  public CSVLine(CSVFile csvFile, String row)
  {
    this(csvFile);
    cells = CSVLineReader.read(row, csvFile.separator);
  }

  public String toString()
  {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < cells.size(); i++)
    {
      String cell = cells.get(i);
      stringBuffer.append(cell);
      if (i != cells.size() - 1)
      {
        stringBuffer.append(csvFile.separator);
      }
    }
    return stringBuffer.toString();
  }

  public void add(String s)
  {
    cells.add(s);
  }

  public List<String> getCells()
  {
    return cells;
  }
}
