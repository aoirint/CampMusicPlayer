package com.github.aoirint.campmusicplayer.db;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.github.aoirint.campmusicplayer.util.HashUtil;

import java.io.IOException;
import java.io.Serializable;

import static android.media.MediaMetadataRetriever.*;

public class Music implements Serializable {
    Integer id;
    String uri;
    String hash;

    public String title;
    public String artist;
    public String album;

    public String genre;
    public String duration;
    public String year;

    public String discNumber;
    public String cdTrackNumber;
    public String numTracks;

    public String author;
    public String composer;
    public String writer;
    public String albumArtist;

    // UTC millis (System.currentTimeMillis)
    Long created_at;
    Long updated_at;
    Long played_at;

    public Music() {
    }

    public void updatePlayedAt() {
        played_at = System.currentTimeMillis();
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public static Music createFromUri(Context context, Uri uri) throws IOException {
        Music info = new Music();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);

        info.uri = uri.toString();
        info.hash = HashUtil.calcUriHash(context, uri);
        info.title = mmr.extractMetadata(METADATA_KEY_TITLE);
        info.artist = mmr.extractMetadata(METADATA_KEY_ARTIST);
        info.album = mmr.extractMetadata(METADATA_KEY_ALBUM);

        info.genre = mmr.extractMetadata(METADATA_KEY_GENRE);
        info.duration = mmr.extractMetadata(METADATA_KEY_DURATION);

        info.year = mmr.extractMetadata(METADATA_KEY_YEAR);
        info.discNumber = mmr.extractMetadata(METADATA_KEY_DISC_NUMBER);

        info.cdTrackNumber = mmr.extractMetadata(METADATA_KEY_CD_TRACK_NUMBER);
        info.numTracks = mmr.extractMetadata(METADATA_KEY_NUM_TRACKS);

        info.author = mmr.extractMetadata(METADATA_KEY_AUTHOR);
        info.composer = mmr.extractMetadata(METADATA_KEY_COMPOSER);
        info.writer = mmr.extractMetadata(METADATA_KEY_WRITER);
        info.albumArtist = mmr.extractMetadata(METADATA_KEY_ALBUMARTIST);

        info.created_at = info.updated_at = System.currentTimeMillis();

        return info;
    }

}
