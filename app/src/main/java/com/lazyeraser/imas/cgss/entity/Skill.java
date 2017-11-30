/**
 * Copyright 2017 bejson.com
 */
package com.lazyeraser.imas.cgss.entity;
import java.util.List;

/**
 * Auto-generated: 2017-09-18 15:47:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Skill {

    private int id;
    private String skill_name;
    private String explain;
    private String skill_type;
    private int judge_type;
    private int skill_trigger_type;
    private int skill_trigger_value;
    private int cutin_type;
    private int condition;
    private int value;
    private int value_2;
    private int max_duration;
    private int max_chance;
    private String explain_en;
    private int skill_type_id;
    private List<Integer> effect_length;
    private List<Integer> proc_chance;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }
    public String getSkill_name() {
        return skill_name;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
    public String getExplain() {
        return explain;
    }

    public void setSkill_type(String skill_type) {
        this.skill_type = skill_type;
    }
    public String getSkill_type() {
        return skill_type;
    }

    public void setJudge_type(int judge_type) {
        this.judge_type = judge_type;
    }
    public int getJudge_type() {
        return judge_type;
    }

    public void setSkill_trigger_type(int skill_trigger_type) {
        this.skill_trigger_type = skill_trigger_type;
    }
    public int getSkill_trigger_type() {
        return skill_trigger_type;
    }

    public void setSkill_trigger_value(int skill_trigger_value) {
        this.skill_trigger_value = skill_trigger_value;
    }
    public int getSkill_trigger_value() {
        return skill_trigger_value;
    }

    public void setCutin_type(int cutin_type) {
        this.cutin_type = cutin_type;
    }
    public int getCutin_type() {
        return cutin_type;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
    public int getCondition() {
        return condition;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public void setValue_2(int value_2) {
        this.value_2 = value_2;
    }
    public int getValue_2() {
        return value_2;
    }

    public void setMax_duration(int max_duration) {
        this.max_duration = max_duration;
    }
    public int getMax_duration() {
        return max_duration;
    }

    public void setMax_chance(int max_chance) {
        this.max_chance = max_chance;
    }
    public int getMax_chance() {
        return max_chance;
    }

    public void setExplain_en(String explain_en) {
        this.explain_en = explain_en;
    }
    public String getExplain_en() {
        return explain_en;
    }

    public void setSkill_type_id(int skill_type_id) {
        this.skill_type_id = skill_type_id;
    }
    public int getSkill_type_id() {
        return skill_type_id;
    }

    public void setEffect_length(List<Integer> effect_length) {
        this.effect_length = effect_length;
    }
    public List<Integer> getEffect_length() {
        return effect_length;
    }

    public void setProc_chance(List<Integer> proc_chance) {
        this.proc_chance = proc_chance;
    }
    public List<Integer> getProc_chance() {
        return proc_chance;
    }

}