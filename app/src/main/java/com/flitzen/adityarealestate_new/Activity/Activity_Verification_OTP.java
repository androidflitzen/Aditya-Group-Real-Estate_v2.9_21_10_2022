package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_Verification_OTP extends AppCompatActivity {
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    TextView btnSubmit,textMobile;
    ProgressBar progressBar;
    String phoneNumber;
    String OtpID;
    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_otp);
        btnSubmit = findViewById(R.id.btnSubmits);
        textMobile = findViewById(R.id.textMobile);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        sharedPreferences = SharePref.getSharePref(Activity_Verification_OTP.this);

        mAuth = FirebaseAuth.getInstance();
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        findViewById(R.id.tvResendOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateotp();
                new CToast(Activity_Verification_OTP.this).simpleToast("OTP Resend Successful.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
            }
        });
        phoneNumber = getIntent().getStringExtra("mobile").toString();
        textMobile.setText(phoneNumber);
        initiateotp();
        setupOTPInputs();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCode1.getText().toString().isEmpty()
                        || inputCode2.getText().toString().isEmpty()
                        || inputCode3.getText().toString().isEmpty()
                        || inputCode4.getText().toString().isEmpty()
                        || inputCode5.getText().toString().isEmpty()
                        || inputCode6.getText().toString().isEmpty()) {
                    new CToast(Activity_Verification_OTP.this).simpleToast("Please enter valid code",Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                }else {
                    String code = inputCode1.getText().toString()
                            + inputCode2.getText().toString()
                            + inputCode3.getText().toString()
                            + inputCode4.getText().toString()
                            + inputCode5.getText().toString()
                            + inputCode6.getText().toString();
                    PhoneAuthCredential credential =PhoneAuthProvider.getCredential(OtpID,code.toString());
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
    }

    private void initiateotp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                Activity_Verification_OTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        OtpID = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        new CToast(Activity_Verification_OTP.this).simpleToast("The verification Failed"+e, Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SharePref.isLoggedIn, true);
                            editor.commit();

                            new CToast(Activity_Verification_OTP.this).simpleToast("Successful.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                            Intent intent = new Intent(Activity_Verification_OTP.this, Activity_Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            new CToast(Activity_Verification_OTP.this).simpleToast("The verification code is invalid User", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }
                    }
                });
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

}