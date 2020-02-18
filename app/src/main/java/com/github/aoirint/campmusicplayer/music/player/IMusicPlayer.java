package com.github.aoirint.campmusicplayer.music.player;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

public interface IMusicPlayer {

    void prepare() throws IOException;
    void release();

    void start();
    void pause();
    void stop();

    void setDataSource(Context context, Uri uri) throws IOException;

    boolean isPlaying();
    int getCurrentPosition();

    void setOnCompletionListener(OnCompletionListener listener);

    interface OnCompletionListener {
        void onCompletion(IMusicPlayer musicPlayer);
    }

}
