package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.R;

public class Activity_Phone_Number extends AppCompatActivity {
    TextView btnSubmit;
    EditText edt_phone_number;
    ProgressBar progressBar;
//    String number = "9904588813";
//    String number1 = "6354528225";
//    String number2 = "9904588813";
//    String number3 = "9724300085";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        btnSubmit = findViewById(R.id.btnSubmit);
        edt_phone_number = findViewById(R.id.edt_phone_numbers);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_phone_number.getText().toString().trim().isEmpty()) {
                    edt_phone_number.setError("Enter Mobile Number");
                    edt_phone_number.requestFocus();
                    return;
                } else {
                  //  if (number.equals(edt_phone_number.getText().toString()) || number1.equals(edt_phone_number.getText().toString()) || number2.equals(edt_phone_number.getText().toString()) || number3.equals(edt_phone_number.getText().toString())) {
                        Intent intent = new Intent(Activity_Phone_Number.this, Activity_Verification_OTP.class);
                        intent.putExtra("mobile", edt_phone_number.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                   // } else {
                    //    new CToast(Activity_Phone_Number.this).simpleToast("Please enter valid code", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                   // }
                }
            }
        });
    }

}