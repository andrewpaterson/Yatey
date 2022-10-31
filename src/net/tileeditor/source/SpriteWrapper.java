package net.tileeditor.source;

import net.engine.GamePanel;
import net.engine.cel.Cel;
import net.engine.file.xml.XMLBody;
import net.engine.file.xml.XMLTag;
import net.engine.graphics.Sprite;
import net.engine.parser.IntegerPointer;
import net.engine.parser.ParseResult;
import net.engine.parser.TextParser;
import net.tileeditor.*;

public class SpriteWrapper extends SimpleObjectWrapper
{
  public Sprite fakeRenderSprite;

  public SpriteWrapper()
  {
    defaultValue = new DefaultSimpleObject(new SimpleBrush(null, null, 0));
    fakeRenderSprite = new Sprite(null);
  }

  public Class getTileClass()
  {
    return SimpleBrush.class;
  }

  public SimpleObject getClearValue()
  {
    return defaultValue;
  }

  public void render(GamePanel gamePanel, Object object, int x, int y, int width, int height, int layer)
  {
    SimpleBrush brush = (SimpleBrush) object;
    if (brush.getCel() != null)
    {
      fakeRenderSprite.setCel(0, brush.getCel());
      fakeRenderSprite.setPosition(x, y);
      gamePanel.renderSprite(fakeRenderSprite);
    }
  }

  public SimpleObject fromXML(XMLBody body, SimpleObjects simpleObjects)
  {
    int celIndex = Integer.parseInt((String) body.getData("CelIndex"));
    int brushId = Integer.parseInt((String) body.getData("BrushID"));
    String sourceName = (String) body.getData("SourceName");

    BrushSource brushSource = Source.getInstance().getBrushSource(sourceName);
    if (brushSource != null)
    {
      Cel cel = brushSource.getCel(celIndex);
      if (cel != null)
      {
        SimpleBrush objectValue = new SimpleBrush(cel, brushSource, brushId);
        return new SimpleObject(body, objectValue, simpleObjects);
      }
    }
    return null;
  }

  public void toXML(XMLBody body, Object value)
  {
    SimpleBrush simpleBrush = (SimpleBrush) value;
    BrushSource source = simpleBrush.getBrushSource();
    String celIndexString = Integer.toString(source.getCelIndex(simpleBrush.getCel()));
    String idString = simpleBrush.getId().toString();

    body.put("CelIndex", celIndexString);
    body.put("BrushID", idString);
    body.put("SourceName", source.getFileName());
  }
}
