package com.github.aoirint.campmusicplayer.activity.main.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.db.Group;
import com.github.aoirint.campmusicplayer.db.Music;

import java.io.IOException;

public class GroupArtworkEntryView extends ConstraintLayout {

    Group group;

    public GroupArtworkEntryView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_artwork_entry_view, this);
    }

    public void setGroup(Group group) {
        CampMusicPlayer app = (CampMusicPlayer) getContext().getApplicationContext();

        this.group = group;

        String text = group.name;
        TextView textView = findViewById(R.id.musicTextView);
        textView.setText(text);

        Bitmap artwork = null;
        try {
            // TODO: artwork handling
            if (group.artworkPath == null) {
                Music first = group.musics[0]; // > 0
                artwork = app.artworkCacheManager.loadOrCreate(first);
            }
            else {
                artwork = BitmapFactory.decodeFile(group.artworkPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView imageView = findViewById(R.id.artworkImageView);
        imageView.setImageBitmap(artwork);
    }

    public Group getGroup() {
        return group;
    }

}
