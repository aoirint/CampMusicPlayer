package com.github.aoirint.campmusicplayer.db.data;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class Tag implements Serializable {
    public Integer id;
    public String name;

    // UTC millis (System.currentTimeMillis)
    public Long created_at;
    public Long updated_at;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (! (obj instanceof Tag)) return false;
        Tag other = (Tag) obj;
        if (this.id == null && other.id == null) { // not in Database
            return Tag.isSame(this, other.name);
        }
        return this.id == other.id; // in Database
    }

    public static boolean isSame(Tag tag, String tagName) {
        return tag.name.equals(tagName);
    }

    public static Tag find(List<Tag> tags, String tagName) {
        for (int i=0; i<tags.size(); i++) {
            if (isSame(tags.get(i), tagName)) return tags.get(i);
        }
        return null;
    }


}
