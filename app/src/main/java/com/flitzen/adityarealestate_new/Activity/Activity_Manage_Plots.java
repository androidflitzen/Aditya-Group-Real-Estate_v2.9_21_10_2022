package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Plot_List;
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Manage_Plots extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;

    TextView spn_sites;
    RecyclerView rec_plot_list;
    FloatingActionButton fab_add_plot;
    Adapter_Plot_List adapter_plot_list;
    ArrayList<Item_Sites_List> itemListSite = new ArrayList<>();
    ArrayList<Item_Plot_List> itemListPlot = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_plots);

        getSupportActionBar().setTitle(Html.fromHtml("Manage Plots"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Manage_Plots.this;

        spn_sites = (TextView) findViewById(R.id.spn_site);
        fab_add_plot = (FloatingActionButton) findViewById(R.id.fab_add_plot);
        rec_plot_list = (RecyclerView) findViewById(R.id.rec_plot_list);

        spn_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListSite.size() == 0) {
                } else {
                    siteDialog();
                }
            }
        });

        fab_add_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spn_sites.getText().toString().equals("Select Site")) {
                    new CToast(mActivity).simpleToast("Please Select Site", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                } else {
                    LayoutInflater localView = LayoutInflater.from(mActivity);
                    View promptsView = localView.inflate(R.layout.dialog_plot_add, null);

                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                    alertDialogBuilder.setView(promptsView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);

                    final TextView txt_title = (TextView) promptsView.findViewById(R.id.txt_title);
                    final EditText edt_plot_no = (EditText) promptsView.findViewById(R.id.edt_plot_no);

                    TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                    Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_plot);

                    txt_title.setText("Add plot for " + spn_sites.getText().toString());

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    btn_add_site.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (edt_plot_no.getText().toString().trim().equals("")) {
                                edt_plot_no.setError("Enter Plot No");
                                edt_plot_no.requestFocus();
                                return;
                            } else {
                                alertDialog.dismiss();
                              //  addPlot(edt_plot_no.getText().toString().trim());
                            }
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        rec_plot_list.setLayoutManager(new GridLayoutManager(mActivity, 5));
        rec_plot_list.setHasFixedSize(true);
        adapter_plot_list = new Adapter_Plot_List(mActivity, itemListPlot);
        rec_plot_list.setAdapter(adapter_plot_list);

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
               // getPlotList(arrayListId.get(position));

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
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}