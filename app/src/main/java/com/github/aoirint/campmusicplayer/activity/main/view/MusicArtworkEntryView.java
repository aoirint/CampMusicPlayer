package com.github.aoirint.campmusicplayer.activity.main.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.db.data.Music;

import java.io.IOException;

public class MusicArtworkEntryView extends ConstraintLayout {

    Music music;

    public MusicArtworkEntryView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_artwork_entry_view, this);
    }

    public void setMusic(Music music) {
        CampMusicPlayer app = (CampMusicPlayer) getContext().getApplicationContext();

        this.music = music;

        String text = music.title + " - " + music.album.artist.name;
        TextView textView = findViewById(R.id.musicTextView);
        textView.setText(text);

        Bitmap artwork = null;
        try {
            artwork = app.artworkCacheManager.loadOrCreate(music.getUri());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView imageView = findViewById(R.id.artworkImageView);
        imageView.setImageBitmap(artwork);
    }

    public Music getMusic() {
        return music;
    }

}
