package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.originqiu.library.EditTag;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.PlacesArrayAdapter;
import team8.codepath.sightseeingapp.adapters.PlacesRecyclerAdapter;
import team8.codepath.sightseeingapp.models.TripModel;

public class TripDetailActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    String name;
    String distance;
    String time;

    private ArrayList<String> places;
    private PlacesArrayAdapter aPlaces;
    private ListView lvPlaces;
    private GoogleMap mMap;
    private List<String> tripTags;

    private RecyclerView recyclerView;

    public static final String TAG = "PLACES API";
    protected GoogleApiClient mGoogleApiClient;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SightseeingApplication app = (SightseeingApplication) getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.rvTrips);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(0, 0, 0));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        TripModel trip = (TripModel) Parcels.unwrap(getIntent().getParcelableExtra("trip"));
        name = trip.getName();
        distance = trip.getDistance();
        time = trip.getHumanReadableTotalLength();
        tripTags = trip.getTripTags();

        TextView tripName = (TextView) findViewById(R.id.tvTitle);
        tripName.setText(name);

        EditTag etViewTripTags = (EditTag) findViewById(R.id.etViewTripTags);
        if (tripTags!=null) {
            etViewTripTags.setTagList(tripTags);
            etViewTripTags.setEditable(false);
        }

        TextView tripDistance = (TextView) findViewById(R.id.tvDistance);
        tripDistance.setText(distance);

        TextView tripTime = (TextView) findViewById(R.id.tvTime);
        tripTime.setText("Length: " + time);

        final TextView tvPlacesNumber = (TextView) findViewById(R.id.tvPlacesNumber);

        databaseReference = app.getPlacesReference().child(trip.getId().toString());
        adapter = new PlacesRecyclerAdapter(R.layout.item_place, databaseReference, getSupportFragmentManager(), mGoogleApiClient);

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


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
