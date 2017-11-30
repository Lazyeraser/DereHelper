package com.lazyeraser.imas.main;

import android.annotation.SuppressLint;

import com.lazyeraser.imas.derehelper.BuildConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lazyEraser on 2017/4/20.
 */

public class SStaticR {

    public final static boolean isDebug = BuildConfig.DEBUG;

    private final static String SERVER_URL_DEBUG = "http://starlight.346lab.org";
    private final static String SERVER_URL_RELEASE = "http://starlight.346lab.org";

    public final static String SERVER_URL_RES = "https://hoshimoriuta.kirara.ca";
    public final static String SERVER_URL_UPDATE = "https://raw.githubusercontent.com/Lazyeraser/DereHelper/master/appupdate/";

    public final static String SERVER_URL = isDebug ? SERVER_URL_DEBUG : SERVER_URL_RELEASE;

    public final static String API = SERVER_URL + "/api/v1";

    //static data
    @SuppressLint("UseSparseArrays")
    public final static Map<Integer, String> rarityMap = new LinkedHashMap<>();
    public final static Map<String, Integer> rarityMap_rev = new LinkedHashMap<>();
    public final static Map<Integer, String> rarityMap_lite = new LinkedHashMap<>();
    public final static Map<String, String> typeMap = new LinkedHashMap<>();
    public final static Map<String, Integer> typeMap_int = new LinkedHashMap<>();
    public final static Map<String, Integer> sortTypeMap = new LinkedHashMap<>();
    static {
        rarityMap.put(1, "N");
        rarityMap.put(2, "N+");
        rarityMap.put(3, "R");
        rarityMap.put(4, "R+");
        rarityMap.put(5, "SR");
        rarityMap.put(6, "SR+");
        rarityMap.put(7, "SSR");
        rarityMap.put(8, "SSR+");

        rarityMap_rev.put("N", 1);
        rarityMap_rev.put("N+", 2);
        rarityMap_rev.put("R", 3);
        rarityMap_rev.put("R+", 4);
        rarityMap_rev.put("SR", 5);
        rarityMap_rev.put("SR+", 6);
        rarityMap_rev.put("SSR", 7);
        rarityMap_rev.put("SSR+", 8);

        rarityMap_lite.put(1, "N");
        rarityMap_lite.put(3, "R");
        rarityMap_lite.put(5, "SR");
        rarityMap_lite.put(7, "SSR");

        typeMap.put("cute", "CUTE");
        typeMap.put("cool", "COOL");
        typeMap.put("passion", "PASSION");

        typeMap_int.put("cute", 1);
        typeMap_int.put("cool", 2);
        typeMap_int.put("passion", 3);

        sortTypeMap.put("ID", 0);
        sortTypeMap.put("Vi", 1);
        sortTypeMap.put("Vo", 2);
        sortTypeMap.put("Da", 3);
        sortTypeMap.put("All", 4);

    }
}
