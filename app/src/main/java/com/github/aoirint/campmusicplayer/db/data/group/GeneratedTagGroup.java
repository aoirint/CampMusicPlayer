package com.github.aoirint.campmusicplayer.db.data.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.Album;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.Tag;

import java.io.File;
import java.io.IOException;

public class GeneratedTagGroup implements Group {
    public Tag tag;

    public GeneratedTagGroup(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String getName(Context context) {
        return tag.name;
    }

    @Override
    public Music[] getMusics(Context context) {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        Music[] musics = app.musicDatabase.musicTagRelationTable.getMusics(tag);
        return musics;
    }

    @Override
    public Bitmap getArtwork(Context context) throws IOException {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        Music first = app.musicDatabase.musicTagRelationTable.getLatestMusic(tag); // musics.length > 0

        return app.artworkCacheManager.loadOrCreate(first.getUri());
    }

}
