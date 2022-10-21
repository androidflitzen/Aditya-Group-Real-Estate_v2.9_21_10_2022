package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Payment_For_Plots extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    TextView spn_sites, spn_plot, txt_cust_name, txt_mobile_no, txt_date;
    EditText edt_paid_amount, edt_remark;
    View btn_add_payment;

    int pending_amount;
    String time = "";

    ArrayList<Item_Sites_List> itemListSite = new ArrayList<>();
    ArrayList<Item_Plot_List> itemListPlot = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payament_for_plots);

        getSupportActionBar().setTitle(Html.fromHtml("Add Payment For Plot"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Add_Payment_For_Plots.this;
        spn_sites = (TextView) findViewById(R.id.spn_site);
        spn_plot = (TextView) findViewById(R.id.spn_plot);
        txt_cust_name = (TextView) findViewById(R.id.txt_cust_name);
        txt_mobile_no = (TextView) findViewById(R.id.txt_mobile_no);
        txt_date = (TextView) findViewById(R.id.txt_date);

        edt_paid_amount = (EditText) findViewById(R.id.edt_paid_amount);
        edt_remark = (EditText) findViewById(R.id.edt_remark);

        btn_add_payment = findViewById(R.id.btn_add_payment);


        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        String hours = String.valueOf(hour);
        String min = String.valueOf(minute);

        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }

        time = hours + ":" + min + ":00";

        spn_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListSite.size() == 0) {
                } else {
                    siteDialog();
                }
            }
        });

        spn_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                plotDialog();

                /*if (itemListPlot.size() == 0) {
                    getPlotList(spn_sites.getTag().toString());
                } else {
                    plotDialog();
                }*/
            }
        });

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_date);
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spn_sites.getText().toString().equals("Select Site")) {
                    new CToast(mActivity).simpleToast("Please Select Site", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                } else if (spn_plot.getText().toString().equals("Select Plot")) {
                    new CToast(mActivity).simpleToast("Please Select Plot", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                } else if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter paid amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else if (txt_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > pending_amount) {
                    edt_paid_amount.setError("You enter more then pending amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else {
                }
            }
        });
    }
    public void siteDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);

        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

        for (int i = 0; i < itemListSite.size(); i++) {
            arrayListTemp.add(itemListSite.get(i).getSite_name());
            arrayListId.add(itemListSite.get(i).getId());
        }

        list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemListSite.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListSite.get(j).getSite_name().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListSite.get(j).getSite_name());
                            arrayListId.add(itemListSite.get(j).getId());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int i = 0; i < itemListSite.size(); i++) {
                        arrayListTemp.add(itemListSite.get(i).getSite_name());
                        arrayListId.add(itemListSite.get(i).getId());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                spn_sites.setText(arrayListTemp.get(position));
                spn_sites.setTag(arrayListId.get(position));
                alertDialog.dismiss();
              //  getPlotList(arrayListId.get(position));

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
    public void plotDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);

        edtSearchLocation.setHint("Search Plot");
        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();
        final ArrayList<String> arrayListCustId = new ArrayList<>();
        final ArrayList<String> arrayListCustName = new ArrayList<>();
        final ArrayList<String> arrayListMobile = new ArrayList<>();
        final ArrayList<String> arrayListPending = new ArrayList<>();

        for (int i = 0; i < itemListPlot.size(); i++) {
            arrayListTemp.add(itemListPlot.get(i).getPlot_no());
            arrayListId.add(itemListPlot.get(i).getId());
            arrayListCustId.add(itemListPlot.get(i).getCustomer_id());
            arrayListCustName.add(itemListPlot.get(i).getCustomer_name());
            arrayListMobile.add(itemListPlot.get(i).getContact_no());
            arrayListPending.add(itemListPlot.get(i).getPending_amount());
        }

        list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemListPlot.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListPlot.get(j).getPlot_no().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListPlot.get(j).getPlot_no());
                            arrayListId.add(itemListPlot.get(j).getId());
                            arrayListCustName.add(itemListPlot.get(j).getCustomer_name());
                            arrayListMobile.add(itemListPlot.get(j).getContact_no());
                            arrayListPending.add(itemListPlot.get(j).getPending_amount());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int i = 0; i < itemListPlot.size(); i++) {
                        arrayListTemp.add(itemListPlot.get(i).getPlot_no());
                        arrayListId.add(itemListPlot.get(i).getId());
                        arrayListCustName.add(itemListPlot.get(i).getCustomer_name());
                        arrayListMobile.add(itemListPlot.get(i).getContact_no());
                        arrayListPending.add(itemListPlot.get(i).getPending_amount());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                spn_plot.setText(arrayListTemp.get(position));
                spn_plot.setTag(arrayListId.get(position));
                pending_amount = Integer.parseInt(arrayListPending.get(position));
                edt_paid_amount.setText(arrayListPending.get(position));
                txt_mobile_no.setText(arrayListMobile.get(position));
                txt_cust_name.setText(arrayListCustName.get(position));
                txt_cust_name.setTag(arrayListCustId.get(position));

                alertDialog.dismiss();

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
