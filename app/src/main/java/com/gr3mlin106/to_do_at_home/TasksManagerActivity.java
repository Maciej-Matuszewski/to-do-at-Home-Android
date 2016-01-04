package com.gr3mlin106.to_do_at_home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TasksManagerActivity extends AppCompatActivity {

    TasksManagerAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.tasksManager_ListView);
        taskAdapter = new TasksManagerAdapter(this,this);

        listView.setAdapter(taskAdapter);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TaskType");
        query.whereEqualTo("home", ParseUser.getCurrentUser().getParseObject("home"));
        query.orderByAscending("title");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {

                    if(objects.size()>0)findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
                    else findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);

                    taskAdapter.addList(objects);
                    taskAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tasks_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task_type:

                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.task_add_popup,(ViewGroup) findViewById(R.id.task_add_layout));
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                final PopupWindow pw = new PopupWindow(layout, displayMetrics.widthPixels, 845, true);
                pw.setOutsideTouchable(true);
                pw.showAsDropDown(layout);

                final ParseObject taskType = new ParseObject("TaskType");

                final TextView title = (TextView) layout.findViewById(R.id.addTask_title);
                final TextView frequency = (TextView) layout.findViewById(R.id.addTask_frequency);

                layout.findViewById(R.id.addTask_save_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        if(title.getText().length() > 0 && frequency.getText().length() > 0){
                            int freq = Integer.valueOf(frequency.getText().toString());
                            if(freq>0){
                                final ParseUser user = ParseUser.getCurrentUser();
                                final ParseObject home = user.getParseObject("home");

                                taskType.put("title",title.getText().toString());

                                taskType.put("frequency",freq);
                                taskType.put("home",home);
                                taskType.put("readyUntil",(new Date(0)));
                                taskType.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        showMessageInfo(v.getResources().getString(R.string.success_task_added));
                                        taskAdapter.add(taskType);
                                        pw.dismiss();
                                    }
                                });
                            }else{
                                showMessageInfo(v.getResources().getString(R.string.error_frequency_more_than_zero));

                            }

                        }else{
                            showMessageInfo(v.getResources().getString(R.string.error_all_field_required));
                        }
                    }
                });

                pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        taskAdapter.notifyDataSetChanged();
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showMessageInfo(String messageText){
        Snackbar.make(findViewById(R.id.tasksManager_layout), messageText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}

class TasksManagerAdapter extends BaseAdapter {

    Context context;
    ArrayList<ParseObject> tasks = new ArrayList<>();
    TasksManagerActivity tma = null;

    private static LayoutInflater inflater = null;

    public TasksManagerAdapter(Context context, TasksManagerActivity tasksManagerActivity) {
        this.context = context;
        this.tma = tasksManagerActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        if(tasks.size()>0)tma.findViewById(R.id.nothingToShow_label).setVisibility(View.GONE);
        else tma.findViewById(R.id.nothingToShow_label).setVisibility(View.VISIBLE);
    }

    public void add(ParseObject object){
        tasks.add(object);
    }

    public void addList(List<ParseObject> list){
        this.tasks.addAll(list);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;

        if(tasks.get(position) != null){

            final ParseObject taskType = tasks.get(position);

            vi = inflater.inflate(R.layout.task_list_item_tasks_manager, null);

            TextView title = (TextView) vi.findViewById(R.id.tasksManagerItem_title);
            TextView subtitle = (TextView) vi.findViewById(R.id.tasksManagerItem_subtitle);
            title.setText(taskType.getString("title"));
            subtitle.setText(vi.getResources().getString(R.string.prompt_every) + (taskType.getInt("frequency") == 1 ? vi.getResources().getString(R.string.time_day) : " " + taskType.getInt("frequency") + " " + vi.getResources().getString(R.string.time_days)));

            vi.findViewById(R.id.tasksManagerItem_deleteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.findViewById(R.id.tasksManagerItem_deleteButton).setBackground(v.getResources().getDrawable(R.color.colorRed));
                    taskType.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            tasks.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            final View finalVi = vi;
            vi.findViewById(R.id.tasksManagerItem_editButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ImageButton editButton = (ImageButton) v.findViewById(R.id.tasksManagerItem_editButton);
                    editButton.setBackground(v.getResources().getDrawable(R.color.colorAccent));

                    LayoutInflater inflater = (LayoutInflater) finalVi.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View layout = inflater.inflate(R.layout.task_edit_popup, (ViewGroup) v.findViewById(R.id.task_edit_layout));
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((Activity) finalVi.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    final PopupWindow pw = new PopupWindow(layout, displayMetrics.widthPixels, 845, true);
                    pw.setOutsideTouchable(true);
                    pw.showAsDropDown(layout);

                    final TextView title = (TextView) layout.findViewById(R.id.editTask_title);
                    final TextView frequency = (TextView) layout.findViewById(R.id.editTask_frequency);

                    title.setText(taskType.getString("title"));
                    frequency.setText(taskType.getInt("frequency") + "");
                    layout.findViewById(R.id.editTask_save_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                            if(title.getText().length() > 0 && frequency.getText().length() > 0){
                                int freq = Integer.valueOf(frequency.getText().toString());
                                if(freq>0){
                                    final ParseUser user = ParseUser.getCurrentUser();
                                    final ParseObject home = user.getParseObject("home");

                                    taskType.put("title",title.getText().toString());

                                    taskType.put("frequency",freq);
                                    taskType.saveInBackground(new SaveCallback() {

                                        @Override
                                        public void done(ParseException e) {
                                            tma.showMessageInfo(v.getResources().getString(R.string.success_task_saved));
                                            pw.dismiss();
                                            editButton.setBackground(finalVi.getResources().getDrawable(R.color.colorInactive));
                                        }
                                    });
                                }else{
                                    tma.showMessageInfo(v.getResources().getString(R.string.error_frequency_more_than_zero));

                                }

                            }else{
                                tma.showMessageInfo(v.getResources().getString(R.string.error_all_field_required));
                            }
                        }
                    });

                    pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            notifyDataSetChanged();
                        }
                    });
                }
            });

        }

        return vi;
    }


}