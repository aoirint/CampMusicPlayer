package com.github.aoirint.campmusicplayer.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.aoirint.campmusicplayer.db.MusicDatabase;
import com.github.aoirint.campmusicplayer.db.data.Artist;

public class ArtistTable {
    MusicDatabase musicDatabase;

    public ArtistTable(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }

    public Artist getOrCreate(SQLiteDatabase db, String name) {
        Artist artist = get(name);
        if (artist != null) return artist;
        return create(db, name);
    }
    public Artist get(int id) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM artist WHERE id=?", new String[] { String.valueOf(id) });
        if (! cur.moveToFirst()) return null;
        return loadFromCursor(cur);
    }

    private Artist loadFromCursor(Cursor cur) {
        Artist artist = new Artist();

        artist.id = cur.getInt(0);
        artist.mbid = cur.getString(1);
        artist.name = cur.getString(2);
        artist.created_at = cur.getLong(3);
        artist.updated_at = cur.getLong(4);

        return artist;
    }
    private Artist get(String name) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM artist WHERE name=?", new String[] { name });
        if (! cur.moveToFirst()) return null;
        return loadFromCursor(cur);
    }
    private Artist create(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();

        long time = System.currentTimeMillis();
        values.put("name", name);
        values.put("created_at", time);
        values.put("updated_at", time);

        long rowId = db.insert("artist", null, values);
        Cursor cur = db.rawQuery("SELECT * FROM artist WHERE rowid=?", new String[] { String.valueOf(rowId) });
        cur.moveToFirst();
        return loadFromCursor(cur);
    }


    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE artist(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mbid TEXT, " +
                "name TEXT, " +
                "created_at INTEGER, " +
                "updated_at INTEGER" +
                ")"
        );

    }

}
