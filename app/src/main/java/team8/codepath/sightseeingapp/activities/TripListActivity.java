package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.TripsArrayAdapter;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;

public class TripListActivity extends AppCompatActivity {

    @BindView(R.id.fabCreateTrip)
    FloatingActionButton fabCreateTrip;
    @BindView(R.id.ndTrips)
    DrawerLayout ndTrips;
    @BindView(R.id.nvView)
    NavigationView nvView;
    private ArrayList<TripModel> trips;
    private TripsArrayAdapter aTrips;
    private ListView lvTrips;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(this);

        SightseeingApplication app = (SightseeingApplication) getApplicationContext();
        UserModel currentUser = app.getUser();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        lvTrips = (ListView) findViewById(R.id.lvTrips);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("trips");
        final FirebaseListAdapter<TripModel> mAdapter = new FirebaseListAdapter<TripModel>(this, TripModel.class, R.layout.item_trip, ref) {
            @Override
            protected void populateView(View view, TripModel trip, int position) {
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
                TripModel trip = mAdapter.getItem(position);
                Log.d("Trip", trip.toString());

                intent.putExtra("trip", Parcels.wrap(trip));
                startActivity(intent);
            }
        });


        fabCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TripListActivity.this, CreateTripActivity.class);
                startActivity(i);
            }
        });

        // Setup drawer view
        setupDrawerContent(nvView);


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
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                ndTrips.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {

            case R.id.navLogout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);

                break;
        }

        // Close the navigation drawer
        ndTrips.closeDrawers();
    }


}
