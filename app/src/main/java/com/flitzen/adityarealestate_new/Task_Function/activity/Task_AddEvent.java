package com.flitzen.adityarealestate_new.Task_Function.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Function.model.EventModelDB;
import com.flitzen.adityarealestate_new.Task_Function.util.TimeAlarm;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;

public class Task_AddEvent extends AppCompatActivity {


    private EditText edtEvent, edtDate, edtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button btnDone;
    private AlarmManager am;
    private String eventEntered;
    private String timeEntered;
    private String dateEntered;
    private String formattedTime;
    private Long tsCurrent, tsSet;
    private Calendar c1, c2;
    private String ts;
    private String toParse;
    private SimpleDateFormat formatter;
    private Date date;
    private RelativeLayout rlSpeak;
    private String timeToNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        edtEvent = (EditText)findViewById(R.id.event);
        edtTime = (EditText)findViewById(R.id.time);
        edtDate = (EditText)findViewById(R.id.date);
        btnDone = (Button)findViewById(R.id.button);
        rlSpeak = (RelativeLayout)findViewById(R.id.speak);


        rlSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, 1);

                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });


        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            c1 = Calendar.getInstance();
            mHour = c1.get(Calendar.HOUR_OF_DAY);
            mMinute = c1.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(Task_AddEvent.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            timeToNotify = hourOfDay+":"+minute;
                            formattedTime = FormatTime(hourOfDay, minute);
                            edtTime.setText(formattedTime);
                        }
                    }, mHour, mMinute, false);

                timePickerDialog.show();
        }

      });

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c2 = Calendar.getInstance();
                mYear = c2.get(Calendar.YEAR);
                mMonth = c2.get(Calendar.MONTH);
                mDay = c2.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(Task_AddEvent.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                edtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Save Entered data to Realm dataabse
                eventEntered= edtEvent.getText().toString();
                timeEntered= edtTime.getText().toString();
                dateEntered= edtDate.getText().toString();

                if(eventEntered.length() > 0 && timeEntered.length() > 0 && dateEntered.length() > 0){
                    Realm myRealm = Realm.getInstance(getBaseContext());
                    myRealm.beginTransaction();

                    // Create an object
                    EventModelDB eventDetails = myRealm.createObject(EventModelDB.class);

                    // Set its fields
                    eventDetails.setEvent(eventEntered);
                    eventDetails.setTime(timeEntered);
                    eventDetails.setDate(dateEntered);

                    tsCurrent = System.currentTimeMillis();
                    ts = tsCurrent.toString();
                    eventDetails.setTimestamp(ts);

                    myRealm.commitTransaction();

                    // set the alarm
                    am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    setOneTimeAlarm();

                    Toast.makeText(getBaseContext(), "Reminder Set for new Event", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Task_AddEvent.this, Task_MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }


                else{
                    Toast.makeText(getBaseContext(), "Fill Up The Input fields", Toast.LENGTH_LONG).show();
                }


            }
        });

    }


    public void setOneTimeAlarm() {
        Intent intent = new Intent(this, TimeAlarm.class);

        intent.putExtra("event", eventEntered);
        intent.putExtra("time", timeEntered);
        intent.putExtra("date", dateEntered);
        Log.v("TAG","=== event "+eventEntered+" timeEntered "+timeEntered+" dateEntered "+dateEntered);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        try {
            // Convert the set date and time to timestamp
            toParse = dateEntered + " " + timeToNotify;
            formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
            date = formatter.parse(toParse);
            tsSet = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        am.set(AlarmManager.RTC_WAKEUP, tsSet, pendingIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    edtEvent.setText(text.get(0));
                }
                break;
            }
        }
    }



    public String FormatTime(int hour, int minute){

        String time;
        time = "";
        String formattedMinute;

        if(minute/10 == 0){
            formattedMinute = "0"+minute;
        }
        else{
            formattedMinute = ""+minute;
        }


        if(hour == 0){
            time = "12" + ":" + formattedMinute + " AM";
        }
        else if(hour < 12){
            time = hour + ":" + formattedMinute + " AM";
        }
        else if(hour == 12){
            time = "12" + ":" + formattedMinute + " PM";
        }
        else{
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }



        return time;
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
