package com.github.aoirint.campmusicplayer.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicTable {
    MusicDatabase musicDatabase;

    public MusicTable(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }


    public Music[] listMusicsRecentlyAdded() {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music ORDER BY created_at DESC LIMIT 10", new String[]{});

        int count = cur.getCount();
        Music[] result = new Music[count];

        for (int index=0; index<count; index++) {
            cur.moveToNext();
            result[index] = loadFromCursor(cur);
        }

        return result;
    }

    Music loadFromCursor(Cursor cur) {
        Music info = new Music();

        info.id = cur.getInt(0);
        info.uri = cur.getString(1);
        info.hash = cur.getString(2);
        info.title = cur.getString(3);
        info.artist = cur.getString(4);
        info.album = cur.getString(5);

        info.genre = cur.getString(6);
        info.duration = cur.getString(7);
        info.year = cur.getString(8);

        info.discNumber = cur.getString(9);
        info.cdTrackNumber = cur.getString(10);
        info.numTracks = cur.getString(11);

        info.author = cur.getString(12);
        info.composer = cur.getString(13);
        info.writer = cur.getString(14);
        info.albumArtist = cur.getString(15);

        info.created_at = cur.getLong(16);
        info.updated_at = cur.getLong(17);
        info.played_at = cur.getLong(18);

        return info;
    }

    public void updatePlayedAt(Music info) {
        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("played_at", info.played_at);

        db.update("music", values, "id=?", new String[] { String.valueOf(info.id) });
    }
    public void updatePlayedAtNow(Music info) {
        info.updatePlayedAt();
        updatePlayedAt(info);
    }



    public Music getOrLoad(Uri audioUri) throws IOException {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE uri=?", new String[] { audioUri.toString() });
        if (cur.moveToFirst()) {
            return loadFromCursor(cur);
        }

        return loadAndRegister(audioUri);
    }

    public Music[] get(int[] musicIds) {
        StringBuilder sb = new StringBuilder();
        for (int index=0; index<musicIds.length; index++) {
            sb.append(musicIds[index]);
            if (index != musicIds.length-1) sb.append(',');
        }

        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE id IN (" + sb.toString() + ")", new String[0]);
        int count = cur.getCount();
        Map<Integer, Music> id2MusicMap = new HashMap<>();

        for (int i=0; i<count; i++) {
            cur.moveToNext();
            Music music = loadFromCursor(cur);
            id2MusicMap.put(music.id, music);
        }

        Music[] musics = new Music[count];
        for (int i=0; i<count; i++) {
            musics[i] = id2MusicMap.get(musicIds[i]);
        }

        return musics;
    }

    public Music loadAndRegister(Uri audioUri) throws IOException {
        Music info = Music.createFromUri(musicDatabase.context, audioUri);

        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("uri", info.uri);
        values.put("hash", info.hash);
        values.put("title", info.title);
        values.put("artist", info.artist);
        values.put("album", info.album);

        values.put("genre", info.genre);
        values.put("duration", info.duration);
        values.put("year", info.year);

        values.put("discNumber", info.discNumber);
        values.put("cdTrackNumber", info.cdTrackNumber);
        values.put("numTracks", info.numTracks);

        values.put("author", info.author);
        values.put("composer", info.composer);
        values.put("writer", info.writer);
        values.put("albumArtist", info.albumArtist);

        values.put("created_at", info.created_at);
        values.put("updated_at", info.updated_at);
        values.put("played_at", info.played_at);

        long rowId = db.insert("music", null, values);
        Cursor cur = db.rawQuery("SELECT id FROM music WHERE rowid=?", new String[] { String.valueOf(rowId) });
        cur.moveToFirst();
        int musicId = cur.getInt(0);

        info.id = musicId;
        return info;
    }



    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE music(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "uri TEXT, " +
                "hash TEXT, " +
                "title TEXT, " +
                "artist TEXT, " +
                "album TEXT, " +
                "genre TEXT, " +
                "duration TEXT, " +
                "year TEXT, " +
                "discNumber TEXT, " +
                "cdTrackNumber TEXT, " +
                "numTracks TEXT, " +
                "author TEXT, " +
                "composer TEXT, " +
                "writer TEXT, " +
                "albumArtist TEXT, " +
                "artwork_path TEXT, " +
                "created_at INTEGER, " +
                "updated_at INTEGER, " +
                "played_at INTEGER" +
                ")"
        );

    }

}
