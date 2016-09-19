package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.originqiu.library.EditTag;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.PlacesRecyclerAdapter;
import team8.codepath.sightseeingapp.classes.AppBarStateChangeListener;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;

public class TripDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    String name = "";
    String distance = "";
    String time = "";

    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.tvRating)
    TextView tvRating;
    @BindView(R.id.tvPriceAvg)
    TextView tvPriceAvg;
    @BindView(R.id.llPrice)
    LinearLayout llPrice;

    private View mMap;
    DatabaseReference databaseReferenceRecent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            // set an enter transition
            getWindow().setEnterTransition(new Explode());
            // set an exit transition
            getWindow().setExitTransition(new Explode());

        }

        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SightseeingApplication app = (SightseeingApplication) getApplicationContext();
        UserModel user = app.getUserInfo();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvTrips);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        databaseReferenceRecent = app.getUsersReference()
                .child(Utilities.encodeEmail(user.getEmail()))
                .child(Constants.FIREBASE_LOCATION_LIST_RECENT);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        TripModel trip = Parcels.unwrap(getIntent().getParcelableExtra("trip"));
        name = trip.getName();
        distance = trip.getDistance();
        time = trip.getHumanReadableTotalLength();
        List<String> tripTags = trip.getTripTags();
        //Recently viewed
        databaseReferenceRecent.child(trip.getId()).setValue(trip);

        final TextView tripName = (TextView) findViewById(R.id.tvTitle);
        tripName.setText(name);

        EditTag etViewTripTags = (EditTag) findViewById(R.id.etViewTripTags);
        if (tripTags != null) {
            etViewTripTags.setTagList(tripTags);
            etViewTripTags.setEditable(false);
        }

        TextView tripDistance = (TextView) findViewById(R.id.tvDistance);
        tripDistance.setText(distance);

        TextView tripTime = (TextView) findViewById(R.id.tvTime);
        tripTime.setText("Length: " + time);

        final TextView tvPlacesNumber = (TextView) findViewById(R.id.tvPlacesNumber);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        collapsingToolbarLayout.setTitle(trip.getName());
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = mMapFragment.getView();

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.name().equals("COLLAPSED")) {
                    mMap.animate().translationY(mMap.getHeight());
                    tripName.animate().alpha(0.0f);
                    tripName.setVisibility(View.GONE);

                } else if (state.name().equals("EXPANDED")) {
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
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        DatabaseReference databaseReference = app.getPlacesReference().child(trip.getId().toString());
        FirebaseRecyclerAdapter adapter = new PlacesRecyclerAdapter(R.layout.item_place, databaseReference, getSupportFragmentManager(), mGoogleApiClient, fab, tvRating, tvPriceAvg, llPrice);

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
