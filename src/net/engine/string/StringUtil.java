package net.engine.string;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
  public static List<String> split(String string, String separator)
  {
    List<String> strings = new ArrayList<String>();

    if ((separator.length() > 1) || (separator.length() < 0))
    {
      throw new RuntimeException("Separator[" + separator + "] must be one character long");
    }

    char charSeparator = separator.charAt(0);

    int index = 0;
    for (int i = 0; i < string.length(); i++)
    {
      char c = string.charAt(i);
      if (charSeparator == c)
      {
        strings.add(string.substring(index, i));
        index = i + 1;
      }
    }

    int length = string.length();
    if (index > length)
    {
      strings.add("");
    }
    else
    {
      strings.add(string.substring(index, length));
    }

    return strings;
  }
}
