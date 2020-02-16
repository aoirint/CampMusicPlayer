package com.github.aoirint.campmusicplayer.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.aoirint.campmusicplayer.db.MusicDatabase;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.MusicKey;

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
        Music music = new Music();

        music.id = cur.getInt(0);
        music.mbid = cur.getString(1);
        music.uri = cur.getString(2);
        music.hash = cur.getString(3);
        music.title = cur.getString(4);
        int albumId = cur.getInt(5);

        music.genre = cur.getString(6);
        music.duration = cur.getString(7);
        music.year = cur.getString(8);

        music.discNumber = cur.getString(9);
        music.cdTrackNumber = cur.getString(10);
        music.numTracks = cur.getString(11);

        music.author = cur.getString(12);
        music.composer = cur.getString(13);
        music.writer = cur.getString(14);
        music.albumArtist = cur.getString(15);

        music.created_at = cur.getLong(16);
        music.updated_at = cur.getLong(17);
        music.played_at = cur.getLong(18);

        music.album = musicDatabase.albumTable.get(albumId);

        return music;
    }
    Music[] loadArrayFromCursor(Cursor cur) {
        int count = cur.getCount();
        Music[] musics = new Music[count];
        for (int i=0; i<count; i++) {
            cur.moveToNext();
            musics[i] = loadFromCursor(cur);
        }
        return musics;
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


    public Music getOrCreate(MusicKey key) throws IOException {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE uri=?", new String[] { key.uri.toString() });
        if (cur.moveToFirst()) {
            return loadFromCursor(cur);
        }

        return loadAndRegister(key);
    }


    public Music get(int musicId) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE id=?", new String[] { String.valueOf(musicId) });
        cur.moveToFirst();
        return loadFromCursor(cur);
    }

    @Deprecated
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
            musics[i] = id2MusicMap.get(musicIds[i]); // keep order
        }

        return musics;
    }

    public Music loadAndRegister(MusicKey key) throws IOException {
        Music music = Music.createFromKey(musicDatabase.context, key);
        assert music.album.id != null;

        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("mbid", music.mbid);
        values.put("uri", music.uri);
        values.put("hash", music.hash);
        values.put("title", music.title);
        values.put("albumId", music.album.id);

        values.put("genre", music.genre);
        values.put("duration", music.duration);
        values.put("year", music.year);

        values.put("discNumber", music.discNumber);
        values.put("cdTrackNumber", music.cdTrackNumber);
        values.put("numTracks", music.numTracks);

        values.put("author", music.author);
        values.put("composer", music.composer);
        values.put("writer", music.writer);
        values.put("albumArtist", music.albumArtist);

        values.put("created_at", music.created_at);
        values.put("updated_at", music.updated_at);
        values.put("played_at", music.played_at);

        long rowId = db.insert("music", null, values);
        Cursor cur = db.rawQuery("SELECT id FROM music WHERE rowid=?", new String[] { String.valueOf(rowId) });
        cur.moveToFirst();
        int musicId = cur.getInt(0);

        music.id = musicId;
        return music;
    }



    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE music(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mbid TEXT, " +
                "uri TEXT, " +
                "hash TEXT, " +
                "title TEXT, " +
                "albumId INTEGER, " +
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
