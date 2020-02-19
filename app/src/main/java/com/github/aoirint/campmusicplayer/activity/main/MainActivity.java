package com.github.aoirint.campmusicplayer.activity.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.activity.main.view.GroupArtworkEntryView;
import com.github.aoirint.campmusicplayer.activity.main.view.MusicArtworkEntryView;
import com.github.aoirint.campmusicplayer.activity.group.GroupActivity;
import com.github.aoirint.campmusicplayer.activity.music.MusicSearchActivity;
import com.github.aoirint.campmusicplayer.activity.tag.TagActivity;
import com.github.aoirint.campmusicplayer.db.data.Album;
import com.github.aoirint.campmusicplayer.db.data.group.GeneratedAlbumGroup;
import com.github.aoirint.campmusicplayer.db.data.group.GeneratedTagGroup;
import com.github.aoirint.campmusicplayer.db.data.group.Group;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.Tag;
import com.github.aoirint.campmusicplayer.util.TriCheckState;
import com.github.aoirint.campmusicplayer.util.UnitUtil;

import java.io.IOException;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_MUSIC_REQUEST_CODE = 1000;
    public static final int MUSIC_TAG_REQUEST_CODE = 1001;
    public static final int REMOVE_MUSIC_REQUEST_CODE = 1002;
    public static Logger logger = Logger.getLogger("CampMusicPlayer");

    CampMusicPlayer app;
    ImageButton addButton;
    ImageButton removeButton;
    ImageButton toggleRepeatingButton;
    ImageButton toggleKaraokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicFileChooser();
            }
        });

        removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicRemover();
            }
        });

        toggleRepeatingButton = findViewById(R.id.toggleRepeatingButton);
        toggleKaraokeButton = findViewById(R.id.toggleKaraokeButton);

        toggleRepeatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean repeating = ! app.musicQueuePlayer.isRepeating();
                app.musicQueuePlayer.setRepeating(repeating);

                int icon;
                if (repeating) icon = R.drawable.ic_repeat_white_32dp;
                else icon = R.drawable.ic_arrow_forward_white_32dp;

                toggleRepeatingButton.setImageResource(icon);
            }
        });

        toggleKaraokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean karaoke = ! app.musicQueuePlayer.isKaraoke();
                app.musicQueuePlayer.setKaraoke(karaoke);

                int icon;
                if (karaoke) icon = R.drawable.ic_music_note_white_32dp;
                else icon = R.drawable.ic_music_voice;

                toggleKaraokeButton.setImageResource(icon);
            }
        });

        app = (CampMusicPlayer) getApplication();

        updateList();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    // TODO: create class
    private LinearLayout.LayoutParams initContainer(LinearLayout view) {
        Context context = getApplicationContext();
        view.removeAllViews();

        int size = 200; // dp
        int sizePx = UnitUtil.dp2px(context, size);
        int margin = 8;
        int marginPx = UnitUtil.dp2px(context, margin);

        view.setPadding(marginPx, marginPx, 0, marginPx);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx-marginPx*2, sizePx-marginPx*2);
        params.setMargins(0, 0, marginPx, 0);

        return params;
    }

    private void updateMusicsRecentlyAdded() {
        Context context = getApplicationContext();

        LinearLayout view = findViewById(R.id.musicContainer);
        LinearLayout.LayoutParams params = initContainer(view);

        Music[] musics = app.musicDatabase.musicTable.listMusicsRecentlyAdded();
        for (Music music : musics) {
            final MusicArtworkEntryView artworkView = new MusicArtworkEntryView(context);
            artworkView.setMusic(music);

            artworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMusicArtworkEntryClicked(artworkView);
                }
            });

            view.addView(artworkView, params);
        }

    }

    private void updateTagsRecentlyAdded() {
        Context context = getApplicationContext();

        LinearLayout view = findViewById(R.id.tagContainer);
        LinearLayout.LayoutParams params = initContainer(view);

        Tag[] tags = app.musicDatabase.tagTable.listTagsRecentlyAssigned();
        Group[] groups = new Group[tags.length];
        for (int i=0; i<tags.length; i++) {
            Tag tag = tags[i];
            groups[i] = new GeneratedTagGroup(tag);
        }

        for (Group group: groups) {
            final GroupArtworkEntryView artworkView = new GroupArtworkEntryView(context);
            artworkView.setGroup(group);

            artworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGroupArtworkEntryClicked(artworkView);
                }
            });

            view.addView(artworkView, params);
        }

    }

    private void updateAlbumsRecentlyAdded() {
        Context context = getApplicationContext();

        LinearLayout view = findViewById(R.id.albumContainer);
        LinearLayout.LayoutParams params = initContainer(view);

        Album[] albums = app.musicDatabase.albumTable.getAlbumsRecentlyAssigned();
        Group[] groups = new Group[albums.length];
        for (int i=0; i<albums.length; i++) {
            Album album = albums[i];
            groups[i] = new GeneratedAlbumGroup(album);
        }

        for (Group group: groups) {
            final GroupArtworkEntryView artworkView = new GroupArtworkEntryView(context);
            artworkView.setGroup(group);

            artworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGroupArtworkEntryClicked(artworkView);
                }
            });

            view.addView(artworkView, params);
        }
    }

    public void updateList() {
        updateMusicsRecentlyAdded();
        updateTagsRecentlyAdded();
        updateAlbumsRecentlyAdded();
    }

    public void openMusicFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("audio/*");

        startActivityForResult(intent, ADD_MUSIC_REQUEST_CODE);
    }

    public void openMusicRemover() {
        Intent intent = new Intent(getApplicationContext(), MusicSearchActivity.class);

        startActivityForResult(intent, REMOVE_MUSIC_REQUEST_CODE);
    }

    public void onMusicArtworkEntryClicked(MusicArtworkEntryView artworkView) {
        Intent intent = new Intent(this, TagActivity.class);

        Music[] musics = new Music[] { artworkView.getMusic() };
        intent.putExtra("musics", musics);

        startActivityForResult(intent, MUSIC_TAG_REQUEST_CODE);
    }

    // TODO: show music list
    public void onGroupArtworkEntryClicked(GroupArtworkEntryView artworkView) {
        Group group = artworkView.getGroup();

        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("group", group);

        startActivity(intent);
    }


    public void showToast(final String text, final int duration) {
        final Context context = getApplicationContext();
        Handler handler = new Handler(Looper.myLooper().getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
    }

    public void addMusicAsync(Uri... audioDocumentUris) {
        final Context context = getApplicationContext();

        MainActivityAddMusicTask task = new MainActivityAddMusicTask(context);
        task.delegate = new MainActivityAddMusicTask.Delegate() {
            @Override
            public void onStart() {
                showToast("Importing musics...", Toast.LENGTH_SHORT);
            }
            @Override
            public void onDoneAll(Music[] musics) {
                Handler handler = new Handler(Looper.myLooper().getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        showToast("Done importing!", Toast.LENGTH_SHORT);
                    }
                });
            }
            @Override
            public void onProgress(int progress) {
            }
            @Override
            public void onSuccess(Music info) {
            }
            @Override
            public void onFailed(Uri uri, IOException error) {
            }
        };
        task.execute(audioDocumentUris);
    }

    public void addMusicsAsync(ClipData uriClipData) {
        Uri[] uris = new Uri[uriClipData.getItemCount()];

        for (int i=0; i<uriClipData.getItemCount(); i++) {
            uris[i] = uriClipData.getItemAt(i).getUri();
        }

        addMusicAsync(uris);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == ADD_MUSIC_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    addMusicsAsync(clipData);
                } else {
                    Uri audioUri = data.getData();
                    addMusicAsync(audioUri);
                }
            }
        }
        else if (requestCode == MUSIC_TAG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Music[] musics = (Music[]) data.getSerializableExtra("musics");
                Tag[] tags = (Tag[]) data.getSerializableExtra("tags");
                TriCheckState[] checkStates = (TriCheckState[]) data.getSerializableExtra("checkStates");

                for (int i=0; i<tags.length; i++) {
                    if (checkStates[i] != TriCheckState.CHECKED) continue;
                    tags[i] = app.musicDatabase.tagTable.getOrCreate(tags[i].name);
                    logger.info(tags[i].id + ", " + tags[i].name);
                }

                long time = System.currentTimeMillis();
                for (Music music: musics) {
                    for (int i=0; i<tags.length; i++) {
                        Tag tag = tags[i];
                        TriCheckState checkState = checkStates[i];

                        switch (checkState) {
                            case UNDEFINED: // keep current state
                                break;
                            case UNCHECKED: // removed
                                app.musicDatabase.musicTagRelationTable.removeTag(music, tag);
                                break;
                            case CHECKED: // added
                                app.musicDatabase.musicTagRelationTable.addTag(music, tag, time);
                                break;
                        }
                    }
                }

                updateList();
            }
        }
        else if (requestCode == REMOVE_MUSIC_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Music[] musics = (Music[]) data.getSerializableExtra("musics");

                app.musicDatabase.musicTable.removeAll(musics);

                updateList();
            }

       }

    }
}
