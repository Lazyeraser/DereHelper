package com.lazyeraser.imas.cgss.utils;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import com.lazyeraser.imas.derehelper.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lazyeraser on 2017/11/29.
 * 星座计算
 */

public class ConstellationHelper {

    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};

    private final static Integer[] constellationArr = new Integer[]{
            R.string.constellation_10,
            R.string.constellation_11,
            R.string.constellation_12,
            R.string.constellation_1,
            R.string.constellation_2,
            R.string.constellation_3,
            R.string.constellation_4,
            R.string.constellation_5,
            R.string.constellation_6,
            R.string.constellation_7,
            R.string.constellation_8,
            R.string.constellation_9,
            R.string.constellation_10
    };

    public static Integer getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1]
                : constellationArr[month];
    }
}
