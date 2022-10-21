package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Visitors_list;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_Visitors extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;
    ImageView ivEdit1;
    String type = "", id = "", name = "", mobile = "", address = "";

    TextView txt_add_customer, txt_date;
    EditText edt_cust_name, edt_cust_no, edt_cust_address, edt_remark;
    View btn_add_customer;
    String cust_name, cust_no, remark, date, cust_address;

    private Item_Visitors_list visitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitors);

        mActivity = Activity_Edit_Visitors.this;

        visitor = (Item_Visitors_list) getIntent().getSerializableExtra("visitor");

        ivEdit1 = findViewById(R.id.ivEdit1);
        txt_add_customer = (TextView) findViewById(R.id.txt_add_visitors);
        edt_cust_name = (EditText) findViewById(R.id.edt_cust_name);
        edt_cust_no = (EditText) findViewById(R.id.edt_cust_no);
        edt_cust_address = (EditText) findViewById(R.id.edt_cust_address);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        txt_date = (TextView) findViewById(R.id.txt_date);
        btn_add_customer = findViewById(R.id.btn_add_visitors);

        edt_cust_name.setText(visitor.getName());
        edt_cust_no.setText(visitor.getContact_no());
        edt_cust_address.setText(visitor.getAddress());
        edt_remark.setText(visitor.getRemark());
        txt_date.setText(visitor.getDate());

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_date);
            }
        });

        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_add_customer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                edt_cust_address.getText().toString();
                edt_remark.getText().toString();
                txt_date.getText().toString();
                if (edt_cust_name.getText().toString().equals("")) {
                    edt_cust_name.setError("Enter Name");
                    edt_cust_name.requestFocus();
                    return;
                } else if (edt_cust_no.getText().toString().equals("")) {
                    edt_cust_no.setError("Enter Mobile No.");
                    edt_cust_no.requestFocus();
                    return;
                } else if (edt_cust_no.getText().toString().length() != 10) {
                    edt_cust_no.setError("Mobile No. must be 10 digit");
                    edt_cust_no.requestFocus();
                    return;
                } else {
                    addCustomer();
                }
            }
        });
    }

    public void addCustomer() {
        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> map = new HashMap<>();
        map.put("name", edt_cust_name.getText().toString());
        map.put("contact_no", edt_cust_no.getText().toString());
        map.put("address", edt_cust_address.getText().toString());
        map.put("remark", edt_remark.getText().toString());
        map.put("Date", txt_date.getText().toString());
//        map.put("id" ,visitor.getId());

        rootRef.child("Visitors").child(visitor.getId()).updateChildren(map).addOnCompleteListener(Activity_Edit_Visitors.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(mActivity).simpleToast("Visitor updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                overridePendingTransition(0, 0);
                finish();
            }

        }).addOnFailureListener(Activity_Edit_Visitors.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Activity_Edit_Visitors.this, "Please try later...", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}