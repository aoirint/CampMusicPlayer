package com.github.aoirint.campmusicplayer.db.data.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.Music;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomGroup implements Group {
    public String name;
    public List<Music> musics;
    // TODO: create artwork class and database
    public File artworkFile;

    public CustomGroup() {
        musics = new ArrayList<>();
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public Music[] getMusics(Context context) {
        return musics.toArray(new Music[musics.size()]);
    }

    @Override
    public Bitmap getArtwork(Context context) throws IOException {
        if (artworkFile == null) return null;
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        return app.artworkCacheManager.loadArtworkCache(artworkFile);
    }

}
