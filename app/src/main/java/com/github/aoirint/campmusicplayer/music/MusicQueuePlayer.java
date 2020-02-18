package com.github.aoirint.campmusicplayer.music;

import android.content.Context;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.PowerManager;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.group.Group;
import com.github.aoirint.campmusicplayer.music.queue.ImmutableQueuePlayer;
import com.github.aoirint.campmusicplayer.music.queue.QueuePlayer;

import java.io.File;
import java.io.IOException;

public class MusicQueuePlayer {
    Context context;
    CampMusicPlayer app;
    MediaPlayer mediaPlayer;
    Equalizer equalizer;

    QueuePlayer queue;
    Music music;
    File artworkFile;

    boolean pausing;
    boolean loop;

    public MusicQueuePlayer(Context context) {
        this.context = context;
        this.app = (CampMusicPlayer) context.getApplicationContext();
    }

    public Music getCurrentMusic() {
        return music;
    }
    public File getCurrentArtwork() {
        return artworkFile;
    }

    public void clearMediaPlayer() {
        if (mediaPlayer == null) return;

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        equalizer.release();
        equalizer = null;

        pausing = false;
    }
    public void initMediaPlayer() {
        clearMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                clearMediaPlayer();
            }
        });

        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);
        short numOfBands = equalizer.getNumberOfBands();
        short minLevel = equalizer.getBandLevelRange()[0];
        for (short i=0; i<numOfBands; i++) {
            equalizer.setBandLevel(i, minLevel);
        }
    }

    public void reset() {
        clearMediaPlayer();
        if (music == null) return;

        initMediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, music.getUri());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        app.sendUpdateNotification();
    }

    AudioTrack track;
    public void play() {
        reset();

        // TODO: karaoke play feature
//        mediaPlayer.stop();
//
//        try {
//            track = MusicUtil.playKaraoke(context, music.getUri());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        app.sendUpdateNotification();

    }

    public void stop() {
        clearMediaPlayer();
    }

    public void pause() {
        if (mediaPlayer == null) return;
        mediaPlayer.pause();
        pausing = true;

        app.sendUpdateNotification();
    }

    public void resume() {
        if (mediaPlayer == null) return;
        mediaPlayer.start();
        pausing = false;

        app.sendUpdateNotification();
    }

    public boolean isPausing() {
        return pausing;
    }


    private void updateCurrent() {
        if (this.queue == null) {
            clearQueue();
            return;
        }

        Music music = this.queue.get(this.queue.getIndex());
        Uri musicUri = music.getUri();
        File artworkFile = app.artworkCacheManager.getArtworkCachePath(musicUri);

        this.music = music;
        this.artworkFile = artworkFile;

        app.sendUpdateNotification();
    }

    public void goNext() {
        if (this.queue == null) return;
        boolean hasNext = this.queue.goNext();
        if (! hasNext && loop) {
            hasNext = this.queue.goFirst();
        }
        if (hasNext) updateCurrent();
        else clearQueue();

        if (isPlaying()) {
            play();
        }
        else {
            reset();
        }
    }
    public void goPrev() {
        if (this.queue == null) return;
        boolean hasNext = this.queue.goPrev();
        if (! hasNext && loop) {
            hasNext = this.queue.goLast();
        }
        if (hasNext) updateCurrent();
        else clearQueue();

        if (isPlaying()) {
            play();
        }
        else {
            reset();
        }
    }

    public void clearQueue() {
        this.queue = null;
        this.music = null;
        this.artworkFile = null;
        clearMediaPlayer();

        app.sendUpdateNotification();
    }
    public void setQueue(Group group) {
        Music[] musics = group.getMusics(context);
        this.queue = new ImmutableQueuePlayer(musics);
        updateCurrent();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isBeginning() {
        int timeMillis = mediaPlayer.getCurrentPosition();
        return timeMillis < 1200;
    }

}