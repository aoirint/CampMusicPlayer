package com.github.aoirint.campmusicplayer.activity.tag.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.github.aoirint.campmusicplayer.util.TriCheckState;

import static com.github.aoirint.campmusicplayer.activity.main.MainActivity.logger;

public class TriStateCheckbox extends CheckBox {
    TriCheckState checkState = TriCheckState.UNDEFINED;
    public boolean hasNoUndefined;
    public Delegate delegate;

    public TriStateCheckbox(Context context) {
        super(context);
        init();
    }

    public TriStateCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TriStateCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        updateButtonDrawable();

        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TriCheckState nextState = TriCheckState.UNCHECKED;
                switch (checkState) {
                    case UNDEFINED:
                        nextState = TriCheckState.UNCHECKED;
                        break;
                    case UNCHECKED:
                        nextState = TriCheckState.CHECKED;
                        break;
                    case CHECKED:
                        if (hasNoUndefined) {
                            nextState = TriCheckState.UNCHECKED;
                        }
                        else {
                            nextState = TriCheckState.UNDEFINED;
                        }
                        break;
                }

                setCheckState(nextState);
            }
        });
    }

    void updateButtonDrawable() {
        setButtonDrawable(checkState.getDrawable());
    }

    public void setCheckState(TriCheckState checkState) {
        this.checkState = checkState;
        updateButtonDrawable();
        if (delegate != null) delegate.onCheckedChanged(this, this.checkState);
    }

    public TriCheckState getCheckState() {
        return checkState;
    }


    public interface Delegate {
        void onCheckedChanged(CompoundButton buttonView, TriCheckState checkState);

    }

}
