package com.example.saahil.autotorrent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchTorrent extends AppCompatActivity {
    private JSONObject JSONResponse;
    private View.OnClickListener mOnClickListener;
    public static final String USER_ID = "UserId";
    //https://auto-torrent.herokuapp.com/getWishlist
    private static final String ADD_WISHLIST_URL = "https://auto-torrent.herokuapp.com/addWishlist";
    private static final String GET_TORRENT_URL = "http://auto-torrent.herokuapp.com/getTorrent";

    RequestQueue requestQueue;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public void requestResponseADD(String requestURL) {

        //Toast.makeText(SearchTorrent.this, requestURL, Toast.LENGTH_LONG).show();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        //editor.clear().commit();
        String originalRequestUrl = sharedPreferences.getString("wishlist", "no string returned");
        if (originalRequestUrl.equalsIgnoreCase("no string returned"))
            originalRequestUrl = "";
        requestURL = requestURL +  originalRequestUrl + "^";
        sharedPreferences.edit().putString("wishlist",requestURL).commit();

        Toast.makeText(SearchTorrent.this, sharedPreferences.getString("wishlist", "no wishlist returned"), Toast.LENGTH_LONG).show();

        /*requestQueue = Volley.newRequestQueue(this);

        // Creating the JsonObjectRequest class called obreq, passing required parameters:
        //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, requestURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONObject>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONResponse = response;
                        try {
                            Toast.makeText(SearchTorrent.this, JSONResponse.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
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
        obreq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);*/
    }
    public void requestResponseGET(String requestURL) {

        requestQueue = Volley.newRequestQueue(this);

        // Creating the JsonObjectRequest class called obreq, passing required parameters:
        //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, requestURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONObject>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONResponse = response;
                        CoordinatorLayout coordinatorLayout = null;
                        try {
                            if(JSONResponse.getString("message").contentEquals("0")) {
                                coordinatorLayout = (CoordinatorLayout) findViewById(
                                        R.id.coordinatorLayout);
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Add to wishlist ?",Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Add", mOnClickListener);
                                View snackbarView = snackbar
                                        .setActionTextColor(Color.WHITE)
                                        .getView();
                                snackbarView.setBackgroundColor(Color.BLACK);
                                TextView textView =(TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.WHITE);
                                snackbar.show();
                            }
                            else {
                                String displayString = "Name: " + JSONResponse.getString("title") + "\nSize: " + JSONResponse.getString("size") + "\nSeeds: " + JSONResponse.getString("seeds") + "\nPeers: " + JSONResponse.getString("peers");
                                //Toast.makeText(SearchTorrent.this, JSONResponse.getString("title"), Toast.LENGTH_LONG).show();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(SearchTorrent.this);
                                builder.setMessage("Torrent Details: " + "\n" + displayString)
                                        .setTitle(R.string.add_torrent);
                                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(SearchTorrent.this, "Intent",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        try {
                                            intent.setData(Uri.parse(JSONResponse.getString("magnet")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        PackageManager packageManager = SearchTorrent.this.getPackageManager();
                                        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        if (resolveInfo.size() > 0) {
                                            List<Intent> targetedShareIntents = new ArrayList<Intent>();
                                            for (ResolveInfo r : resolveInfo)   {
                                                Intent progIntent = (Intent)intent.clone();
                                                String packageName = r.activityInfo.packageName;

                                                progIntent.setPackage(packageName);
                                                if (r.activityInfo.packageName.contains("torrent"))
                                                    targetedShareIntents.add(progIntent);
                                            }
                                            if (targetedShareIntents.size() > 0)    {
                                                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),"view");
                                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                                                startActivity(chooserIntent);
                                            }
                                        }
                                        /*Intent intent = new Intent(Intent.ACTION_VIEW);
                                        try {
                                            intent.setDataAndType(Uri.parse(JSONResponse.getString("magnet")), "application/x-bittorrent");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(Intent.createChooser(intent, "view"));*/
                                    }
                                    });
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
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
        obreq.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_torrent);
        final String id = getIntent().getStringExtra("UserId");
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText search = (EditText) findViewById(R.id.input_box);
                Spinner type = (Spinner) findViewById(R.id.type);
                //Toast.makeText(SearchTorrent.this, searchString.getText().toString() + " " + type.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                String searchString = search.getText().toString();
                String typeString = type.getSelectedItem().toString().toLowerCase();
                String encodedSearch = null;
                String encodedType = null;
                try {
                    encodedSearch = URLEncoder.encode(searchString, "UTF-8");
                    encodedType = URLEncoder.encode(typeString, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String requestURL = ADD_WISHLIST_URL + "?id=" + id + "&query=" + encodedSearch + "&type=" + encodedType;
                requestResponseADD(requestURL);

            }
        };
        loadSpinner();

        final Button findButton = (Button) findViewById(R.id.find_button);

        findButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = SearchTorrent.this.getCurrentFocus();
                        if (view != null)   {
                            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        Toast.makeText(SearchTorrent.this, "Loading... Please Wait", Toast.LENGTH_LONG).show();
                        boolean found = false;
                        EditText search = (EditText) findViewById(R.id.input_box);
                        Spinner type = (Spinner) findViewById(R.id.type);
                        //Toast.makeText(SearchTorrent.this, searchString.getText().toString() + " " + type.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                        String searchString = search.getText().toString();
                        String typeString = type.getSelectedItem().toString().toLowerCase();
                        String encodedSearch = null;
                        String encodedType = null;
                        try {
                            encodedSearch = URLEncoder.encode(searchString, "UTF-8");
                            encodedType = URLEncoder.encode(typeString, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String requestURL = GET_TORRENT_URL + "?id=" + id + "&query=" + encodedSearch + "&type=" + encodedType;
                        Log.e("requestURL", requestURL);
                        requestResponseGET(requestURL);

                    }
                }
        );

    }

    public void loadSpinner() {
        List<String> types = new ArrayList<>();
        types.add("All");
        types.add("Video");
        types.add("Audio");
        types.add("Movies");
        types.add("Games");
        types.add("Software");

        Spinner spinner = (Spinner) SearchTorrent.this.findViewById(R.id.type);
        // Creating adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,types );
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(adapter);
        /*
        int spinnerPosition = adapter.getPosition();
        Log.d("spinner_set_selection", s_attname + spinnerPosition);
        spinner.setSelection(spinnerPosition);
        */
    }
}
