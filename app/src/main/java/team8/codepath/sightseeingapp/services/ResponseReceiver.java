package team8.codepath.sightseeingapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;

import java.util.HashMap;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.TripListActivity;

public class ResponseReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP =
            "team8.codepath.sightseeingapp.intent.action.MESSAGE_PROCESSED";

    public ResponseReceiver(Handler handler) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


    }

    public void setReceiver(TripListActivity tripListActivity) {
    }

    public interface Receiver {
    }
}