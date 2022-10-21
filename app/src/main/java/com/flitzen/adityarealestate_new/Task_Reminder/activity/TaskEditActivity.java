package com.flitzen.adityarealestate_new.Task_Reminder.activity;

import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.ImageView;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.AlertDialogFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.TaskEditFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditFinished;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.onSaveTask;


public class TaskEditActivity extends AppCompatActivity implements OnEditFinished, onSaveTask {
    public static final String EXTRA_TASKID = "taskId";
    TaskEditFragment fragment;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar2));

         ivBack = (ImageView) findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        long id =  getIntent().getLongExtra(TaskEditActivity.EXTRA_TASKID,0L);
        fragment = TaskEditFragment.newInstance(id);

        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;

        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.container,fragment,fragmentTag).commit();
        }
    }

    @Override
    public void finishEditingTask() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new AlertDialogFragment();
        newFragment.show(fragmentTransaction,"saveDialog");

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void doSave() {
        fragment.save();
        finish();
    }
}
