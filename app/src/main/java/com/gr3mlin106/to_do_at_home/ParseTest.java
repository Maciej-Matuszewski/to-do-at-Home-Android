package com.gr3mlin106.to_do_at_home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParseTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ParseUser user = ParseUser.getCurrentUser();
        final ParseObject home = user.getParseObject("home");

        /*
        final ParseObject taskType = new ParseObject("TaskType");
        taskType.put("title","Clean Kitchen");
        taskType.put("frequency",2);
        taskType.put("home",home);
        taskType.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                home.addUnique("taskTypes", taskType);
            }
        });
        //*/

        //*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TaskType");
        query.whereEqualTo("home", home);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {

                    for(ParseObject taskType : objectList){

                        ParseObject task = new ParseObject("Task");
                        task.put("title",taskType.getString("title"));
                        task.put("user",user);
                        task.put("taskType",taskType);
                        task.put("done",false);
                        task.put("home",home);
                        task.put("startDate", new Date());
                        task.put("endDate", new Date());

                        task.saveInBackground();

                    }

                } else {
                }
            }
        });
        //*/



    }
}
