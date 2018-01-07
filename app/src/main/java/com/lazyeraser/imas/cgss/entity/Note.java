package com.lazyeraser.imas.cgss.entity;

import java.lang.reflect.Field;

/**
 * Created by lazyeraser on 2018/1/4.
 */

public class Note {

    public int id;
    public float sec;
    public int type;
    public int startPos;
    public int finishPos;
    public int status;
    public int sync;
    public int groupId;

    private final static String noteFieldNames = "id,sec,type,startPos,finishPos,status,sync,groupId";

    public Note(String value) {
        String[] values = value.split(",");
        String[] fieldsNames = noteFieldNames.split(",");
        for (int i = 0; i < values.length; i++) {
            try {
                Field field = getClass().getField(fieldsNames[i]);
                if (field.getType().equals(int.class)){
                    field.set(this, Integer.valueOf(values[i]));
                }else {
                    field.set(this, Float.valueOf(values[i]));
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

    }

}
