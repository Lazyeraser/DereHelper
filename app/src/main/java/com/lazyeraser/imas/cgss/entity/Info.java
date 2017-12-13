package com.lazyeraser.imas.cgss.entity;

/**
 * Created by lazyeraser on 2017/12/12.
 */

public class Info {

    private String truth_version;

    private int api_revision;

    private int api_major;

    public void setTruth_version(String truth_version){
        this.truth_version = truth_version;
    }
    public String getTruth_version(){
        return this.truth_version;
    }
    public void setApi_revision(int api_revision){
        this.api_revision = api_revision;
    }
    public int getApi_revision(){
        return this.api_revision;
    }
    public void setApi_major(int api_major){
        this.api_major = api_major;
    }
    public int getApi_major(){
        return this.api_major;
    }

}
