package com.gr3mlin106.to_do_at_home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                String title = "";
                switch (position){
                    case 0:
                        title = getResources().getString(R.string.title_summary_fragment_one);
                        break;
                    case 1:
                        title = getResources().getString(R.string.title_summary_fragment_two);
                        break;
                    case 2:
                        title = getResources().getString(R.string.title_summary_fragment_three);
                        break;
                }
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static class PlaceholderFragment extends Fragment {

        private int sectionNumber = 0;

        public PlaceholderFragment() {

        }

        @SuppressLint("ValidFragment")
        public PlaceholderFragment(int sectionNumber){
            this.sectionNumber = sectionNumber;
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment(sectionNumber);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            switch (this.sectionNumber){
                case 1:
                    return loadFragmentOne(inflater.inflate(R.layout.fragment_summary, container, false));
                case 2:
                    return loadFragmentTwo(inflater.inflate(R.layout.fragment_summary, container, false));
                case 3:
                    return loadFragmentThree(inflater.inflate(R.layout.fragment_summary, container, false));

                default:
                    return inflater.inflate(R.layout.fragment_summary, container, false);
            }
        }

        private View loadFragmentOne(final View v){

            ListView listView = (ListView) v.findViewById(R.id.summary_fragment_taskList_listView);
            final TaskAdapterSummary taskAdapter = new TaskAdapterSummary(this.getContext());

            listView.setAdapter(taskAdapter);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
            query.whereGreaterThan("endDate", new Date());
            query.whereEqualTo("done", true);
            query.orderByAscending("endDate");
            query.whereEqualTo("home", ParseUser.getCurrentUser().getParseObject("home"));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {

                        if(objects.size()>0)v.findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
                        else v.findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);

                        for (ParseObject task : objects) {

                            TaskRecord tr = new TaskRecord();
                            tr.title = task.getString("title");
                            tr.id = task.getObjectId();
                            tr.done = task.getBoolean("done");
                            tr.startDate = task.getDate("startDate");
                            tr.endDate = task.getDate("endDate");
                            tr.parseObject = task;


                            taskAdapter.add(tr);


                        }
                        taskAdapter.notifyDataSetChanged();

                    }
                }
            });

            return v;
        }

        private View loadFragmentTwo(final View v){

            ListView listView = (ListView) v.findViewById(R.id.summary_fragment_taskList_listView);
            final TaskAdapterSummary taskAdapter = new TaskAdapterSummary(this.getContext());

            listView.setAdapter(taskAdapter);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
            query.whereGreaterThan("endDate", new Date());
            query.whereEqualTo("done", false);
            query.orderByAscending("endDate");
            query.whereEqualTo("home", ParseUser.getCurrentUser().getParseObject("home"));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {

                        if(objects.size()>0)v.findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
                        else v.findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);

                        for (ParseObject task : objects) {

                            TaskRecord tr = new TaskRecord();
                            tr.title = task.getString("title");
                            tr.id = task.getObjectId();
                            tr.done = task.getBoolean("done");
                            tr.startDate = task.getDate("startDate");
                            tr.endDate = task.getDate("endDate");
                            tr.parseObject = task;



                            int days = (int) (((tr.endDate.getTime() - (new Date()).getTime()) / 1000) / 3600)/24;

                            switch (days) {
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

                    }
                }
            });

            return v;
        }

        private View loadFragmentThree(final View v){

            ListView listView = (ListView) v.findViewById(R.id.summary_fragment_taskList_listView);
            final TaskAdapterSummary taskAdapter = new TaskAdapterSummary(this.getContext());

            listView.setAdapter(taskAdapter);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
            query.whereLessThan("endDate", new Date());
            query.orderByDescending("endDate");
            query.whereEqualTo("home", ParseUser.getCurrentUser().getParseObject("home"));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {

                        if(objects.size()>0)v.findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
                        else v.findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);

                        for (ParseObject task : objects) {

                            TaskRecord tr = new TaskRecord();
                            tr.title = task.getString("title");
                            tr.id = task.getObjectId();
                            tr.done = task.getBoolean("done");
                            tr.startDate = task.getDate("startDate");
                            tr.endDate = task.getDate("endDate");
                            tr.parseObject = task;
                            tr.history = true;


                            int days = (int) (((tr.endDate.getTime() - (new Date()).getTime()) / 1000) / 3600)/24;

                            switch (days){
                                case 0:
                                    taskAdapter.addHeader(getResources().getString(tr.history ? R.string.time_yesterday : R.string.time_today));
                                    break;
                                case 1:
                                    taskAdapter.addHeader(getResources().getString(R.string.time_tomorrow));
                                    break;
                                default:
                                    if(tr.history)taskAdapter.addHeader(Math.abs(days) + " " + getResources().getString(R.string.time_days_ago));
                                    else taskAdapter.addHeader(getResources().getString(R.string.time_in) + " " + days + " " + getResources().getString(R.string.time_days));
                                    break;
                            }


                            taskAdapter.add(tr);


                        }
                        taskAdapter.notifyDataSetChanged();

                    }
                }
            });

            return v;
        }

    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}

class TaskAdapterSummary extends BaseAdapter {

    Context context;
    ArrayList<TaskRecord> tasks = new ArrayList<>();
    ArrayList <String> heders = new ArrayList<>();


    private static LayoutInflater inflater = null;

    public TaskAdapterSummary(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;

        if(tasks.get(position) != null){

            vi = inflater.inflate(R.layout.task_list_item_summary_done, null);

            final TaskRecord tr = tasks.get(position);

            ((TextView) vi.findViewById(R.id.taskListItemSummary_title)).setText(tr.title);

            final View finalVi = vi;
            tr.parseObject.getParseObject("user").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {

                    ((TextView) finalVi.findViewById(R.id.taskListItemSummary_description)).setText(object.getString("name"));
                }
            });

            final View view = vi;


            final ImageButton likeButton = (ImageButton) vi.findViewById(R.id.taskListItemSummary_like_button);
            if(tr.parseObject.getParseObject("user").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) || !tr.done || tr.history)likeButton.setVisibility(View.GONE);
            final ImageButton dislikeButton = (ImageButton) vi.findViewById(R.id.taskListItemSummary_dislike_button);
            if(tr.parseObject.getParseObject("user").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) || !tr.done || tr.history)dislikeButton.setVisibility(View.GONE);

            final ArrayList <String> likes = new ArrayList<String>();
            List <String> lLikes = tr.parseObject.getList("likes");
            if(lLikes != null)likes.addAll(lLikes);

            final ArrayList <String> dislikes = new ArrayList<String>();
            List <String> lDislikes = tr.parseObject.getList("dislikes");
            if(lDislikes != null)dislikes.addAll(lDislikes);

            likeButton.setBackgroundColor(vi.getResources().getColor( likes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorGreen : R.color.colorInactive));
            dislikeButton.setBackgroundColor(vi.getResources().getColor( dislikes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorRed : R.color.colorInactive));


            if(tr.history){
                ImageView checkbox = (ImageView) vi.findViewById(R.id.taskListItemSummary_checkbox);
                checkbox.setVisibility(View.VISIBLE);
                if(tr.done && likes.size()>=dislikes.size())checkbox.setImageDrawable(vi.getResources().getDrawable(R.drawable.ic_check_box_done));
                else checkbox.setImageDrawable(vi.getResources().getDrawable(R.drawable.ic_check_box_empty));
            }

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!likes.contains(ParseUser.getCurrentUser().getObjectId())) {
                        likes.add(ParseUser.getCurrentUser().getObjectId());
                    }

                    dislikes.remove(ParseUser.getCurrentUser().getObjectId());

                    tr.parseObject.put("likes", likes);
                    tr.parseObject.put("dislikes", dislikes);

                    tr.parseObject.saveInBackground();

                    likeButton.setBackgroundColor(v.getResources().getColor(likes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorGreen : R.color.colorInactive));
                    dislikeButton.setBackgroundColor(v.getResources().getColor(dislikes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorRed : R.color.colorInactive));


                }
            });

            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!dislikes.contains(ParseUser.getCurrentUser().getObjectId())) {
                        dislikes.add(ParseUser.getCurrentUser().getObjectId());
                    }

                    likes.remove(ParseUser.getCurrentUser().getObjectId());

                    tr.parseObject.put("likes", likes);
                    tr.parseObject.put("dislikes", dislikes);

                    tr.parseObject.saveInBackground();

                    likeButton.setBackgroundColor(v.getResources().getColor(likes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorGreen : R.color.colorInactive));
                    dislikeButton.setBackgroundColor(v.getResources().getColor(dislikes.contains(ParseUser.getCurrentUser().getObjectId()) ? R.color.colorRed : R.color.colorInactive));


                }
            });

        }else{
            vi = inflater.inflate(R.layout.task_list_header, null);

            ((TextView) vi.findViewById(R.id.header_title_textView)).setText(heders.get(position));
        }

        return vi;
    }
}