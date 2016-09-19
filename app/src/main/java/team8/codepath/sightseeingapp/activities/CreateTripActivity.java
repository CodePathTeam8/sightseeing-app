package team8.codepath.sightseeingapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.originqiu.library.EditTag;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.PlaceListArrayAdapter;
import team8.codepath.sightseeingapp.fragments.CreateTrip.CreateTripLengthFragment;
import team8.codepath.sightseeingapp.fragments.CreateTrip.CreateTripNameFragment;
import team8.codepath.sightseeingapp.fragments.CreateTrip.CreateTripPlacesFragment;
import team8.codepath.sightseeingapp.fragments.CreateTrip.CreateTripTagsFragment;
import team8.codepath.sightseeingapp.models.PlaceModel;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;

public class CreateTripActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{


    public static final String TAG = "CreateTripActivity";
    public GoogleApiClient mGoogleApiClient;

    public ArrayList<PlaceModel> places;
    public FragmentPagerAdapter fragmentPagerAdapter;

    public TripModel newTrip;

    public GeoFire geoFire;

    int tripID = 0;
    int placeID = 0;

    public MenuItem createItem;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("trips");
    DatabaseReference databaseReferenceCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SightseeingApplication app = (SightseeingApplication)getApplicationContext();
        UserModel user = app.getUserInfo();

        newTrip = new TripModel();


        databaseReferenceCreated = app.getUsersReference()
                .child(Utilities.encodeEmail(user.getEmail()))
                .child(Constants.FIREBASE_LOCATION_LIST_CREATED);
        setContentView(R.layout.activity_create_trip);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_trip_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new CreateTripPagerAdapter(getSupportFragmentManager()));

        fragmentPagerAdapter = (FragmentPagerAdapter) vpPager.getAdapter();

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference geoFireRef = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(geoFireRef);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_create, menu);
        createItem = menu.findItem(R.id.action_create);
        createItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                writeNewTrip(newTrip.name, newTrip.totalLength, newTrip.placeId,  newTrip.places, newTrip.tripTags);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    // Connection to Google API Fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private void writeNewTrip(String name, int totalLength, String placeId, ArrayList<PlaceModel> places, List<String> tripTags) {

        TripModel trip = new TripModel(tripID+"", name, placeId, totalLength, null, null, tripTags);
        Map<String, Object> childUpdates = new HashMap<>();

        // make a new child object under Trips, and get key for it.
        String key = databaseReference.child("trips").push().getKey();
        Map<String, Object> tripValues = trip.toMap(key);
        childUpdates.put("trips/" + key, tripValues);

        // make a new child object under Places
        databaseReference.child("places/" + key).push();

        if (places != null) {
            for (int i = 0; i <= places.size()-1; i++) {
                PlaceModel newPlace = places.get(i);
                Map<String, Object> placeValues = newPlace.toMap();
                String childPlaceKey = databaseReference.child("places/" + key).push().getKey();
                childUpdates.put("places/" + key + "/" + childPlaceKey, placeValues);
                geoFire.setLocation(newPlace.getPlaceId(), new GeoLocation(newPlace.latitude, newPlace.longitude));
                placeID++;
            }
        };

        databaseReference.updateChildren(childUpdates);

        //save created by - User reference
        databaseReferenceCreated.child(key).setValue(tripValues);
        tripID ++;
        finish();
    }

    // Tab Slider Setup
    // This will make the create trip look like it is multiple pages
    public class CreateTripPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = {"Name your trip", "Add the places", "Estimate Trip Length", "Add Tags"};

    // Adapter gets teh manager insert of remove fragment from activity
    public CreateTripPagerAdapter(FragmentManager fm){
        super(fm);
    }

    // Order and creation of fragments within the pager
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new CreateTripNameFragment();
        }
        else if (position ==1){
            return new CreateTripPlacesFragment();
        }
        else if (position == 2){
            return new CreateTripLengthFragment();
        }
        else if (position == 3){
            return new CreateTripTagsFragment();
        }
        else
            return null;
    }

    // Return Tab Title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    // Returns number of tabs
    @Override
    public int getCount() {
        return tabTitles.length;
    }


}


}
