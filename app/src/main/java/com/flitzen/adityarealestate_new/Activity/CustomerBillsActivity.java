package com.flitzen.adityarealestate_new.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.CustomerBillListAdapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.CustomerBill;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBillsActivity extends AppCompatActivity {
    String property_name, customer_name, hired_since, property_id;
    TextView txt_property_name, txt_customer_name;
    FloatingActionButton fab_add_bill;
    ProgressDialog prd;
    GridView gvBills;
    List<CustomerBill> customerBillList = new ArrayList<>();
    public static LinearLayout layoutNoResult;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bills);
        initUI();
    }

    private void initUI() {
        getSupportActionBar().setTitle("Bills");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        property_name = getIntent().getStringExtra("property_name");
        customer_name = getIntent().getStringExtra("customer_name");
        hired_since = getIntent().getStringExtra("hired_since");
        property_id = getIntent().getStringExtra("property_id");
        txt_property_name = (TextView) findViewById(R.id.txt_property_name);
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        fab_add_bill = (FloatingActionButton) findViewById(R.id.fab_add_bill);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        layoutNoResult = findViewById(R.id.layoutNoResult);
        gvBills = (GridView) findViewById(R.id.gvBills);
        fab_add_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerBillsActivity.this, AddCustomerBillActivity.class)
                        .putExtra("property_id", property_id));
            }
        });

        txt_property_name.setText(property_name);
        txt_customer_name.setText(customer_name);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetBillAPI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetBillAPI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    private void GetBillAPI() {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Light_bill").orderByKey();
        databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                hideSwipeRefresh();
                customerBillList.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if(npsnapshot.child("Properties_id").getValue().toString().equals(property_id)){
                                CustomerBill customerBill = new CustomerBill();
                                customerBill.setBill_id(npsnapshot.child("Bill_id").getValue().toString());
                                customerBill.setProperties_id(npsnapshot.child("Properties_id").getValue().toString());
                                customerBill.setBill_month(npsnapshot.child("Bill_month").getValue().toString());
                                // customerBill.setBill_Rs(obj.getString("Bill_Rs"));
                                //customerBill.setOther_Notes(obj.getString("Other_Notes"));
                                customerBill.setBill_photo(npsnapshot.child("Bill_photo").getValue().toString());

                                try {
                                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date d = f.parse(npsnapshot.child("created_date").getValue().toString());
                                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                    customerBill.setCreate_date(date.format(d));
                                    customerBill.setCreate_time(time.format(d));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                customerBillList.add(customerBill);
                            }
                        }

                        gvBills.setAdapter(new CustomerBillListAdapter(CustomerBillsActivity.this, customerBillList));
                        if(customerBillList.size()>0){
                            gvBills.setVisibility(View.VISIBLE);
                            layoutNoResult.setVisibility(View.GONE);
                        }
                        else {
                            gvBills.setVisibility(View.GONE);
                            layoutNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        gvBills.setVisibility(View.GONE);
                        layoutNoResult.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(CustomerBillsActivity.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                hidePrd();

            }
        });
    }

    private void hideSwipeRefresh() {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
    }


    public void showPrd() {
        prd = new ProgressDialog(CustomerBillsActivity.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}
