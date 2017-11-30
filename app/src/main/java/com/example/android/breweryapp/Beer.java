package com.example.android.breweryapp;

public class Beer {
    private String mName;
    private String mImageURL;
    private String mABV;
    private String mDescription;
    private String mStyle;
    private String mID;

    public Beer(String name, String imageURL, String ABV, String description, String style, String ID) {
        mName = name;
        mImageURL = imageURL;
        mABV = ABV;
        mDescription = description;
        mStyle = style;
        mID = ID;
    }


    public String getName() {return mName;}
    public String getImageURL() {return mImageURL;}
    public String getABV() {return mABV;}
    public String getDescription() {return mDescription;}
    public String getStyle() {return mStyle;}
    public String getID() {return mID;}

    @Override
    public String toString() {
        return mName;
    }
}
