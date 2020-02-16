package com.github.aoirint.campmusicplayer.db.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import java.io.Serializable;

public class Music implements Serializable {
    public Integer id;
    public String mbid;
    public String uri;
    public String hash;

    public String title;
    public Album album;

    public String genre;
    public String duration;
    public String year;

    public String discNumber;
    public String cdTrackNumber;
    public String numTracks;

    public String author;
    public String composer;
    public String writer;
    public String albumArtist;

    // UTC millis (System.currentTimeMillis)
    public Long created_at;
    public Long updated_at;
    public Long played_at;

    public Music() {
    }

    public Music(MusicKey key, Album album) {
        assert album.id != null;

        this.uri = key.uri;
        this.hash = key.hash;
        this.title = key.title;

        this.album = album;

        this.genre = key.genre;
        this.duration = key.duration;
        this.year = key.year;

        this.discNumber = key.discNumber;
        this.cdTrackNumber = key.cdTrackNumber;
        this.numTracks = key.numTracks;

        this.author = key.author;
        this.composer = key.composer;
        this.writer = key.writer;
        this.albumArtist = key.albumArtist;

        this.created_at = this.updated_at = System.currentTimeMillis();
    }

    public void updatePlayedAt() {
        played_at = System.currentTimeMillis();
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public static Music createFromKey(Context context, MusicKey key) {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();
        SQLiteDatabase db = app.musicDatabase.getWritableDatabase();
        Artist artist = app.musicDatabase.artistTable.getOrCreate(db, key.artist);

        AlbumKey albumKey = new AlbumKey(key.album, artist);
        Album album = app.musicDatabase.albumTable.getOrCreate(db, albumKey);

        Music music = new Music(key, album);

        return music;
    }

}
