package net.meshtest;

public class IntegerHelper
{
  public static int setFlag(int dest, int flag, boolean flagValue)
  {
    //If the value is true then or it with dest.
    if (flagValue)
    {
      return dest |= flag;
    }
    //If the value is false then negate and and it with dest.
    else
    {
      return dest &= (~flag);
    }
  }
}
