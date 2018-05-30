package com.nymeria.admin.model;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image {
    private String name;
    private String id;

    public Image() {
    }

    public Image(String name, String id) {
        this.name = name;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

