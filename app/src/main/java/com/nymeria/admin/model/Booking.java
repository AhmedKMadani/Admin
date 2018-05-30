package com.nymeria.admin.model;

/**
 * Created by user on 16/11/17.
 */

public class Booking {
    String id;
    String csname;
    String bcode;
    String price;
    String total;
    String hallname;
    String date;
    String state;

    public Booking() {
    }


    public Booking( String id , String csname, String bcode, String price,
                  String total, String hallname , String date , String state ) {
        super();

        this.id = id;
        this.csname = csname;
        this.bcode = bcode;
        this.price = price;
        this.total = total;
        this.hallname = hallname;
        this.date = date;
        this.state = state;

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCSName(String csname) {
        this.csname = csname;
    }

    public void setBCode(String bcode) {
        this.bcode = bcode;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setHallname(String hallname) {
        this.hallname =  hallname;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setState(String state) {
        this.state = state;
    }




    public String getId() {
        return id;
    }

    public String getCSName() {
        return csname;
    }

    public String getBcode() {
        return bcode;
    }

    public String getPrice() {
        return price;
    }

    public String getTotal() {
        return total;
    }

    public String getHallname() {
        return hallname;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

}