package com.github.aoirint.campmusicplayer.util;

import android.content.Context;

public class UnitUtil {

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((dp*density) + 0.5);
    }

}
