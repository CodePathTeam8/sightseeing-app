package team8.codepath.sightseeingapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import team8.codepath.sightseeingapp.models.UserModel;

public class SightseeingApplication extends Application{

    UserModel currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    public void setUser(UserModel user){
         currentUser = user;
    }

    public UserModel getUser(){
        return currentUser;
    }
}
