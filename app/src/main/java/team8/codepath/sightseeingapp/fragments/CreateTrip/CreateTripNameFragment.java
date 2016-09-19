package team8.codepath.sightseeingapp.fragments.CreateTrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.CreateTripActivity;

public class CreateTripNameFragment extends Fragment {
    public EditText etTripName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_trip_name, container, false);
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        final CreateTripActivity mainActivity = (CreateTripActivity) getActivity();
        etTripName = (EditText) v.findViewById(R.id.etTripName);
        etTripName.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        etTripName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mainActivity.newTrip.name = etTripName.getText().toString();
                    viewPager.setCurrentItem(1);
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



}
