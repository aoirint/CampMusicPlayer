package com.github.aoirint.campmusicplayer.db.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Album implements Serializable {
    public Integer id;
    public String mbid;
    public String name;
    public Artist artist;
    public Long created_at;
    public Long updated_at;

    public Album() {
    }

    public Album(AlbumKey key) {
        this.name = key.name;
        this.artist = key.artist;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Album)) return false;
        Album other = (Album) obj;
        if (this.id == null && other.id == null) {
            return Objects.equals(this.artist, other.artist) && Objects.equals(this.name, other.name);
        }
        return this.id == other.id;
    }

}
