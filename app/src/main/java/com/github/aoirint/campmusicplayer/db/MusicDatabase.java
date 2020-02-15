package com.github.aoirint.campmusicplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDatabase extends SQLiteOpenHelper {
    public static final String DB_PATH = "db.sqlite3";
    public static final int DB_VERSION = 1;

    Context context;

    public MusicTable musicTable;
    public TagTable tagTable;
    public MusicTagRelationTable musicTagRelationTable;

    public MusicDatabase(Context context) {
        super(context, DB_PATH, null, DB_VERSION);
        this.context = context;

        musicTable = new MusicTable(this);
        tagTable = new TagTable(this);
        musicTagRelationTable = new MusicTagRelationTable(this);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        musicTable.createTable(db);
        tagTable.createTable(db);
        musicTagRelationTable.createTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
