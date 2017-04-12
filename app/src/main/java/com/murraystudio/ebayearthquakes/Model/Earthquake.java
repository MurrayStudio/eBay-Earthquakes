package com.murraystudio.ebayearthquakes.Model;

/**
 * Author: Shamus Murray
 *
 * This class holds the data of the JSON team member.
 */

public class Earthquake {
    String date;
    float depth;
    float lng;
    float magnitude;
    float lat;


    String getDate(){
        return date;
    }

    float getdepth(){
        return depth;
    }

    float getLng(){
        return lng;
    }

    float getMagnitude(){
        return magnitude;
    }

    float getLat(){
        return lat;
    }
}
