package com.github.aoirint.campmusicplayer.music.queue;

import com.github.aoirint.campmusicplayer.db.data.Music;

public class ImmutableQueuePlayer implements QueuePlayer {
    Music[] musics;
    int index;

    public ImmutableQueuePlayer(Music[] musics) {
        this.musics = musics;
        this.index = 0;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public int getCount() {
        return musics.length;
    }

    @Override
    public Music getCurrent() {
        return musics[index];
    }

    @Override
    public Music get(int index) {
        return musics[index];
    }

    boolean valid(int index) {
        if (index < 0) return false;
        if (this.musics.length <= index) return false;
        return true;
    }

    @Override
    public boolean go(int index) {
        if (! valid(index)) return false;
        this.index = index;
        return true;
    }

    @Override
    public boolean goNext() {
        int index = this.index + 1;
        return go(index);
    }

    @Override
    public boolean goPrev() {
        int index = this.index - 1;
        return go(index);
    }

    @Override
    public boolean goFirst() {
        int index = 0;
        return go(index);
    }

    @Override
    public boolean goLast() {
        int index = this.musics.length - 1;
        return go(index);
    }

}
