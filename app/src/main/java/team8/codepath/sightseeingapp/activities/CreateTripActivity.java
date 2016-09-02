package team8.codepath.sightseeingapp.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
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
import java.util.Map;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.PlaceListArrayAdapter;
import team8.codepath.sightseeingapp.models.PlaceModel;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.classes.PhotoTask;

public class CreateTripActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{


    public static final String TAG = "CreateTripActivity";
    public GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;
    EditText etTripName;
    ImageButton btnClear;
    ImageView ivPlacePhoto;
    TextView tvPlacePhotoInfo;

    private ArrayList<PlaceModel> places;
    private PlaceListArrayAdapter aPlaces;
    private ListView lvPlaces;
    private NumberPicker npTripLength;

    int tripID = 0;
    int placeID = 0;

    public InputMethodManager imm;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("trips");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_create_trip);
        setupViews();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setupPlacesAutoComplete();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_create, menu);
        MenuItem createItem = menu.findItem(R.id.action_create);
        createItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                String name = etTripName.getText().toString();
                int totalLength = npTripLength.getValue();
                // Temporary hardcoded to get the first banner photo
                //tvPlacePhotoInfo.getText().toString();
                //String bannerPhoto = aPlaces.getItem(0).bannerPhoto;
                String placeId = places.get(0).placeId;
                writeNewTrip(name, totalLength, placeId, places);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void setupViews(){

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_trip_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        etTripName = (EditText) findViewById(R.id.etTripName);
        btnClear = (ImageButton) findViewById(R.id.btnClear);
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        ivPlacePhoto = (ImageView) findViewById(R.id.ivPlacePhoto);
        tvPlacePhotoInfo = (TextView) findViewById(R.id.tvPlacePhotoInfo);//.findViewById(R.id.languageHeader)


        // Setup list of Places within trip
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        places = new ArrayList<>();
        aPlaces = new PlaceListArrayAdapter(this, places, mGoogleApiClient);
        lvPlaces.setAdapter(aPlaces);


        // Setup Number Picker for Trip Length
        npTripLength = (NumberPicker) findViewById(R.id.npTripLength);
        npTripLength.setMinValue(1);
        npTripLength.setMaxValue(48);
        npTripLength.setWrapSelectorWheel(true);
        npTripLength.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }


    private void setupPlacesAutoComplete() {

        actvPlaces = (AutoCompleteTextView) findViewById(R.id.actv_places);
        // Register a listener that receives callbacks when a suggestion has been selected
        actvPlaces.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);

        // Set up the 'clear text' button that clears the text in the autocomplete view
        ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvPlaces.setText("");
            }
        });

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

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a PlaceModel object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Called getPlaceById to get PlaceModel details for " + placeId);
        }
    };



    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
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
            actvPlaces.setText("");
            aPlaces.add(newPlace);
            Log.i(TAG, "PlaceModel details received: " + place.getName());
            places.release();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }





    private void writeNewTrip(String name, int totalLength, String placeId, ArrayList<PlaceModel> places) {

        TripModel trip = new TripModel(tripID+"", name, placeId, totalLength +"", null, null);
        Map<String, Object> childUpdates = new HashMap<>();

        // make a new child object under Trips, and get key for it.
        String key = databaseReference.child("trips").push().getKey();
        Map<String, Object> tripValues = trip.toMap(key);
        childUpdates.put("trips/" + key, tripValues);

        // make a new child object under Places
        databaseReference.child("places/" + key).push();

        if (places != null) {
            for (int i = 0; i <= places.size()-1; i++) {
                Map<String, Object> placeValues = places.get(i).toMap();
                String childPlaceKey = databaseReference.child("places/" + key).push().getKey();
                childUpdates.put("places/" + key + "/" + childPlaceKey, placeValues);
                placeID++;
            }
        };

        databaseReference.updateChildren(childUpdates);
        tripID ++;
        finish();
    }
}
