package net.engine;

public class IntegerRange
{
  int min;
  int max;

  public IntegerRange(int min, int max)
  {
    if (max >= min)
    {
      this.min = min;
      this.max = max;
    }
    else
    {
      this.min = max;
      this.max = min;
    }
  }

  public int getMin()
  {
    return min;
  }

  public int getMax()
  {
    return max;
  }
}
