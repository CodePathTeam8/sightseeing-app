package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.TripsArrayAdapter;
import team8.codepath.sightseeingapp.models.Trip;

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.fabCreateTrip)
    FloatingActionButton fabCreateTrip;
    private ArrayList<Trip> trips;
    private TripsArrayAdapter aTrips;
    private ListView lvTrips;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(this);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        lvTrips = (ListView) findViewById(R.id.lvTrips);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("trips");
        final FirebaseListAdapter<Trip> mAdapter = new FirebaseListAdapter<Trip>(this, Trip.class, R.layout.item_trip, ref) {
            @Override
            protected void populateView(View view, Trip trip, int position) {
                ImageView ivTripBanner = (ImageView) view.findViewById(R.id.ivTripBanner);
                TextView tvTripName = (TextView) view.findViewById(R.id.tvTripName);
                TextView tvTripDistance = (TextView) view.findViewById(R.id.tvTripDistance);
                TextView tvTripLength = (TextView) view.findViewById(R.id.tvTripLength);

                ivTripBanner.setImageResource(android.R.color.transparent); // clear out old image for recycled view
                Picasso.with(getApplicationContext()).load(trip.getBannerPhoto()).into(ivTripBanner);
                tvTripName.setText(trip.getName());
                tvTripDistance.setText(trip.getDistance());
                tvTripLength.setText(trip.getTotalLength());


            }


        };
        lvTrips.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        lvTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                Trip trip = mAdapter.getItem(position);
                Log.d("Trip", trip.toString());
                intent.putExtra("trip", Parcels.wrap(trip));
                startActivity(intent);
            }
        });

//        fabCreateTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(TripListActivity.this, CreateTripActivity.class);
//                startActivity(i);
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // make search call
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
/*        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
