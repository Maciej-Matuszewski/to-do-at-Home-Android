package com.gr3mlin106.to_do_at_home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewHomeCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_home_complete);

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            ((TextView) findViewById(R.id.newHome_homeID_label)).setText(getResources().getString(R.string.prompt_home_id) + ": " + (String) b.get("homeID"));
            ((TextView) findViewById(R.id.newHome_password_label)).setText(getResources().getString(R.string.prompt_password)+": "+(String) b.get("homePassword"));
        }

        ((Button) findViewById(R.id.newHome_done_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
