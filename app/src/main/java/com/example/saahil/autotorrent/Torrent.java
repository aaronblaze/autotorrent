package com.example.saahil.autotorrent;

/**
 * Created by Saahil on 07-10-2017.
 */

public class Torrent {
    private String firstLetter;
    private String query;
    private long timeInMilliseconds;
    private String type;

    public Torrent(String firstLetter, String query, long timeInMilliseconds, String type) {
        this.firstLetter = firstLetter.charAt(0) + "";
        this.query = query;
        this.timeInMilliseconds = timeInMilliseconds;
        this.type = type;
    }


    public String getFirstLetter() {return firstLetter.toUpperCase();}

    public String getQuery() {return query;}

    public long getTimeInMilliseconds() {return timeInMilliseconds;}

    public String getType() {return type;}
}