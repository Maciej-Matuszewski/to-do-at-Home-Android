package com.gr3mlin106.to_do_at_home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class AddHomeActivity extends AppCompatActivity {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);
        setupActionBar();

        ((Button) findViewById(R.id.addHome_createNewHome_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseObject home = new ParseObject("Home");
                home.put("password", randomString(8));
                home.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("home", home);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Intent i = new Intent(getBaseContext(), NewHomeCompleteActivity.class);
                                i.putExtra("homeID", home.getObjectId());
                                i.putExtra("homePassword", (String) home.get("password"));
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                });
            }
        });

        ((Button)findViewById(R.id.addHome_connectToHome_button)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Home");
                query.getInBackground(((EditText)findViewById(R.id.addHome_houseID_textfield)).getText().toString(), new GetCallback<ParseObject>() {
                    public void done(final ParseObject object, ParseException e) {
                        if (e == null) {
                            if(object.getString("password").equals(((EditText)findViewById(R.id.addHome_password_textField)).getText().toString())){

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        ParseUser user = ParseUser.getCurrentUser();
                                        user.put("home", object);
                                        user.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                finish();
                                            }
                                        });
                                    }
                                });

                            }else{
                                showMessageInfo("Password is not correct!");
                            }
                        } else {
                            showMessageInfo("Home not exist!");
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void showMessageInfo(String messageText){
        Snackbar.make(findViewById(R.id.addHomeLinearLayout), messageText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}

