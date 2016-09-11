package team8.codepath.sightseeingapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.models.UserModel;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    SightseeingApplication app;
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

        app = (SightseeingApplication) getApplicationContext();

        setSupportActionBar(toolbar);

        setUserInformation();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void setUserInformation() {

        UserModel user = app.getUserInfo();

        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setTitle(user.getName());

        tvUserName.setText(user.getName());
        tvLocationName.setText(user.getLocationName());

    }

}
