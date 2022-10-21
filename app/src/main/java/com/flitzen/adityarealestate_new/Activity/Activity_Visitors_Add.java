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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Activity_Visitors_Add extends AppCompatActivity {
    ProgressDialog prd;
    Activity mActivity;
    ImageView ivEdit1;
    String type = "", id = "", name = "", mobile = "", address = "";

    TextView txt_add_customer,txt_date;
    EditText edt_cust_name, edt_cust_no, edt_cust_address, edt_remark;
    View btn_add_customer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors_add);

        mActivity = Activity_Visitors_Add.this;
        ivEdit1 = findViewById(R.id.ivEdit1);
        txt_add_customer = (TextView) findViewById(R.id.txt_add_visitors);
        edt_cust_name = (EditText) findViewById(R.id.edt_cust_name);
        edt_cust_no = (EditText) findViewById(R.id.edt_cust_no);
        //  edt_cust_other_no = (EditText) findViewById(R.id.edt_cust_other_no);
        //   edt_cust_email = (EditText) findViewById(R.id.edt_cust_email);
        edt_cust_address = (EditText) findViewById(R.id.edt_cust_address);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        txt_date = (TextView) findViewById(R.id.txt_date);

        //    edt_cust_city = (EditText) findViewById(R.id.edt_cust_city);

        btn_add_customer = findViewById(R.id.btn_add_visitors);

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

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
        String key = rootRef.child("Visitors").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("name", edt_cust_name.getText().toString());
        map.put("contact_no", edt_cust_no.getText().toString());
        map.put("address", edt_cust_address.getText().toString());
        map.put("remark", edt_remark.getText().toString());
        map.put("Date" ,txt_date.getText().toString());
        map.put("id", key);
        map.put("status", 0);

        rootRef.child("Visitors").child(key).setValue(map).addOnCompleteListener(Activity_Visitors_Add.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(mActivity).simpleToast("Customer added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                overridePendingTransition(0, 0);
                finish();
            }

        }).addOnFailureListener(Activity_Visitors_Add.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Activity_Visitors_Add.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }
    // }

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