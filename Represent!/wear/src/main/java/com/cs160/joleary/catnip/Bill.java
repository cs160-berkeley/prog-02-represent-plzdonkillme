package com.cs160.joleary.catnip;

/**
 * Created by robinhu on 3/2/16.
 */
public class Bill {
    private String name;
    private String date;

    public Bill(String n, String d) {
        name = n;
        date = d;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
