package com.flitzen.adityarealestate_new.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

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
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Fragment.CashPaymentFragment;

import com.flitzen.adityarealestate_new.Items.Item_Site_Payment_List;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Site_Payment_List extends RecyclerView.Adapter<Adapter_Site_Payment_List.ViewHolder> {

    Context context;
    ArrayList<Item_Site_Payment_List> itemList = new ArrayList<Item_Site_Payment_List>();
    OnItemLongClickListener mItemClickListener;
    ProgressDialog prd;

    boolean isedit = false;

    CashPaymentFragment cashPaymentFragment;

    String edit_id = "", remaining_amount = "", edite_date = "", edit_amount = "", edit_remark = "",editfinaldate ="";

    public Adapter_Site_Payment_List(Context context, ArrayList<Item_Site_Payment_List> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_site_payment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getAmount())));

        String[] date = itemList.get(position).getPayment_date().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_date.setText(date[2] + " " + mm + " " + date[0]);

        holder.txt_remarks.setText("Remarks : " + itemList.get(position).getRemarks());

        if (!itemList.get(position).getRemarks().equals("")) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
        } else {
            holder.txt_remarks.setVisibility(View.INVISIBLE);
        }

        /*holder.view_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
                return true;
            }
        });*/

       /* holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_id = itemList.get(position).getId();
                edit_amount = itemList.get(position).getAmount();
                edite_date = itemList.get(position).getPayment_date();
                edit_remark = itemList.get(position).getRemarks();
                opendailogedit(edite_date, edit_amount, edit_remark, edit_id, isedit = true);


            }
        });*/

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_amount, txt_date, txt_time, txt_name, txt_remarks;
        View view_main;
        ImageView plotSummaryEdit;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
            view_main = itemView.findViewById(R.id.view_main);
            plotSummaryEdit = itemView.findViewById(R.id.plotSummaryEdit);
        }
    }

    public void OnItemLongClickListener(final OnItemLongClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {

        void onItemClick(int position);
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
