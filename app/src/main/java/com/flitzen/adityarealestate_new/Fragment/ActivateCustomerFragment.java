package com.flitzen.adityarealestate_new.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Details;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Cutomer_List;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivateCustomerFragment extends Fragment {

    Activity mActivity;
    ProgressDialog prd;

    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_customer_list;
    Adapter_Cutomer_List adapter_cutomer_list;
    ArrayList<Item_Customer_List> itemListCustomer = new ArrayList<>();
    ArrayList<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();
    TextView tvNoActiveCustomer;

    private EditText edtSearch;
    private ImageView imgClearSearch;

    int REQUEST_ADD = 01;
    Menu menu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activate_customer, null);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        mActivity = getActivity();

        edtSearch = (EditText) view.findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) view.findViewById(R.id.img_clear_search);
        tvNoActiveCustomer =  view.findViewById(R.id.tvNoActiveCustomer);

        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                getCustomerList();

            }
        });


        rec_customer_list = (RecyclerView) view.findViewById(R.id.rec_customer_list);
        rec_customer_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_customer_list.setHasFixedSize(true);
        adapter_cutomer_list = new Adapter_Cutomer_List(mActivity, itemListCustomer,false);
        rec_customer_list.setAdapter(adapter_cutomer_list);

        adapter_cutomer_list.setOnItemClickListener(new Adapter_Cutomer_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                Aditya.POSITION = position;
                Aditya.ID = itemListCustomer.get(position).getId();
                Aditya.NAME = itemListCustomer.get(position).getName();
                Intent intent=new Intent(mActivity, Activity_Customer_Details.class);
                intent.putExtra("id",itemListCustomer.get(position).getId());
                intent.putExtra("name",itemListCustomer.get(position).getName());
                Log.e("List Screen  ",itemListCustomer.get(position).getId());
                startActivityForResult(intent, 002);
                mActivity.overridePendingTransition(0, 0);
                //mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                /* final CharSequence[] item_Name = {"View Customer Detail", "Edit Customer", "Deactivate Customer"};
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
                                    .setTitle(Html.fromHtml("<b> Deactivate Customer </b>"))
                                    .setMessage("Are you sure you want to deactivate " + itemListCustomer.get(position).getName() + "?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            remove_Customer(itemListCustomer.get(position).getId());

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
                alert.show(); */


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
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getCustomerList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ADD) {
            getCustomerList();
        }

        if (requestCode == 002) {
            getCustomerList();
        }

    }


    public void getCustomerList() {
        swipe_refresh.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                itemListCustomer.clear();
                itemListCustomerTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("status").getValue().toString().equals("0")) {
                               // for (int i = 0; i < jsonArray.length(); i++) {

                                Item_Customer_List item = new Item_Customer_List();
                                    item.setId(npsnapshot.child("id").getValue().toString());
                                    item.setName(npsnapshot.child("name").getValue().toString());
                                    item.setCity(npsnapshot.child("city").getValue().toString());
                                    item.setContact_no(npsnapshot.child("contact_no").getValue().toString());
                                    item.setAnother_no(npsnapshot.child("contact_no1").getValue().toString());
                                    item.setEmail(npsnapshot.child("email").getValue().toString());
                                    item.setAddress(npsnapshot.child("address").getValue().toString());

                                    itemListCustomer.add(item);
                                    itemListCustomerTemp.add(item);
                               // }
                            }
                        }
                        if(itemListCustomer.size()>0){
                            tvNoActiveCustomer.setVisibility(View.GONE);
                            rec_customer_list.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                            rec_customer_list.setVisibility(View.GONE);
                        }
                        adapter_cutomer_list.notifyDataSetChanged();
                    }
                    else {
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("Test  ",e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(mActivity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipe_refresh.setRefreshing(false);

            }
        });
    }


    public void getCustomerList1() {
        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                swipe_refresh.setRefreshing(false);
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setAnother_no(object.getString("contact_no1"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }
                        adapter_cutomer_list.notifyDataSetChanged();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
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
