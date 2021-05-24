package com.learntodroid.postrequestwithjson;

import com.google.gson.annotations.SerializedName;

public class Post {
    //Searlized name določa dejansko vrednost v JSON!
    @SerializedName("ID")
    private final String id;

    @SerializedName("Ime_naprave")
    private final String ime_naprave;

    @SerializedName("Lon_input")
    private final String longitude;

    @SerializedName("Lat_input")
    private final String latitude;

    //Order is very important!
    public Post(String id, String ime_naprave, String latitude, String longitude) {
        this.id = id;
        this.ime_naprave = ime_naprave;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

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