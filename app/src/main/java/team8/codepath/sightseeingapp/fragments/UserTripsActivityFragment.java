package team8.codepath.sightseeingapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.adapters.TripsRecyclerAdapter;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;

public class UserTripsActivityFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.rvTrips)
    RecyclerView rvTrips;


    public UserTripsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_trips, container, false);
        ButterKnife.bind(this, view);

        SightseeingApplication app = (SightseeingApplication) getActivity().getApplicationContext();
        UserModel user = app.getUserInfo();

        getActivity().setTitle("Trips created by " + Utilities.getFirstName(user.getName()));


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();


        DatabaseReference databaseReference = app.getUsersReference()
                .child(Utilities.encodeEmail(user.getEmail()))
                .child(Constants.FIREBASE_LOCATION_LIST_CREATED);

        FirebaseRecyclerAdapter adapter = new TripsRecyclerAdapter(R.layout.item_trip, databaseReference, mGoogleApiClient, databaseReference);
        rvTrips.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTrips.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
