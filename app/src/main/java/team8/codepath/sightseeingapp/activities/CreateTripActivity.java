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
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;
    EditText etTripName;
    ImageButton btnClear;
    ImageView ivPlacePhoto;

    public ArrayList<PlaceModel> alPlaces;
    private PlaceListArrayAdapter aPlaces;
    private ListView lvPlaces;
    private NumberPicker npTripLengthHours;
    private NumberPicker npTripLengthDays;
    public EditTag etTripTags;
    public FragmentPagerAdapter fragmentPagerAdapter;

    public TripModel newTrip;

    public GeoFire geoFire;

    int tripID = 0;
    int placeID = 0;

    public InputMethodManager imm;

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
/*
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();*/
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



//        setupPlacesAutoComplete();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_create, menu);
        MenuItem createItem = menu.findItem(R.id.action_create);
        createItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                newTrip.places = alPlaces;
                Log.d("debug", newTrip.toString());
                Log.d("debug", newTrip.toString());
/*                Fragment fragmentTripLength = fragmentPagerAdapter.getItem(2);

                EditText etTripName = (EditText) findViewById(R.id.etTripName);
                String name = etTripName.getText().toString();

            //    int totalLength = getTripLength();
                //String placeId = places.get(0).placeId;
                //List<String> tripTags = etTripTags.getTagList();
                //writeNewTrip(name, totalLength, placeId, places, tripTags);*/
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

    private void setupViews(){

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_trip_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
       vpPager.setAdapter(new CreateTripPagerAdapter(getSupportFragmentManager()));


        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

/*
        etTripName = (EditText) findViewById(R.id.etTripName);
        btnClear = (ImageButton) findViewById(R.id.btnClear);
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        etTripTags = (EditTag) findViewById(R.id.etTripTags);
        etTripTags.setEditable(true);
        ivPlacePhoto = (ImageView) findViewById(R.id.ivPlacePhoto);
*/

        /*// Setup list of Places within trip
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        places = new ArrayList<>();
        aPlaces = new PlaceListArrayAdapter(this, places, mGoogleApiClient);
        lvPlaces.setAdapter(aPlaces);

        lvPlaces.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id){
                final int position = pos;
                new AlertDialog.Builder(CreateTripActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Remove place?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                PlaceModel placeToDelete = aPlaces.getItem(position);
                                aPlaces.remove(placeToDelete);
                                aPlaces.notifyDataSetChanged();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });*/

/*
        // Setup Number Picker for Trip Length
        npTripLengthDays = (NumberPicker) findViewById(R.id.npTripLengthDays);
        npTripLengthDays.setMinValue(0);
        npTripLengthDays.setMaxValue(7);
        npTripLengthDays.setWrapSelectorWheel(true);
        npTripLengthDays.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npTripLengthHours = (NumberPicker) findViewById(R.id.npTripLengthHours);
        npTripLengthHours.setMinValue(0);
        npTripLengthHours.setMaxValue(23);
        npTripLengthHours.setWrapSelectorWheel(true);
        npTripLengthHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);*/
    }


    private void setupPlacesAutoComplete() {

        actvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_places);
        // Register a listener that receives callbacks when a suggestion has been selected
     //   actvPlaces.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API
/*        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);*/

        // Set up the 'clear text' button that clears the text in the autocomplete view
/*        ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvPlaces.setText("");
            }
        });*/

    }



    // Once a user has selected a Place from autocomplete
/*
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            */
/*
             Issue a request to the Places Geo Data API to retrieve a PlaceModel object with additional
             details about the place.
              *//*

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Called getPlaceById to get PlaceModel details for " + placeId);
        }
    };
*/



/*    // Callback from successfully retrieving Google Place.
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "PlaceModel query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the PlaceModel object from the buffer.
            final Place place = places.get(0);
            PlaceModel newPlace = new PlaceModel();
            newPlace.placeId = place.getId();
            newPlace.name = place.getName().toString();
            newPlace.longitude = place.getLatLng().longitude;
            newPlace.latitude = place.getLatLng().latitude;
            actvPlaces.setText("");
            aPlaces.add(newPlace);
            Log.i(TAG, "PlaceModel details received: " + place.getName());
            places.release();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    };*/


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
/*
    public int getTripLength(){
        int totalHours;
        int daysCount = npTripLengthDays.getValue();
        int hoursCount = npTripLengthHours.getValue();
        totalHours = daysCount > 0 ? (daysCount * 24) : 0;
        totalHours += hoursCount;
        return totalHours;
    }*/

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
