package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.models.PlaceModel;

/**
 * Created by meganoneill on 8/21/16.
 */
public class PlaceListArrayAdapter extends ArrayAdapter<PlaceModel> {

    public PlaceListArrayAdapter(Context context, List<PlaceModel> places) {
        super(context, android.R.layout.simple_list_item_1, places);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceModel place = this.getItem(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_place_list, parent, false);
        }

        String name = place.getName();
        String placeId = place.getPlaceId();

        TextView placeName = (TextView) convertView.findViewById(R.id.tvPlaceName);
        placeName.setText(name);

        TextView placeHours = (TextView) convertView.findViewById(R.id.tvPlaceId);
        placeHours.setText(placeId);

        return convertView;
    }
}
