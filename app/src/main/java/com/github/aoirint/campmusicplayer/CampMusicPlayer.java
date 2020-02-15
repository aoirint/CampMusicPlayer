package com.github.aoirint.campmusicplayer;

import android.app.Application;
import android.content.Context;

import com.github.aoirint.campmusicplayer.db.ArtworkCacheManager;
import com.github.aoirint.campmusicplayer.db.MusicDatabase;

public class CampMusicPlayer extends Application {
    public MusicDatabase musicDatabase;
    public ArtworkCacheManager artworkCacheManager;

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();
        musicDatabase = new MusicDatabase(context);
        artworkCacheManager = new ArtworkCacheManager(context);

    }

}
