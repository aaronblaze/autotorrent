package com.example.saahil.autotorrent;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<Torrent>>{

    SharedPreferences prefs = null;
    public static final String LOG_TAG = MainActivity.class.getName();

    private TorrentAdapter mAdapter;
    // URL of object to be parsed
    String JsonURL = "https://auto-torrent.herokuapp.com/new";
    private TextView mEmptyStateTextView;
    //Store the id of the user
    String userId;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    //private SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
    //private SharedPreferences.Editor editor2 = sharedPreferences.edit();
    private static final int TORRENT_LOADER_ID = 1;
    private static final String USGS_REQUEST_URL = "https://auto-torrent.herokuapp.com/getWishlist";


    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, SearchTorrent.class);
                intent.putExtra("UserId",userId);
                  startActivity(intent);
            }
        });
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        */

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefs = getSharedPreferences("com.example.saahil.autotorrent", MODE_PRIVATE);
        ListView torrentListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        torrentListView.setEmptyView(mEmptyStateTextView);



        //Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new TorrentAdapter(this, new ArrayList<Torrent>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        torrentListView.setAdapter(mAdapter);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected())   {
            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the int ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the LoaderCallBacks parameter (which is valid
            //because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(TORRENT_LOADER_ID, null, this);
        }else   {
            //Otherwise, display error
            //First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.all) {
            // Handle the camera action
        }  else if (id == R.id.audio) {

        }   else if (id == R.id.video) {

        }   else if (id == R.id.applications) {

        }   else if (id == R.id.games) {

        }   else if (id == R.id.other) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Get the url of not found torrents from sharedPreferences
        String urlNotFoundTorrents = sharedPreferences.getString("wishlist", "wishlist not found");
        Toast.makeText(MainActivity.this, urlNotFoundTorrents, Toast.LENGTH_LONG).show();


        userId = sharedPref.getString("id", "NR1");
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
            //PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("UserId","3");

            // Creates the Volley request queue
            requestQueue = Volley.newRequestQueue(this);

            // Creating the JsonObjectRequest class called obreq, passing required parameters:
            //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
            Log.e("URL", JsonURL );
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, JsonURL,
                    // The third parameter Listener overrides the method onResponse() and passes
                    //JSONObject as a parameter
                    new Response.Listener<JSONObject>() {
                        // Takes the response from the JSON request
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String id = response.getString("id");
                                editor.putString("id",id);
                                editor.commit();
                                Log.e("ID :", sharedPref.getString("id", "NR3"));
                                userId = sharedPref.getString("id", "NR2");
                            }
                            catch (JSONException e) {
                                // If an error occurs, this prints the error to the log
                                e.printStackTrace();
                            }
                        }
                        // The final parameter overrides the method onErrorResponse() and passes VolleyError
                        //as a parameter
                    },
                    new Response.ErrorListener() {
                        // Handles errors that occur due to Volley
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error",error);
                        }
                    }
            );
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);
        }
        //Read from SharedPreferences
    }
    @Override
    public Loader<List<Torrent>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        uriBuilder.appendQueryParameter("id",sharedPref.getString("id", "NR5"));

        //Toast.makeText(this,uriBuilder.toString(), Toast.LENGTH_LONG ).show();

        //Create a new loader for the given URL
        return new TorrentLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<Torrent>> loader, List<Torrent> data) {
        //Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //Set empty state to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_torrents);

        //Clear the adapter of previous earthquake data
        mAdapter.clear();

        //If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Torrent>> loader) {
        //Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }


}

