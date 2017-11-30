package com.example.android.breweryapp;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BreweryCatalog {

    private static BreweryCatalog singleton;

    private ArrayList<Brewery> mBreweries;

    private BreweryCatalog() {
        mBreweries = new ArrayList<>();
    }

    public static BreweryCatalog getInstance() {
        if (singleton == null) {
            singleton = new BreweryCatalog();
        }
        return singleton;
    }

    public void reset() {
        mBreweries = new ArrayList<Brewery>();
    }

    public void addBrewery(Brewery brewery) {
        mBreweries.add(brewery);
    }

    public Brewery getBrewery (int i) {
        if (i < mBreweries.size()) { return mBreweries.get(i); }
        return null;
    }

    public Brewery getBrewery(String id) {
        for (Brewery b : mBreweries) {
            if (b.getID().equals(id)) { return b;  }
        }
        return null;
    }

    public void addBeer(String breweryID, Beer beer) {
        for (Brewery b : mBreweries) {
            if (b.getID().equals(breweryID)) {
                b.addBeer(beer);
                break;
            }
        }
    }

    public ArrayList<Brewery> getBreweries() { return mBreweries; }
}
