package net.engine.file.xml;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XMLBody
{
  private List<XMLTag> xmlTags;
  private XMLTag parentTag;

  public XMLBody(XMLTag parentTag)
  {
    this.parentTag = parentTag;
    xmlTags = new ArrayList<XMLTag>();
  }

  public List<XMLTag> getTags()
  {
    return xmlTags;
  }

  public void put(String tag, Object object)
  {
    xmlTags.add(new XMLTag(tag, object, this));
  }

  public XMLTag put(String tagName)
  {
    XMLTag tag = new XMLTag(tagName, null, this);
    xmlTags.add(tag);
    return tag;
  }

  public void putUnique(String tag, Object object)
  {
    if (tagExists(tag))
    {
      throw new RuntimeException("XML already contains a tag called [" + tag + "]");
    }
    put(tag, object);
  }

  public Object getData(String tagName)
  {
    XMLTag tag = getTag(tagName);
    if (tag == null)
    {
      return null;
    }

    return tag.getData();
  }

  public XMLTag getTag(String tag)
  {
    for (XMLTag xmlTag : xmlTags)
    {
      if (xmlTag.getName().equals(tag))
      {
        return xmlTag;
      }
    }
    return null;
  }

  public XMLBody getXml(String tag)
  {
    XMLTag xmlTag = getTag(tag);
    if (xmlTag != null)
    {
      return (XMLBody) xmlTag.getData();
    }
    return null;
  }

  private boolean tagExists(String tag)
  {
    return getTag(tag) != null;
  }

  public static String tagInline(String tag, String contents)
  {
    StringBuffer stringBuffer = new StringBuffer();
    if (!contents.isEmpty())
    {
      stringBuffer.append(openTag(tag));
      stringBuffer.append(contents);
      stringBuffer.append(closeTag(tag));
    }
    else
    {
      stringBuffer.append(emptyTag(tag));
    }
    return convertToCharset(stringBuffer.toString());
  }

  private static String convertToCharset(String string)
  {
    return new String(string.getBytes(Charset.forName(XMLFile.charsetName)));
  }

  private static String emptyTag(String tag)
  {
    return "<" + tag + "/>";
  }

  private static String openTag(String tag)
  {
    return "<" + tag + ">";
  }

  private static String closeTag(String tag)
  {
    return "</" + tag + ">";
  }

  public void sort()
  {
    Collections.sort(xmlTags);

    for (XMLTag xmlTag : xmlTags)
    {
      Object data = xmlTag.getData();
      if (data instanceof XMLBody)
      {
        ((XMLBody) data).sort();
      }
    }
  }

  protected List<String> toXML()
  {
    List<String> lines = new ArrayList<String>();

    List<XMLTag> xmlTags = getTags();
    for (XMLTag xmlTag : xmlTags)
    {
      Object data = xmlTag.getData();
      String tagName = xmlTag.getName();
      if (data == null)
      {
        lines.add(tagInline(tagName, ""));
      }
      else if (data instanceof XMLBody)
      {
        List<String> contents = ((XMLBody) data).toXML();
        if (contents == null)
        {
          lines.add(tagInline(tagName, ""));
        }
        else if (contents.size() == 1)
        {
          lines.add(tagInline(tagName, contents.get(0)));
        }
        else
        {
          lines.add(convertToCharset(openTag(tagName)));
          lines.addAll(contents);  //Contents must already have been converted.
          lines.add(convertToCharset(closeTag(tagName)));
        }
      }
      else if (data instanceof String)
      {
        String replaced = escapeSpecialCharacters((String) data);
        lines.add(tagInline(tagName, replaced));
      }
      else if (data instanceof List)
      {
        lines.add(convertToCharset(openTag(tagName)));
        List<String> contents = (List<String>) data;
        for (String content : contents)
        {
          lines.add(convertToCharset(content));
        }
        lines.add(convertToCharset(closeTag(tagName)));
      }
      else
      {
        throw new RuntimeException("Can't XML'iffy class [" + xmlTag.getData().getClass().getSimpleName() + "]");
      }
    }
    return lines;
  }

  protected String escapeSpecialCharacters(String contents)
  {
    if (contents == null)
    {
      return "";
    }
    else
    {
      return contents.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;");
    }
  }

  public XMLTag getParentTag()
  {
    return parentTag;
  }

  public XMLTag getOpenTag()
  {
    for (XMLTag xmlTag : xmlTags)
    {
      if (xmlTag.isOpen())
      {
        return xmlTag;
      }
    }
    return null;
  }

  public List<XMLTag> getTags(String s)
  {
    List<XMLTag> matches = new ArrayList<XMLTag>();
    for (XMLTag xmlTag : xmlTags)
    {
      if (xmlTag.getName().equals(s))
      {
        matches.add(xmlTag);
      }
    }
    return matches;
  }
}
