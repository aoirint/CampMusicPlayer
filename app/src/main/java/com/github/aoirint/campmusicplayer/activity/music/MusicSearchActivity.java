package com.github.aoirint.campmusicplayer.activity.music;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.activity.tag.view.TriStateCheckbox;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.util.LogUtil;
import com.github.aoirint.campmusicplayer.util.TriCheckState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.aoirint.campmusicplayer.activity.main.MainActivity.logger;

public class MusicSearchActivity extends AppCompatActivity {
    CampMusicPlayer app;

    Button cancelButton;
    Button removeButton;

    TextView searchTextView;

    RecyclerView musicListView;
    RecyclerView.Adapter<MusicSearchActivity.ViewHolder> musicListAdapter;

    List<Music> musicsSelected;
    List<Music> musicCandidates;
    List<Music> musics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_search);

        app = (CampMusicPlayer) getApplication();

        musics = new ArrayList<>();
        musicsSelected = new ArrayList<>();
        musicCandidates = new ArrayList<>();

        cancelButton = findViewById(R.id.cancelButton);
        removeButton = findViewById(R.id.removeButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCancel();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishRemove();
            }
        });

        initMusicListView();

        searchTextView = findViewById(R.id.keyword);
        searchTextView.requestFocus();
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    musics = new ArrayList<>();
                    musics.addAll(musicsSelected);
                    for (Music music: app.musicDatabase.musicTable.listMusicsRecentlyAdded()) {
                        if (musics.contains(music)) continue;
                        musics.add(music);
                    }
                }
                else {
                    musics = app.musicDatabase.search(s.toString());
                }

                musicListAdapter.notifyDataSetChanged();
            }
           @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchTextView.setText("");

    }

    void initMusicListView() {
        musicListView = findViewById(R.id.musicSearchListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListView.setLayoutManager(layoutManager);
        musicListView.setFocusable(false);

        musicListAdapter = new RecyclerView.Adapter<MusicSearchActivity.ViewHolder>() {
            @NonNull
            @Override
            public MusicSearchActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MusicSearchActivity.ViewHolder(getLayoutInflater().inflate(R.layout.cell_music_selection, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull MusicSearchActivity.ViewHolder viewHolder, int position) {
                final Music music = musics.get(position);
                String text = music.title + " - " + music.album.artist;
                viewHolder.checkBox.setText(text);

                viewHolder.checkBox.delegate = null;

                viewHolder.checkBox.setCheckState(musicsSelected.contains(music) ? TriCheckState.CHECKED : TriCheckState.UNCHECKED);

                viewHolder.checkBox.delegate = new TriStateCheckbox.Delegate() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, TriCheckState checkState) {
                        if (checkState == TriCheckState.CHECKED) {
                            musicsSelected.add(music);
                        } else {
                            musicsSelected.remove(music);
                        }

                        musicListAdapter.notifyDataSetChanged();
                    }
                };
            }

            @Override
            public int getItemCount() {
                return musics.size();
            }
        };
        musicListView.setAdapter(musicListAdapter);
    }

    void finishCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
    void finishRemove() {
        Intent intent = new Intent();
        intent.putExtra("musics", musicsSelected.toArray(new Music[musicsSelected.size()]));

        setResult(RESULT_OK, intent);
        finish();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TriStateCheckbox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.musicTitleCheckbox);
            checkBox.hasNoUndefined = true;
        }

    }

}
