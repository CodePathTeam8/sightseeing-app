package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    SightseeingApplication app;
    private GoogleMap mMap;

    @BindView(R.id.ivProfile)
    ProfilePictureView ivProfile;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvLocationName)
    TextView tvLocationName;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tvLanguages)
    TextView tvLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        app = (SightseeingApplication) getApplicationContext();

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUserInformation();

    }

    private void setUserInformation() {

        UserModel user = app.getUserInfo();

        ivProfile.setProfileId(user.getId());
        tvUserName.setText(user.getName());
        tvLocationName.setText(user.getLocationName());

        tvBio.setText(user.getBio());
        if (user.getBio().equals(Constants.EMPTY_STRING)) {
            tvBio.setVisibility(View.GONE);
        }
        tvEmail.setText(user.getEmail());
        //tvLanguages.setText(user.getLanguages());

        //Set marker in User location
        LatLng userLatLong = getUserLatLong(user.getLocationName());
        mMap.addMarker(new MarkerOptions()
                .position(userLatLong)
                .icon(BitmapDescriptorFactory.fromBitmap(Utilities.writeTextOnDrawable(this, R.drawable.user_marker, "")))
                .title(user.getLocationName()));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));

        //Collapsing Toolbar options
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        //collapsingToolbarLayout.setTitle(user.getName());
        collapsingToolbarLayout.setTitle(" ");
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

    }

    private LatLng getUserLatLong(String locationName) {

        LatLng userLatLong = null;
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(this);
                List<Address> addresses = gc.getFromLocationName(locationName, 1); // get the found Address Objects

                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        userLatLong = new LatLng(a.getLatitude(), a.getLongitude());
                    }
                }
            } catch (IOException e) {
                Log.e("Geocoder Error", e.toString());
            }
        }

        return userLatLong;
    }

}
