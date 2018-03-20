package com.example.saahil.autotorrent;

/**
 * Created by Saahil on 07-10-2017.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class TorrentLoader extends AsyncTaskLoader {

    /** Tag for log messages  **/
    private static final String LOG_TAG = TorrentLoader.class.getName();

    /** Query URL */
    private String mUrl;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TorrentLoader(Context context, String url)    {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading()     {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public Object loadInBackground() {
        if (mUrl == null)
            return null;
        //Perform the network request, parse the response, and extract a list of torrents.

        List<Torrent> torrents = QueryUtils.fetchTorrentData(mUrl);
        return torrents;
    }
}

