package com.github.aoirint.campmusicplayer.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArtworkCacheManager {
    public static final String ARTWORK_DIR = "artworks";

    Context context;

    public ArtworkCacheManager(Context context) {
        this.context = context;
    }

    public File getArtworkCachePath(Music music) {
        File cacheDir = context.getCacheDir();
        File artworkCacheDir = new File(cacheDir, ARTWORK_DIR);

        return new File(artworkCacheDir, new File(String.format("%d.jpg", music.id)).getName());
    }

    public Bitmap loadOrCreate(Music music) throws IOException {
        File path = getArtworkCachePath(music);
        if (path.exists()) {
            return loadArtworkCache(music);
        }

        return createArtworkCache(music);
    }

    public Bitmap loadArtworkCache(Music music) throws IOException {
        File path = getArtworkCachePath(music);

        FileInputStream fis = new FileInputStream(path);
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        fis.close();

        return bitmap;
    }

    public Bitmap createArtworkCache(Music music) throws IOException {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, music.getUri());

        byte[] artworkBytes = mmr.getEmbeddedPicture();

        Bitmap artwork = BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.length);
        File path = getArtworkCachePath(music);
        path.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(path);
        artwork.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        return artwork;
    }


}
