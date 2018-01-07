package com.lazyeraser.imas.main;

import com.lazyeraser.imas.cgss.entity.TextData;
import com.lazyeraser.imas.derehelper.BuildConfig;
import com.lazyeraser.imas.derehelper.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lazyEraser on 2017/4/20.
 */

public class SStaticR {

    public final static boolean isDebug = BuildConfig.DEBUG;
    public final static boolean isCn = Locale.getDefault().getLanguage().endsWith("zh");
    public final static boolean isJp = Locale.getDefault().getLanguage().endsWith("ja");

    public final static String SERVER_URL_RES = "https://truecolor.kirara.ca";
    public final static String SERVER_URL_UPDATE = "https://raw.githubusercontent.com/Lazyeraser/DereHelper/master/appupdate/";

    private final static String SERVER_URL_CN = "http://starlight.346lab.org";
    private final static String SERVER_URL_EN = "https://starlight.kirara.ca";
    public final static String API_SERVER_URL = isCn ? SERVER_URL_CN : SERVER_URL_EN;

    public final static String API = API_SERVER_URL + "/api/v1/";


    //static data
    public final static String UNITY_VERSION = "5.4.5p1";

    public final static String charaIconUrl = SStaticR.SERVER_URL_RES + "/icon_char/%s.png";

    public final static Map<Integer, String> rarityMap = new LinkedHashMap<>();
    public final static Map<String, Integer> rarityMap_rev = new LinkedHashMap<>();
    public final static Map<Integer, String> rarityMap_lite = new LinkedHashMap<>();
    public final static Map<String, String> typeMap = new LinkedHashMap<>();
    public final static Map<String, Integer> typeMap_int = new LinkedHashMap<>();
    public final static Map<String, Integer> sortTypeMap_Card = new LinkedHashMap<>();
    public final static Map<Integer, String> songTypeMap = new LinkedHashMap<>();
    public final static Map<Integer, String> sortTypeMap_Song = new LinkedHashMap<>();

    public static Map<String, Integer> skillTypeMap = new LinkedHashMap<>();
    public static Map<Integer, String> skillTypeNameMap = new LinkedHashMap<>();
    public static List<TextData> textDataList;
    static {
        if (isJp){
            skillTypeMap.put("PERFECTボーナス" ,1);
            skillTypeMap.put("SCOREボーナス" ,2);
            skillTypeMap.put("COMBOボーナス" ,4);
            skillTypeMap.put("初級PERFECTサポート" ,5);
            skillTypeMap.put("中級PERFECTサポート" ,6);
            skillTypeMap.put("高級PERFECTサポート" ,7);
            skillTypeMap.put("COMBOサポート" ,9);
            skillTypeMap.put("ダメージガード" ,12);
            skillTypeMap.put("オーバーロード" ,14);
            skillTypeMap.put("コンセントレーション" ,15);
            skillTypeMap.put("アンコール" ,16);
            skillTypeMap.put("ライフ回復" ,17);
            skillTypeMap.put("スキルブースト" ,20);
            skillTypeMap.put("Cuteフォーカス" ,21);
            skillTypeMap.put("Coolフォーカス" ,22);
            skillTypeMap.put("Passionフォーカス" ,23);
            skillTypeMap.put("オールラウンド" ,24);
            skillTypeMap.put("ライブスパークル" ,25);
        }
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
//        typeMap.put("all", "ALL");

        typeMap_int.put("cute", 1);
        typeMap_int.put("cool", 2);
        typeMap_int.put("passion", 3);
        typeMap_int.put("all", 4);

        songTypeMap.put(1, "CUTE");
        songTypeMap.put(2, "COOL");
        songTypeMap.put(3, "PASSION");
        songTypeMap.put(4, "ALL");

        sortTypeMap_Card.put("ID", 0);
        sortTypeMap_Card.put("Vi", 1);
        sortTypeMap_Card.put("Vo", 2);
        sortTypeMap_Card.put("Da", 3);
        sortTypeMap_Card.put("All", 4);

        sortTypeMap_Song.put(R.string.online_date, "a.start_date ");
        sortTypeMap_Song.put(R.string.bpm, "b.bpm ");

    }
}
