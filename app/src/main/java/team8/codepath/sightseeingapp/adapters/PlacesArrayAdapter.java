package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;

/**
 * Created by meganoneill on 8/21/16.
 */
public class PlacesArrayAdapter extends ArrayAdapter<String> {

    public PlacesArrayAdapter(Context context, ArrayList<String> places) {
        super(context, android.R.layout.simple_list_item_1, places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String place = this.getItem(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_place, parent, false);
        }

//        String name = place.getName();
//        String hours = place.getHours();
//
//        TextView placeName = (TextView) convertView.findViewById(R.id.tvPlaceName);
//        placeName.setText(name);
//
//        TextView placeHours = (TextView) convertView.findViewById(R.id.tvPlaceHours);
//        placeHours.setText(hours);

        return convertView;
    }
}
