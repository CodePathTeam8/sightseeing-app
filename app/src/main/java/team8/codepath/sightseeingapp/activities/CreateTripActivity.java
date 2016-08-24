package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import team8.codepath.sightseeingapp.R;

public class CreateTripActivity extends AppCompatActivity {

    EditText etTripName;
    EditText etPlaceName;
    ImageButton btnAddPlace;
    ListView lvPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    private void setupViews(){
        etTripName = (EditText) findViewById(R.id.etTripName);
        etPlaceName = (EditText) findViewById(R.id.etPlaceName);
        btnAddPlace = (ImageButton) findViewById(R.id.btnAddPlace);
        lvPlaces = (ListView) findViewById(R.id.lvPlaces);
        btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }



}
