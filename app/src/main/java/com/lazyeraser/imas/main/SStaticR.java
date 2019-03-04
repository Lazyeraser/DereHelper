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
    public final static boolean isCnMainLand = Locale.getDefault().toString().startsWith("zh_CN");
    public final static boolean isJp = Locale.getDefault().getLanguage().endsWith("ja");
    public final static boolean isEN = !(isCn || isJp);

    public final static String SERVER_URL_RES = "https://truecolor.kirara.ca";
    public final static String SERVER_URL_UPDATE = "https://raw.githubusercontent.com/Lazyeraser/DereHelper/master/appupdate/";

    private final static String SERVER_URL_CN = "http://starlight.346lab.org";
    private final static String SERVER_URL_EN = "https://starlight.kirara.ca";
    public final static String API_SERVER_URL = isCn ? SERVER_URL_CN : SERVER_URL_EN;

    public final static String API = API_SERVER_URL + "/api/v1/";

    public static boolean ANALYTICS_ON = false;

    //static data
    public final static String UNITY_VERSION = "2018.2.20f1";

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
    public static Map<String, Integer> connMap = new LinkedHashMap<>(); // constellation translate from JP
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
        }else {
            connMap.put("牡羊座" ,R.string.constellation_1);
            connMap.put("牡牛座" ,R.string.constellation_2);
            connMap.put("双子座" ,R.string.constellation_3);
            connMap.put("獅子座" ,R.string.constellation_5);
            connMap.put("天秤座" ,R.string.constellation_7);
            connMap.put("蠍座" ,R.string.constellation_8);
            connMap.put("射手座" ,R.string.constellation_9);
            connMap.put("山羊座" ,R.string.constellation_10);
            connMap.put("水瓶座" ,R.string.constellation_11);
            connMap.put("魚座" ,R.string.constellation_12);

            connMap.put("蟹座" ,R.string.constellation_4);
            connMap.put("かに座" ,R.string.constellation_4);
            connMap.put("乙女座" ,R.string.constellation_6);
            connMap.put("花も恥らう乙女座" ,R.string.constellation_6_special);
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
