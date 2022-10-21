package com.flitzen.adityarealestate_new.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Activity.AddCustomerBillActivity;
import com.flitzen.adityarealestate_new.Adapter.CustomerBillAdapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.CustomerBill;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBillListFragment extends Fragment {
    String property_name, customer_name, hired_since, property_id;
    FloatingActionButton fab_add_bill;
    ProgressDialog prd;
    List<CustomerBill> customerBillList = new ArrayList<>();
    public static LinearLayout layoutNoResult;
    SwipeRefreshLayout swipe_refresh;
    Activity mActivity;

    RecyclerView rvBills;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_bill_list, null);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        mActivity = getActivity();
        property_name = getArguments().getString("property_name");
        customer_name = getArguments().getString("customer_name");
        hired_since = getArguments().getString("hired_since");
        property_id = getArguments().getString("property_id");
        fab_add_bill = (FloatingActionButton) view.findViewById(R.id.fab_add_bill);
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        layoutNoResult = view.findViewById(R.id.layoutNoResult);
        rvBills=view.findViewById(R.id.rvBills);
        rvBills.setLayoutManager(new GridLayoutManager(mActivity, 2));
        fab_add_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, AddCustomerBillActivity.class)
                        .putExtra("property_id", property_id));
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //GetBillAPI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hideSwipeRefresh() {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
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
}
