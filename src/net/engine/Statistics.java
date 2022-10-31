package net.engine;

public class Statistics
{
  public static int averageLength = 20;

  private long immOvertime;
  private long immFrameTime;
  private long immSleepTime;
  private long immActiveObjects;

  private long avgOvertime[];
  private long avgFrameTime[];
  private long avgSleepTime[];
  private long avgActiveObjects[];
  private int posOvertime;
  private int posFrameTime;
  private int posSleepTime;
  private int posActiveObjects;

  public Statistics()
  {
    avgOvertime = new long[averageLength];
    avgFrameTime = new long[averageLength];
    avgSleepTime = new long[averageLength];
    avgActiveObjects = new long[averageLength];
    posOvertime = 0;
    posFrameTime = 0;
    posSleepTime = 0;
    posActiveObjects = 0;
  }

  private int increment(int i)
  {
    i++;
    if (i >= averageLength)
    {
      i = 0;
    }
    return i;
  }

  public void setImmOvertime(long immOvertime)
  {
    this.immOvertime = immOvertime;
    avgOvertime[posOvertime] = immOvertime;
    posOvertime = increment(posOvertime);
  }

  public void setImmFrameTime(long immFrameTime)
  {
    this.immFrameTime = immFrameTime;
    avgFrameTime[posFrameTime] = immFrameTime;
    posFrameTime = increment(posFrameTime);
  }

  public void setImmSleepTime(long immSleepTime)
  {
    this.immSleepTime = immSleepTime;
    avgSleepTime[posSleepTime] = immSleepTime;
    posSleepTime = increment(posSleepTime);
  }

  public void setActiveObjects(long immActiveObjects)
  {
    this.immActiveObjects = immActiveObjects;
    avgActiveObjects[posActiveObjects] = immActiveObjects;
    posActiveObjects = increment(posActiveObjects);
  }

  public long getImmOvertime()
  {
    return immOvertime;
  }

  public long getImmFrameTime()
  {
    return immFrameTime;
  }

  public long getImmSleepTime()
  {
    return immSleepTime;
  }

  public long getImmActiveObjects()
  {
    return immActiveObjects;
  }

  public long getAvgOvertime()
  {
    return getAverage(avgOvertime);
  }

  public long getAvgFrameTime()
  {
    return getAverage(avgFrameTime);
  }

  public long getAvgSleepTime()
  {
    return getAverage(avgSleepTime);
  }

  public long getAvgActiveObjects()
  {
    return getAverage(avgActiveObjects);
  }

  private long getAverage(long[] longs)
  {
    long total = 0;
    for (long l : longs)
    {
      total += l;
    }
    return total / longs.length;
  }
}
