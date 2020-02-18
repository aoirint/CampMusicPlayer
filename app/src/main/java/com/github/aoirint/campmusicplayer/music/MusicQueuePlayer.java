package com.github.aoirint.campmusicplayer.music;

import android.content.Context;
import android.net.Uri;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.group.Group;
import com.github.aoirint.campmusicplayer.music.player.KaraokeMusicPlayer;
import com.github.aoirint.campmusicplayer.music.player.IMusicPlayer;
import com.github.aoirint.campmusicplayer.music.player.BasicMusicPlayer;
import com.github.aoirint.campmusicplayer.music.queue.ImmutableQueuePlayer;
import com.github.aoirint.campmusicplayer.music.queue.QueuePlayer;

import java.io.File;
import java.io.IOException;

public class MusicQueuePlayer {
    Context context;
    CampMusicPlayer app;
    IMusicPlayer musicPlayer;

    QueuePlayer queue;
    Music music;
    File artworkFile;

    boolean pausing;
    boolean repeating = true;
    boolean karaoke;

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

    public void clearMusicPlayer() {
        if (musicPlayer == null) return;

        musicPlayer.stop();
        musicPlayer.release();
        musicPlayer = null;

        pausing = false;
    }
    public void initMediaPlayer() {
        clearMusicPlayer();

        if (! karaoke) {
            musicPlayer = new BasicMusicPlayer(context);
        }
        else {
            musicPlayer = new KaraokeMusicPlayer(context);
        }

        musicPlayer.setOnCompletionListener(new IMusicPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMusicPlayer musicPlayer) {
                clearMusicPlayer();
            }
        });

    }

    public void reset() {
        clearMusicPlayer();
        if (music == null) return;

        initMediaPlayer();
        try {
            musicPlayer.release();
            musicPlayer.setDataSource(context, music.getUri());
            musicPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        app.sendUpdateNotification();
    }

    public void play() {
        reset();
        musicPlayer.start();

        app.sendUpdateNotification();

    }

    public void stop() {
        clearMusicPlayer();
    }

    public void pause() {
        if (musicPlayer == null) return;
        musicPlayer.pause();
        pausing = true;

        app.sendUpdateNotification();
    }

    public void resume() {
        if (musicPlayer == null) return;
        musicPlayer.start();
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
        if (! hasNext && repeating) {
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
        if (! hasNext && repeating) {
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
        clearMusicPlayer();

        app.sendUpdateNotification();
    }
    public void setQueue(Group group) {
        Music[] musics = group.getMusics(context);
        this.queue = new ImmutableQueuePlayer(musics);
        updateCurrent();
    }

    public boolean isPlaying() {
        return musicPlayer != null && musicPlayer.isPlaying();
    }

    public boolean isBeginning() {
        int timeMillis = musicPlayer.getCurrentPosition();
        return timeMillis < 1200;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }


    public boolean isKaraoke() {
        return karaoke;
    }

    public void setKaraoke(boolean karaoke) {
        this.karaoke = karaoke;

        // TODO: reflect to current track
    }


}
