package net.engine.file.chunk;

/**
 * Copyright (c) 2002-2005 Lautus Solutions
 */
public class ArraySimpleHeader
{
  public static final int sizeof_this = ExtendedFile.sizeof_int + ExtendedFile.sizeof_void_ptr;

  public int usedElements;

  public ArraySimpleHeader()
  {
  }
}
