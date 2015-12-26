package com.gr3mlin106.to_do_at_home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HomeManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ParseObject home = ParseUser.getCurrentUser().getParseObject("home");
        home.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ((TextView) findViewById(R.id.homeManager_homeID_label)).setText(getResources().getString(R.string.prompt_home_id) + ": " + home.getObjectId());
                ((TextView) findViewById(R.id.homeManager_password_label)).setText(getResources().getString(R.string.prompt_password) + ": " + home.getString("password"));
            }
        });

        findViewById(R.id.homeManager_manageTask_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeManagerActivity.this, TasksManagerActivity.class));
            }
        });

        findViewById(R.id.homeManager_logoutFromHome_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ParseUser user = ParseUser.getCurrentUser();
                                user.remove("home");
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeManagerActivity.this);
                builder.setMessage(R.string.prompt_are_you_sure).setPositiveButton(R.string.prompt_yes, dialogClickListener)
                        .setNegativeButton(R.string.Prompt_no, dialogClickListener).show();
            }
        });

        /*
        findViewById(R.id.homeManager_removeHome_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:



                                ParseUser user = ParseUser.getCurrentUser();
                                user.getParseObject("home").deleteInBackground();
                                user.remove("home");
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeManagerActivity.this);
                builder.setMessage(R.string.prompt_are_you_sure).setPositiveButton(R.string.prompt_yes, dialogClickListener)
                        .setNegativeButton(R.string.Prompt_no, dialogClickListener).show();
            }
        });
        //*/

    }
}
