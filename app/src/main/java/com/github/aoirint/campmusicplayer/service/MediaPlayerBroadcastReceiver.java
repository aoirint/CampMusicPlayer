package com.github.aoirint.campmusicplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;

import static com.github.aoirint.campmusicplayer.activity.main.MainActivity.logger;

public class MediaPlayerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CampMusicPlayer app = (CampMusicPlayer) context.getApplicationContext();

        switch (intent.getAction()) {
            case "play":
                if (app.musicPlayer.isPausing()) {
                    app.musicPlayer.resume();
                }
                else {
                    app.musicPlayer.play();
                }
                break;
            case "pause":
                app.musicPlayer.pause();
                break;
            case "previous":
                if (! app.musicPlayer.isBeginning()) {
                    if (app.musicPlayer.isPlaying()) {
                        app.musicPlayer.play(); // reset
                    }
                    else {
                        app.musicPlayer.reset();
                    }
                }
                else {
                    app.musicPlayer.goPrev();
                }
                break;
            case "next":
                app.musicPlayer.goNext();
                break;
        }

        app.sendUpdateNotification();
    }

}
