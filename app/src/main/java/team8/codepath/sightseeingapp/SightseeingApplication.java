package team8.codepath.sightseeingapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

import team8.codepath.sightseeingapp.models.UserModel;
import team8.codepath.sightseeingapp.utils.Constants;

public class SightseeingApplication extends Application{

    UserModel currentUser;
    private DatabaseReference tripsReference;
    private DatabaseReference placesReference;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // Initialize Firebase instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        tripsReference = database.getReference(Constants.FIREBASE_LOCATION_LIST_TRIPS);
        placesReference = database.getReference(Constants.FIREBASE_LOCATION_LIST_PLACES);

        //Configuration Builder - Uber
        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("uG6_xar-lQ1zhWBnEgMsZwRTFS3kBMD_")
                // required for enhanced button features
                .setServerToken("I-zY9MYBLHg-7QyWAZgMqekbyqGT2-mcas4QSg7U")
                // required for implicit grant authentication
                .setRedirectUri("http://localhost")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);

    }

    public DatabaseReference getTripsReference() {
        return tripsReference;
    }

    public DatabaseReference getPlacesReference() {
        return placesReference;
    }

    public void setUser(UserModel user){
         currentUser = user;
    }

    public UserModel getUser(){
        return currentUser;
    }
}
