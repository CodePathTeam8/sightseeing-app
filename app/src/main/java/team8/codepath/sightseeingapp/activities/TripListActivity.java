package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.TripsRecyclerAdapter;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;

public class TripListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.fabCreateTrip)
    FloatingActionButton fabCreateTrip;
    @BindView(R.id.ndTrips)
    DrawerLayout ndTrips;
    @BindView(R.id.nvView)
    NavigationView nvView;
    @BindView(R.id.rvTrips)
    RecyclerView rvTrips;
    private ArrayList<TripModel> trips;
    private TripsRecyclerAdapter aTrips;
    private ListView lvTrips;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");
    private FirebaseRecyclerAdapter adapter;


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

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        adapter = new TripsRecyclerAdapter(R.layout.item_trip, databaseReference, mGoogleApiClient);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setAdapter(adapter);
        adapter.notifyDataSetChanged();


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
        switch (menuItem.getItemId()) {

            case R.id.navLogout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);

                break;
            case R.id.navProfile:

                Intent p = new Intent(this, ProfileActivity.class);
                startActivity(p);
                break;
        }

        // Close the ic_navigation drawer
        ndTrips.closeDrawers();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
