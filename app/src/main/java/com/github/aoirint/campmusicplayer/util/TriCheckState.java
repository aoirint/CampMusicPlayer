package com.github.aoirint.campmusicplayer.util;


import com.github.aoirint.campmusicplayer.R;

import java.io.Serializable;

public enum TriCheckState implements Serializable {
    UNCHECKED,
    CHECKED,
    UNDEFINED,
    ;

    public int getDrawable() {
        switch (this) {
            case UNDEFINED:
                return R.drawable.ic_indeterminate_check_box_7f7f7f_32dp;
            case UNCHECKED:
                return R.drawable.ic_check_box_outline_blank_7f7f7f_32dp;
            case CHECKED:
                return R.drawable.ic_check_box_7f7f7f_32dp;
        }
        return R.drawable.ic_indeterminate_check_box_7f7f7f_32dp;
    }

}
