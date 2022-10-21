package com.flitzen.adityarealestate_new.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flitzen.adityarealestate_new.Adapter.Adapter_Visitors_list;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Item_Visitors_list;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Activity_Visitors extends AppCompatActivity {
    FloatingActionButton fab_add_customer;
    int REQUEST_ADD = 01;
    Activity_Visitors mActivity;
    ProgressDialog prd;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_customer_list;
    Adapter_Visitors_list adapter_visitors_list;
    ArrayList<Item_Visitors_list> itemListCustomer = new ArrayList<>();
    ArrayList<Item_Visitors_list> itemListCustomerTemp = new ArrayList<>();
    TextView tvNoActiveCustomer;

    private EditText edtSearch;
    private ImageView imgClearSearch;

    ImageView ivEdit1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors);
        mActivity = Activity_Visitors.this;
        ivEdit1 = findViewById(R.id.ivEdit1);
        tvNoActiveCustomer = findViewById(R.id.tvNoActiveCustomer);
        rec_customer_list = (RecyclerView) findViewById(R.id.rec_visitors_list);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        fab_add_customer = (FloatingActionButton) findViewById(R.id.fab_add_customer);

        fab_add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Visitors.this, Activity_Visitors_Add.class);
                startActivity(intent);
            }
        });


        rec_customer_list.setLayoutManager(new LinearLayoutManager(this));
        rec_customer_list.setHasFixedSize(true);
        adapter_visitors_list = new Adapter_Visitors_list(mActivity, itemListCustomer);
        rec_customer_list.setAdapter(adapter_visitors_list);

        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                getCustomerList();

            }
        });

//        adapter_visitors_list.OnItemClickListener(new Adapter_Visitors_list.OnItemClickListener() {
//            @Override
//            public void onItemClick(final int position) {
//                Intent intent = new Intent(Activity_Visitors.this, Activity_Edit_Visitors.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//
//        });

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
                    adapter_visitors_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                        if (itemListCustomerTemp.get(i).getName().toLowerCase().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        } else if (itemListCustomerTemp.get(i).getContact_no().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        }
                    }

                    adapter_visitors_list.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();
        getCustomerList();
    }

    public void getCustomerList() {
        swipe_refresh.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Visitors").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.N)
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
                                Item_Visitors_list item = new Item_Visitors_list();
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setName(npsnapshot.child("name").getValue().toString().toUpperCase(Locale.ROOT));
                                item.setContact_no(npsnapshot.child("contact_no").getValue().toString());
                                item.setAddress(npsnapshot.child("address").getValue().toString());
                                item.setDate(npsnapshot.child("Date").getValue().toString());
                                item.setRemark(npsnapshot.child("remark").getValue().toString());
                                item.setKey(npsnapshot.getKey());

                                itemListCustomer.add(item);
                                itemListCustomerTemp.add(item);
                                // }
                            }
//                            Collections.sort(itemListCustomer, new Comparator<Item_Visitors_list>() {
//                                @Override
//                                public int compare(Item_Visitors_list o1, Item_Visitors_list o2) {
//                                    return o1.getDate().compareTo(o2.getDate());
//                                }
//                            }.reversed());
                           // Collections.sort(itemListCustomer, Comparator.comparing(Item_Visitors_list::getDate));
                           // Collections.sort(itemListCustomerTemp, Comparator.comparing(Item_Visitors_list::getDate));
                          //  adapter_visitors_list.notifyDataSetChanged();
                        }
                        if (itemListCustomer.size() > 0) {
//                            Collections.sort(itemListCustomer, Comparator.comparing(Item_Visitors_list::getDate));
//                            Collections.sort(itemListCustomerTemp, Comparator.comparing(Item_Visitors_list::getDate));
//                            adapter_visitors_list.notifyDataSetChanged();
                            // Collections.sort(itemListCustomer,Comparator.comparing(Item_Visitors_list::getDate));
                            //   Collections.sort(itemListCustomerTemp,Comparator.comparing(Item_Visitors_list::getDate));
//                            Collections.sort(itemListCustomer);
//                            for (int i = 0; i < itemListCustomer.size(); i++) {
//                                String dateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date(Item_Visitors_list::getDate.getYear()));
//                                Log.e("!_@@ date", dateString + "");
//                            }
                          //  Collections.sort(itemListCustomer, new Comparator<Item_Visitors_list>() {
//                                @Override
//                                public int compare(Item_Visitors_list item1, Item_Visitors_list item2) {
//                                    return item1.getDate().compareTo(item2.getDate());
//
//                                }
//                            });
                            //Collections.reverse(itemListCustomer);


                            //adapter_visitors_list.notifyDataSetChanged();
                            tvNoActiveCustomer.setVisibility(View.GONE);
                            rec_customer_list.setVisibility(View.VISIBLE);
                        } else {
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                            rec_customer_list.setVisibility(View.GONE);
                        }
                        adapter_visitors_list.notifyDataSetChanged();
                    } else {
                        swipe_refresh.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesActivity", databaseError.getMessage());
                new CToast(mActivity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipe_refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Activity_Visitors.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}