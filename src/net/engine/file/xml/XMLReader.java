package net.engine.file.xml;

import net.engine.parser.ParseResult;
import static net.engine.parser.ParseResult.*;
import net.engine.parser.StringZero;
import net.engine.parser.TextParser;

import java.util.ArrayList;
import java.util.List;

public class XMLReader
{
  private List<String> rows;
  public TextParser textParser;
  public XMLFile xml;
  public XMLTag openTag;
  private List<String> strings;

  public XMLReader(List<String> rows)
  {
    this.rows = rows;
  }

  public static XMLFile read(List<String> rows)
  {
    XMLReader reader = new XMLReader(rows);
    return reader.read();
  }

  public XMLFile read()
  {
    xml = new XMLFile();
    openTag = null;
    strings = null;
    for (int i = 1; i < rows.size(); i++)
    {
      String row = rows.get(i);
      ParseResult result = readLine(row);
      if (result != TRUE)
      {
        return null;
      }
    }
    return xml;
  }

  private ParseResult readLine(String row)
  {
    textParser = new TextParser(row);
    textParser.setCppStyleCommentsAsWhiteSpace(false);
    boolean foundAnything = false;
    for (; ;)
    {
      StringZero tagName = new StringZero();
      ParseResult result = readEmptyTag(tagName);
      if (result == TRUE)
      {
        openTag(tagName.toString());
        closeTag();
        foundAnything = true;
        continue;
      }
      else if (result == ERROR)
      {
        return ERROR;
      }

      result = readOpenTag(tagName);
      if (result == TRUE)
      {
        openTag(tagName.toString());
        foundAnything = true;
        continue;
      }
      else if (result == ERROR)
      {
        return ERROR;
      }

      if (openTag != null)
      {
        result = readCloseTag(openTag.getName());
        if (result == TRUE)
        {
          closeTag();
          foundAnything = true;
          continue;
        }
        else if (result == ERROR)
        {
          return ERROR;
        }

        StringZero data = new StringZero();
        result = readData(data);
        if ((result == TRUE) || (result == EOF))
        {
          if (strings == null)
          {
            strings = new ArrayList<String>();
          }
          if (data.length() > 0)
          {
            foundAnything = true;
            strings.add(data.toString());
          }
          if (result == TRUE)
          {
            continue;
          }
          else if (result == EOF)
          {
            return TRUE;
          }
        }
      }
      if (!foundAnything)
      {
        strings.add("");
      }
      return TRUE;
    }
  }

  private ParseResult readData(StringZero data)
  {
    StringBuffer stringBuffer = new StringBuffer();
    StringZero stringZero = new StringZero();
    for (; ;)
    {
      ParseResult result = textParser.getCharacter(stringZero);
      if (result == ERROR)
      {
        data.copy(stringBuffer.toString());
        return EOF;
      }

      char c = stringZero.get(0);
      if (c == '&')
      {
        ParseResult escapeCode = textParser.getExactIdentifier("amp");
        if (escapeCode == TRUE)
        {
          stringBuffer.append('&');
          continue;
        }

        escapeCode = textParser.getExactIdentifier("gt");
        if (escapeCode == TRUE)
        {
          stringBuffer.append('>');
          continue;
        }

        escapeCode = textParser.getExactIdentifier("apos");
        if (escapeCode == TRUE)
        {
          stringBuffer.append('\'');
          continue;
        }

        escapeCode = textParser.getExactIdentifier("quot");
        if (escapeCode == TRUE)
        {
          stringBuffer.append('"');
          continue;
        }

        escapeCode = textParser.getExactIdentifier("lt");
        if (escapeCode == TRUE)
        {
          stringBuffer.append('<');
          continue;
        }

        return ERROR;
      }
      else
      {
        if (c == '<')
        {
          textParser.stepLeft();
          data.copy(stringBuffer.toString());
          return TRUE;
        }
        stringBuffer.append(c);
      }
    }
  }


  private ParseResult readEmptyTag(StringZero tagName)
  {
    textParser.pushPosition();
    ParseResult result = textParser.getExactCharacter('<');
    if (result != TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getExactCharacter('/');
    if (result == TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getIdentifier(tagName);
    if (result != TRUE)
    {
      textParser.popPosition();
      return ERROR;
    }

    result = textParser.getExactCharacterSequence("/>");
    if (result == ERROR)
    {
      textParser.popPosition();
      return ERROR;
    }
    if (result == FALSE)
    {
      textParser.popPosition();
      return FALSE;
    }

    textParser.passPosition();
    return TRUE;
  }

  private ParseResult readOpenTag(StringZero tagName)
  {
    textParser.pushPosition();
    ParseResult result = textParser.getExactCharacter('<');
    if (result != TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getExactCharacter('/');
    if (result == TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getIdentifier(tagName);
    if (result != TRUE)
    {
      textParser.popPosition();
      return ERROR;
    }

    result = textParser.getExactCharacter('>');
    if (result == ERROR)
    {
      textParser.popPosition();
      return ERROR;
    }
    if (result == FALSE)
    {
      textParser.popPosition();
      return FALSE;
    }

    textParser.passPosition();
    return TRUE;
  }

  private ParseResult readCloseTag(String tagName)
  {
    textParser.pushPosition();
    ParseResult result = textParser.getExactCharacter('<');
    if (result != TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getExactCharacter('/');
    if (result != TRUE)
    {
      textParser.popPosition();
      return FALSE;
    }

    result = textParser.getExactIdentifier(tagName);
    if (result != TRUE)
    {
      textParser.popPosition();
      return ERROR;
    }

    result = textParser.getExactCharacter('>');
    if (result != TRUE)
    {
      textParser.popPosition();
      return ERROR;
    }

    textParser.passPosition();
    return TRUE;
  }

  private void openTag(String tagName)
  {
    if (openTag == null)
    {
      openTag = xml.getBody().put(tagName);
    }
    else
    {
      XMLBody body = (XMLBody) openTag.getData();
      if (body == null)
      {
        body = openTag.setData();
      }
      openTag = body.put(tagName);
    }
    openTag.setOpen(true);
  }

  private void closeTag()
  {
    openTag.setOpen(false);
    if (strings != null)
    {
      if (strings.size() == 1)
      {
        String data = strings.get(0);
        openTag.setData(data);
      }
      else if (strings.size() > 1)
      {
        openTag.setData(strings);
      }
      strings = null;
    }
    XMLTag parentTag = openTag.getParentBody().getParentTag();
    if (parentTag != null)
    {
      openTag = parentTag.getParentBody().getOpenTag();
    }
    else
    {
      openTag = xml.getBody().getOpenTag();
    }
  }
}
