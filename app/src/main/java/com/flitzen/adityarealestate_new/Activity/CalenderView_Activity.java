package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Transaction_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_calenderPopupList;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalenderView_Activity extends AppCompatActivity {


    Adapter_calenderPopupList adapterCalenderPopupList;
    Adapter_Transaction_List adapterTransactionList;

    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();


    Activity mActivity;
    WebAPI webapi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_view_);

        webapi = Utils.getRetrofitClient().create(WebAPI.class);


        getSupportActionBar().setTitle(Html.fromHtml("Calendar View"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = CalenderView_Activity.this;



        initUi();

    }

    private void initUi() {




        final MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        Utils.showLog("getdate"+transactionlist.size());


        for (int i = 0; i < transactionlist.size(); i++) {



            String date = transactionlist.get(i).getTransactionDate();

            if(!TextUtils.isEmpty(date) && date.contains("-")){
                String[] datearry = date.split("-");

                int month = Integer.valueOf(datearry[1]);
                int year = Integer.valueOf(datearry[2]);
                int date1 = Integer.valueOf(datearry[0]);
                //  Utils.showLog("=== date1 "+date1+"month "+month+" year "+year);
                //  String mm = Helper.getMonth(month);
                calendarView.setDateSelected(CalendarDay.from(year, month, date1), true);
                // calendarView.setDateSelected(CalendarDay.from(2019,8,23),true);

            }else {
                System.out.println("nulldata -> "+transactionlist.get(i).getTransactionId());
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

                        String tempdate = date.getDay() + "-0" + date.getMonth() + "-" + date.getYear();
                        //  Utils.showLog("date" + tempdate);
                        //  Utils.showLog("newdatelist" + tempdate+" "+transactionlist.get(i));
                        if (tempdate.equals(transactionlist.get(i).getTransactionDate())) {

                            Newtransactionlist.add(transactionlist.get(i));


                        }

                    }
                    openTransDateListDailog(Newtransactionlist);

                } else {

                    calendarView.setDateSelected(date, false);
                }


            }
        });

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
        adapterCalenderPopupList = new Adapter_calenderPopupList(mActivity, Newtransactionlist, false);
        rvTransPaymentlist.setAdapter(adapterCalenderPopupList);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        TransPopUpDate.setText(Newtransactionlist.get(0).getTransactionDate());

        btn_add_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, Add_fab_Transaction_Activity.class));
                overridePendingTransition(0, 0);
               // mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

            }
        });


        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
