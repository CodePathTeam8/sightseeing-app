package team8.codepath.sightseeingapp.services;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import team8.codepath.sightseeingapp.SightseeingApplication;
import team8.codepath.sightseeingapp.classes.PhotoTask;
import team8.codepath.sightseeingapp.models.TripModel;
import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;
import team8.codepath.sightseeingapp.utils.Utilities;


public class ImagesIntentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    GoogleApiClient mGoogleApiClient;
    DatabaseReference  dbReferenceFavs;

    HashMap images = new HashMap();


    public ImagesIntentService() {
        super("ImagesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        SightseeingApplication app = (SightseeingApplication) getApplicationContext();
        UserModel user = app.getUserInfo();
        dbReferenceFavs  = app.getUsersReference().child(Utilities.encodeEmail(user.getEmail())).child(Constants.FIREBASE_LOCATION_LIST_FAVORITES);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mGoogleApiClient.connect();

        //Log.d("TEST", "Executing");


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        dbReferenceFavs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    final TripModel trip = child.getValue(TripModel.class);

                    String PhotoPlaceId = trip.getPlaceId();
                    new PhotoTask(400, 400, mGoogleApiClient) {
                        @Override
                        protected void onPreExecute() {}
                        @Override
                        protected void onPostExecute(AttributedPhoto attributedPhoto) {
                            if (attributedPhoto != null) {
                                images.put(trip.getId(), attributedPhoto.bitmap);

                                //Log.d("TEST", images.toString());
                            }
                        }
                    }.execute(PhotoPlaceId);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}