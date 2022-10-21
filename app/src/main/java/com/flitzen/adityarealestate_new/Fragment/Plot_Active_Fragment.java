package com.flitzen.adityarealestate_new.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Add;
import com.flitzen.adityarealestate_new.Activity.Activity_Plots_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Sites_List;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Plot_Active_Fragment extends Fragment {

    ProgressDialog prd;
    Activity mActivity;

    FloatingActionButton fab_add_sites;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_sites_list;
    Adapter_Sites_List adapter_sites_list;
    ArrayList<Item_Sites_List> itemList = new ArrayList<>();
    ArrayList<Item_Sites_List> itemListTemp = new ArrayList<>();
    TextView tvNoActiveCustomer;

    private EditText edtSearch;
    private ImageView imgClearSearch,ivback,ivAddReminder;



    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plot_activesite_fragment, null);

        mActivity = getActivity();

        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

              //  edtSearch.setText(null);
               // edtSearch.clearFocus();
             //   InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
              //  imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                getSiteList();

            }
        });


        rec_sites_list = (RecyclerView) view.findViewById(R.id.rec_sites_list);
        tvNoActiveCustomer =  view.findViewById(R.id.tvNoActiveCustomer);
        rec_sites_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_sites_list.setHasFixedSize(true);
        adapter_sites_list = new Adapter_Sites_List(mActivity, itemList, false);
        rec_sites_list.setAdapter(adapter_sites_list);

        fab_add_sites = (FloatingActionButton) view.findViewById(R.id.fab_add_sites);

        fab_add_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_sites_add, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_site_name = (EditText) promptsView.findViewById(R.id.edt_site_name);
                final EditText edt_site_address = (EditText) promptsView.findViewById(R.id.edt_site_address);
                final EditText edt_site_purchase_price = (EditText) promptsView.findViewById(R.id.edt_site_purchase_price);
                final EditText edt_site_size = (EditText) promptsView.findViewById(R.id.edt_site_size);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_site);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt_site_name.getText().toString().trim().equals("")) {
                            edt_site_name.setError("Site Name");
                            edt_site_name.requestFocus();
                            return;
                        } else {
                            if(edt_site_purchase_price.getText().toString().trim().equals("")){
                                edt_site_purchase_price.setText("0");
                            }
                            if(edt_site_size.getText().toString().trim().equals("")){
                                edt_site_size.setText("0");
                            }

                            alertDialog.dismiss();
                            addSite(edt_site_name.getText().toString().trim(), edt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });

        adapter_sites_list.setOnItemClickListener(new Adapter_Sites_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

               /* final CharSequence[] item_Name = {"View Plots", "Edit Site"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder.setTitle("Select Option");
                builder.setItems(item_Name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {

                            Intent intent = new Intent(mActivity, Activity_Plots_List.class);
                            intent.putExtra("id", itemList.get(position).getId());
                            intent.putExtra("name", itemList.get(position).getSite_name());
                            startActivity(intent);
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                        } else if (item == 1) {

                            LayoutInflater localView = LayoutInflater.from(mActivity);
                            View promptsView = localView.inflate(R.layout.dialog_sites_edit, null);

                            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                            alertDialogBuilder.setView(promptsView);
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);

                            final TextView txt_site_name = (TextView) promptsView.findViewById(R.id.txt_site_name);
                            final TextView txt_site_address = (TextView) promptsView.findViewById(R.id.txt_site_address);
                            final EditText edt_site_purchase_price = (EditText) promptsView.findViewById(R.id.edt_site_purchase_price);
                            final EditText edt_site_size = (EditText) promptsView.findViewById(R.id.edt_site_size);

                            TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                            Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_site);

                            txt_site_name.setText(itemList.get(position).getSite_name());
                            txt_site_name.setTag(itemList.get(position).getId());
                            txt_site_address.setText(itemList.get(position).getSite_address());

                            edt_site_purchase_price.setText(itemList.get(position).getSite_price());
                            edt_site_size.setText(itemList.get(position).getSite_size());

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                }
                            });

                            btn_add_site.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (edt_site_purchase_price.getText().toString().trim().equals("")) {
                                        edt_site_purchase_price.setError("Site Purchase Price");
                                        edt_site_purchase_price.requestFocus();
                                        return;
                                    } else if (edt_site_size.getText().toString().trim().equals("")) {
                                        edt_site_size.setError("Site Size");
                                        edt_site_size.requestFocus();
                                        return;
                                    } else {
                                        alertDialog.dismiss();
                                        editSite(txt_site_name.getTag().toString().trim(), txt_site_name.getText().toString().trim(), txt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                                    }
                                }
                            });

                            alertDialog.show();

                        }
                    }
                });

                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();*/

                Intent intent = new Intent(mActivity, Activity_Plots_List.class);
                intent.putExtra("id", itemList.get(position).getId());
                intent.putExtra("name", itemList.get(position).getSite_name());
                intent.putExtra("address", itemList.get(position).getSite_address());
                intent.putExtra("size", itemList.get(position).getSite_size());
                intent.putExtra("TYPE", "ADD");
                intent.putExtra("ACTIVE_DEACTIVE", "ACTIVE");
                startActivity(intent);
              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });


        getSiteList();

        return view;
    }


    public void getSiteList() {
        //showPrd();
        swipe_refresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Sites").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                itemList.clear();
                itemListTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if(npsnapshot.child("status").getValue().toString().equals("1")){
                                Item_Sites_List item = new Item_Sites_List();
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setSite_name(npsnapshot.child("site_name").getValue().toString());
                                item.setSite_address(npsnapshot.child("site_address").getValue().toString());
                                item.setSite_size(npsnapshot.child("size").getValue().toString());
                                item.setSite_price(npsnapshot.child("purchase_price").getValue().toString());
                                itemList.add(item);
                                itemListTemp.add(item);
                                tvNoActiveCustomer.setVisibility(View.GONE);

                            }
                            // }
                        }
                        if(itemList.size()!=0){
                            adapter_sites_list.notifyDataSetChanged();
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        }
                        else {
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        }
                        adapter_sites_list.notifyDataSetChanged();
                    }
                    else {
                        //rec_sites_list.setVisibility(View.GONE);
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
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

    public void addSite(final String site_name, final String site_Address, final String site_Price, final String site_Size) {
        showPrd();

           DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String key = rootRef.child("Sites").push().getKey();
            Map<String, Object> map = new HashMap<>();
            map.put("file", "");
            map.put("id", key);
            map.put("purchase_price", site_Price);
            map.put("site_address", site_Address);
            map.put("site_name", site_name);
            map.put("size", site_Size);
            map.put("status", 1);

            rootRef.child("Sites").child(key).setValue(map).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hidePrd();
                    new CToast(mActivity).simpleToast("Site added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    getSiteList();
                }

            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(getActivity(), "Please try later...", Toast.LENGTH_SHORT).show();
                }

            });
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
