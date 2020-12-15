package com.learntodroid.postrequestwithjson;

import com.google.gson.annotations.SerializedName;

public class Comment
{
    //Searlized name določa dejansko vrednost v JSON!
    @SerializedName("ID")
    private String id;

    @SerializedName("Ime_naprave")
    private String ime_naprave;

    @SerializedName("Lon_input")
    private String longitude;

    @SerializedName("Lat_input")
    private String latitude;

    /*
    * Referenca:
    * {
        "ID":"2",
        "Ime_naprave":"Gasper Tine",
        "Lon_input":"15.56767",
        "Lat_input":"46.12345"
    }
    *
    * Tako mora biti nastavljen JSON za POST
    *
    *
    * */


    //Order is very important!
    public Comment(String id, String ime_naprave, String latitude, String longitude) {
        this.id = id;
        this.ime_naprave = ime_naprave;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getIme_naprave() {
        return ime_naprave;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}