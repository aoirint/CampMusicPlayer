package com.github.aoirint.campmusicplayer.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.github.aoirint.campmusicplayer.util.HashUtil;

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

    public File getArtworkCachePath(Uri musicUri) {
        File cacheDir = context.getCacheDir();
        File artworkCacheDir = new File(cacheDir, ARTWORK_DIR);

        String hash = HashUtil.calcUriStringHash(musicUri);

        return new File(artworkCacheDir, new File(String.format("%s.jpg", hash)).getName());
    }

    public Bitmap loadOrCreate(Uri musicUri) throws IOException {
        File file = getArtworkCachePath(musicUri);
        if (file.exists()) {
            return loadArtworkCache(file);
        }

        return createArtworkCache(musicUri);
    }

    public Bitmap loadArtworkCache(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        fis.close();

        return bitmap;
    }

    private Bitmap createArtworkCache(Uri musicUri) throws IOException {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, musicUri);

        byte[] artworkBytes = mmr.getEmbeddedPicture();

        Bitmap artwork = BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.length);
        File path = getArtworkCachePath(musicUri);
        path.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(path);
        artwork.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        return artwork;
    }


}
