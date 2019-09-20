package com.udacity.ak.localroots.data.database;


import java.util.List;

public class MarketDetail {

    String Address;
    String GoogleLink;
    String Schedule;
    String Products;

    public MarketDetail(String address, String googleLink, String schedule, String products) {
        this.Address = address;
        this.GoogleLink = googleLink;
        this.Schedule = schedule;
        this.Products = products;
    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getGoogleLink() {
        return GoogleLink;
    }

    public void setGoogleLink(String googleLink) {
        this.GoogleLink = googleLink;
    }

    public String getSchedule() {
        return Schedule;
    }

    public void setSchedule(String schedule) {
        this.Schedule = schedule;
    }

    public String getProducts() {
        return Products;
    }

    public void setProducts(String products) {
        this.Products = products;
    }


}

 /*
            String strMiles = marketname.substring(0, marketname.indexOf(' '));
            String strName = marketname.substring(marketname.indexOf(' ') + 1);
            this.miles = strMiles;
            this.marketname = strName;
            */
