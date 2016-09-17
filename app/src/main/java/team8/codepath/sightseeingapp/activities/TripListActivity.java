package team8.codepath.sightseeingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.TripsRecyclerAdapter;
import team8.codepath.sightseeingapp.fragments.FilterFragment;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;

public class TripListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.fabCreateTrip)
    FloatingActionButton fabCreateTrip;
    @BindView(R.id.ndTrips)
    DrawerLayout ndTrips;
    @BindView(R.id.nvView)
    NavigationView nvView;
    @BindView(R.id.rvTrips)
    RecyclerView rvTrips;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("trips");
    private DatabaseReference geoDatabase = FirebaseDatabase.getInstance().getReference("geofire");
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;
    public InputMethodManager imm;
    private FirebaseRecyclerAdapter adapter;
    Location mLastLocation;
    DatabaseReference newDbQuery;
    FirebaseRecyclerAdapter newAdapter;
    private SharedPreferences pref;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;

    SightseeingApplication app;
    UserModel user;
    DatabaseReference dbReferenceFavs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        ButterKnife.bind(this);

        setTitle("");

        app = (SightseeingApplication) getApplicationContext();
        user = app.getUserInfo();

        //User favorites
        dbReferenceFavs  = app.getUsersReference().child(Utilities.encodeEmail(user.getEmail())).child(Constants.FIREBASE_LOCATION_LIST_FAVORITES);

        // Find the toolbar view inside the activity layout
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerToggle = setupDrawerToggle();
        ndTrips.addDrawerListener(drawerToggle);

        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        adapter = new TripsRecyclerAdapter(R.layout.item_trip, databaseReference, mGoogleApiClient, dbReferenceFavs);
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

        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        actvPlaces = (AutoCompleteTextView) toolbar.findViewById(R.id.actv_search_places);
        // Register a listener that receives callbacks when a suggestion has been selected
        actvPlaces.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);

        ImageView filter = (ImageView) toolbar.findViewById(R.id.miFilter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFilterFragment();
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(this);


        // Check for location information
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
        } else {
            Toast.makeText(this, "Enable GPS for results near you",
                    Toast.LENGTH_LONG).show();
        }

        // Setup drawer view
        setupDrawerContent(nvView);
        setupPlacesAutoComplete(toolbar);
        setProfileInfo();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, ndTrips, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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

            case R.id.navHome:
                ndTrips.closeDrawers();
                break;

            case R.id.navProfile:
                Intent p = new Intent(this, ProfileActivity.class);
                startActivity(p);
                break;

            case R.id.navUserTrips:
                Intent u = new Intent(this, UserTripsActivity.class);
                startActivity(u);
                break;

            case R.id.navFavorites:
                Intent f = new Intent(this, FavoritesActivity.class);
                startActivity(f);
                break;

            case R.id.navNewTrip:
                Intent t = new Intent(this, CreateTripActivity.class);
                startActivity(t);
                break;

            case R.id.navLogout:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);

                break;

        }
        // Close the ic_navigation drawer
        ndTrips.closeDrawers();
    }

    private void launchFilterFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment editNameDialogFragment = FilterFragment.newInstance();
        editNameDialogFragment.show(fm, "fragment_filters");
    }

    private void setupPlacesAutoComplete(Toolbar toolbar) {
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);
    }

    public void setProfileInfo(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View header = navigationView.getHeaderView(0);
        TextView tvUserName = (TextView) header.findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView) header.findViewById(R.id.tvEmail);

        tvUserName.setText(user.getName());
        tvEmail.setText(user.getEmail());
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Retrieve the place ID of the selected item from the Adapter.
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            //Issue a request to the Places Geo Data API to retrieve a PlaceModel
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the PlaceModel object from the buffer.
            final Place place = places.get(0);
            LatLng location = place.getLatLng();
            places.release();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            Intent i = new Intent(TripListActivity.this, SearchActivity.class);
            i.putExtra("latitude", location.latitude);
            i.putExtra("longitude", location.longitude);
            if(pref.contains("distance")){
                String storedDistance = pref.getString("distance", "5");
                i.putExtra("distance", storedDistance);
            }else{
                i.putExtra("distance", "5");
            }
            startActivity(i);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private void findNearbyPlaces(Double lat, Double longit, Integer distance){
        GeoFire geoFire = new GeoFire(geoDatabase);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, longit), distance);
        final ArrayList<String> placeKeys = new ArrayList<>();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                placeKeys.add(key);
                Log.d("Found result", key);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                findMatchingTrips(placeKeys);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });



    }

    public void findMatchingTrips(ArrayList<String> placeKeys){
        Query queryRef;

        if(placeKeys.size() > 0){
            String firstRef = "trips/" + placeKeys.get(0);
            newDbQuery = FirebaseDatabase.getInstance().getReference(firstRef);
            FirebaseRecyclerAdapter newAdapter = new TripsRecyclerAdapter(R.layout.item_trip, newDbQuery, mGoogleApiClient, dbReferenceFavs);
            rvTrips.setAdapter(newAdapter);
        }

        for(int i=0; i<placeKeys.size() - 1; i++){
            placeKeys.get(i);
            //Insert query here to find a trip with placeId that matches
            queryRef = mDatabase.orderByChild("placeId").equalTo(placeKeys.get(i)).limitToFirst(1);
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Inside child event", dataSnapshot.getValue().toString());
                    newAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }



    }
}
