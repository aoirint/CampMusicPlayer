package com.github.aoirint.campmusicplayer.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.aoirint.campmusicplayer.db.MusicDatabase;
import com.github.aoirint.campmusicplayer.db.data.Album;
import com.github.aoirint.campmusicplayer.db.data.AlbumKey;
import com.github.aoirint.campmusicplayer.db.data.Music;

import java.util.HashMap;
import java.util.Map;

public class AlbumTable {
    MusicDatabase musicDatabase;

    public AlbumTable(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }

    public Album getOrCreate(SQLiteDatabase db, AlbumKey key) {
        assert key.artist.id != null;

        Album album = get(key);
        if (album != null) return album;
        return create(db, key);
    }

    public Album get(int id) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM album WHERE id=?", new String[] { String.valueOf(id) });
        if (! cur.moveToFirst()) return null;
        return loadFromCursor(cur);
    }

    @Deprecated
    public Album[] get(int[] albumIds) {
        StringBuilder sb = new StringBuilder();
        for (int index=0; index<albumIds.length; index++) {
            sb.append(albumIds[index]);
            if (index != albumIds.length-1) sb.append(',');
        }

        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM album WHERE id IN (" + sb.toString() + ")", new String[0]);
        int count = cur.getCount();
        Map<Integer, Album> id2AlbumMap = new HashMap<>();

        for (int i=0; i<count; i++) {
            cur.moveToNext();
            Album album = loadFromCursor(cur);
            id2AlbumMap.put(album.id, album);
        }

        Album[] albums = new Album[count];
        for (int i=0; i<count; i++) {
            albums[i] = id2AlbumMap.get(albumIds[i]); // keep order
        }

        return albums;
    }

    public Album[] getAlbumsRecentlyAssigned() {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT albumId FROM music GROUP BY albumId ORDER BY created_at DESC", new String[0]);
        int count = cur.getCount();
        int[] albumIds = new int[count];
        for (int i=0; i<count; i++) {
            cur.moveToNext();
            albumIds[i] = cur.getInt(0);
        }

        return get(albumIds);
    }

    public Music[] getMusics(Album album) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE albumId=? ORDER BY created_at", new String[] { String.valueOf(album.id) });
        return musicDatabase.musicTable.loadArrayFromCursor(cur);
    }

    public Music getFirstMusic(Album album) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM music WHERE albumId=? ORDER BY created_at LIMIT 1", new String[] { String.valueOf(album.id) });
        cur.moveToFirst();
        return musicDatabase.musicTable.loadFromCursor(cur);
    }

    Album loadFromCursor(Cursor cur) {
        Album album = new Album();

        album.id = cur.getInt(0);
        album.mbid = cur.getString(1);
        album.name = cur.getString(2);
        int artistId = cur.getInt(3);
        album.created_at = cur.getLong(4);
        album.updated_at = cur.getLong(5);

        album.artist = musicDatabase.artistTable.get(artistId);

        return album;
    }
    Album[] loadArrayFromCursor(Cursor cur) {
        int count = cur.getCount();
        Album[] albums = new Album[count];
        for (int i=0; i<count; i++) {
            cur.moveToNext();
            albums[i] = loadFromCursor(cur);
        }
        return albums;
    }

    private Album get(AlbumKey key) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM album WHERE name=? AND artist=?", new String[] { key.name, String.valueOf(key.artist.id) });
        if (! cur.moveToFirst()) return null;
        return loadFromCursor(cur);
    }
    private Album create(SQLiteDatabase db, AlbumKey key) {
        ContentValues values = new ContentValues();

        long time = System.currentTimeMillis();
        values.put("name", key.name);
        values.put("artist", key.artist.id);
        values.put("created_at", time);
        values.put("updated_at", time);

        long rowId = db.insert("album", null, values);
        Cursor cur = db.rawQuery("SELECT * FROM album WHERE rowid=?", new String[] { String.valueOf(rowId) });
        cur.moveToFirst();
        return loadFromCursor(cur);
    }

    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE album(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mbid TEXT, " +
                "name TEXT, " +
                "artist INTEGER," +
                "created_at INTEGER, " +
                "updated_at INTEGER" +
                ")"
        );

    }

}
