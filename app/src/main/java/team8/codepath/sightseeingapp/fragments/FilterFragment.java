package team8.codepath.sightseeingapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import team8.codepath.sightseeingapp.R;

/**
 * Created by meganoneill on 9/11/16.
 */
public class FilterFragment extends DialogFragment {
    private Button btn;
    private Spinner spinner;
    private SharedPreferences.Editor edit;
    private SharedPreferences pref;

    public FilterFragment(){

    }

    public static FilterFragment newInstance(){
        FilterFragment frag = new FilterFragment();
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        edit = pref.edit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filters, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Distance Spinner logic
        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.distance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(pref.contains("distance")){
            String storedDistance = pref.getString("distance", "5");
            spinner.setSelection(adapter.getPosition(storedDistance));
        }

        //save new preferances and dismiss dialog
        btn = (Button) view.findViewById(R.id.btnSave);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSharedPreferences();
            }
        });
    }

    public void addSharedPreferences(){
        String distance = spinner.getSelectedItem().toString();
        edit.putString("distance", distance);
        edit.commit();
        dismiss();
    }
}
