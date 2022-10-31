package net.engine.file.csv;

import java.util.ArrayList;
import java.util.List;

public class CSVFile
{
  protected List<CSVLine> lines;
  protected char separator;

  public CSVFile(char separator)
  {
    this.separator = separator;
    lines = new ArrayList<CSVLine>();
  }

  public void addRow(String row)
  {
    lines.add(new CSVLine(this, row));
  }

  public CSVLine addRow()
  {
    CSVLine csvLine = new CSVLine(this);
    lines.add(csvLine);
    return csvLine;
  }

  public CSVLine getRow(int i)
  {
    return lines.get(i);
  }

  public int size()
  {
    return lines.size();
  }

  public void addRows(List<String> rows)
  {
    for (String row : rows)
    {
      addRow(row);
    }
  }
}
