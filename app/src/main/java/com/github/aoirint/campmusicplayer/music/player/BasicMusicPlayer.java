package com.github.aoirint.campmusicplayer.music.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.PowerManager;

import java.io.IOException;

public class BasicMusicPlayer implements IMusicPlayer, IHasEffector {
    Context context;
    MediaPlayer mediaPlayer;
    Equalizer equalizer;
    OnCompletionListener listener;

    public BasicMusicPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void prepare() throws IOException {
        mediaPlayer.prepare();
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null) listener.onCompletion(BasicMusicPlayer.this);
            }
        });

        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);
        short numOfBands = equalizer.getNumberOfBands();
        short minLevel = equalizer.getBandLevelRange()[0];
        for (short i=0; i<numOfBands; i++) {
            equalizer.setBandLevel(i, minLevel);
        }

        mediaPlayer.setDataSource(context, uri);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

}
