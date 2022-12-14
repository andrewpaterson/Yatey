package net.engine.parser;

public class StringZero
{
  private char[] values;
  private int offset;

  public StringZero()
  {
    values = new char[0];
    offset = 0;
  }

  public StringZero(StringZero szString, int offset)
  {
    values = szString.values;
    this.offset = offset;
  }

  public StringZero(int length)
  {
    values = new char[length];
    offset = 0;
  }

  public StringZero(char[] text)
  {
    values = new char[text.length + 1];
    System.arraycopy(text, 0, values, 0, text.length);
    setEnd(text.length);
    offset = 0;
  }

  public StringZero(String s)
  {
    copy(s);
  }

  public char get(int index)
  {
    index += offset;

    return values[index];
  }

  public void set(int index, char c)
  {
    index += offset;

    if (index >= this.values.length)
    {
      char[] t = new char[index + 1];
      System.arraycopy(this.values, 0, t, 0, this.values.length);
      this.values = t;
    }
    this.values[index] = c;
  }

  public void setEnd(int index)
  {
    set(index, (char) 0);
  }

  public char[] getValues()
  {
    return values;
  }

  public int length()
  {
    if ((values.length == 0) && (offset == 0))
    {
      return 0;
    }
    for (int i = offset; i < values.length; i++)
    {
      char value = values[i];
      if (value == (char) 0)
      {
        return i;
      }
    }
    throw new ArrayIndexOutOfBoundsException("No end of string zero terminator");
  }

  public String toString()
  {
    if (length() == 0)
    {
      return "";
    }
    return new String(values, offset, length());
  }

  public String toStringFrom(int offset)
  {
    if (length() == 0)
    {
      return "";
    }
    return new String(values, offset, values.length - offset-1);
  }

  public void copy(StringZero source, int start, int end)
  {
    int length = end - start;
    values = new char[length + 1];
    System.arraycopy(source.getValues(), start, values, 0, length);
    setEnd(length);
  }

  public void copy(String s)
  {
    values = new char[s.length()+1];
    offset = 0;
    for (int i = 0; i < s.length(); i++)
    {
      set(i, s.charAt(i));
    }
    setEnd(s.length());
  }
}
