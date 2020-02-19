package com.github.aoirint.campmusicplayer.db.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.aoirint.campmusicplayer.db.MusicDatabase;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.Tag;

public class MusicTagRelationTable {
    MusicDatabase musicDatabase;

    public MusicTagRelationTable(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }

    // TODO: rewrite with INNER JOIN
    public Tag[] getTags(Music music) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT tagId FROM musicTagRelation WHERE musicId=? ORDER BY created_at DESC", new String[] { String.valueOf(music.id) });
        int count = cur.getCount();
        int[] tagIds = new int[count];
        for (int i=0; i<count; i++) {
            cur.moveToNext();
            tagIds[i] = cur.getInt(0);
        }

        return musicDatabase.tagTable.get(tagIds);
    }

    // TODO: rewrite with INNER JOIN
    public Music[] getMusics(Tag tag) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT musicId FROM musicTagRelation WHERE tagId=? ORDER BY created_at DESC", new String[] { String.valueOf(tag.id) });
        int count = cur.getCount();
        int[] musicIds = new int[count];
        for (int i=0; i<count; i++) {
            cur.moveToNext();
            musicIds[i] = cur.getInt(0);
        }

        return musicDatabase.musicTable.get(musicIds);
    }

    // TODO: rewrite with INNER JOIN
    public Music getLatestMusic(Tag tag) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT musicId FROM musicTagRelation WHERE tagId=? ORDER BY created_at DESC LIMIT 1", new String[] { String.valueOf(tag.id) });
        cur.moveToFirst();
        int musicId = cur.getInt(0);

        return musicDatabase.musicTable.get(musicId);
    }

    public boolean addTag(Music music, Tag tag, long created_at) {
        if (hasTag(music, tag)) return false;

        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("musicId", music.id);
        values.put("tagId", tag.id);
        values.put("created_at", created_at);
        values.put("updated_at", created_at);

        db.insert("musicTagRelation", null, values);
        return true;
    }

    public void clearTag(Music music) {
        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("musicId", music.id);

        db.delete("musicTagRelation", "musicId=?", new String[] { String.valueOf(music.id) });
    }

    public void removeTag(Music music, Tag tag) {
        SQLiteDatabase db = musicDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("musicId", music.id);
        values.put("tagId", tag.id);

        db.delete("musicTagRelation", "musicId=? AND tagId=?", new String[] { String.valueOf(music.id), String.valueOf(tag.id) });
    }

    public void removeAllTags(SQLiteDatabase db, Music music) {
        ContentValues values = new ContentValues();
        values.put("musicId", music.id);

        db.delete("musicTagRelation", "musicId=?", new String[] { String.valueOf(music.id) });
    }

    public boolean hasTag(Music music, Tag tag) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM musicTagRelation WHERE musicId=? AND tagId=?", new String[] { String.valueOf(music.id), String.valueOf(tag.id) });
        cur.moveToFirst();
        return cur.getInt(0) > 0;
    }


    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE musicTagRelation(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "musicId INTEGER, " +
                "tagId INTEGER," +
                "created_at INTEGER, " +
                "updated_at INTEGER" +
                ")"
        );

    }

}
