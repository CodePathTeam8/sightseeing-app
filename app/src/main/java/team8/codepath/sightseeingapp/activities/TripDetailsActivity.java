package team8.codepath.sightseeingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.PlacesArrayAdapter;
import team8.codepath.sightseeingapp.models.Trip;

public class TripDetailsActivity extends AppCompatActivity {
    String name;
    String distance;
    String time;
    private ArrayList<String> places;
    private PlacesArrayAdapter aPlaces;
    private ListView lvPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Trip trip = (Trip) Parcels.unwrap(getIntent().getParcelableExtra("trip"));
        name = trip.getName();
        distance = trip.getDistance();
        time = trip.getTotalLength();

        TextView tripName = (TextView) findViewById(R.id.tvTitle);
        tripName.setText(name);

        TextView tripDistance = (TextView) findViewById(R.id.tvDistance);
        tripDistance.setText(distance);

        TextView tripTime = (TextView) findViewById(R.id.tvTime);
        tripTime.setText("Time: " + time + " Hours");

        ImageView tripImage = (ImageView) findViewById(R.id.ivMap);
        Picasso.with(getApplicationContext()).load(trip.getBannerPhoto()).into(tripImage);

        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        places = new ArrayList<String>();
        aPlaces = new PlacesArrayAdapter(this, places);
        lvPlaces.setAdapter(aPlaces);


        aPlaces.addAll(trip.getPlaces());


        aPlaces.notifyDataSetChanged();

    }


}
