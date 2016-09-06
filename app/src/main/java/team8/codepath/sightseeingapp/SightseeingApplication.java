package team8.codepath.sightseeingapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
