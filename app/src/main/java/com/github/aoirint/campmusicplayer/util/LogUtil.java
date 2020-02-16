package com.github.aoirint.campmusicplayer.util;

import static com.github.aoirint.campmusicplayer.activity.main.MainActivity.logger;

public class LogUtil {

    public void info(Iterable iterable) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Object obj: iterable) sb.append(obj).append(',');
        sb.append(']');
        logger.info(sb.toString());
    }

}
