package com.lazyeraser.imas.cgss.entity;

/**
 * Created by lazyeraser on 2017/12/25.
 */

public class Manifest {

    private String name;
    private String hash;
    private int attr;
    private String category;
    private String decrypt_key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDecrypt_key() {
        return decrypt_key;
    }

    public void setDecrypt_key(String decrypt_key) {
        this.decrypt_key = decrypt_key;
    }
}
