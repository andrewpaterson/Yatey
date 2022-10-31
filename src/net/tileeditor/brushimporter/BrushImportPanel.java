package net.tileeditor.brushimporter;

import net.engine.GamePanel;
import net.tileeditor.general.TileEditor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BrushImportPanel extends JPanel
{
  private BrushImportViewPanel brushImportViewPanel;
  private BrushImporterEditorPanelInfo editorPanel;

  public BrushImportPanel(TileEditor tileEditor)
  {
    setLayout(new BorderLayout());
    brushImportViewPanel = new BrushImportViewPanel();
    add(brushImportViewPanel, BorderLayout.CENTER);
    editorPanel = new BrushImporterEditorPanelInfo(tileEditor, brushImportViewPanel);
    brushImportViewPanel.setEditorPanelInfo(editorPanel);
    add(editorPanel, BorderLayout.SOUTH);
  }

  public GamePanel getViewPanel()
  {
    return brushImportViewPanel;
  }

  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    brushImportViewPanel.setEnabled(enabled);
  }

  public void updateVales()
  {
    editorPanel.updateVales();
  }

  public BrushImporterEditorPanelInfo getEditorPanel()
  {
    return editorPanel;
  }

  public void defaultValues(String filename)
  {
    editorPanel.defaultValues(filename);
  }

  public void defaultValues(List<String> filenames)
  {
    editorPanel.defaultValues(filenames);
  }
}
