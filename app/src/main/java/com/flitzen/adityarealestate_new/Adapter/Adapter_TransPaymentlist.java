package com.flitzen.adityarealestate_new.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.flitzen.adityarealestate_new.Activity.Edit_Transaction_Activity;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.TransPaymentList;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_TransPaymentlist extends RecyclerView.Adapter<Adapter_TransPaymentlist.ViewHolder> {

    Context context;
    ArrayList<TransPaymentList> transactionlist = new ArrayList<>();
    boolean value;
    ProgressDialog prd;

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    public Adapter_TransPaymentlist(Context context, ArrayList<TransPaymentList> Transactionlist, Boolean value) {
        this.context = context;
        this.transactionlist = Transactionlist;
        this.value = value;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transactionlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tvTransCustomerName.setText(transactionlist.get(position).getTranscations().get(0).getCustomerName());
        holder.tvTrancNote.setText(transactionlist.get(position).getTranscations().get(0).getTransactionNote());

        if (transactionlist.get(position).getTranscations().get(0).getPaymentType().equals("0")  ) {
            holder.tvTransAmount.setText(transactionlist.get(position).getTranscations().get(0).getAmount());
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.color_green));
        } else  {
            holder.tvTransAmount.setText(transactionlist.get(position).getTranscations().get(0).getAmount());
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.msg_fail));
        }

        String[] date = transactionlist.get(position).getTranscations().get(0).getTransactionDate().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.tvTransDate.setText(date[0] + " " + mm + " " + date[2]);

        // holder.tvTransDate.setText(transactionlist.get(position).getTransactionDate());

        holder.ivTransDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);

            }
        });

        holder.ivTransEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openUpdateDialog(position);
                Intent intent = new Intent(context, Edit_Transaction_Activity.class);
                intent.putExtra("customer_id", transactionlist.get(position).getTranscations().get(0).getCustomerId());
                intent.putExtra("transaction_id", transactionlist.get(position).getTranscations().get(0).getTransactionId());
                intent.putExtra("customer_name", transactionlist.get(position).getTranscations().get(0).getCustomerName());
                intent.putExtra("payment_type", transactionlist.get(position).getTranscations().get(0).getPaymentType());
                intent.putExtra("transaction_date", transactionlist.get(position).getTranscations().get(0).getTransactionDate());
                intent.putExtra("amount", transactionlist.get(position).getTranscations().get(0).getAmount());
                intent.putExtra("transaction_note", transactionlist.get(position).getTranscations().get(0).getTransactionNote());
                context.startActivity(intent);

            }
        });


    }

    private void openDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this Transaction ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                     //   deleteEntryAPI(position);
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


    @Override
    public int getItemCount() {
        return transactionlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTransCustomerName, tvTrancNote, tvTransAmount, tvTransDate;
        ImageView ivTransEdit, ivTransDelete, ivTransDetail;
        LinearLayout liMianView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransCustomerName = (TextView) itemView.findViewById(R.id.tvTransCustomerName);
            tvTrancNote = (TextView) itemView.findViewById(R.id.tvTrancNote);
            tvTransAmount = (TextView) itemView.findViewById(R.id.tvTransAmount);
            tvTransDate = (TextView) itemView.findViewById(R.id.tvTransDate);

            ivTransDelete = (ImageView) itemView.findViewById(R.id.ivTransDelete);
            ivTransEdit = (ImageView) itemView.findViewById(R.id.ivTransEdit);
            liMianView = (LinearLayout) itemView.findViewById(R.id.liMianView);

        }
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
