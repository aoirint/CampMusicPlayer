package com.github.aoirint.campmusicplayer.db.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class Artist implements Serializable {
    public Integer id;
    public String mbid;
    public String name;
    public Long created_at;
    public Long updated_at;

    public Artist() {
    }

    public Artist(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Artist)) return false;
        Artist other = (Artist) obj;
        if (this.id == null && other.id == null) {
            return this.name.equals(other.name);
        }
        return this.id == other.id;
    }

}
