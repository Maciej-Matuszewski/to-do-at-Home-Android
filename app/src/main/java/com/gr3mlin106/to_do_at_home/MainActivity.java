package com.gr3mlin106.to_do_at_home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listview;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), ParseTest.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        ParseUser currentUser = ParseUser.getCurrentUser();

        if(!currentUser.has("home")){
            Intent i = new Intent(getBaseContext(), AddHomeActivity.class);
            startActivity(i);
        }


        listview = (ListView) findViewById(R.id.main_taskList_listView);

        taskAdapter = new TaskAdapter(this);

        listview.setAdapter(taskAdapter);


        //mainListView = (ListView) findViewById( R.id.main_taskList_listView);

        ///listAdapter = new ArrayAdapter<String>(this, R.layout.task_list_item, new ArrayList<String>());

        //mainListView.setAdapter(listAdapter);

        loadTasks();

    }

    private void loadTasks(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        query.whereLessThan("startDate", new Date());
        query.whereGreaterThan("endDate", new Date());
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {

                    for(ParseObject task : objects){

                        if(!task.getBoolean("done")){
                            TaskRecord tr = new TaskRecord();
                            tr.title = task.getString("title");
                            tr.id = task.getObjectId();
                            tr.done = task.getBoolean("done");
                            tr.startDate = task.getDate("startDate");
                            tr.endDate = task.getDate("endDate");
                            tr.parseObject = task;

                            taskAdapter.add(tr);
                        }


                    }
                    taskAdapter.notifyDataSetChanged();

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    private void showMessageInfo(String messageText){
        Snackbar.make(findViewById(R.id.mainLayout), messageText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class TaskAdapter extends BaseAdapter {

    Context context;
    ArrayList <TaskRecord> tasks = new ArrayList<>();

    private static LayoutInflater inflater = null;

    public TaskAdapter(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(TaskRecord taskRecord){
        this.tasks.add(taskRecord);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.task_list_item, null);

        final TaskRecord tr = tasks.get(position);

        ((TextView) vi.findViewById(R.id.taskListItem_title)).setText(tr.title);

        int hours = (int) (((tr.endDate.getTime() - (new Date()).getTime()) / 1000) / 3600);

        final TextView timeLabel = ((TextView) vi.findViewById(R.id.taskListItem_time));
        timeLabel.setText(vi.getResources().getString(R.string.prompt_time_left) + ": " + hours + "h");


        final View view = vi;

        final ImageButton doneButton = (ImageButton) vi.findViewById(R.id.taskListItem_done_button);
        if(tr.done){
            doneButton.setVisibility(View.INVISIBLE);
            timeLabel.setText(vi.getResources().getString(R.string.prompt_done));
        }
        else{
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tr.done = true;
                    timeLabel.setText(v.getResources().getString(R.string.prompt_done));
                    doneButton.setVisibility(View.INVISIBLE);
                    ParseObject object = tr.parseObject;
                    object.put("done",true);
                    object.saveInBackground();

                }
            });
        }

        return vi;
    }
}

class TaskRecord{
    String title;
    String id;
    Date startDate;
    Date endDate;
    Boolean done;
    ParseObject parseObject;

}