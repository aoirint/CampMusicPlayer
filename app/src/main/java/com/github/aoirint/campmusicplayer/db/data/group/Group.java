package com.github.aoirint.campmusicplayer.db.data.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.github.aoirint.campmusicplayer.db.data.Music;

import java.io.File;
import java.io.IOException;

// TODO: group in database
public interface Group {

    String getName(Context context);
    Music[] getMusics(Context context);

    Bitmap getArtwork(Context context) throws IOException;

}
