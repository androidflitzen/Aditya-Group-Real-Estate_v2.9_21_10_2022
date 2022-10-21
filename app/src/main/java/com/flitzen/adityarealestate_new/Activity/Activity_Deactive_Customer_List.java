package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Cutomer_List;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Deactive_Customer_List extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_customer_list;
    Adapter_Cutomer_List adapter_cutomer_list;
    ArrayList<Item_Customer_List> itemListCustomer = new ArrayList<>();
    ArrayList<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    private EditText edtSearch;
    private ImageView imgClearSearch;

    int REQUEST_ADD = 01;
    Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate_customer_list);

        getSupportActionBar().setTitle(Html.fromHtml("Deactivate Customers"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Deactive_Customer_List.this;

        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

            }
        });

        rec_customer_list = (RecyclerView) findViewById(R.id.rec_customer_list);
        rec_customer_list.setLayoutManager(new LinearLayoutManager(this));
        rec_customer_list.setHasFixedSize(true);
        adapter_cutomer_list = new Adapter_Cutomer_List(mActivity, itemListCustomer,true);
        rec_customer_list.setAdapter(adapter_cutomer_list);

        adapter_cutomer_list.setOnItemClickListener(new Adapter_Cutomer_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                Aditya.POSITION = position;
                Aditya.ID = itemListCustomer.get(position).getId();
                Aditya.NAME = itemListCustomer.get(position).getName();

                Intent intent=new Intent(mActivity, Activity_Deactivate_Customer_Details.class);
                intent.putExtra("id",itemListCustomer.get(position).getId());
                intent.putExtra("name",itemListCustomer.get(position).getName());
                startActivityForResult(intent, 001);
                overridePendingTransition(0, 0);
                //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                //dialogCustomerInfo(position);

                /*final CharSequence[] item_Name = {"View Customer Detail", "Activate Customer"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Select Option");
                builder.setItems(item_Name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {

                            Aditya.POSITION = position;
                            Aditya.ID = itemListCustomer.get(position).getId();
                            Aditya.NAME = itemListCustomer.get(position).getName();

                            startActivity(new Intent(mActivity, Activity_Customer_Details.class));
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                        } else if (item == 1) {

                            Intent intent = new Intent(mActivity, Activity_Customer_Add.class);
                            intent.putExtra("TYPE", "EDIT");
                            intent.putExtra("ID", itemListCustomer.get(position).getId());
                            intent.putExtra("NAME", itemListCustomer.get(position).getName());
                            intent.putExtra("MOBILE", itemListCustomer.get(position).getContact_no());
                            intent.putExtra("MOBILE1", itemListCustomer.get(position).getAnother_no());
                            intent.putExtra("EMAIL", itemListCustomer.get(position).getEmail());
                            intent.putExtra("ADDRESS", itemListCustomer.get(position).getAddress());
                            intent.putExtra("CITY", itemListCustomer.get(position).getCity());
                            startActivityForResult(intent, REQUEST_ADD);
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                        } else if (item == 2) {

                            new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                                    .setTitle(Html.fromHtml("<b> Activate Customer</b>"))
                                    .setMessage("Are you sure you want to activate this customer?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            activate_Customer(itemListCustomer.get(position).getId());

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    adapter_cutomer_list.notifyDataSetChanged();
                                }
                            }).show();

                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();*/

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                itemListCustomer.clear();
                if (word.trim().isEmpty()) {
                    itemListCustomer.addAll(itemListCustomerTemp);
                    adapter_cutomer_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                        if (itemListCustomerTemp.get(i).getName().toLowerCase().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        } else if (itemListCustomerTemp.get(i).getContact_no().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        }
                    }
                    adapter_cutomer_list.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    imgClearSearch.setVisibility(View.VISIBLE);
                else
                    imgClearSearch.setVisibility(View.GONE);

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        imgClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 001) {

        }

    }


    public void dialogCustomerInfo(final int position) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_customer_info, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        TextView txt_cust_name = (TextView) promptsView.findViewById(R.id.txt_cust_name);
        TextView txt_mobile_no = (TextView) promptsView.findViewById(R.id.txt_mobile_no);
        TextView txt_cust_email = (TextView) promptsView.findViewById(R.id.txt_cust_email);
        TextView txt_address = (TextView) promptsView.findViewById(R.id.txt_address);
        TextView txt_city = (TextView) promptsView.findViewById(R.id.txt_city);

        View view_activae_customer = promptsView.findViewById(R.id.view_activae_customer);

        txt_cust_name.setText(itemListCustomer.get(position).getName());
        txt_mobile_no.setText(itemListCustomer.get(position).getContact_no());

        if (!itemListCustomer.get(position).getEmail().equals(""))
            txt_cust_email.setText(itemListCustomer.get(position).getEmail());
        else
            txt_cust_email.setText("-");

        if (!itemListCustomer.get(position).getAddress().equals(""))
            txt_address.setText(itemListCustomer.get(position).getAddress());
        else
            txt_address.setText("-");

        if (!itemListCustomer.get(position).getCity().equals(""))
            txt_city.setText(itemListCustomer.get(position).getCity());
        else
            txt_city.setText("-");

        view_activae_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Activate Customer</b>"))
                        .setMessage("Are you sure you want to activate " + itemListCustomer.get(position).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                alertDialog.dismiss();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter_cutomer_list.notifyDataSetChanged();
                    }
                }).show();
            }
        });

        alertDialog.show();
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
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}