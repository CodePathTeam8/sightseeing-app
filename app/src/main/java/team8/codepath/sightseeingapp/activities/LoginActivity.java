package team8.codepath.sightseeingapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import team8.codepath.sightseeingapp.R;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, TripListActivity.class);
        startActivity(i);
    }


    public void login(View view){
        //
        // Login Logic Here
        //

        // On Success
        onLoginSuccess();
    }

}
