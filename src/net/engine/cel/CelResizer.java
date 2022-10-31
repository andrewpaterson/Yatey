package net.engine.cel;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class CelResizer
{
  //from http://scale2x.sourceforge.net/algorithm.html
  //
  // A B C
  // D E F
  // G H I

  public static BufferedImage scale2x(BufferedImage source)
  {
    // E0 E1
    // E2 E3

    int B[] = new int[4];
    int D[] = new int[4];
    int E[] = new int[4];
    int F[] = new int[4];
    int H[] = new int[4];

    int E0[];
    int E1[];
    int E2[];
    int E3[];

    WritableRaster sourceRaster = source.getRaster();

    BufferedImage dest = new BufferedImage(source.getWidth() * 2, source.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
    WritableRaster destRaster = dest.getRaster();

    for (int sy = 1; sy < source.getHeight() - 1; sy++)
    {
      for (int sx = 1; sx < source.getWidth() - 1; sx++)
      {
        sourceRaster.getPixel(sx, sy - 1, B);
        sourceRaster.getPixel(sx - 1, sy, D);
        sourceRaster.getPixel(sx, sy, E);
        sourceRaster.getPixel(sx + 1, sy, F);
        sourceRaster.getPixel(sx, sy + 1, H);

        if (!equals(B, H) && !equals(D, F))
        {
          E0 = equals(D, B) ? D : E;
          E1 = equals(B, F) ? F : E;
          E2 = equals(D, H) ? D : E;
          E3 = equals(H, F) ? F : E;
        }
        else
        {
          E0 = E;
          E1 = E;
          E2 = E;
          E3 = E;
        }

        int dx = sx * 2;
        int dy = sy * 2;

        destRaster.setPixel(dx, dy, E0);
        destRaster.setPixel(dx + 1, dy, E1);
        destRaster.setPixel(dx, dy + 1, E2);
        destRaster.setPixel(dx + 1, dy + 1, E3);
      }
    }
    return dest;
  }

  private static boolean equals(int[] ai1, int[] ai2)
  {
    if ((ai1[0] == ai2[0]) && (ai1[1] == ai2[1]) && (ai1[2] == ai2[2]) && (ai1[3] == ai2[3]))
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
