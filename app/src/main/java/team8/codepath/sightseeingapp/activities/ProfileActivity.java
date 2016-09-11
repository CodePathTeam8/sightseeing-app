package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    SightseeingApplication app;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    private GoogleMap mMap;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvLocationName)
    TextView tvLocationName;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        app = (SightseeingApplication) getApplicationContext();

        setUserInformation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void setUserInformation() {

        UserModel user = app.getUserInfo();

        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setTitle(user.getName());

        tvUserName.setText(user.getName());
        tvLocationName.setText(user.getLocationName());

        tvBio.setText(user.getBio());
        if(user.getBio().equals(Constants.EMPTY_STRING)){
            tvBio.setVisibility(View.GONE);
        }
        tvEmail.setText(user.getEmail());


    }

}
