package com.gr3mlin106.to_do_at_home;

import android.annotation.SuppressLint;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Double currentVersion = 1.0;
    public static Boolean showAd = true;
    private ListView listview;
    private TaskAdapter taskAdapter;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {

            Intent i = new Intent(getBaseContext(), WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            overridePendingTransition(0,0);
        }else try {
            currentUser.fetchIfNeeded();
            if(!currentUser.has("home")){
                Intent i = new Intent(getBaseContext(), AddHomeActivity.class);
                startActivity(i);
            }
        } catch (ParseException e) {
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        ((TextView)headerView.findViewById(R.id.menu_nav_name)).setText(ParseUser.getCurrentUser().getString("name"));
        ((TextView)headerView.findViewById(R.id.menu_nav_email)).setText(ParseUser.getCurrentUser().getString("email"));

        headerView.setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.main_date_textView)).setText((new SimpleDateFormat("dd MMMM yyyy")).format(new Date()));

        listview = (ListView) findViewById(R.id.main_taskList_listView);

        taskAdapter = new TaskAdapter(this);

        listview.setAdapter(taskAdapter);

        loadTasks();

        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if (e == null) {
                    Double version = config.getDouble("AndroidCurrentVersion");
                    if (version > currentVersion) {
                        Intent i = new Intent(getBaseContext(), UpdateActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        mAdView.loadAd(adRequest);

        if(showAd){
            final InterstitialAd mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-9486440383744688/8428480649");

            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                    showAd = false;
                }
            });


        }


    }

    private void reloadTasks(){
        while (!taskAdapter.tasks.isEmpty()){
            taskAdapter.tasks.remove(0);
        }
        while (!taskAdapter.heders.isEmpty()){
            taskAdapter.heders.remove(0);
        }
        taskAdapter.notifyDataSetChanged();
        loadTasks();
    }

    private void loadTasks(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
        //query.whereLessThan("startDate", new Date());
        query.whereGreaterThan("endDate", new Date());
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByAscending("endDate");
        query.whereEqualTo("home", ParseUser.getCurrentUser().getParseObject("home"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {


                    if(objects.size()>0)findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
                    else findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);

                    for (ParseObject task : objects) {

                        TaskRecord tr = new TaskRecord();
                        tr.title = task.getString("title");
                        tr.id = task.getObjectId();
                        tr.done = task.getBoolean("done");
                        tr.startDate = task.getDate("startDate");
                        tr.endDate = task.getDate("endDate");
                        tr.parseObject = task;

                        int days = (int) (((tr.endDate.getTime() - (new Date()).getTime()) / 1000) / 3600)/24;

                        switch (days){
                            case 0:
                                taskAdapter.addHeader(getResources().getString(R.string.time_today));
                                break;
                            case 1:
                                taskAdapter.addHeader(getResources().getString(R.string.time_tomorrow));
                                break;
                            default:
                                taskAdapter.addHeader(getResources().getString(R.string.time_in) + " " + days + " " + getResources().getString(R.string.time_days));
                                break;
                        }
                        taskAdapter.add(tr);


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
        int id = item.getItemId();

        if (id == R.id.action_reload_tasks) {
            reloadTasks();
            return true;
        }else if(id == R.id.action_wallet){
            showMessageInfo(getString(R.string.prompt_coming_soon));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_summary) {

            startActivity(new Intent(MainActivity.this, SummaryActivity.class));

        } else if (id == R.id.menu_home_manager) {

            startActivity(new Intent(MainActivity.this, HomeManagerActivity.class));

        } else if (id == R.id.menu_settings) {

            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.prompt_check_this_app) + " \n" + getString(R.string.play_store_link));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class TaskAdapter extends BaseAdapter {

    Context context;
    ArrayList <TaskRecord> tasks = new ArrayList<>();
    ArrayList <String> heders = new ArrayList<>();

    private static LayoutInflater inflater = null;

    public TaskAdapter(Context context) {
        this.context = context;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(TaskRecord taskRecord){
        this.tasks.add(taskRecord);
        this.heders.add(null);
    }

    public void addHeader(String title){
        if(!this.heders.contains(title)){
            this.tasks.add(null);
            this.heders.add(title);
        }

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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;

        if(tasks.get(position) != null){

            vi = inflater.inflate(R.layout.task_list_item, null);

            final TaskRecord tr = tasks.get(position);

            ((TextView) vi.findViewById(R.id.taskListItem_title)).setText(tr.title);

            final View view = vi;

            final ImageButton doneButton = (ImageButton) vi.findViewById(R.id.taskListItem_done_button);
            doneButton.setBackgroundColor(vi.getResources().getColor(tr.done ? R.color.colorGreen : R.color.colorInactive));
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!tr.done) {
                        tr.done = true;
                    } else {
                        tr.done = false;
                    }


                    doneButton.setBackgroundColor(v.getResources().getColor(tr.done ? R.color.colorGreen : R.color.colorInactive));
                    ParseObject object = tr.parseObject;
                    object.put("done", tr.done);
                    object.saveInBackground();

                }
            });

        }else{
            vi = inflater.inflate(R.layout.task_list_header, null);

            ((TextView) vi.findViewById(R.id.header_title_textView)).setText(heders.get(position));
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
    Boolean history = false;

}