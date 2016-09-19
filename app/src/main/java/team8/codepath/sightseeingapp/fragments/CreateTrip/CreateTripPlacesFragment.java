package team8.codepath.sightseeingapp.fragments.CreateTrip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.CreateTripActivity;
import team8.codepath.sightseeingapp.adapters.PlaceAutocompleteAdapter;
import team8.codepath.sightseeingapp.adapters.PlaceListArrayAdapter;
import team8.codepath.sightseeingapp.models.PlaceModel;


public class CreateTripPlacesFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {


    private ArrayList<PlaceModel> places;
    private PlaceListArrayAdapter aPlaces;
    private ListView lvPlaces;
    public GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView actvPlaces;

    public InputMethodManager imm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_trip_places, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();

        final CreateTripActivity mainActivity = (CreateTripActivity) getActivity();

        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainActivity.places = places;
                mainActivity.newTrip.placeId = places.get(0).placeId;
                viewPager.setCurrentItem(2);
            }
        });

        // Setup list of Places within trip
        lvPlaces = (ListView) v.findViewById(R.id.lvPlaces);
        places = new ArrayList<>();
        aPlaces = new PlaceListArrayAdapter(getContext(), places, mGoogleApiClient);
        lvPlaces.setAdapter(aPlaces);
        actvPlaces = (AutoCompleteTextView) v.findViewById(R.id.actv_places);
        ImageButton btnClear = (ImageButton) v.findViewById(R.id.btnClear);
        // Set up the 'clear text' button that clears the text in the autocomplete view
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvPlaces.setText("");
            }
        });

        imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        lvPlaces.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id){
                final int position = pos;
                new AlertDialog.Builder(getContext())
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
        });

        setupPlacesAutoComplete();
        return v;
    }


    private void setupPlacesAutoComplete() {

        // Register a listener that receives callbacks when a suggestion has been selected
        actvPlaces.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API
        mAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, null,
                null);
        actvPlaces.setAdapter(mAdapter);


    }



    // Once a user has selected a Place from autocomplete
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    // Callback from successfully retrieving Google Place.
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("hay", "PlaceModel query did not complete. Error: " + places.getStatus().toString());
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
            places.release();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    };


    // Connection to Google API Fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


}
