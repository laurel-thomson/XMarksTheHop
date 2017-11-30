package com.example.android.breweryapp;

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ExpandableListView;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.content.Context;

        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;

public class BreweryActivity extends AppCompatActivity {
    private ExpandableAdapter mAdapter;
    private ExpandableListView mBeerListView;
    private Brewery mBrewery;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewery);
        context = this;
        String breweryID = (String) getIntent().getExtras().get("breweryID");
        mBrewery = BreweryCatalog.getInstance().getBrewery(breweryID);

        mBeerListView = (ExpandableListView) findViewById(R.id.expandable_beer_list);

        mAdapter = new ExpandableAdapter(context,mBrewery.getBeers());

        mBeerListView.setAdapter(mAdapter);

        View header = getLayoutInflater().inflate(R.layout.header, null);

        TextView breweryName = (TextView) header.findViewById(R.id.brewery_name);
        TextView breweryWebsite = (TextView) header.findViewById(R.id.brewery_website);
        TextView breweryDescription = (TextView) header.findViewById(R.id.brewery_description);
        ImageView breweryLogo = (ImageView) header.findViewById(R.id.brewery_logo);

        breweryName.setText(mBrewery.getName());
        breweryWebsite.setText(mBrewery.getWebsiteURL());
        breweryDescription.setText(mBrewery.getDescription());

        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context)
                .load(mBrewery.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .into(breweryLogo);

        mBeerListView.addHeaderView(header);

    }
}