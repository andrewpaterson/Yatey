package net.engine.random;

public class Rand
{
  private static Rand instance;

  private int low;
  private int high;

  public static Rand getInstance()
  {
    if (instance == null)
    {
      instance = new Rand();
    }
    return instance;
  }

  public Rand()
  {
    this(0xdeadbeef);
  }

  public Rand(int seed)
  {
    high = seed;
    low = high ^ 0x49616E42;
  }

  int step()
  {
    high = (high << 16) + (high >> 16);
    high += low;
    low += high;
    return high;
  }

  public static int nextInt()
  {
    Rand random = getInstance();
    random.step();
    return random.high & (~0x80000000);
  }
}
