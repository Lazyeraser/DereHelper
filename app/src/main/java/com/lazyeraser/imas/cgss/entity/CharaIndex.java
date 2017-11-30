/**
 * Copyright 2017 bejson.com
 */
package com.lazyeraser.imas.cgss.entity;
import java.util.List;

/**
 * Auto-generated: 2017-09-19 13:46:6
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class CharaIndex {

    private String kana_spaced;
    private String kanji_spaced;
    private String ref;
    private String conventional;
    private List<Integer> cards;
    private int chara_id;
    public void setKana_spaced(String kana_spaced) {
        this.kana_spaced = kana_spaced;
    }
    public String getKana_spaced() {
        return kana_spaced;
    }

    public void setKanji_spaced(String kanji_spaced) {
        this.kanji_spaced = kanji_spaced;
    }
    public String getKanji_spaced() {
        return kanji_spaced;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
    public String getRef() {
        return ref;
    }

    public void setConventional(String conventional) {
        this.conventional = conventional;
    }
    public String getConventional() {
        return conventional;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }
    public List<Integer> getCards() {
        return cards;
    }

    public void setChara_id(int chara_id) {
        this.chara_id = chara_id;
    }
    public int getChara_id() {
        return chara_id;
    }

}