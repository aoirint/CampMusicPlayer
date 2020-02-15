package com.github.aoirint.campmusicplayer.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class TagTable {
    MusicDatabase musicDatabase;

    public TagTable(MusicDatabase musicDatabase) {
        this.musicDatabase = musicDatabase;
    }


    public Tag[] listTagsRecentlyAssigned() {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM (SELECT T.* FROM musicTagRelation R INNER JOIN tag T ON R.tagId=T.id ORDER BY R.created_at DESC) GROUP BY id LIMIT 10", new String[]{});

        int count = cur.getCount();
        Tag[] result = new Tag[count];

        for (int index=0; index<count; index++) {
            cur.moveToNext();
            result[index] = loadFromCursor(cur);
        }

        return result;
    }

    public Tag[] getAll() {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM tag", new String[0]);
        int count = cur.getCount();
        Tag[] result = new Tag[count];

        for (int i=0; i<count; i++) {
            cur.moveToNext();
            result[i] = loadFromCursor(cur);
        }

        return result;
    }

    public Tag[] get(int[] tagIds) {
        StringBuilder sb = new StringBuilder();
        for (int index=0; index<tagIds.length; index++) {
            sb.append(tagIds[index]);
            if (index != tagIds.length-1) sb.append(',');
        }

        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM tag WHERE id IN (" + sb.toString() + ")", new String[0]);
        int count = cur.getCount();
        Map<Integer, Tag> id2TagMap = new HashMap<>();

        for (int i=0; i<count; i++) {
            cur.moveToNext();
            Tag tag = loadFromCursor(cur);
            id2TagMap.put(tag.id, tag);
        }

        Tag[] tags = new Tag[count];
        for (int i=0; i<count; i++) {
            tags[i] = id2TagMap.get(tagIds[i]);
        }

        return tags;
    }

    public Tag getOrCreate(String name) {
        Tag tag = get(name);
        if (tag != null) return tag;
        return create(name);
    }

    private Tag get(String name) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT * FROM tag WHERE name=?", new String[] { name });
        if (! cur.moveToFirst()) return null;

        return loadFromCursor(cur);
    }

    public Tag[] search(String query) {
        SQLiteDatabase db = musicDatabase.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM tag WHERE name LIKE ?", new String[] { "%" + query + "%" });
        int count = cur.getCount();
        Tag[] result = new Tag[count];

        for (int i=0; i<count; i++) {
            cur.moveToNext();
            result[i] = loadFromCursor(cur);
        }

        return result;
    }


    private Tag loadFromCursor(Cursor cur) {
        Tag tag = new Tag();
        tag.id = cur.getInt(0);
        tag.name = cur.getString(1);
        tag.created_at = cur.getLong(2);
        tag.updated_at = cur.getLong(3);

        return tag;
    }

    private Tag create(String name) {
        SQLiteDatabase db = musicDatabase.getWritableDatabase();

        long time = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("created_at", time);
        values.put("updated_at", time);

        long rowId = db.insert("tag", null, values);
        Cursor cur = db.rawQuery("SELECT * FROM tag WHERE rowid=?", new String[] { String.valueOf(rowId) });
        cur.moveToFirst();
        return loadFromCursor(cur);
    }

    public void createTable(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tag(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER" +
                ")"
        );

    }

}
