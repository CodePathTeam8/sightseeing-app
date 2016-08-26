package team8.codepath.sightseeingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.adapters.PlacesArrayAdapter;
import team8.codepath.sightseeingapp.models.PlaceModel;
import team8.codepath.sightseeingapp.models.TripModel;

public class TripDetailsActivity extends FragmentActivity implements OnMapReadyCallback {
    String name;
    String distance;
    String time;
    private ArrayList<PlaceModel> places;
    private PlacesArrayAdapter aPlaces;
    private ListView lvPlaces;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TripModel trip = (TripModel) Parcels.unwrap(getIntent().getParcelableExtra("trip"));
        name = trip.getName();
        distance = trip.getDistance();
        time = trip.getTotalLength();

        TextView tripName = (TextView) findViewById(R.id.tvTitle);
        tripName.setText(name);

        TextView tripDistance = (TextView) findViewById(R.id.tvDistance);
        tripDistance.setText(distance);

        TextView tripTime = (TextView) findViewById(R.id.tvTime);
        tripTime.setText("Time: " + time + " Hours");

        ImageView tripImage = (ImageView) findViewById(R.id.ivMap);
        Picasso.with(getApplicationContext()).load(trip.getBannerPhoto()).into(tripImage);

        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        places = new ArrayList<>();
        aPlaces = new PlacesArrayAdapter(this, places);
        lvPlaces.setAdapter(aPlaces);


        aPlaces.addAll(trip.getPlaces());


        aPlaces.notifyDataSetChanged();

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
