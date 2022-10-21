package com.flitzen.adityarealestate_new.Task_Function.activity;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.R;

/**
 * Created by asu on 20-Aug-16.
 */
public class ShowReminderMessage extends AppCompatActivity {

    private TextView txvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);

        txvMsg = (TextView)findViewById(R.id.reminder_msg);

        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("ReminderMsg");
        txvMsg.setText(msg);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.go_to_home) {
            startActivity(new Intent(getBaseContext(), Task_MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
