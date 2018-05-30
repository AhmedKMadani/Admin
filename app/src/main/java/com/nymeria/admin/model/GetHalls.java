package com.nymeria.admin.model;

/**
 * Created by user on 4/3/2018.
 */

public class GetHalls {
    String id;
    String name_ar;
    String name_en;
    String price;
    String size;
    String des_ar;
    String des_en;
    String loc_ar;
    String loc_en;
    String image;

    public GetHalls() {
    }


    public GetHalls(String id, String name_ar, String name_en, String price,
                    String size, String image, String des_ar, String des_en, String loc_ar, String loc_en) {
        super ();

        this.id = id;
        this.name_ar = name_ar;
        this.name_en = name_en;
        this.price = price;
        this.des_ar = des_ar;
        this.des_en = des_en;
        this.loc_ar = loc_ar;
        this.loc_en = loc_en;
        this.size = size;
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setLoc_ar(String loc_ar) {
        this.loc_ar = loc_ar;
    }

    public void setLoc_en(String loc_en) {
        this.loc_en = loc_en;
    }

    public void setDes_ar(String des_ar) {
        this.des_ar = des_ar;
    }

    public void setDes_en(String des_en) {
        this.des_en = des_en;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getId() {
        return id;
    }

    public String getName_ar() {
        return name_ar;
    }

    public String getName_en() {
        return name_en;
    }

    public String getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public String getLoc_ar() {
        return loc_ar;
    }

    public String getLoc_en() {
        return loc_en;
    }

    public String getDes_ar() {
        return des_ar;
    }


    public String getDes_en() {
        return des_en;
    }

    public String getImage() {
        return image;
    }
}