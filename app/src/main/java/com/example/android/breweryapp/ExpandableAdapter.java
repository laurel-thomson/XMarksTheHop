package com.example.android.breweryapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laure_000 on 11/27/2017.
 */

public class ExpandableAdapter implements ExpandableListAdapter {
    private Context context;
    private ArrayList<Beer> beers;

    public ExpandableAdapter (Context context, ArrayList<Beer> beers) {
        this.context = context;
        this.beers = beers;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return beers.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return beers.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return beers.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {
        Beer beer = beers.get(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_item, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.beer_name);
        heading.setText(beer.getName());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean isLastChild, View view, ViewGroup viewGroup) {
        Beer beer = beers.get(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_item, null);
        }
        TextView style = (TextView) view.findViewById(R.id.style);
        style.setText(beer.getStyle());
        TextView ABV = (TextView) view.findViewById(R.id.ABV);
        ABV.setText(beer.getABV() + "% ABV");
        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(beer.getDescription());

        ImageView beerLogo = (ImageView) view.findViewById(R.id.beer_logo);
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context)
                .load(beer.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .into(beerLogo);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
}
