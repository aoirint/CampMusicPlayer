package com.github.aoirint.campmusicplayer.activity.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.db.data.group.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    Context context;
    CampMusicPlayer app;
    Group group;
    List<Music> musics;

    Button cancelButton;
    Button saveButton;
    ImageButton playGroupButton;
    RecyclerView musicListView;
    RecyclerView.Adapter<ViewHolder> musicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        context = getApplicationContext();
        app = (CampMusicPlayer) getApplicationContext();
        musics = new ArrayList<>();

        loadIntent();

        initView();
        initMusicListView();

        musicListAdapter.notifyDataSetChanged();
    }

    void loadIntent() {
        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");
        musics = new ArrayList<>(Arrays.asList(group.getMusics(context)));
    }

    void initView() {
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishCancel();
            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSave();
            }
        });

        saveButton.setEnabled(group.isEditable());

        playGroupButton = findViewById(R.id.playGroupButton);
        playGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group.isEditable()) {
                    // update group?
                }

                app.musicPlayer.setQueue(group);
                app.musicPlayer.play();
            }
        });

    }

    void initMusicListView() {
        musicListView = findViewById(R.id.musicListView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListView.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (! group.isEditable()) return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, 0);
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int oldPosition = viewHolder.getAdapterPosition();
                int newPosition = target.getAdapterPosition();
                Collections.swap(musics, oldPosition, newPosition);
                musicListAdapter.notifyItemMoved(oldPosition, newPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        touchHelper.attachToRecyclerView(musicListView);

        musicListAdapter = new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.cell_music, parent, false));
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
                final Music music = musics.get(position);
                String text = music.title + " - " + music.album.artist;
                viewHolder.textView.setText(text);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
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
    void finishSave() {
        setResult(RESULT_OK);
        Intent intent = new Intent();
        intent.putExtra("musics", musics.toArray(new Music[musics.size()]));

        setIntent(intent);
        finish();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.musicTitleTextView);
        }

    }

}
