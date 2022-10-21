package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Calender_TransList;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Calender_TransList_Plot;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Fragment.Plot_Active_Fragment;
import com.flitzen.adityarealestate_new.Fragment.Plot_Deactive_Fragment;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;

import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flitzen.adityarealestate_new.R.layout.calender_spinner_year;

public class Activity_Plot_ActiveList extends AppCompatActivity {

    @BindView(R.id.ivback)
    ImageView ivback;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.liTabmain)
    LinearLayout liTabmain;
    @BindView(R.id.view_search)
    CardView viewSearch;
    @BindView(R.id.SiteFragment)
    FrameLayout SiteFragment;

    ProgressDialog prd;
    Activity mActivity;
    @BindView(R.id.tvPlottabActive)
    TextView tvPlottabActive;
    @BindView(R.id.tvPlottabDeactive)
    TextView tvPlottabDeactive;
    @BindView(R.id.ivAddReminder)
    ImageView ivAddReminder;

    Plot_Active_Fragment plotActiveFragment;

    ImageView TransCalender;
    public String CalenderMonth;
    int FinalMonth = 1, finalYear;
    Adapter_Calender_TransList_Plot adapterCalenderTransList;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot__active_);
        ButterKnife.bind(this);

        mActivity = Activity_Plot_ActiveList.this;

        plotActiveFragment = new Plot_Active_Fragment();

        pushFragment(new Plot_Active_Fragment());
        
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvPlottabActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPlottabActive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                tvPlottabDeactive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                pushFragment(new Plot_Active_Fragment());
            }
        });


        tvPlottabDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvPlottabActive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvPlottabDeactive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                pushFragment(new Plot_Deactive_Fragment());
            }
        });


        ivAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, TaskListActivity.class);
                startActivity(intent);
            }
        });


        TransCalender = (ImageView) findViewById(R.id.TransCalender);
        TransCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransCalanderDailog();
            }
        });

      //  getAllTransactionlist();
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


    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                   // .setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0, 0)
                    .replace(R.id.SiteFragment, fragment)
                    //.addToBackStack("fragment")
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAllTransactionlist();
        // pushFragment(new Plot_Active_Fragment());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        Intent intent = new Intent(Activity_Plot_ActiveList.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }


    private void TransCalanderDailog() {


        LayoutInflater localView = LayoutInflater.from(mActivity);
        final View promptsView = localView.inflate(R.layout.dailog_transcalender, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final MaterialCalendarView calendarView = (MaterialCalendarView) promptsView.findViewById(R.id.calendarView);
        final ImageView ivAddReminder = (ImageView) promptsView.findViewById(R.id.ivAddReminder);


        final RelativeLayout rvCalenderMonth = (RelativeLayout) promptsView.findViewById(R.id.rvCalenderMonth);
        final RelativeLayout rvCalenderYear = (RelativeLayout) promptsView.findViewById(R.id.rvCalenderYear);
        final TextView tvCalenderYear = (TextView) promptsView.findViewById(R.id.tvCalenderYear);
        final TextView tvCalenderMonth = (TextView) promptsView.findViewById(R.id.tvCalenderMonth);

     /*   tvCalenderYear.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        String monthName=new DateFormatSymbols().getMonths()[Calendar.getInstance().get(Calendar.MONTH)];
        tvCalenderMonth.setText(monthName);*/



        final Spinner spinner1 = (Spinner) promptsView.findViewById(R.id.spinner1);
        ivAddReminder.setVisibility(View.GONE);

        ivAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mActivity, TaskListActivity.class);
                startActivity(intent);
                alertDialog.dismiss();

            }
        });


        int curentmonth = calendarView.getCurrentDate().getMonth();
        int currentyear = calendarView.getCurrentDate().getYear();
        int currentdate = calendarView.getCurrentDate().getDay();

        //calendarView.setCurrentDate(CalendarDay.from(currentyear, curentmonth, currentdate));
        //calendarView.setDateSelected(CalendarDay.from(currentyear, curentmonth, currentdate), true);
        //calendarView.setCurrentDate(CalendarDay.from(finalYear, FinalMonth + 1, 1));


        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        // String formattedDate = df.format(currentDate);
        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        String[] finalcurrentdate = formattedDate.split("/");

        //  Utils.showLog("==FinalCuurrent"+formattedDate);
/*
        int currentyear = Integer.valueOf(finalcurrentdate[2]);
        int currentmonth = Integer.valueOf(finalcurrentdate[1]);
        int currentdate = Integer.valueOf(finalcurrentdate[0]);*/

        // Utils.showLog("==currentmonth"+finalcurrentdate+"  "+currentmonth+"  "+currentyear);


        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> Finallist = new ArrayList<>();

        for (int i = 0; i < transactionlist.size(); i++) {

            String date = transactionlist.get(i).getTransactionDate();

            if(!TextUtils.isEmpty(date) && date.contains("-")) {
                String[] datearry = date.split("-");

                int year = Integer.valueOf(datearry[2]);

                list.add(String.valueOf(year));
            }else{

                System.out.println("nulldata -> "+transactionlist.get(i).getTransactionId());
            }

        }

        Utils.showLog("-yearlist" + list);

        // Finallist = new ArrayList<String>();
        for (String dupWord : list) {
            if (!Finallist.contains(dupWord)) {
                Finallist.addAll(Collections.singleton(dupWord));


            }
        }


        final ArrayList<String> finalFinallist = Finallist;
        // finalFinallist.addAll(list);

        //  Utils.showLog("==yearlist"+Finallist);



        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                finalYear = Integer.parseInt(finalFinallist.get(position));
                int curentmonth = calendarView.getCurrentDate().getMonth();
                calendarView.setCurrentDate(CalendarDay.from(finalYear, curentmonth, 1));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

      /*  String[] Subject = new String[]{
                "Maths",
                "Hindi",
                "English",
                "Computer"
        };*/


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.calender_spinner_year, Finallist);

        spinnerArrayAdapter.setDropDownViewResource(calender_spinner_year);

        spinner1.setAdapter(spinnerArrayAdapter);

        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(String.valueOf(currentyear)));

        rvCalenderMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Plot_ActiveList.this);
                builder.setTitle("Select Month");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        tvCalenderMonth.setText(items[item]);
                        tvCalenderMonth.setTextColor(getResources().getColor(R.color.text_color1));
                        CalenderMonth = tvCalenderMonth.getText().toString();
                        FinalMonth = item + 1;
                        calendarView.setCurrentDate(CalendarDay.from(finalYear, FinalMonth, 1));

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        //  calendarView.setCurrentDate(CalendarDay.from(finalYear,FinalMonth,1));


        //Utils.showLog("===list"+transactionlist.size());


        for (int i = 0; i < transactionlist.size(); i++) {

            String date = transactionlist.get(i).getTransactionDate();


            if(!TextUtils.isEmpty(date) && date.contains("-")) {
                String[] datearry = date.split("-");

                Utils.showLog("getdate" + date);

                int date1 = Integer.parseInt(datearry[0]);
                int month = Integer.parseInt(datearry[1]);
                int year = Integer.parseInt(datearry[2]);

                // Utils.showLog("=== date1 " + date1 + "month " + month + " year " + year);
                //  String mm = Helper.getMonth(month);


                calendarView.setDateSelected(CalendarDay.from(year, month, date1), true);

                //calendarView.setDateSelected(CalendarDay.from(2019,8,23),true);
            }

        }


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                //  Utils.showLog("hello"+selected);

                if (!selected) {
                    calendarView.setDateSelected(date, true);


                    ArrayList<Transcation> Newtransactionlist = new ArrayList<>();


                    // Utils.showLog("newdatelist" + transactionlist.size());

                    for (int i = 0; i < transactionlist.size(); i++) {

                        //  Utils.showLog("==tempdate"+transactionlist.size());


                        String tempdate = getFormatedMonth(date.getDay()) + "-" + getFormatedMonth(date.getMonth()) + "-" + getFormatedMonth(date.getYear());


                        if (tempdate.equals(transactionlist.get(i).getTransactionDate())) {


                            Newtransactionlist.add(transactionlist.get(i));

                            //  Utils.showLog("==getdate"+Newtransactionlist);


                        }

                    }
                    openTransDateListDailog(Newtransactionlist);

                } else {

                    calendarView.setDateSelected(date, false);
                }


            }
        });


       /* tvCalenderMonth.setText("September");
        tvCalenderMonth.setTextColor(getResources().getColor(R.color.text_color1));
        CalenderMonth = tvCalenderMonth.getText().toString();
        FinalMonth = 8 + 1;
        calendarView.setCurrentDate(CalendarDay.from(2020, FinalMonth, 1));*/

        Calendar cal = Calendar.getInstance();
        tvCalenderMonth.setText((String)android.text.format.DateFormat.format("MMMM", new Date()));
        tvCalenderMonth.setTextColor(getResources().getColor(R.color.text_color1));
        CalenderMonth = tvCalenderMonth.getText().toString();
        FinalMonth = cal.get(Calendar.MONTH) + 1;
        calendarView.setCurrentDate(CalendarDay.from(currentyear, FinalMonth, 1));

        //calendarView.setCurrentDate(CalendarDay.from(currentyear,currentmonth,currentdate));

        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }


    private void openTransDateListDailog(ArrayList<Transcation> Newtransactionlist) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_calender_popup, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();


        final RecyclerView rvTransPaymentlist = (RecyclerView) promptsView.findViewById(R.id.rvTransPopUpList);
        final TextView btn_add_Trans = (TextView) promptsView.findViewById(R.id.btn_add_Trans);
        final TextView TransPopUpDate = (TextView) promptsView.findViewById(R.id.TransPopUpDate);
        final ImageView ivBack = (ImageView) promptsView.findViewById(R.id.ivBack);
        rvTransPaymentlist.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransPaymentlist.setHasFixedSize(true);
        adapterCalenderTransList = new Adapter_Calender_TransList_Plot(mActivity, Newtransactionlist, false,alertDialog);
        rvTransPaymentlist.setAdapter(adapterCalenderTransList);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Utils.showLog("==size" + Newtransactionlist.size());

        TransPopUpDate.setText(Newtransactionlist.get(0).getTransactionDate());

      /*  btn_add_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, Add_fab_Transaction_Activity.class));
                mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

            }
        });*/


        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }

    private String getFormatedMonth(int month) {
        String tempMonth = String.valueOf(month);
        if (tempMonth.length() == 1) {
            tempMonth = "0" + tempMonth;
            return tempMonth;
        }
        return String.valueOf(month);
    }


    private void getAllTransactionlist() {

        transactionlist.clear();
        transactionlistTemp.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryPayment = databaseReference.child("Payments").orderByKey();
        // databaseReference.keepSynced(true);
        queryPayment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                try {
                    if (dataSnapshot1.exists()) {
                        int payeeAmount = 0;
                        for (DataSnapshot npsnapshot2 : dataSnapshot1.getChildren()) {
                            if (!(npsnapshot2.child("plot_id").getValue().toString().equals("0"))) {
                                if(npsnapshot2.child("payment_status").getValue().toString().equals("0")){
                                    if(npsnapshot2.child("customer_status").getValue().toString().equals("0")){
                                        Transcation item1 = new Transcation();
                                        item1.setTransactionId(npsnapshot2.child("id").getValue().toString());
                                        item1.setKey(npsnapshot2.getKey());
                                        item1.setCustomerId(npsnapshot2.child("customer_id").getValue().toString());

                                        item1.setPaymentType(npsnapshot2.child("payment_status").getValue().toString());

                                        try {
                                            System.out.println("========npsnapshot2.child(\"id\").getValue().toString()   "+npsnapshot2.child("id").getValue().toString());
                                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date d = f.parse(npsnapshot2.child("payment_date").getValue().toString());
                                            DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                                            //DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                            System.out.println("=====Date: " + date.format(d));
                                            System.out.println("======Time: " + time.format(d));
                                            item1.setTransactionDate(date.format(d));
                                            item1.setTransactionTime(time.format(d));
                                        } catch (ParseException e) {
                                            System.out.println("========ParseException  "+npsnapshot2.child("id").getValue().toString());
                                            System.out.println("========ParseException  "+npsnapshot2.getKey().toString());
                                            e.printStackTrace();
                                        }

                                        item1.setTransactionNote(npsnapshot2.child("remarks").getValue().toString());
                                        item1.setAmount(npsnapshot2.child("amount").getValue().toString());

                                        Query queryCustomer = databaseReference.child("Customers").orderByKey();
                                        queryCustomer.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                            if (npsnapshot1.child("id").getValue().toString().equals(npsnapshot2.child("customer_id").getValue().toString())) {
                                                                String name = npsnapshot1.child("name").getValue().toString();
                                                                item1.setCustomerName(name);
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {

                                                    Log.e("Ex   ", e.toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("error ", error.getMessage());
                                            }
                                        });

                                        transactionlist.add(item1);
                                        transactionlistTemp.add(item1);
                                    }
                                }
                            }
                        }
                        adapterCalenderTransList.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("Ex Payment  ", e.toString());
                }
            }
              @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(mActivity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });
    }

}
