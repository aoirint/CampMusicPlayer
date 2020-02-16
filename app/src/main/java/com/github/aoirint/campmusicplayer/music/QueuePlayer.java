package com.github.aoirint.campmusicplayer.music;

import com.github.aoirint.campmusicplayer.db.data.Music;

public interface QueuePlayer {

    Integer getIndex();
    int getCount();

    Music getCurrent();
    Music get(int index);

    boolean go(int index);
    boolean goNext();
    boolean goPrev();
    boolean goFirst();
    boolean goLast();

}
