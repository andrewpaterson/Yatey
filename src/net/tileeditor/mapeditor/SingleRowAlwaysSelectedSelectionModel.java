package net.tileeditor.mapeditor;

import javax.swing.*;

public class SingleRowAlwaysSelectedSelectionModel extends DefaultListSelectionModel
{
  public SingleRowAlwaysSelectedSelectionModel()
  {
    setSelectionMode(SINGLE_SELECTION);
    setSelectionInterval(0,0);
  }

  public void removeSelectionInterval(int index0, int index1)
  {
  }

  public void setSelectionInterval(int index0, int index1)
  {
    super.setSelectionInterval(index0, index1);
  }
}
