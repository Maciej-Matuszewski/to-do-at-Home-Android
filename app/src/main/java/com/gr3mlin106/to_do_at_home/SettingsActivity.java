package com.gr3mlin106.to_do_at_home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity {

    ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.settings_change_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName();
            }
        });

        findViewById(R.id.settings_change_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

        findViewById(R.id.settings_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        findViewById(R.id.settings_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void changeName(){
        final View layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.change_popup, (ViewGroup) findViewById(R.id.change_popup_layout));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final PopupWindow pw = new PopupWindow(layout, displayMetrics.widthPixels, 845, true);
        pw.setOutsideTouchable(true);
        pw.showAsDropDown(layout);

        ((TextView)layout.findViewById(R.id.change_popup_title)).setText(getString(R.string.title_popup_change_name));

        final AutoCompleteTextView textViewOne = (AutoCompleteTextView)layout.findViewById(R.id.change_popup_textview_one);
        textViewOne.setHint(R.string.prompt_name);
        textViewOne.setText(user.getString("name"));

        layout.findViewById(R.id.change_popup_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewOne.getText().toString().length() >0){
                    user.put("name",textViewOne.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            pw.dismiss();
                            showMessageInfo(getString(R.string.success_profile_updated));
                        }
                    });
                }else{
                    showMessageInfo(getString(R.string.error_field_required));
                    pw.dismiss();
                }
            }
        });

        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    private void changeEmail(){
        final View layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.change_popup, (ViewGroup) findViewById(R.id.change_popup_layout));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final PopupWindow pw = new PopupWindow(layout, displayMetrics.widthPixels, 845, true);
        pw.setOutsideTouchable(true);
        pw.showAsDropDown(layout);

        ((TextView)layout.findViewById(R.id.change_popup_title)).setText(getString(R.string.title_popup_change_email));

        final AutoCompleteTextView textViewOne = (AutoCompleteTextView)layout.findViewById(R.id.change_popup_textview_one);
        textViewOne.setHint(R.string.prompt_email);
        textViewOne.setText(user.getString("email"));

        layout.findViewById(R.id.change_popup_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewOne.getText().toString().length() > 3 && textViewOne.getText().toString().contains("@") && textViewOne.getText().toString().contains(".")) {
                    user.put("username", textViewOne.getText().toString());
                    user.put("email", textViewOne.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            pw.dismiss();
                            showMessageInfo(getString(R.string.success_profile_updated));
                        }
                    });
                } else if (textViewOne.getText().toString().length() == 0) {
                    showMessageInfo(getString(R.string.error_field_required));
                    pw.dismiss();
                }else{
                    showMessageInfo(getString(R.string.error_invalid_email));
                    pw.dismiss();
                }
            }
        });

        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    private void changePassword(){
        final View layout = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.change_popup, (ViewGroup) findViewById(R.id.change_popup_layout));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final PopupWindow pw = new PopupWindow(layout, displayMetrics.widthPixels, 845, true);
        pw.setOutsideTouchable(true);
        pw.showAsDropDown(layout);

        ((TextView)layout.findViewById(R.id.change_popup_title)).setText(getString(R.string.title_popup_change_password));

        final AutoCompleteTextView textViewOne = (AutoCompleteTextView)layout.findViewById(R.id.change_popup_textview_one);
        final AutoCompleteTextView textViewTwo = (AutoCompleteTextView)layout.findViewById(R.id.change_popup_textview_two);
        final AutoCompleteTextView textViewThree = (AutoCompleteTextView)layout.findViewById(R.id.change_popup_textview_three);

        textViewOne.setVisibility(View.GONE);
        textViewTwo.setVisibility(View.VISIBLE);
        textViewThree.setVisibility(View.VISIBLE);

        textViewTwo.setHint(R.string.prompt_password);
        textViewThree.setHint(R.string.prompt_password_repeat);

        layout.findViewById(R.id.change_popup_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewTwo.getText().toString().length() > 5 && textViewTwo.getText().toString().equals(textViewThree.getText().toString())) {
                    user.put("password", textViewTwo.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            pw.dismiss();
                            showMessageInfo(getString(R.string.success_profile_updated));
                        }
                    });
                } else if (textViewTwo.getText().toString().length() == 0 || textViewThree.getText().toString().length() == 0) {
                    showMessageInfo(getString(R.string.error_all_field_required));
                    pw.dismiss();
                } else if(!textViewTwo.getText().toString().equals(textViewThree.getText().toString())){
                    showMessageInfo(getString(R.string.error_not_equal_passwords));
                    pw.dismiss();
                }else{
                    showMessageInfo(getString(R.string.error_invalid_password));
                    pw.dismiss();
                }
            }
        });

        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    private void logout(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                user.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Intent i = new Intent(getBaseContext(), WelcomeActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
                                    }
                                });

                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage(R.string.prompt_are_you_sure).setPositiveButton(R.string.prompt_yes, dialogClickListener)
                .setNegativeButton(R.string.Prompt_no, dialogClickListener).show();
    }

    public void showMessageInfo(String messageText){
        Snackbar.make(findViewById(R.id.settings_layout), messageText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
