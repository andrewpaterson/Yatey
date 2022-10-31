package net.engine.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil
{
  public static File createTemporaryFile(String prefix, String suffix)
  {
    try
    {
      File tempFile = File.createTempFile(prefix, suffix);
      tempFile.deleteOnExit();
      return tempFile;
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public static boolean makeDirectory(String directory)
  {
    File file = new File(directory);
    return file.mkdirs();
  }

  public static List<File> findFiles(String directoryName, String extension)
  {
    if (!extension.startsWith("."))
    {
      extension = "." + extension;
    }

    List<File> files = new ArrayList<File>();

    File directory = new File(directoryName);
    if (directory.isDirectory())
    {
      findFiles(directory, extension, files);
    }
    return files;
  }

  private static void findFiles(File directory, String extension, List<File> fileList)
  {
    File[] files = directory.listFiles();
    for (int i = 0; i < files.length; i++)
    {
      File file = files[i];
      if (file.isDirectory())
      {
        findFiles(file, extension, fileList);
      }
      else
      {
        if (file.getName().endsWith(extension))
        {
          fileList.add(file);
        }
      }
    }
  }

  public static boolean deleteDirectory(String directoryName)
  {
    File file = new File(directoryName);
    return deleteDirectory(file);
  }

  public static void deleteFileOnExit(String fileName)
  {
    new File(fileName).deleteOnExit();
  }

  public static void deleteFile(String fileName)
  {
    File file = new File(fileName);
    file.delete();

    if (exists(fileName))
    {
      throw new RuntimeException("Could not delete file [" + fileName + "]");
    }
  }

  public static boolean exists(String fileName)
  {
    return new File(fileName).exists();
  }

  public static long lastModifiedTime(String fileName)
  {
    File file = new File(fileName);
    boolean b = file.exists();
    if (b)
    {
      return file.lastModified();
    }
    return 0;
  }

  private static boolean deleteDirectory(File dir)
  {
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++)
      {
        boolean success = deleteDirectory(new File(dir, children[i]));
        if (!success)
        {
          return false;
        }
      }
    }
    return dir.delete();
  }

  public static File createFile(String fileName)
  {
    File file = new File(fileName);
    try
    {
      if (!file.createNewFile())
      {
        return null;
      }
      return file;
    }
    catch (IOException e)
    {
      return null;
    }
  }

  public static void writeFile(String fileName, String contents)
  {
    writeFile(new File(fileName), contents);
  }

  public static void writeFile(File file, String contents)
  {
    try
    {
      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write(contents);
      fileWriter.close();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public static String readFile(File file)
  {
    StringBuffer result = new StringBuffer();

    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new FileReader(file));

      String nextLine;

      while ((nextLine = reader.readLine()) != null)
      {
        result.append(nextLine);
        result.append("\n");
      }

      reader.close();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException e)
        {
        }
      }
    }

    return result.toString();
  }

  public static void touch(String touchFilename)
  {
    File file = new File(touchFilename);
    try
    {
      file.createNewFile();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }

    if (!exists(touchFilename))
    {
      throw new RuntimeException("Could not touch file name [" + touchFilename + "]");
    }
  }
}
