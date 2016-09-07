package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberButton;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;

import org.parceler.Parcels;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.PlacesArrayAdapter;
import team8.codepath.sightseeingapp.adapters.PlacesRecyclerAdapter;
import team8.codepath.sightseeingapp.classes.AppBarStateChangeListener;
import team8.codepath.sightseeingapp.models.TripModel;

public class TripDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    String name = "";
    String distance = "";
    String time = "";

    private ArrayList<String> places;
    private PlacesArrayAdapter aPlaces;
    private ListView lvPlaces;

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;

    public static final String TAG = "PLACES API";
    protected GoogleApiClient mGoogleApiClient;

    RideRequestButton requestButton;
    private View mMap;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SightseeingApplication app = (SightseeingApplication) getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.rvTrips);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        TripModel trip = (TripModel) Parcels.unwrap(getIntent().getParcelableExtra("trip"));
        name = trip.getName();
        distance = trip.getDistance();
        time = trip.getHumanReadableTotalLength();

        final TextView tripName = (TextView) findViewById(R.id.tvTitle);
        tripName.setText(name);

        TextView tripDistance = (TextView) findViewById(R.id.tvDistance);
        tripDistance.setText(distance);

        TextView tripTime = (TextView) findViewById(R.id.tvTime);
        tripTime.setText("Lenght: " + time);

        final TextView tvPlacesNumber = (TextView) findViewById(R.id.tvPlacesNumber);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        collapsingToolbarLayout.setTitle(trip.getName());
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = mMapFragment.getView();

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if(state.name().equals("COLLAPSED")){
                    mMap.animate().translationY(mMap.getHeight());
                    tripName.animate().alpha(0.0f);
                    tripName.setVisibility(View.GONE);

                }else if(state.name().equals("EXPANDED")){
                    mMap.animate().translationY(0);
                    tripName.animate().alpha(1.0f);
                    tripName.setVisibility(View.VISIBLE);
                }
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        databaseReference = app.getPlacesReference().child(trip.getId().toString());
        adapter = new PlacesRecyclerAdapter(R.layout.item_place, databaseReference, getSupportFragmentManager(), mGoogleApiClient, fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numChildren = dataSnapshot.getChildrenCount();
                tvPlacesNumber.setText(String.valueOf(numChildren) + " Places");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*requestButton = (RideRequestButton) findViewById(R.id.btnRequest);
        RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location.
                .setDropoffLocation(37.795079, -122.4397805, "Embarcadero", "One Embarcadero Center, San Francisco")
                .build();

        // set parameters for the RideRequestButton instance
        requestButton.setRideParameters(rideParams);*/

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
