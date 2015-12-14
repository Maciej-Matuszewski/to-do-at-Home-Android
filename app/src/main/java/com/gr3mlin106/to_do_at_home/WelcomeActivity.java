package com.gr3mlin106.to_do_at_home;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTheme(R.style.AppTheme_NoActionBar);

        setContentView(R.layout.activity_welcome);


        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        }

        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }
}
