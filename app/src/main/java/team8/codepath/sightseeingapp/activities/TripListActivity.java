package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.parceler.Parcels;

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

        lvTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                Trip trip = aTrips.getItem(position);
                intent.putExtra("trip", Parcels.wrap(trip));
                startActivity(intent);
            }
        });

    }
}
