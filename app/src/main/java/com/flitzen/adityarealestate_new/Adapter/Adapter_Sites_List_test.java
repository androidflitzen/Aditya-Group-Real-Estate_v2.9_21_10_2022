package com.flitzen.adityarealestate_new.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Sites_List_test extends RecyclerView.Adapter<Adapter_Sites_List_test.ViewHolder> {

    ArrayList<Item_Sites_List> itemList = new ArrayList<>();
    Context context;
    boolean value;
    OnItemClickListener mItemClickListener;
    ProgressDialog prd;

    public Adapter_Sites_List_test(Context context, ArrayList<Item_Sites_List> itemList, Boolean value) {
        this.context = context;
        this.itemList = itemList;
        this.value = value;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sites_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.ivPopUp.setChecked(true);
        holder.txt_site_name.setText(itemList.get(position).getSite_name());

        String s = itemList.get(position).getSite_name() + ", " + itemList.get(position).getSite_address();
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1f), 0, itemList.get(position).getSite_name().length(), 0); // set size
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, itemList.get(position).getSite_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.txt_site_name_1.setText(ss1);


        //holder.txt_site_name_1.setText(itemList.get(position).getSite_name());


        //holder.txt_site_address.setText("Address : " + itemList.get(position).getSite_address());

        holder.txt_site_size.setText("Size (Sq. Yard): " + itemList.get(position).getSite_size());

        if (!itemList.get(position).getSite_price().equals(""))
            holder.txt_site_price.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getSite_price())));
        else
            holder.txt_site_price.setText("-");

        if (value) {
            holder.txt_site_name.setVisibility(View.VISIBLE);
            holder.txt_site_name_1.setVisibility(View.GONE);
        } else {
            holder.txt_site_name.setVisibility(View.GONE);
            holder.txt_site_name_1.setVisibility(View.VISIBLE);
        }

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });

        holder.ivPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure deactive site ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //holder.ivPopUp.setChecked(false);
                                toDeactiveSiteApi(position);
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                holder.ivPopUp.setChecked(true);
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //opendailogDeactive(position);
            }
        });
    }

    private void opendailogDeactive(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure deactive site ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        toDeactiveSiteApi(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void toDeactiveSiteApi(final int position) {

        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Sites").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(itemList.get(position).getId())) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Sites").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 0);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Payment status change
                                        databaseReference.child("Plots")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {

                                                            if (postsnapshot.child("site_id").getValue().toString().equals(itemList.get(position).getId())) {

                                                                // Change payment status plot related payments
                                                                String id = postsnapshot.child("id").getValue().toString();
                                                                databaseReference.child("Payments")
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshotPayment) {
                                                                                for (DataSnapshot postsnapshotPay : dataSnapshotPayment.getChildren()) {
                                                                                    if (postsnapshotPay.child("plot_id").getValue().toString().equals(id)) {
                                                                                        DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(postsnapshotPay.getKey());
                                                                                        Map<String, Object> map = new HashMap<>();
                                                                                        map.put("payment_status", 1);
                                                                                        Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {

                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                hidePrd();
                                                                                                new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                                notifyDataSetChanged();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                                Log.w("TAG: ", databaseError.getMessage());
                                                                                new CToast(context).simpleToast(databaseError.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                            }

                                                                        });
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Log.w("TAG: ", databaseError.getMessage());
                                                        new CToast(context).simpleToast(databaseError.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                    }

                                                });

                                        hidePrd();
                                        new CToast(context).simpleToast("Site deactivate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_site_name, txt_site_name_1, txt_site_address, txt_site_size, txt_site_price;
        View view_main;
        ImageView plot_soldout;
        SwitchMaterial ivPopUp;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_site_name = (TextView) itemView.findViewById(R.id.txt_site_name);
            txt_site_name_1 = (TextView) itemView.findViewById(R.id.txt_site_name_1);
            txt_site_address = (TextView) itemView.findViewById(R.id.txt_site_address);
            txt_site_size = (TextView) itemView.findViewById(R.id.txt_site_size);
            txt_site_price = (TextView) itemView.findViewById(R.id.txt_site_price);
            plot_soldout = (ImageView) itemView.findViewById(R.id.plot_soldout);
            view_main = itemView.findViewById(R.id.view_main);
            ivPopUp = itemView.findViewById(R.id.ivPopUp);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void showPrd() {
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        if (prd != null) {
            if (prd.isShowing()) {
                prd.dismiss();
            }
        }
    }
}
