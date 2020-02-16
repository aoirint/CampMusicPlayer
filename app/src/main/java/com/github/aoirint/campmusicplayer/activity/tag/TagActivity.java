package com.github.aoirint.campmusicplayer.activity.tag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.aoirint.campmusicplayer.activity.tag.view.TriStateCheckbox;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.CampMusicPlayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.db.data.Tag;
import com.github.aoirint.campmusicplayer.util.TriCheckState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagActivity extends AppCompatActivity {
    Context context;
    CampMusicPlayer app;

    Music[] musics;
    List<Tag> candidateTags;
    List<Tag> selectionTags;
    Map<Tag, TriCheckState> firstStateMap;
    List<TriCheckState> checkStates;

    Button cancelButton;
    Button saveButton;
    ListView tagSelectListView;
    BaseAdapter tagSelectListAdapter;

    TextView tagInputTextView;

    Button cancelTagButton;
    Button createTagButton;
    ListView tagSearchListView;
    BaseAdapter tagSearchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        context = getApplicationContext();
        app = (CampMusicPlayer) getApplicationContext();

        loadIntent();

        candidateTags = new ArrayList<>();
        selectionTags = new ArrayList<>();
        checkStates = new ArrayList<>();

        initTagInputTextView();

        initSelectView();
        initSelectListView();

        initSearchView();
        initSearchListView();

        updateCandidates();
        initSelections();
    }

    void initSelections() {
        selectionTags = new ArrayList<>();
        checkStates = new ArrayList<>();
        firstStateMap = new HashMap<>();

        for (Music music: musics) {
            List<Tag> musicTags = Arrays.asList(app.musicDatabase.musicTagRelationTable.getTags(music));
            for (Tag tag: musicTags) {
                if (selectionTags.contains(tag)) continue;
                selectionTags.add(0, tag);
                checkStates.add(0, TriCheckState.CHECKED);
            }

            for (int index=0; index<selectionTags.size(); index++) {
                Tag tag = selectionTags.get(index);
                if (musicTags.contains(tag)) continue;
                checkStates.set(index, TriCheckState.UNDEFINED);
            }
        }

        for (int index=0; index<selectionTags.size(); index++) {
            firstStateMap.put(selectionTags.get(index), checkStates.get(index));
        }
    }

    void updateCandidates() {
        String name = tagInputTextView.getText().toString();

        Tag[] candidates = !name.isEmpty() ? app.musicDatabase.tagTable.search(name) : new Tag[0];
        candidateTags = new ArrayList<>(Arrays.asList(candidates));

        tagSearchListAdapter.notifyDataSetChanged(); // make listview unfocusable; otherwise inputtextview lose focus
    }

    void unfocusTagInput() {
        tagInputTextView.setText("");

        tagInputTextView.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tagInputTextView.getWindowToken(), 0);

        showSelectListView();
    }

    void loadIntent() {
        Intent intent = getIntent();
        musics = (Music[]) intent.getSerializableExtra("musics");
    }

    void showSelectListView() {
        tagSelectListView.setVisibility(View.VISIBLE);
        tagSearchListView.setVisibility(View.INVISIBLE);
    }
    void showSearchListView() {
        tagSelectListView.setVisibility(View.INVISIBLE);
        tagSearchListView.setVisibility(View.VISIBLE);
    }

    void initTagInputTextView() {
        tagInputTextView = findViewById(R.id.tagInputTextView);

        tagInputTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cancelButton.setVisibility(View.INVISIBLE);
                    saveButton.setVisibility(View.INVISIBLE);
                    createTagButton.setVisibility(View.VISIBLE);
                    cancelTagButton.setVisibility(View.VISIBLE);
                }
                else {
                    cancelButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    createTagButton.setVisibility(View.INVISIBLE);
                    cancelTagButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        tagInputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCandidates();
                if (s.length() == 0) {
                    showSelectListView();
                }
                else {
                    showSearchListView();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    void initSelectView() {
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
    }

    void initSelectListView() {
        tagSelectListView = findViewById(R.id.tagSelectListView);
        tagSelectListView.setFocusable(false);
        tagSelectListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return selectionTags.size();
            }

            @Override
            public Object getItem(int position) {
                return selectionTags.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.cell_tag, null);
                }

                final Tag tag = (Tag) getItem(position);
                TriStateCheckbox checkBox = convertView.findViewById(R.id.tagCheckbox);
                checkBox.setText(tag.name);

                checkBox.delegate = null;

                final TriCheckState checkState = checkStates.get(position);
                TriCheckState prevCheckState = checkBox.getCheckState();
                if (checkState != prevCheckState) {
                    checkBox.setCheckState(checkState);
                    checkBox.jumpDrawablesToCurrentState(); // skip anim
                }

                checkBox.hasNoUndefined = firstStateMap.get(tag) != TriCheckState.UNDEFINED;

                checkBox.delegate = new TriStateCheckbox.Delegate() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, TriCheckState checkState) {
                        checkStates.set(position, checkState);
                        tagSelectListAdapter.notifyDataSetChanged();
                    }
                };

                return convertView;
            }
        };

        tagSelectListView.setAdapter(tagSelectListAdapter);
    }

    void initSearchView() {
        cancelTagButton = findViewById(R.id.cancelTagButton);
        cancelTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfocusTagInput();
            }
        });

        createTagButton = findViewById(R.id.createTagButton);
        createTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = tagInputTextView.getText().toString();
                if (tagName.isEmpty()) return;

                Tag tag = Tag.find(candidateTags, tagName);
                if (tag == null) {
                    tag = Tag.find(selectionTags, tagName);
                }

                if (tag == null) {
                    tag = new Tag();
                    tag.name = tagName;
                }
                else {
                    int index;
                    while ((index = selectionTags.indexOf(tag)) != -1) {
                        selectionTags.remove(index);
                        checkStates.remove(index);
                    }
                }

                selectionTags.add(0, tag);
                checkStates.add(0, TriCheckState.CHECKED);

                tagSelectListAdapter.notifyDataSetChanged();
                unfocusTagInput();
            }
        });

    }

    void initSearchListView() {
        tagSearchListView = findViewById(R.id.tagSearchListView);
        tagSearchListView.setFocusable(false);
        tagSearchListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return candidateTags.size();
            }

            @Override
            public Object getItem(int position) {
                return candidateTags.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.cell_tag, null);
                }

                final Tag tag = (Tag) getItem(position);
                TriStateCheckbox checkBox = convertView.findViewById(R.id.tagCheckbox);
                checkBox.setText(tag.name);

                checkBox.delegate = null;

                int tagIndex = selectionTags.indexOf(tag);
                TriCheckState checkState = TriCheckState.UNCHECKED;
                if (tagIndex != -1) checkState = checkStates.get(tagIndex);

                TriCheckState prevCheckState = checkBox.getCheckState();
                if (checkState != prevCheckState) {
                    checkBox.setCheckState(checkState);
                    checkBox.jumpDrawablesToCurrentState(); // skip anim
                }

                checkBox.hasNoUndefined = firstStateMap.get(tag) != TriCheckState.UNDEFINED;

                checkBox.delegate = new TriStateCheckbox.Delegate() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, TriCheckState checkState) {
                        int tagIndex = selectionTags.indexOf(tag);

                        if (tagIndex == -1) {
                            selectionTags.add(0, tag);
                            checkStates.add(0, checkState);
                        }
                        else {
                            checkStates.set(tagIndex, checkState);
                        }

                        tagSelectListAdapter.notifyDataSetChanged();
                    }
                };

                return convertView;
            }
        };

        tagSearchListView.setAdapter(tagSearchListAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void finishCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void finishSave() {
        Intent intent = new Intent();
        intent.putExtra("musics", musics);

        Tag[] tags = selectionTags.toArray(new Tag[selectionTags.size()]);
        intent.putExtra("tags", tags);

        TriCheckState[] states = checkStates.toArray(new TriCheckState[checkStates.size()]);
        intent.putExtra("checkStates", states);

        setResult(RESULT_OK, intent);
        finish();
    }

}
