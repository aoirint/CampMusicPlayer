package com.github.aoirint.campmusicplayer.activity.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.MusicKey;

import java.io.IOException;

public class MainActivityAddMusicTask extends AsyncTask<Uri, Integer, Music[]> {

    public Delegate delegate;

    Context context;

    public MainActivityAddMusicTask(Context context) {
        this.context = context;
    }

    @Override
    protected Music[] doInBackground(Uri[] uris) {
        Music[] musics = new Music[uris.length];

        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();

        publishProgress(0);
        if (delegate != null) delegate.onStart();

        for (int index=0; index<uris.length; index++) {
            Uri uri = uris[index];

            context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                MusicKey key = MusicKey.createFromUri(context, uri);
                app.artworkCacheManager.loadOrCreate(uri);

                Music music = app.musicDatabase.musicTable.getOrCreate(key);
                if (delegate != null) delegate.onSuccess(music);

                musics[index] = music;

            } catch (IOException error) {
                error.printStackTrace();
                if (delegate != null) delegate.onFailed(uri, error);
            }

            publishProgress(index+1);
        }

        return musics;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (delegate != null) delegate.onProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Music[] result) {
        if (delegate != null) {
            delegate.onDoneAll(result);
        }
    }

    public interface Delegate {
        void onStart();
        void onProgress(int progress);
        void onDoneAll(Music[] musics);

        void onSuccess(Music info);
        void onFailed(Uri uri, IOException error);

    }

}
