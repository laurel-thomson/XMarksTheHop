package com.example.android.breweryapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Brewery {

    private String mName;
    private String mID = "";
    private String mLogoSource = "";
    private String mWebsiteURL = "";
    private String mDescription ="";
    private ArrayList<Beer> mBeers;

    public Brewery(String name) {
        mName = name;
        mBeers = new ArrayList<Beer>();
    }

    public String getID() { return mID; }
    public String getName() { return mName; }
    public String getImageURL() {return mLogoSource;}
    public String getWebsiteURL() {return mWebsiteURL;}
    public String getDescription() {return mDescription;}
    public ArrayList<Beer> getBeers() { return mBeers; }


    public void addBeer (Beer b) {
        mBeers.add(b);
    }

    public void setID(String id) { mID = id; }
    public void setLogoSource(String logoSource) { mLogoSource = logoSource; }
    public void setWebsiteUrl(String websiteURL) { mWebsiteURL = websiteURL; }
    public void setDescription(String description) {mDescription = description; }

    @Override
    public String toString() {
        return mName;
    }
}
