package net.engine.file.xml;

import java.util.List;
import java.util.ArrayList;

public class XMLFile
{
  public static final String charsetName = "ISO_8859-1";

  private XMLBody body;

  public XMLFile()
  {
    body = new XMLBody(null);
  }

  public XMLBody getBody()
  {
    return body;
  }

  public static String header()
  {
    return "<?xml version=\"1.0\" encoding=\"" + charsetName + "\" ?>";
  }

  public String toString()
  {
    StringBuffer stringBuffer = new StringBuffer();
    List<String> stringList = toLines();
    for (String s : stringList)
    {
      stringBuffer.append(s);
      stringBuffer.append("\n");
    }
    return stringBuffer.toString();
  }

  public List<String> toLines()
  {
    List<String> lines = new ArrayList<String>();
    lines.add(header());
    lines.addAll(body.toXML());
    return lines;
  }
}
