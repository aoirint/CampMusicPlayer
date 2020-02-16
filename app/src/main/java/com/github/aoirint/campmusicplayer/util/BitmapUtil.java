package com.github.aoirint.campmusicplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class BitmapUtil {

    @Deprecated
    public static int calcAverage(Bitmap bitmap) {
        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;

        for (int y=0; y<bitmap.getHeight(); y++) {
            for (int x=0; x<bitmap.getWidth(); x++) {
                int rgb = bitmap.getPixel(x, y);
                redSum += Color.red(rgb);
                greenSum += Color.green(rgb);
                blueSum += Color.blue(rgb);
            }
        }

        int red = (int) (redSum / (bitmap.getWidth()*bitmap.getHeight()));
        int green = (int) (greenSum / (bitmap.getWidth()*bitmap.getHeight()));
        int blue = (int) (blueSum / (bitmap.getWidth()*bitmap.getHeight()));

        return Color.argb(0xFF, red, green, blue);
    }

    public static int calcCommon(Bitmap bitmap) {
        Bitmap sb = Bitmap.createScaledBitmap(bitmap, 1, 1, false);
        return sb.getPixel(0, 0);
    }

}
