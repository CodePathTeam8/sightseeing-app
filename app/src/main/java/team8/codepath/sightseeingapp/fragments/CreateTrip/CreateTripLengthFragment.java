package team8.codepath.sightseeingapp.fragments.CreateTrip;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import me.originqiu.library.EditTag;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.CreateTripActivity;

public class CreateTripLengthFragment extends Fragment {

    private NumberPicker npTripLengthHours;
    private NumberPicker npTripLengthDays;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_trip_length, container, false);


        // Setup Number Picker for Trip Length
        npTripLengthDays = (NumberPicker) v.findViewById(R.id.npTripLengthDays);
        npTripLengthDays.setMinValue(0);
        npTripLengthDays.setMaxValue(7);
        npTripLengthDays.setWrapSelectorWheel(true);
        npTripLengthDays.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npTripLengthHours = (NumberPicker) v.findViewById(R.id.npTripLengthHours);
        npTripLengthHours.setMinValue(0);
        npTripLengthHours.setMaxValue(23);
        npTripLengthHours.setWrapSelectorWheel(true);
        npTripLengthHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final CreateTripActivity mainActivity = (CreateTripActivity) getActivity();

        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        Button button = (Button) v.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainActivity.newTrip.totalLength = getTripLength();
                mainActivity.createItem.setVisible(true);
                viewPager.setCurrentItem(3);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public int getTripLength(){
        int totalHours;
        int daysCount = npTripLengthDays.getValue();
        int hoursCount = npTripLengthHours.getValue();
        totalHours = daysCount > 0 ? (daysCount * 24) : 0;
        totalHours += hoursCount;
        return totalHours;
    }


}
