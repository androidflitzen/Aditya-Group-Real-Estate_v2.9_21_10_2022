package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Complete_Task_Details extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    String type = "";

    TextView txt_task_subject, txt_task_desc, txt_task_date, txt_task_time;
    View view_remove_task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_task_details);

        getSupportActionBar().setTitle(Html.fromHtml("Complete Task Details"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Complete_Task_Details.this;
        txt_task_subject = (TextView) findViewById(R.id.txt_task_subject);
        txt_task_desc = (TextView) findViewById(R.id.txt_task_desc);
        txt_task_date = (TextView) findViewById(R.id.txt_task_date);
        txt_task_time = (TextView) findViewById(R.id.txt_task_time);

        view_remove_task = findViewById(R.id.view_remove_task);

        view_remove_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    public void showPrd() {
        prd = new ProgressDialog(mActivity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}
