package com.gr3mlin106.to_do_at_home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(this.getClass().getName(), "Start");

        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_welcome);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            try {
                currentUser.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            overridePendingTransition(0,0);
        }

        Button loginButton = (Button) findViewById(R.id.welcome_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });

        Button registerButton = (Button) findViewById(R.id.welcome_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
            }
        });

    }
}
