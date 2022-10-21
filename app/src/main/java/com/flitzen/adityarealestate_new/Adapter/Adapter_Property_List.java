package com.flitzen.adityarealestate_new.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.Items.Item_Property_List_New;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Property_List extends RecyclerView.Adapter<Adapter_Property_List.ViewHolder> {

    ArrayList<Item_Property_List_New> itemList = new ArrayList<>();
    Activity context;
    boolean value;
    OnItemClickListener mItemClickListener;
    ProgressDialog prd;

    public Adapter_Property_List(Activity context, ArrayList<Item_Property_List_New> itemList, Boolean value) {
        this.context = context;
        this.itemList = itemList;
        this.value = value;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_property_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        System.out.println("==========position    "+position+"   "+itemList.get(position).isCheckDateIsGone());

       try {
            if (itemList.get(position).getIs_hired().equals("1")) {

                //holder.view_main.setCardBackgroundColor(context.getResources().getColor(R.color.gray));

                holder.view_main.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.rent_soldout.setVisibility(View.VISIBLE);

                if(itemList.get(position).isCheckDateIsGone()==true){
                    holder.view_main.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.view.setBackgroundColor(context.getResources().getColor(R.color.light_red));
                    holder.txtRentDate.setTextColor(context.getResources().getColor(R.color.light_red));
                    holder.rent_soldout.setVisibility(View.VISIBLE);
                }else {
                    holder.view_main.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    holder.txtRentDate.setTextColor(context.getResources().getColor(R.color.blackText2));
                    holder.rent_soldout.setVisibility(View.VISIBLE);
                }

            } else {
                holder.view_main.setBackgroundColor(context.getResources().getColor(R.color.gray));
                holder.txtRentDate.setTextColor(context.getResources().getColor(R.color.blackText2));
                holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                holder.rent_soldout.setVisibility(View.GONE);
            }
        }catch (Exception e) {
           e.printStackTrace();
       }

        holder.txt_site_name.setText(itemList.get(position).getProperty_name());
        holder.txt_site_name_1.setText(itemList.get(position).getProperty_name());

        holder.txt_site_address.setText("Address : " + itemList.get(position).getAddress());
        try {
            if(itemList.get(position).getCustomer_name()==null){
                itemList.get(position).setCustomer_name("");
                holder.txt_cust_name.setText("Customer : " + itemList.get(position).getCustomer_name());
            }
            else {
                holder.txt_cust_name.setText("Customer : " + itemList.get(position).getCustomer_name());
            }
        }catch (Exception e){

        }


        if(itemList.get(position).getHired_since().equals("0000-00-00")){
            holder.txtRentDate.setText("Rent Date : -");
        }
        else {
            try {

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");

                Date oneWayTripDate;
                oneWayTripDate = input.parse(itemList.get(position).getHired_since());  // parse input
                String data1=output.format(oneWayTripDate);
                holder.txtRentDate.setText("Rent Date : "+data1);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


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

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_site_name, txt_site_name_1, txt_site_address,txt_cust_name,txtRentDate;
        RelativeLayout view_main;
        ImageView ivDelete,rent_soldout;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_site_name = (TextView) itemView.findViewById(R.id.txt_site_name);
            txt_site_name_1 = (TextView) itemView.findViewById(R.id.txt_site_name_1);
            txt_site_address = (TextView) itemView.findViewById(R.id.txt_site_address);
            view_main = (RelativeLayout) itemView.findViewById(R.id.view_main);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            rent_soldout = (ImageView) itemView.findViewById(R.id.rent_soldout);
            txt_cust_name = (TextView) itemView.findViewById(R.id.txt_cust_name);
            txtRentDate = (TextView) itemView.findViewById(R.id.txtRentDate);
            view =  itemView.findViewById(R.id.view);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    private void openDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setText("If you delete this property, all the data related to this property will be deleted.");
        textView.setTextSize(15);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(context.getResources().getColor(R.color.msg_fail));
        textView.setPadding(30, 15, 30, 5);
        builder.setMessage("Delete Property ?");
        builder.setCustomTitle(textView);
        builder.setCancelable(true);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showPrd();
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Properties").child(itemList.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    databaseReference.child("Properties").child(itemList.get(position).getKey()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hidePrd();
                                            if(task.isSuccessful()){
                                                //itemList.remove(position);
                                                notifyDataSetChanged();
                                                new CToast(context).simpleToast("Property delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            }
                                            else {
                                                new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }

                                        }
                                    }).addOnFailureListener(context, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hidePrd();
                                            new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        }
                                    });
                                }
                                else {
                                    hidePrd();
                                    new CToast(context).simpleToast("Property not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                hidePrd();
                                new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        });
                       // deleteEntryAPI(position);
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

    public void showPrd() {
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}
