package com.flitzen.adityarealestate_new.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_View_Emi;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Fragment.ActionBottomDialogFragment;
import com.flitzen.adityarealestate_new.Items.Iteams_All_Loan_Application;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Items_View_EMI;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.LoanDetailsForPDF;
import com.flitzen.adityarealestate_new.PDFUtility;
import com.flitzen.adityarealestate_new.PDFUtility_Loan;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_LoanDetail_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,ActionBottomDialogFragment.ItemClickListener {

    Activity mActivity;
    ArrayList<Items_View_EMI> itemArray_EMI = new ArrayList<>();

    SharedPreferences sharedPreferences;
    String applicationId, applicationNumber, ruppe, applicantName = "", node_id, loan_id;
    private LinearLayout btnLoanDetail, btnLoanInstallment;
    private View viewLoanDetail, viewLoanInstallment, viewPendingContent, mainView, viewRejectContent, viewApproveContent;
    private TextView txt_monthlu_emi, txt_loan_type, txtLoanRejectRemarks, txt_pay_emi_date;
    DecimalFormat formatter;
    private TextView txtTotalEmi, txt_loan_status, txt_loan_date, txt_loan_reason, txt_loan_approve_date, txt_loan_amount, txt_approved_amount, txt_loan_interest, txtHeaderApplicantName;
    private ListView listview_EMI;
    private FloatingActionButton fab_AddEmi;
    private Adapter_View_Emi mAdapter;
    private View btn_ApproveLoan, btn_RejectLoan;
    // private View viewEmiContent, viewSimpleContent, viewBotttomPayableA;
    private LinearLayout swipeRefresh;
    // private TextView txt_s_paid, txt_s_emi;
    String customerId, LOAN_TYPE;
    private Button btnCloseLoan;
    private String getEMI_AMOUNT;
    private TextView txtPaidAmountTillNowLabel, txtPendingLoanAmountLabel, txtPendingLoanAmount, txtPaidAmountTillNow;
    RelativeLayout tvViewPaymentPDF;
    private String InstallMent_Type = "", file_url = "";
    ProgressDialog progressDialog;
    int totalPaidAmount = 0;
    String emiAmount = "";
    LoanDetailsForPDF loanDetailsForPDF = new LoanDetailsForPDF();
    String path = "";
    private static final int MENU_ITEM_ITEM1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loan_detail);
        progressDialog = new ProgressDialog(Admin_LoanDetail_Activity.this);
        progressDialog.setTitle("Aditya Group");
        progressDialog.setMessage("Please wait..");

        mActivity = Admin_LoanDetail_Activity.this;

        txtHeaderApplicantName = (TextView) findViewById(R.id.txtHeaderApplicantName);
        ruppe = getResources().getString(R.string.rupee);
        formatter = new DecimalFormat(Helper.DECIMAL_FORMATE);

        applicationId = getIntent().getStringExtra("Application_id");
        applicationNumber = getIntent().getStringExtra("Application_number");
        applicantName = getIntent().getStringExtra("Application_name");
        node_id = getIntent().getStringExtra("node_id");
        loan_id = getIntent().getStringExtra("loan_id");

        Log.e("loan_id  Detail  ", loan_id);

        getSupportActionBar().setTitle("Loan No : " + applicationNumber);
        //getSupportActionBar().setSubtitle("From : ");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtHeaderApplicantName.setText(applicantName);
        swipeRefresh = (LinearLayout) findViewById(R.id.swipe_refresh_layout_loan_detail);
        mainView = findViewById(R.id.activity_admin_loan_detail);
        mainView.setVisibility(View.GONE);

        btnLoanDetail = (LinearLayout) findViewById(R.id.btn_loan_detail);
        btnLoanInstallment = (LinearLayout) findViewById(R.id.btn_loan_detail_installment);
        txtPaidAmountTillNowLabel = (TextView) findViewById(R.id.txtPaidAmountTillNowLabel);
        txtPaidAmountTillNow = (TextView) findViewById(R.id.txt_loan_detail_paid_amount);
        txtPendingLoanAmountLabel = (TextView) findViewById(R.id.txtPendingLoanAmountLabel);
        txtPendingLoanAmount = (TextView) findViewById(R.id.txt_loan_detail_unpaid_amount);
        tvViewPaymentPDF = (RelativeLayout) findViewById(R.id.tvViewPaymentPDF);
        btnCloseLoan = (Button) findViewById(R.id.btn_loan_detail_close_loan);
        btn_ApproveLoan = findViewById(R.id.btn_loan_detail_approve_loan);
        btn_RejectLoan = findViewById(R.id.btn_loan_detail_reject_loan);
        viewLoanDetail = findViewById(R.id.view_loan_detail);
        viewLoanInstallment = findViewById(R.id.view_loan_installment);

        viewPendingContent = findViewById(R.id.view_loan_detail_pending_view);
        viewRejectContent = findViewById(R.id.view_admin_loan_d_reject_content);
        viewApproveContent = findViewById(R.id.view_admin_loan_d_approve_content);

        txtTotalEmi = (TextView) findViewById(R.id.txt_loan_detail_total_emi);
        txtLoanRejectRemarks = (TextView) findViewById(R.id.txt_loan_detail_reject_remarks);
        txt_monthlu_emi = (TextView) findViewById(R.id.txt_loan_detail_monthly_emi);
        txt_pay_emi_date = (TextView) findViewById(R.id.txt_loan_detail_pay_emi_date);
        txt_loan_status = (TextView) findViewById(R.id.txt_loan_detail_status);
        txt_loan_date = (TextView) findViewById(R.id.txt_loan_detail_date);
        txt_loan_reason = (TextView) findViewById(R.id.txt_loan_detail_reason);
        txt_loan_approve_date = (TextView) findViewById(R.id.txt_loan_detail_approved_date);
        txt_loan_amount = (TextView) findViewById(R.id.txt_loan_detail_loan_amount);
        txt_approved_amount = (TextView) findViewById(R.id.txt_loan_detail_approved_amount);
        txt_loan_interest = (TextView) findViewById(R.id.txt_loan_detail_loan_interest);
        txt_loan_type = (TextView) findViewById(R.id.txt_loan_detail_loan_type);

        listview_EMI = (ListView) findViewById(R.id.listview_loan_detail_emi_list);
        fab_AddEmi = (FloatingActionButton) findViewById(R.id.fab_loan_detail_add_emi);

        mAdapter = new Adapter_View_Emi(mActivity, itemArray_EMI);
        listview_EMI.setAdapter(mAdapter);

        //swipeRefresh.setOnRefreshListener(this);
        //swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        getData();

        tvViewPaymentPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int finalTotalAmount = 0;
                for (int i = 0; i < itemArray_EMI.size(); i++) {
                    if (itemArray_EMI.get(i).getEmi_amount() != null && !(itemArray_EMI.get(i).getEmi_amount().equals(""))) {
                        finalTotalAmount = finalTotalAmount + Integer.parseInt(itemArray_EMI.get(i).getEmi_amount());
                    }
                }

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
//                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    path = Admin_LoanDetail_Activity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + ts + "_payment_list.pdf";
                } else {
                    path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                }
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Loan.createPdf(v.getContext(), new PDFUtility_Loan.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                            //  Toast.makeText(Admin_LoanDetail_Activity.this, "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Admin_LoanDetail_Activity.this, ViewPdfForAll.class);
                            intent.putExtra("path", path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, loanDetailsForPDF, finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

               /* Intent intent=new Intent(Admin_LoanDetail_Activity.this, PdfCreatorLoanActivity.class);
                intent.putExtra("LoanDetails",loanDetailsForPDF);
                intent.putExtra("paymentList",itemArray_EMI);
                startActivity(intent);*/

            }
        });

        btnLoanDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView(true);
            }
        });

        btnLoanInstallment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchView(false);
            }
        });

        btn_ApproveLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApprovedDialog();
            }
        });
        btn_RejectLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RejectDialog();
            }
        });
        fab_AddEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEmiDialog();
            }
        });

        listview_EMI.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String selectedAmount = itemArray_EMI.get(position).getEmi_amount();
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete " + ruppe + selectedAmount + " EMI?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //showPrd();

                                System.out.println("==========setOnItemLongClickListener");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query applesQuery = ref.child("EMI_Received").orderByChild("id").equalTo(itemArray_EMI.get(position).getEmi_id());

                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                            appleSnapshot.getRef().removeValue();
                                        }
                                        new CToast(mActivity).simpleToast("Installment delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        getData();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("cancel  ", "onCancelled", databaseError.toException());
                                    }
                                });

                               /* StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_EMI,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Log.e("Delete payment", response);
                                                //hidePrd();
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("result").equals("success")) {

                                                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        getData();

                                                    } else {
                                                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                //hidePrd();
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("type", "Delete");
                                        params.put("id", itemArray_EMI.get(position).getEmi_id());
                                        return params;
                                    }

                                };

                                try {
                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                            30000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                    RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
                                    requestQueue.add(stringRequest);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/

                            }
                        }).setNegativeButton("NO", null).show();
                return false;
            }
        });

        btnCloseLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle("COMPLETE LOAN")
                        .setMessage("Are you sure you want to complete this loan ? ")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                Query query = databaseReference.child("LoanDetails").orderByKey();
                                //databaseReference.keepSynced(true);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot npsnapshot) {
                                        try {
                                            if (npsnapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                                    if (dataSnapshot.child("id").getValue().toString().equals(loan_id)) {

                                                        DatabaseReference cineIndustryRef = databaseReference.child("LoanDetails").child(dataSnapshot.getKey());
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("Loan_Status", 4);
                                                        Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                new CToast(mActivity).simpleToast("Loan complete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                                getData();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                                        Log.e("databaseError   ", databaseError.getMessage());
                                    }
                                });
                            }
                        }).setNegativeButton("NO", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_loan_edit, menu);
        return true;
    }


    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount = 0;
        for (int i = 0; i < itemArray_EMI.size(); i++) {

            String data1 = "";
            String data2 = "";
            String data3 = "";
            String data4 = "";
            String data5 = "";

            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
            try {
                Date oneWayTripDate;
                Date oneWayTripDateT;
                oneWayTripDate = input.parse(itemArray_EMI.get(i).getEmi_date());  // parse input
                data1 = output.format(oneWayTripDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (loanDetailsForPDF.getCustomerName() != null) {
                data2 = loanDetailsForPDF.getCustomerName();
            }

            data3 = itemArray_EMI.get(i).getEmi_remark();
            data4 = itemArray_EMI.get(i).getEmi_type();
            data5 = Helper.getFormatPrice(Integer.parseInt(itemArray_EMI.get(i).getEmi_amount()));

            temp.add(new String[]{data1, data2, data3, data4, data5});
        }
        return temp;
    }

    public void getData() {
        //swipeRefresh.setRefreshing(true);

        progressDialog.show();
        loanDetailsForPDF = new LoanDetailsForPDF();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference circleMemebersRef = rootRef.child("LoanDetails");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("id").getValue().toString().equals(loan_id)) {
                        loanDetailsForPDF.setLoanId(ds.child("id").getValue().toString());
                        customerId = ds.child("Customer_Id").getValue().toString();
                        loanDetailsForPDF.setCustomerID(customerId);

                        loanDetailsForPDF.setApplicationNo(ds.child("Applicantion_Number").getValue().toString());
                        loanDetailsForPDF.setDateApplied(ds.child("Date_Applied").getValue().toString());
                        loanDetailsForPDF.setDeposit("0");
                        loanDetailsForPDF.setLoanAmount(ds.child("Loan_Amount").getValue().toString());

                        Query queryName = rootRef.child("Customers").orderByKey();
                        // databaseReference.keepSynced(true);
                        queryName.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                            if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                String name = npsnapshot1.child("name").getValue().toString();
                                                Log.e("Name  ", name);
                                                loanDetailsForPDF.setCustomerName(name);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e("Ex   ", e.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("error ", error.getMessage());
                            }
                        });


                        String loanType = ds.child("Loan_Type").getValue().toString();
                        loanDetailsForPDF.setLoanType(ds.child("Loan_Type").getValue().toString());
                        if (loanType.equals("1")) {
                            txt_loan_type.setText("Regular");
                        } else if (loanType.equals("2")) {
                            txt_loan_type.setText("EMI");
                        } else if (loanType.equals("3")) {
                            txt_loan_type.setText("Daily");
                        }


                        totalPaidAmount = 0;
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("EMI_Received").orderByKey();
                        query.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        itemArray_EMI.clear();
                                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                            if (npsnapshot.child("Loan_Id").getValue().toString().equals(loan_id)) {

                                                totalPaidAmount += Integer.parseInt(npsnapshot.child("EMI_Amount").getValue().toString());

                                                Items_View_EMI item = new Items_View_EMI();
                                                item.setEmi_id(npsnapshot.child("id").getValue().toString());
                                                item.setEmi_date(npsnapshot.child("EMI_Date").getValue().toString());
                                                item.setEmi_amount(npsnapshot.child("EMI_Amount").getValue().toString());
                                                item.setEmi_remark(npsnapshot.child("Loan_Remarks").getValue().toString());
                                                item.setEmi_type(npsnapshot.child("Type").getValue().toString());
                                                itemArray_EMI.add(item);

                                            }
                                        }

                                        loanDetailsForPDF.setInterest(String.valueOf(totalPaidAmount));

                                        int totalPayableAmount = Integer.parseInt(ds.child("Payable_Amount").getValue().toString());
                                        int unpaidAmount = totalPayableAmount - totalPaidAmount;
                                        txtPendingLoanAmount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Approved_Amount").getValue().toString())));
                                        loanDetailsForPDF.setPendingAmount(ds.child("Approved_Amount").getValue().toString());
                                        loanDetailsForPDF.setApprovedAmount(ds.child("Approved_Amount").getValue().toString());
                                        txtPaidAmountTillNow.setText(ruppe + formatter.format(Helper.getRoundValue(totalPaidAmount)));

                                        if (itemArray_EMI.size() > 0) {
                                            tvViewPaymentPDF.setVisibility(View.VISIBLE);
                                        } else {
                                            tvViewPaymentPDF.setVisibility(View.GONE);
                                        }
                                        /*mAdapter = new Adapter_View_Emi(mActivity, itemArray_EMI);
                                        listview_EMI.setAdapter(mAdapter);*/

                                        Collections.sort(itemArray_EMI, new Comparator<Items_View_EMI>() {
                                            @Override
                                            public int compare(Items_View_EMI o1, Items_View_EMI o2) {
                                                return o1.getEmi_date().compareTo(o2.getEmi_date());
                                            }
                                        }.reversed());

                                        mAdapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    Log.e("Test  ", e.getMessage());
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                                progressDialog.dismiss();
                                new CToast(Admin_LoanDetail_Activity.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        });


                        if (txt_loan_type.getText().toString().trim().equals("EMI")) {
                            LOAN_TYPE = Helper.LOAN_TYPE_EMI;
                        } else {
                            LOAN_TYPE = Helper.LOAN_TYPE_SIMPLE;
                        }

                        String lStatus = ds.child("Loan_Status").getValue().toString();
                        loanDetailsForPDF.setLoanStatus(lStatus);
                        String status = "";
                        if (lStatus.equals("1")) {
                            status = "Pending";
                            txt_loan_status.setText(status);
                            txt_loan_status.setTextColor(getLoanStatusColor(status.toLowerCase()));
                        } else if (lStatus.equals("2")) {
                            status = "Approved";
                            txt_loan_status.setText(status);
                            txt_loan_status.setTextColor(getLoanStatusColor(status.toLowerCase()));
                        } else if (lStatus.equals("3")) {
                            status = "Rejected";
                            txt_loan_status.setText(status);
                            txt_loan_status.setTextColor(getLoanStatusColor(status.toLowerCase()));
                        } else if (lStatus.equals("4")) {
                            status = "Completed";
                            txt_loan_status.setText(status);
                            txt_loan_status.setTextColor(getLoanStatusColor(status.toLowerCase()));
                        }


                        txt_loan_date.setText(ds.child("Date_Applied").getValue().toString());
                        txt_loan_interest.setText(ds.child("Interest_Rate").getValue().toString() + "%");
                        loanDetailsForPDF.setInterestRate(ds.child("Interest_Rate").getValue().toString());
                        txt_loan_reason.setText(ds.child("Reason_For_Loan").getValue().toString());
                        loanDetailsForPDF.setReasonForLoan(ds.child("Reason_For_Loan").getValue().toString());
                        txt_loan_amount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Loan_Amount").getValue().toString())));
                        txtTotalEmi.setText(ds.child("Loan_Tenure").getValue().toString());
                        loanDetailsForPDF.setLoanTenure(ds.child("Loan_Tenure").getValue().toString());
                        loanDetailsForPDF.setPayEMIDate(ds.child("Pay_EMI_Date").getValue().toString());

                        if (status.equals("Pending")) {
                            viewPendingContent.setVisibility(View.VISIBLE);
                        } else {
                            viewPendingContent.setVisibility(View.GONE);
                        }
                        if (status.equals("Approved")) {
                            viewApproveContent.setVisibility(View.VISIBLE);
                            btnCloseLoan.setVisibility(View.VISIBLE);
                            fab_AddEmi.setVisibility(View.VISIBLE);

                            SimpleDateFormat input = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
                            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy hh:mm");

                            try {
                                Date oneWayTripDate;
                                oneWayTripDate = input.parse(ds.child("Approved_Date").getValue().toString());  // parse input
                                txt_loan_approve_date.setText(output.format(oneWayTripDate));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            txt_approved_amount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Original_Amount").getValue().toString())));
                            if (!(ds.child("Monthly_EMI").getValue().toString().equals(""))) {
                                txt_monthlu_emi.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Monthly_EMI").getValue().toString())));
                                getEMI_AMOUNT = formatter.format(Integer.parseInt(ds.child("Monthly_EMI").getValue().toString()));
                            }

                            //txt_pay_emi_date.setText(Html.fromHtml("EMI date : <b>" + jObj_Details.getString("Pay_EMI_Date") + "</b>"));
                            txt_pay_emi_date.setText(Html.fromHtml(" <b>" + ds.child("Pay_EMI_Date").getValue().toString() + " of every month" + "</b>"));
                        } else {
                            btnCloseLoan.setVisibility(View.GONE);
                            fab_AddEmi.setVisibility(View.GONE);
                            viewApproveContent.setVisibility(View.GONE);
                        }
                        if (status.equals("Rejected")) {
                            viewRejectContent.setVisibility(View.VISIBLE);
                            txtLoanRejectRemarks.setText(ds.child("Reject_Remarks").getValue().toString());
                        } else {
                            viewRejectContent.setVisibility(View.GONE);
                        }
                        if (status.equals("Completed")) {

                            btnCloseLoan.setVisibility(View.GONE);
                            viewApproveContent.setVisibility(View.VISIBLE);
                            fab_AddEmi.setVisibility(View.GONE);

                            SimpleDateFormat input = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
                            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy hh:mm");

                            try {
                                Date oneWayTripDate;
                                oneWayTripDate = input.parse(ds.child("Approved_Date").getValue().toString());  // parse input
                                txt_loan_approve_date.setText(output.format(oneWayTripDate));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            txt_loan_amount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Loan_Amount").getValue().toString())));
                            txt_approved_amount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Original_Amount").getValue().toString())));

                            loanDetailsForPDF.setApprovedAmount(ds.child("Original_Amount").getValue().toString());

                            txtPendingLoanAmount.setText(ruppe + formatter.format(Integer.parseInt(ds.child("Approved_Amount").getValue().toString())));

                            if (!(ds.child("Monthly_EMI").getValue().toString().equals(""))) {
                                txt_monthlu_emi.setText(Html.fromHtml("EMI : <b>" + ruppe + formatter.format(Integer.parseInt(ds.child("Monthly_EMI").getValue().toString())) + "</b>"));
                                getEMI_AMOUNT = formatter.format(Integer.parseInt(ds.child("Monthly_EMI").getValue().toString()));
                            }

                            txt_pay_emi_date.setText(Html.fromHtml(" <b>" + ds.child("Pay_EMI_Date").getValue().toString() + " of every month" + "</b>"));

                        }
                        mainView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.d("databaseError ", databaseError.getMessage());
            }
        };
        circleMemebersRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void switchView(boolean status) {
        if (status) {
            viewLoanInstallment.setVisibility(View.GONE);
            viewLoanDetail.setVisibility(View.VISIBLE);

            btnLoanDetail.setBackground(getResources().getDrawable(R.drawable.bg_loan_detail_tab));
            txtPaidAmountTillNowLabel.setTextColor(getResources().getColor(R.color.text_color1));
            txtPaidAmountTillNow.setTextColor(getResources().getColor(R.color.text_color1));
            txtPendingLoanAmountLabel.setTextColor(getResources().getColor(R.color.whiteText1));
            txtPendingLoanAmount.setTextColor(getResources().getColor(R.color.whiteText1));
            btnLoanInstallment.setBackgroundColor(getResources().getColor(R.color.transperent));


        } else {
            viewLoanDetail.setVisibility(View.GONE);
            viewLoanInstallment.setVisibility(View.VISIBLE);

            btnLoanInstallment.setBackground(getResources().getDrawable(R.drawable.bg_loan_detail_tab));
            txtPendingLoanAmountLabel.setTextColor(getResources().getColor(R.color.text_color1));
            txtPendingLoanAmount.setTextColor(getResources().getColor(R.color.text_color1));
            txtPaidAmountTillNowLabel.setTextColor(getResources().getColor(R.color.whiteText1));
            txtPaidAmountTillNow.setTextColor(getResources().getColor(R.color.whiteText1));
            btnLoanDetail.setBackgroundColor(getResources().getColor(R.color.transperent));

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.edit_loan:
                Intent intent = new Intent(Admin_LoanDetail_Activity.this, Activity_Edit_Loan.class);
                intent.putExtra("loanDetails", loanDetailsForPDF);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getLoanStatusColor(String status) {

        if (status.equals("pending")) {
            return getResources().getColor(R.color.pending_bg);
        } else if (status.equals("rejected")) {
            return getResources().getColor(R.color.reject_bg);
        } else {
            return getResources().getColor(R.color.approw_bg);
        }
    }

    double final_amount = 0.0, aEmi = 0.0;
    String SelectedEmiDate = null;

    public void ApprovedDialog() {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_admin_approved_loan, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        TextView txtSubtitle = (TextView) promptsView.findViewById(R.id.txt_approved_loan_dialog_sub_title);
        final EditText txtSelectEmi = (EditText) promptsView.findViewById(R.id.txt_approved_loan_dialog_select_emi_date);
        final EditText txtApproveDate = (EditText) promptsView.findViewById(R.id.txt_approved_loan_dialog_select_approve_date);
        final EditText edt_amount = (EditText) promptsView.findViewById(R.id.edt_enter_loan_amount);
        final EditText edt_interest = (EditText) promptsView.findViewById(R.id.edt_enter_loan_rate);
        final EditText edt_months = (EditText) promptsView.findViewById(R.id.edt_enter_loan_months);
        final Button btnCalculate = (Button) promptsView.findViewById(R.id.btn_approved_loan_dialog_calculate);
        final Button btnAdd = (Button) promptsView.findViewById(R.id.btn_approved_loan_dialog_add);
        final View viewBottom = promptsView.findViewById(R.id.view_approved_loan_dialog_bottom);
        final View viewTenure = promptsView.findViewById(R.id.view_approved_loan_dialog_tenure);
        final TextView txt_Totalpayble = (TextView) promptsView.findViewById(R.id.txt_paybale_amount);
        final TextView txt_Emi = (TextView) promptsView.findViewById(R.id.txt_paybale_monthly_emi);
        final RadioButton rdbSimple = (RadioButton) promptsView.findViewById(R.id.rdb_approved_loan_dialog_simple);
        final RadioButton rdbEmi = (RadioButton) promptsView.findViewById(R.id.rdb_approved_loan_dialog_emi);
        final RadioButton rdbDaily = (RadioButton) promptsView.findViewById(R.id.rdb_approved_loan_dialog_daily);

        txtSubtitle.setText("( Requested amount is " + txt_loan_amount.getText().toString().trim() + " )");
        edt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnAdd.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
            }
        });
        edt_interest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnAdd.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
            }
        });
        edt_months.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnAdd.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
            }
        });
        rdbEmi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAdd.setVisibility(View.GONE);
                    viewBottom.setVisibility(View.GONE);
                }

            }
        });
        rdbDaily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewBottom.setVisibility(View.GONE);
                    btnAdd.setVisibility(View.GONE);
                    btnCalculate.setText("Ok");
                    edt_interest.setEnabled(false);
                } else {
                    edt_interest.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);
                    btnCalculate.setText("Calculate");
                    edt_interest.setEnabled(true);
                }

            }
        });
        rdbSimple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnAdd.setVisibility(View.GONE);
                    viewBottom.setVisibility(View.GONE);
                    edt_months.setEnabled(false);
                } else
                    edt_months.setEnabled(true);
            }
        });

        txtSelectEmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectEmiDateDialog(txtSelectEmi);
            }
        });
        txtApproveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pick_date(txtApproveDate, 1);
            }
        });
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean loanType = true;
                if (rdbSimple.isChecked())
                    loanType = false;


                if (TextUtils.isEmpty(edt_amount.getText().toString().trim())) {
                    edt_amount.setError(Html.fromHtml("<font color='#ffffff'>Enter Amount</font>"));
                    edt_amount.requestFocus();
                    return;
                } else if (!rdbDaily.isChecked()) {
                    if (TextUtils.isEmpty(edt_interest.getText().toString().trim())) {
                        edt_interest.setError(Html.fromHtml("<font color='#ffffff'>Enter Interest</font>"));
                        edt_interest.requestFocus();
                        return;
                    }
                } else if (TextUtils.isEmpty(edt_months.getText().toString().trim())) {
                    if (loanType) {
                        edt_months.setError(Html.fromHtml("<font color='#ffffff'>Enter Months</font>"));
                        edt_months.requestFocus();
                        return;
                    }
                }

                if (!rdbDaily.isChecked()) {
                    double lAMOUNT = Double.parseDouble(edt_amount.getText().toString().trim());
                    double lTENURE = ((edt_months.getText().toString().trim().length() == 0) ? 0.0 : Double.parseDouble(edt_months.getText().toString().trim()));
                    double lRATE = Double.parseDouble(edt_interest.getText().toString().trim());

                    btnAdd.setVisibility(View.VISIBLE);
                    viewBottom.setVisibility(View.VISIBLE);

                    if (loanType) {//EMI LOAN

                        double COUNT_EMI = Helper.GetEMI(lAMOUNT, lRATE, lTENURE);
                        double TOTAL_PAYABLE = COUNT_EMI * lTENURE;

                        final_amount = TOTAL_PAYABLE;
                        aEmi = COUNT_EMI;

                        txt_Totalpayble.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
                        txt_Emi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(COUNT_EMI)));

                    } else {//SIMPLE LOAN

                        double INTEREST_PAYABLE = (lAMOUNT * lRATE) / (100);
                        //double TOTAL_PAYABLE = (INTEREST_PAYABLE * lTENURE) + lAMOUNT;

                        aEmi = INTEREST_PAYABLE;
                        //final_amount = TOTAL_PAYABLE;
                        final_amount = 0.0;

                        //txt_Totalpayble.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
                        txt_Totalpayble.setText("-");
                        txt_Emi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE)));
                    }
                } else {
                    btnAdd.setVisibility(View.VISIBLE);
                }


            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rdbDaily.isChecked()) {
                    if (TextUtils.isEmpty(edt_amount.getText().toString().trim())) {
                        edt_amount.setError(Html.fromHtml("<font color='#ffffff'>Enter Amount</font>"));
                        edt_amount.requestFocus();
                        return;
                    } else if (TextUtils.isEmpty(edt_months.getText().toString().trim())) {
                        edt_months.setError(Html.fromHtml("<font color='#ffffff'>Enter Months</font>"));
                        edt_months.requestFocus();
                        return;
                    }
                }
                if (APPROVE_LOAN_DATE == null) {
                    Toast.makeText(mActivity, "Select Approve date", Toast.LENGTH_SHORT).show();
                    Helper.Vibrate(mActivity, 50);
                    Animation shake = AnimationUtils.loadAnimation(mActivity, R.anim.shake);
                    txtApproveDate.startAnimation(shake);
                    return;
                } else if (SelectedEmiDate == null) {
                    Toast.makeText(mActivity, "Select EMI date", Toast.LENGTH_SHORT).show();
                    Helper.Vibrate(mActivity, 50);
                    Animation shake = AnimationUtils.loadAnimation(mActivity, R.anim.shake);
                    txtSelectEmi.startAnimation(shake);
                    return;
                }

                String loanType = null;
                if (rdbSimple.isChecked()) {
                    loanType = Helper.LOAN_TYPE_SIMPLE;
                } else if (rdbEmi.isChecked()) {
                    loanType = Helper.LOAN_TYPE_EMI;
                } else {
                    loanType = Helper.LOAN_TYPE_DAILY;
                }

                //json
            }
        });

        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public void SelectEmiDateDialog(final TextView textView) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_admin_select_emi_date, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        Button btnOk = (Button) promptsView.findViewById(R.id.btn_select_emi_date_ok);
        Button btnCancel = (Button) promptsView.findViewById(R.id.btn_select_emi_date_cancel);
        final NumberPicker numberPicker = (NumberPicker) promptsView.findViewById(R.id.number_picker_emi_date);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);
        numberPicker.setValue(1);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = String.valueOf(numberPicker.getValue());
                textView.setText(Html.fromHtml("Emi pay date is <b>" + value + "</b> on every months"));
                SelectedEmiDate = value;
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void RejectDialog() {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_admin_reject_loan, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtRemarks = (EditText) promptsView.findViewById(R.id.edt_reject_loan_dialog_remarks);
        Button btnReject = (Button) promptsView.findViewById(R.id.btn_reject_loan_dialog_reject);
        Button btnCancel = (Button) promptsView.findViewById(R.id.btn_reject_loan_dialog_cancel);

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edtRemarks.getText().toString().trim())) {
                    edtRemarks.setError("Enter remarks ( why you reject this loan? )");
                    edtRemarks.requestFocus();
                    return;
                }

                //json

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public void AddEmiDialog() {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_admin_pay_emi, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edt_amount = (EditText) promptsView.findViewById(R.id.edt_emi_amount);
        final EditText edt_date = (EditText) promptsView.findViewById(R.id.edt_emi_date);
        final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_emi_remark);
        final Button btn_pay = (Button) promptsView.findViewById(R.id.btn_pay_emi);
        final Button btn_add_share_payment = (Button) promptsView.findViewById(R.id.btn_add_share_payment);
        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_pay_emi_cancel);
        final RadioButton Rbtn_Interest = (RadioButton) promptsView.findViewById(R.id.rbtn_installmenttype_interest);
        final RadioButton Rbtn_Deposit = (RadioButton) promptsView.findViewById(R.id.rbtn_installmenttype_deposit);
        final LinearLayout LayoutType = (LinearLayout) promptsView.findViewById(R.id.layout_installmenttype);

        String loan_type = txt_loan_type.getText().toString();

        if (loan_type.equals("Regular")) {

            LayoutType.setVisibility(View.VISIBLE);
        } else {
            LayoutType.setVisibility(View.GONE);
        }

        Rbtn_Interest.setChecked(true);
        InstallMent_Type = Rbtn_Interest.getText().toString();

        Rbtn_Deposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                InstallMent_Type = Rbtn_Interest.getText().toString();
            }
        });

        Rbtn_Interest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                InstallMent_Type = Rbtn_Deposit.getText().toString();
            }
        });


        String getEmi = getEMI_AMOUNT.toString();
        getEmi = getEmi.replace(",", "");
        edt_amount.setText(getEmi);


        edt_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Pick_date(edt_date, 0);
                }
            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pick_date(edt_date, 0);
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edt_date.getText().toString())) {
                    edt_date.setError("Select Date");
                    edt_date.requestFocus();
                } else if (TextUtils.isEmpty(edt_amount.getText().toString().trim())) {
                    edt_amount.setError("Enter Amount");
                    edt_amount.requestFocus();
                } else if (Integer.parseInt(edt_amount.getText().toString().trim()) == 0) {
                    edt_amount.setError("Amount must be greater than 0(Zero)");
                    edt_amount.requestFocus();
                } else {

                    //error : ek field no problem che -InstallMent_Type
                    //showPrd();


                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    String key = rootRef.child("EMI_Received").push().getKey();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Customer_Id", customerId);
                    map.put("EMI_Amount", edt_amount.getText().toString());
                    emiAmount = edt_amount.getText().toString();
                    DateFormat df = new SimpleDateFormat("HH:mm:ss"); // Format time
                    String time = df.format(Calendar.getInstance().getTime());

                    map.put("EMI_Date", EMI_date_format + " " + time);
                    map.put("Loan_Id", loan_id);
                    map.put("Loan_Remarks", edt_remark.getText().toString().trim());
                    map.put("Loan_Type", LOAN_TYPE);
                    map.put("Type", InstallMent_Type);
                    map.put("id", key);

                    Log.e("Loan_Type  ", LOAN_TYPE);
                    Log.e("Type  ", InstallMent_Type);

                    rootRef.child("EMI_Received").child(key).setValue(map).addOnCompleteListener(Admin_LoanDetail_Activity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alertDialog.dismiss();
                            new CToast(mActivity).simpleToast("EMI Amount Added", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                            getData();
                        }

                    }).addOnFailureListener(Admin_LoanDetail_Activity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertDialog.dismiss();
                            Toast.makeText(Admin_LoanDetail_Activity.this, "Please try later...", Toast.LENGTH_SHORT).show();
                        }

                    });


                    if (InstallMent_Type.equals("Deposit")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("LoanDetails").orderByKey();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot npsnapshot) {
                                try {
                                    if (npsnapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                            if (dataSnapshot.child("id").getValue().toString().equals(loan_id)) {

                                                String ApprovedAmount = dataSnapshot.child("Approved_Amount").getValue().toString();
                                                String interestS = dataSnapshot.child("Interest_Rate").getValue().toString();

                                                int pendingAmt = (Integer.parseInt(ApprovedAmount)) - (Integer.parseInt(emiAmount));

                                                int interest = ((Integer.parseInt(interestS)) / 100);
                                                int monthlyEmi = interest * pendingAmt;


                                                DatabaseReference cineIndustryRef = databaseReference.child("LoanDetails").child(dataSnapshot.getKey());

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("Monthly_EMI", monthlyEmi);
                                                map.put("Approved_Amount", pendingAmt);
                                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (alertDialog.isShowing()) {
                                                            alertDialog.dismiss();
                                                        }

                                                        //new CToast(mActivity).simpleToast("EMI Amount Added", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        getData();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
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
                                Log.e("databaseError   ", databaseError.getMessage());
                            }
                        });
                    }
                }
            }
        });

        btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edt_date.getText().toString())) {
                    edt_date.setError("Select Date");
                    edt_date.requestFocus();
                } else if (TextUtils.isEmpty(edt_amount.getText().toString().trim())) {
                    edt_amount.setError("Enter Amount");
                    edt_amount.requestFocus();
                } else if (Integer.parseInt(edt_amount.getText().toString().trim()) == 0) {
                    edt_amount.setError("Amount must be greater than 0(Zero)");
                    edt_amount.requestFocus();
                } else {

                    //error : ek field no problem che -InstallMent_Type
                    //showPrd();


                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    String key = rootRef.child("EMI_Received").push().getKey();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Customer_Id", customerId);
                    map.put("EMI_Amount", edt_amount.getText().toString());
                    emiAmount = edt_amount.getText().toString();
                    DateFormat df = new SimpleDateFormat("HH:mm:ss"); // Format time
                    String time = df.format(Calendar.getInstance().getTime());

                    map.put("EMI_Date", EMI_date_format + " " + time);
                    map.put("Loan_Id", loan_id);
                    map.put("Loan_Remarks", edt_remark.getText().toString().trim());
                    map.put("Loan_Type", LOAN_TYPE);
                    map.put("Type", InstallMent_Type);
                    map.put("id", key);

                    Log.e("Loan_Type  ", LOAN_TYPE);
                    Log.e("Type  ", InstallMent_Type);

                    rootRef.child("EMI_Received").child(key).setValue(map).addOnCompleteListener(Admin_LoanDetail_Activity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alertDialog.dismiss();
                            new CToast(mActivity).simpleToast("EMI Amount Added", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
//                            if (appInstalledOrNot() == 0) {
//                                sendMessage(edt_amount.getText().toString(), "com.whatsapp");
//                            } else if (appInstalledOrNot() == 1) {
//                                sendMessage(edt_amount.getText().toString(), "com.whatsapp.w4b");
//                            } else if (appInstalledOrNot() == 2) {
//                                sendMessage(edt_amount.getText().toString(), "both");
//                            }
                            sendMessage(edt_amount.getText().toString());
                            getData();
                        }

                    }).addOnFailureListener(Admin_LoanDetail_Activity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            alertDialog.dismiss();
                            Toast.makeText(Admin_LoanDetail_Activity.this, "Please try later...", Toast.LENGTH_SHORT).show();
                        }

                    });


                    if (InstallMent_Type.equals("Deposit")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("LoanDetails").orderByKey();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot npsnapshot) {
                                try {
                                    if (npsnapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                            if (dataSnapshot.child("id").getValue().toString().equals(loan_id)) {

                                                String ApprovedAmount = dataSnapshot.child("Approved_Amount").getValue().toString();
                                                String interestS = dataSnapshot.child("Interest_Rate").getValue().toString();

                                                int pendingAmt = (Integer.parseInt(ApprovedAmount)) - (Integer.parseInt(emiAmount));

                                                int interest = ((Integer.parseInt(interestS)) / 100);
                                                int monthlyEmi = interest * pendingAmt;


                                                DatabaseReference cineIndustryRef = databaseReference.child("LoanDetails").child(dataSnapshot.getKey());

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("Monthly_EMI", monthlyEmi);
                                                map.put("Approved_Amount", pendingAmt);
                                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (alertDialog.isShowing()) {
                                                            alertDialog.dismiss();
                                                        }

                                                        //new CToast(mActivity).simpleToast("EMI Amount Added", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        getData();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
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
                                Log.e("databaseError   ", databaseError.getMessage());
                            }
                        });
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    String EMI_date_format = null, APPROVE_LOAN_DATE = null;

    public void Pick_date(final EditText editText, final int action) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.pick_dilog, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final DatePicker datePicker = (DatePicker) promptsView.findViewById(R.id.datePicker);

        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        final Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int dmonth = datePicker.getMonth();
                dmonth++;
                int dday = datePicker.getDayOfMonth();
                int dyear = datePicker.getYear();

                alertDialog.dismiss();
                if (action == 0) {
                    EMI_date_format = dyear + "-" + dmonth + "-" + dday;
                    editText.setText("" + dday + " / " + dmonth + " / " + dyear);
                } else {
                    APPROVE_LOAN_DATE = dyear + "-" + dmonth + "-" + dday;
                    editText.setText("" + dday + " / " + dmonth + " / " + dyear);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });

        alertDialog.show();

    }

    private void sendMessage(String amount) {
//        Intent waIntent = new Intent(Intent.ACTION_SEND);
//        waIntent.setType("text/plain");
        String text = "";
        text = "Dear  '" + applicantName + "'\n" + "Your EMI has been credited " + getResources().getString(R.string.rupee) + amount + " to " + getResources().getString(R.string.app_name) + ".\n" + "\nThanks";

        PackageManager pm=getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
//        if (pkg.equalsIgnoreCase("both")) {
//            showBottomSheet(text);
//        } else {
//            waIntent.setPackage(pkg);
//            if (waIntent != null) {
//                waIntent.putExtra(Intent.EXTRA_TEXT, text);
//                startActivity(Intent.createChooser(waIntent, text));
//            } else {
//                Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
    }

    public void showBottomSheet(String text) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                new ActionBottomDialogFragment(text);
        addPhotoBottomDialogFragment.show(this.getSupportFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

    @Override
    public void onItemClick(View view,String text) {
        if (view.getId() == R.id.button1) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else if (view.getId() == R.id.button2) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp.w4b");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int appInstalledOrNot() {
        String pkgW = "com.whatsapp";
        String pkgWB = "com.whatsapp.w4b";
        PackageManager pm = getPackageManager();
        int app_installed_whatsapp = -1;
        int app_installed_w4b = -1;
        int common = -1;
        try {
            pm.getPackageInfo(pkgW, PackageManager.GET_ACTIVITIES);
            app_installed_whatsapp = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_whatsapp = 0;
        }

        try {
            pm.getPackageInfo(pkgWB, PackageManager.GET_ACTIVITIES);
            app_installed_w4b = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_w4b = 0;
        }

        if (app_installed_w4b == 1 & app_installed_whatsapp == 1) {
            common = 2;
        } else if (app_installed_whatsapp == 1) {
            common = 0;
        } else if (app_installed_w4b == 1) {
            common = 1;
        }

        return common;
    }

    @Override
    public void onRefresh() {
        getData();
    }
}
