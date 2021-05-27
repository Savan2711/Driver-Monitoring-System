package com.example.locationtracer;

public class Coordinates {

    private String lat;
    private String lon;
    private int isJourneyOn;

    Coordinates(String lat, String lon, int isJourneyOn)
    {
        this.lat = lat;
        this.lon = lon;
        this.isJourneyOn = isJourneyOn;
    }

    public String getLat()
    {return lat;}

    public String getLon()
    {return lon;}

//    public void setIsJourneyOn(int a)
//    {this.isJourneyOn = a;}

    public int getIsJourneyOn()
    {return isJourneyOn;}

}
