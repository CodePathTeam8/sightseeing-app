package team8.codepath.sightseeingapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.TripsArrayAdapter;
import team8.codepath.sightseeingapp.models.Trip;

public class TripListActivity extends AppCompatActivity {

    private ArrayList<Trip> trips;
    private TripsArrayAdapter aTrips;
    private ListView lvTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);


        lvTrips = (ListView) findViewById(R.id.lvTrips);
        trips = new ArrayList<>();
        aTrips = new TripsArrayAdapter(this, trips);
        lvTrips.setAdapter(aTrips);


        aTrips.addAll(Trip.fromJSONArray());


        aTrips.notifyDataSetChanged();


    }
}
