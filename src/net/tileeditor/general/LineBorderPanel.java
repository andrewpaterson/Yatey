package net.tileeditor.general;

import javax.swing.*;
import java.awt.*;

public class LineBorderPanel extends JPanel
{
  public LineBorderPanel(Component component)
  {
    super(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.WHITE));
    super.add(component, BorderLayout.CENTER);
  }
}
