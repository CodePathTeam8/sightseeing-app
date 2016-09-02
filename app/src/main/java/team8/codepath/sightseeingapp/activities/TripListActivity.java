package team8.codepath.sightseeingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.TripsArrayAdapter;
import team8.codepath.sightseeingapp.models.PlaceModel;
import team8.codepath.sightseeingapp.models.TripModel;

public class TripListActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

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
    private DatabaseReference geoDatabase = FirebaseDatabase.getInstance().getReference("geofire");
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;
    public InputMethodManager imm;

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

        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Setup drawer view
        setupDrawerContent(nvView);
        setupPlacesAutoComplete();
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

    private void setupPlacesAutoComplete() {

        actvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_search_places);
        // Register a listener that receives callbacks when a suggestion has been selected
        actvPlaces.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
//        ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actvPlaces.setText("");
//            }
//        });

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each PlaceModel suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);


            /*
             Issue a request to the Places Geo Data API to retrieve a PlaceModel object with additional
             details about the place.
              */
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
//                Log.e(TAG, "PlaceModel query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the PlaceModel object from the buffer.
            final Place place = places.get(0);
            PlaceModel newPlace = new PlaceModel();
            LatLng location = place.getLatLng();
            newPlace.name = place.getName().toString();
            actvPlaces.setText("");
//            aPlaces.add(newPlace);
//            Log.i(TAG, "PlaceModel details received: " + place.getName());
            String[] splited = location.toString().split("\\s+");
            String latlng = splited[1].replaceAll("\\(|\\)","");
            String[] latlngSplit = latlng.split(",");
            Double latitude = Double.parseDouble(latlngSplit[0]);
            Double longitude = Double.parseDouble(latlngSplit[1]);
            findNearbyPlaces( latitude, longitude, 10);
            Log.d("DEBUG", latlngSplit[0]);
            places.release();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
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
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
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
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }
}
