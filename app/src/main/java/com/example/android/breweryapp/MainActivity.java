package com.example.android.breweryapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String API_URL = "http://api.brewerydb.com/v2/?";
    private final String API_KEY_BREWERYDB = "5f33a523ec77c577d68d0028daf61d16";
    private final String API_KEY_MAPS = "AIzaSyCTGafAV14ZJVXM5NDA4ugpTAi4zVukHtY";
    private final String TEST_QUERY = "http://api.brewerydb.com/v2/search?q=Tallgrass+|+Little+Apple+Brewing&type=brewery&key=5f33a523ec77c577d68d0028daf61d16";
    private final String QUERY_BREWERYDB_SEARCH = "https://api.brewerydb.com/v2/search?q=%s&type=brewery&key=5f33a523ec77c577d68d0028daf61d16";
    private final String QUERY_BREWERYDB_BREWERY = "https://api.brewerydb.com/v2/brewery/%s/beers?key=5f33a523ec77c577d68d0028daf61d16";
    private final String QUERY_MAPS = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=AIzaSyCTGafAV14ZJVXM5NDA4ugpTAi4zVukHtY";
    private final String QUERY_MAPS_PRE = "brewery ";
    private final String QUERY_MAPS_FIXED = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=brewery+manhattan+ks&key=AIzaSyCTGafAV14ZJVXM5NDA4ugpTAi4zVukHtY";

    private ProgressBar mProgressBar;
    private ListView mBreweriesList;
    private EditText mLocationEditText;
    private Button mSearchButton;
    private int mBreweryPos;
    private Brewery mBrewery;
    private String mBreweryID;

    private String mLocationText;

    private BreweryCatalog mCatalog;

    private final static int BREWERIES_OBTAINED = 1000;
    private final static int ID_OBTAINED = 1001;
    private final static int BEERS_OBTAINED = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);

        mLocationEditText = (EditText) findViewById(R.id.et_location);

        mSearchButton = (Button) findViewById(R.id.b_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationText = mLocationEditText.getText().toString();
                new SearchGoogleMaps().execute();
            }
        });

        mBreweriesList = (ListView) findViewById(R.id.lv_breweries);
        mBreweriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBreweryPos = i;
                new RetrieveBreweryIDTask().execute(mCatalog.getBrewery(i).getName());
            }
        });

        mCatalog = BreweryCatalog.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BREWERIES_OBTAINED:
                ArrayAdapter<Brewery> adapter = new ArrayAdapter<Brewery>(
                        this,
                        android.R.layout.simple_list_item_1,
                        mCatalog.getBreweries()
                );
                mBreweriesList.setAdapter(adapter);
                break;
            case ID_OBTAINED:
                mCatalog.getBrewery(mBreweryPos).setID(mBreweryID);
                new RetrieveBeerListTask().execute();
                break;
            case BEERS_OBTAINED:
                Intent breweryIntent = new Intent(MainActivity.this, BreweryActivity.class);
                breweryIntent.putExtra("breweryID", mBreweryID);
                startActivity(breweryIntent);
                break;
        }
    }

    class SearchGoogleMaps extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mCatalog.reset();
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(String.format(QUERY_MAPS, URLEncoder.encode(QUERY_MAPS_PRE + mLocationText,"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray breweries = object.getJSONArray("results");
                for (int i = 0; i < breweries.length(); i++) {
                    JSONObject brewery = breweries.getJSONObject(i);
                    mCatalog.addBrewery(new Brewery(brewery.getString("name")));
                }
                onActivityResult(BREWERIES_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveBreweryIDTask extends AsyncTask<String, Void, String> {

        private Exception exception;


        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(String.format(QUERY_BREWERYDB_SEARCH, URLEncoder.encode(urls[0],"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray breweries = object.getJSONArray("data");
                JSONObject firstBrewery = breweries.getJSONObject(0);
                mBreweryID = firstBrewery.getString("id");
                mBrewery = mCatalog.getBrewery(mBreweryPos);
                mBrewery.setID(mBreweryID);
                mBrewery.setLogoSource(firstBrewery.has("images") ? firstBrewery.getJSONObject("images").getString("large") : null);
                mBrewery.setWebsiteUrl(firstBrewery.has("website") ? firstBrewery.getString("website") : "");
                mBrewery.setDescription(firstBrewery.has("description") ? firstBrewery.getString("description") : "");
                onActivityResult(ID_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveBeerListTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(String.format(QUERY_BREWERYDB_BREWERY, URLEncoder.encode(mBreweryID,"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray beerList = object.getJSONArray("data");
                for (int i = 0; i < beerList.length(); i++) {
                    JSONObject beer = beerList.getJSONObject(i);
                    mCatalog.addBeer(mBreweryID, new Beer(
                            beer.has("name") ? beer.getString("name") : "",
                            beer.has("labels") ? beer.getJSONObject("labels").getString("medium") : null,
                            beer.has("abv") ? beer.getString("abv") : "",
                            beer.has("description") ? beer.getString("description") : "",
                            beer.has("nameDisplay") ? beer.getString("nameDisplay") : "",
                            beer.getString("id")
                    ));
                }
                onActivityResult(BEERS_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
