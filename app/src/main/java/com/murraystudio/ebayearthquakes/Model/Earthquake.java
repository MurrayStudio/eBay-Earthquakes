package com.murraystudio.ebayearthquakes.Model;

/**
 * Author: Shamus Murray
 *
 * This class holds the data of the JSON Earthquake.
 */
public class Earthquake {
    private String date;
    private float depth;
    private float lng;
    private float magnitude;
    private float lat;
    private String place;


    public String getDate(){
        return date;
    }

    public float getdepth(){
        return depth;
    }

    public float getLng(){
        return lng;
    }

    public float getMagnitude(){
        return magnitude;
    }

    public float getLat(){
        return lat;
    }

    public String getPlace(){
        return place;
    }


    public void setDate(String date){
        this.date = date;
    }

    public void setdepth(float depth){
        this.depth = depth;
    }

    public void setLng(float lng){
        this.lng = lng;
    }

    public void setMagnitude(float magnitude){
        this.magnitude = magnitude;
    }

    public void setLat(float lat){
        this.lat = lat;
    }

    public void setPlace(String place){
        this.place = place;
    }


}
