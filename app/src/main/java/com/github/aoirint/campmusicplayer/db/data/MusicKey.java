package com.github.aoirint.campmusicplayer.db.data;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.github.aoirint.campmusicplayer.util.HashUtil;

import java.io.IOException;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_AUTHOR;
import static android.media.MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER;
import static android.media.MediaMetadataRetriever.METADATA_KEY_COMPOSER;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_GENRE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_WRITER;
import static android.media.MediaMetadataRetriever.METADATA_KEY_YEAR;

public class MusicKey {
    public String uri;
    public String hash;

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


    public static MusicKey createFromUri(Context context, Uri uri) throws IOException {
        MusicKey key = new MusicKey();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);

        key.uri = uri.toString();
        key.hash = HashUtil.calcFileHash(context, uri);
        key.title = mmr.extractMetadata(METADATA_KEY_TITLE);
        key.artist = mmr.extractMetadata(METADATA_KEY_ARTIST);
        key.album = mmr.extractMetadata(METADATA_KEY_ALBUM);

        key.genre = mmr.extractMetadata(METADATA_KEY_GENRE);
        key.duration = mmr.extractMetadata(METADATA_KEY_DURATION);

        key.year = mmr.extractMetadata(METADATA_KEY_YEAR);
        key.discNumber = mmr.extractMetadata(METADATA_KEY_DISC_NUMBER);

        key.cdTrackNumber = mmr.extractMetadata(METADATA_KEY_CD_TRACK_NUMBER);
        key.numTracks = mmr.extractMetadata(METADATA_KEY_NUM_TRACKS);

        key.author = mmr.extractMetadata(METADATA_KEY_AUTHOR);
        key.composer = mmr.extractMetadata(METADATA_KEY_COMPOSER);
        key.writer = mmr.extractMetadata(METADATA_KEY_WRITER);
        key.albumArtist = mmr.extractMetadata(METADATA_KEY_ALBUMARTIST);

        return key;
    }

}
