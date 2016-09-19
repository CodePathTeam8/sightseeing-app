package team8.codepath.sightseeingapp.fragments.CreateTrip;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import me.originqiu.library.EditTag;
import team8.codepath.sightseeingapp.R;
import team8.codepath.sightseeingapp.activities.CreateTripActivity;


public class CreateTripTagsFragment extends Fragment {

    public EditTag etTripTags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_create_trip_tags, container, false);

        etTripTags = (EditTag) v.findViewById(R.id.etTripTags);
        etTripTags.setEditable(true);


        final CreateTripActivity mainActivity = (CreateTripActivity) getActivity();

        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        Button button = (Button) v.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainActivity.newTrip.tripTags = etTripTags.getTagList();
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
