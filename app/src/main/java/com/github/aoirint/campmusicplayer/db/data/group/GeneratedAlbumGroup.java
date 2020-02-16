package com.github.aoirint.campmusicplayer.db.data.group;

import android.content.Context;
import android.graphics.Bitmap;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.Album;
import com.github.aoirint.campmusicplayer.db.data.Music;

import java.io.IOException;

public class GeneratedAlbumGroup implements Group {
    public Album album;

    public GeneratedAlbumGroup(Album album) {
        this.album = album;
    }

    @Override
    public String getName(Context context) {
        return album.name + " - " + album.artist.name;
    }

    @Override
    public Music[] getMusics(Context context) {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        Music[] musics = app.musicDatabase.albumTable.getMusics(album);
        return musics;
    }

    @Override
    public Bitmap getArtwork(Context context) throws IOException {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        Music first = app.musicDatabase.albumTable.getFirstMusic(album);
        return app.artworkCacheManager.loadOrCreate(first.getUri());
    }

}
