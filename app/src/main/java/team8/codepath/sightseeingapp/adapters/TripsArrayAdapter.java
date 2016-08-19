package team8.codepath.sightseeingapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.models.Trip;

/**
 * Created by floko_000 on 8/18/2016.
 */
public class TripsArrayAdapter extends ArrayAdapter<Trip>{

    private Context context;

    public TripsArrayAdapter(Context context, List<Trip> trips){
        super(context, 0, trips);
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        Trip trip = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trip, parent, false);
        }

        // Get Subviews
        ImageView ivTripBanner = (ImageView) convertView.findViewById(R.id.ivTripBanner);
        TextView tvTripName = (TextView) convertView.findViewById(R.id.tvTripName);
        TextView tvTripDistance = (TextView) convertView.findViewById(R.id.tvTripDistance);
        TextView tvTripLength = (TextView) convertView.findViewById(R.id.tvTripLength);

        // Populate subviews
        ivTripBanner.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        Picasso.with(getContext()).load(trip.getBannerPhoto()).into(ivTripBanner);
        tvTripName.setText(trip.getName());
        tvTripDistance.setText(trip.getDistance());
        tvTripLength.setText(trip.getTotalLength());



        // 5. Return the view to be inserted into the list
        return convertView;

    }

}
