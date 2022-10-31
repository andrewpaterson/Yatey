package net.engine.cel;

import java.util.LinkedHashMap;
import java.util.Map;

public class CelStore
{
  public static CelStore instance = null;

  public Map<String, CelHelper> celHelpers;

  public CelStore()
  {
    celHelpers = new LinkedHashMap<String, CelHelper>();
  }

  public CelHelper get(String name)
  {
    return celHelpers.get(name);
  }

  public CelHelper addCelHelper(String name, CelHelper celHelper)
  {
    CelHelper storedCelHelper = celHelpers.get(name);
    if (storedCelHelper == null)
    {
      celHelpers.put(name, celHelper);
      return celHelper;
    }
    else
    {
      return storedCelHelper;
    }
  }

  public static CelStore getInstance()
  {
    if (instance == null)
    {
      instance = new CelStore();
    }
    return instance;
  }
}
