package com.example.saahil.autotorrent;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Saahil on 07-10-2017.
 */

public class TorrentAdapter extends ArrayAdapter {

    public TorrentAdapter(Context context, List<Torrent> torrents)  {
        super(context, 0, torrents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)   {
        //Check if there is an existing list item view (called convertView) that we can reuse,
        //otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null)   {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_item, parent,false);
        }

        //Find the torrent at the given position in the list of torrents
        Torrent currentTorrent = (Torrent) getItem(position);

        TextView firstLetterView = (TextView)listItemView.findViewById(R.id.first_letter);
        String firstLetter = currentTorrent.getFirstLetter();
        firstLetterView.setText(firstLetter);

        //Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is GradientDrawable.
        GradientDrawable firstLetterCircle = (GradientDrawable) firstLetterView.getBackground();

        //Get the appropriate background color based on the current earthquake magnitude
        int firstLetterColor = getFirstLetterColor(currentTorrent.getFirstLetter());

        //Set the color on the magnitude circle
        firstLetterCircle.setColor(firstLetterColor);


        TextView typeView =(TextView) listItemView.findViewById(R.id.type);
        String type = currentTorrent.getType();
        type = (type.charAt(0)+"").toUpperCase() + type.substring(1);
        typeView.setText(type);

        TextView queryView = (TextView) listItemView.findViewById(R.id.query);
        String query = currentTorrent.getQuery();
        queryView.setText(query);

        Date dateObject = new Date(currentTorrent.getTimeInMilliseconds());

        //Find the TextView with the view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        //Format the date string
        String formattedDate = formatDate(dateObject);
        //Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);

        //Find the TextView with the view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        //Format the time string
        String formattedTime = formatTime(dateObject);
        //Display the time of the current earthquake in that TextView
        timeView.setText(formattedTime);

        //Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string from a Date object
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted time string from a Date object
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private int getFirstLetterColor(String firstLetter)     {
        int firstLetterColorResourceId = 0;

        switch (firstLetter.charAt(0)%10)   {
            case 0:
                firstLetterColorResourceId = R.color.rem0;
                break;
            case 1:
                firstLetterColorResourceId = R.color.rem1;
                break;
            case 2:
                firstLetterColorResourceId = R.color.rem2;
                break;
            case 3:
                firstLetterColorResourceId = R.color.rem3;
                break;
            case 4:
                firstLetterColorResourceId = R.color.rem4;
                break;
            case 5:
                firstLetterColorResourceId = R.color.rem5;
                break;
            case 6:
                firstLetterColorResourceId = R.color.rem6;
                break;
            case 7:
                firstLetterColorResourceId = R.color.rem7;
                break;
            case 8:
                firstLetterColorResourceId = R.color.rem8;
                break;
            case 9:
                firstLetterColorResourceId = R.color.rem9;
                break;
        }
        return ContextCompat.getColor(getContext(), firstLetterColorResourceId);
    }

}
