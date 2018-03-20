package com.example.saahil.autotorrent;

/**
 * Created by Saahil on 07-10-2017.
 */


        import android.text.TextUtils;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.nio.charset.Charset;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by HP on 13-04-2017.
 */

public class QueryUtils {
    /** Tag for the log messages*/
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Torrent} objects.
     */
    public static List<Torrent> fetchTorrentData(String requestUrl)   {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e)   {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //Extract relevant fields from the JSON response and create a list of {@link Earhquake}s
        List<Torrent> torrents = extractFeatureFromJson(jsonResponse);

        //Return the list of {@link Earthquake}s
        return torrents;
    }

    /**
     * Return a list of {@link Torrent} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Torrent> extractFeatureFromJson(String torrentJSON) {
        //If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(torrentJSON))
            return null;

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Torrent> torrents = new ArrayList<>();
        Log.e("torrentJSON", torrentJSON);
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(torrentJSON);

            //Extract the JSONArray associated with the key called "features",
            //which represents a list of features (or earthquakes_.
            JSONArray torrentArray = baseJsonResponse.getJSONArray("torrents");

            //For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0, size = torrentArray.length(); i < size; i++) {

                //Get a single torrent at position i within the list of earthquakes
                JSONObject currentTorrent = torrentArray.getJSONObject(i);


                String firstLetter = currentTorrent.getString("query");

                //Extract the value for the key called "place"
                String query = currentTorrent.getString("query");

                //Extract the value for the key called "time"
                long time = currentTorrent.getLong("time");

                String type = currentTorrent.getString("type");

                //Create a new {@link Earthquake} to the list of earthquakes.
                Torrent torrent = new Torrent(firstLetter, query, time, type);

                //Add the new {@link Earthquake} to the list of earthquakes
                torrents.add(torrent);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return torrents;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl)  {
        URL url = null;
        try {
            url = new URL(stringUrl);
        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException   {
        String jsonResponse = "empty";
        //If the URL is null, then return early.
        if(url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (reponse code 200),
            // then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)  {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else    {
                Log.e(LOG_TAG, "Error Response code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e)   {
            Log.e(LOG_TAG, "Problem retrieving the torrent JSON results.", e);
        }
        finally {
            if (urlConnection != null)  {
                urlConnection.disconnect();
            }
            if (inputStream != null)    {
                //Closing the input stram could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signatue specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        Log.e("JSON Response",jsonResponse);
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
