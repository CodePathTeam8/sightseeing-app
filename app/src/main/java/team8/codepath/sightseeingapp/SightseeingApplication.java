package team8.codepath.sightseeingapp;

import android.app.Application;
import android.content.SharedPreferences;

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

    public void setUserInfo(UserModel user) {

        SharedPreferences.Editor editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
        editor.putString("id", user.getId());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("gender", user.getGender());
        editor.putString("locationId", user.getLocationId());
        editor.putString("locationName", user.getLocationName());
        editor.putString("bio", user.getBio());
        editor.putString("languages", user.getLanguages());
        editor.commit();
    }

    public UserModel getUserInfo(){
        return getUserPreferencesAsModel();
    }

    public UserModel getUserPreferencesAsModel(){
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);

        UserModel user = new UserModel();
        user.setId(prefs.getString("id", ""));
        user.setName(prefs.getString("name", "User Name"));
        user.setEmail(prefs.getString("email", "Email"));
        user.setGender(prefs.getString("gender", "Gender"));
        user.setLocationId(prefs.getString("locationId", "location id"));
        user.setLocationName(prefs.getString("locationName", "location name"));
        user.setBio(prefs.getString("bio", ""));
        user.setLanguages(prefs.getString("languages", "English"));

        return user;
    }

}
