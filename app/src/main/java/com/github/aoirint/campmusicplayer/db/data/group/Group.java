package com.github.aoirint.campmusicplayer.db.data.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.github.aoirint.campmusicplayer.db.data.Music;

import java.io.IOException;
import java.io.Serializable;

// TODO: group in database
public interface Group extends Serializable {

    String getName(Context context);
    Music[] getMusics(Context context);

    Bitmap getArtwork(Context context) throws IOException;
    boolean isEditable();

}
