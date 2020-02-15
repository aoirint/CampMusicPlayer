package com.github.aoirint.campmusicplayer.db;

import java.io.Serializable;
import java.util.List;

public class Tag implements Serializable {
    public Integer id;
    public String name;

    // UTC millis (System.currentTimeMillis)
    Long created_at;
    Long updated_at;

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
