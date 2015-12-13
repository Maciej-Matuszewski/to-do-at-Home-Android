package com.gr3mlin106.to_do_at_home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.Parse;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

    }
}
