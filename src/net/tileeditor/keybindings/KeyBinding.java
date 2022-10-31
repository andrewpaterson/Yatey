package net.tileeditor.keybindings;

import net.engine.parser.ParseResult;
import net.engine.parser.TextParser;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;

import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class KeyBinding
{
  public static Map<String, Integer> keyTexts;

  protected int keyCode;
  protected int modifiers;

  public KeyBinding()
  {
  }

  public KeyBinding(int keyCode, int modifiers)
  {
    this.keyCode = keyCode;
    this.modifiers = modifiers;
  }

  public KeyBinding(KeyBinding binding)
  {
    keyCode = binding.keyCode;
    modifiers = binding.modifiers;
  }

  public KeyBinding(String bindingString)
  {
    keyBindingFromString(bindingString);
  }

  public KeyBinding(XMLTag tag)
  {
    XMLBody keyBindingBody = (XMLBody) tag.getData();
    String binding = (String) keyBindingBody.getData("Key");

    keyBindingFromString(binding);
  }

  protected void keyBindingFromString(String binding)
  {
    TextParser textParser = new TextParser(binding);

    modifiers = 0;
    for (int i = 0; ; i++)
    {
      ParseResult result;
      if (i > 0)
      {
        result = textParser.getExactCharacter('+');
        if (result != ParseResult.TRUE)
        {
          break;
        }
      }

      result = textParser.getExactIdentifier("Alt");
      if (result == ParseResult.TRUE)
      {
        modifiers |= KeyEvent.ALT_MASK;
        continue;
      }
      result = textParser.getExactIdentifier("Ctrl");
      if (result == ParseResult.TRUE)
      {
        modifiers |= KeyEvent.CTRL_MASK;
        continue;
      }
      result = textParser.getExactIdentifier("Meta");
      if (result == ParseResult.TRUE)
      {
        modifiers |= KeyEvent.META_MASK;
        continue;
      }
      result = textParser.getExactIdentifier("Shift");
      if (result == ParseResult.TRUE)
      {
        modifiers |= KeyEvent.SHIFT_MASK;
        continue;
      }
      break;
    }

    textParser.skipWhiteSpace();
    String remaining = textParser.getRemaining();
    Integer integer = getKeyTexts().get(remaining);
    if (integer != null)
    {
      keyCode = integer;
    }
    else
    {
      throw new RuntimeException("Unknown Keycode [" + binding + "]");
    }
  }

  public String toString()
  {
    return asKeyString();
  }

  protected String asKeyString()
  {
    if (modifiers == 0)
    {
      return KeyEvent.getKeyText(keyCode);
    }
    else
    {
      return KeyEvent.getKeyModifiersText(modifiers) + " " + KeyEvent.getKeyText(keyCode);
    }
  }

  public boolean equals(int keyCode, int modifiers)
  {
    return (this.keyCode == keyCode) && (this.modifiers == modifiers);
  }

  public static Map<String, Integer> getKeyTexts()
  {
    if (keyTexts == null)
    {
      keyTexts = new LinkedHashMap<String, Integer>();
      for (int i = KeyEvent.VK_UNDEFINED; i < KeyEvent.CHAR_UNDEFINED; i++)
      {
        String s = KeyEvent.getKeyText(i);
        if (!s.startsWith("Unknown"))
        {
          keyTexts.put(s, i);
        }
      }
    }
    return keyTexts;
  }

  public void save(XMLBody body)
  {
    XMLTag tag = body.put("KeyBinding");
    XMLBody keyBindingBody = tag.setData();

    keyBindingBody.put("Key", asKeyString());
  }

  public int getKeyCode()
  {
    return keyCode;
  }

  public int getModifiers()
  {
    return modifiers;
  }
}
