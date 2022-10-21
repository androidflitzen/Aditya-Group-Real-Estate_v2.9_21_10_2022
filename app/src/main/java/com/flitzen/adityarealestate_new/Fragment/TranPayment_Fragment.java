package com.flitzen.adityarealestate_new.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Activity.Activity_Plot_Details;
import com.flitzen.adityarealestate_new.Activity.Add_fab_Payment_Activity;
import com.flitzen.adityarealestate_new.Activity.PdfCreatorRentActivity;
import com.flitzen.adityarealestate_new.Activity.PdfCreatorTransactionActivity;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Activity.TransactionDetails_Activity;
import com.flitzen.adityarealestate_new.Activity.ViewPdfForAll;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Trans_PaymentList;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.PDFUtility;
import com.flitzen.adityarealestate_new.PDFUtility_Transaction;
import com.flitzen.adityarealestate_new.R;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class TranPayment_Fragment extends Fragment {

    Adapter_Trans_PaymentList adapterTransPaymentList;


    String rent_amount = "", customer_id = "", customer_name = "", phoneNo = "";
    int position;

    Activity mActivity;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();

    WebAPI webapi;

    String file_url = "";
    String path;

    Unbinder unbinder;

    RecyclerView rvTransPayment;
    @BindView(R.id.ivTransPaymentPdf)
    RelativeLayout ivTransPaymentPdf;
    @BindView(R.id.fab_addTransPayment)
    FloatingActionButton fabAddTransPayment;
    @BindView(R.id.tvTotalPay)
    TextView tvTotalPay;
    @BindView(R.id.tvTotalReceive)
    TextView tvTotalReceive;
    @BindView(R.id.linMainText)
    LinearLayout linMainText;

   /* @BindView(R.id.rvTransPayment)
    RecyclerView rvTransPayment;*/

    TextView tvNoPayment;

    String PaymentTotltal = "0";
    String receivedTotltal = "0";

    @SuppressLint("ValidFragment")
    public TranPayment_Fragment(String customer_id, int position, String customer_name, String contact_no) {

        this.customer_id = customer_id;
        this.position = position;
        this.customer_name = customer_name;
        this.phoneNo = contact_no;

    }

    public TranPayment_Fragment() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmenttrans_payment, null);
        unbinder = ButterKnife.bind(this, view);
        initUI(view);

        return view;


        // customer_id = getArguments().getString(customer_id) ;


    }

    private void initUI(View view) {

        mActivity = getActivity();

        rvTransPayment = (RecyclerView) view.findViewById(R.id.rvTransPayment);
        rvTransPayment.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransPayment.setHasFixedSize(true);
        adapterTransPaymentList = new Adapter_Trans_PaymentList(mActivity, transactionlist, false);
        rvTransPayment.setAdapter(adapterTransPaymentList);

        //   Utils.showLog("cusrt" + customer_id);

        tvNoPayment = (TextView) view.findViewById(R.id.tvNoPayment);

        ivTransPaymentPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent=new Intent(getActivity(), PdfCreatorTransactionActivity.class);
                intent.putExtra("customer_name",customer_name);
                intent.putExtra("phone_no","");
                intent.putExtra("paymentList",transactionlist);
                startActivity(intent);*/

                int finalTotalAmount = 0;
                for (int i = 0; i < transactionlist.size(); i++) {
                    if (transactionlist.get(i).getAmount() != null && !(transactionlist.get(i).getAmount().equals(""))) {
                        finalTotalAmount = finalTotalAmount + Integer.parseInt(transactionlist.get(i).getAmount());
                    }
                }

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
//                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + ts + "_payment_list.pdf";
                } else {
                    path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                }
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Transaction.createPdf(v.getContext(), new PDFUtility_Transaction.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                            // Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path", path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, customer_name, phoneNo, finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabAddTransPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, Add_fab_Payment_Activity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                //    intent.putExtra("type2", transactionlist.get(1).getPaymentType());
                mActivity.startActivity(intent);


             /*   startActivity(new Intent(TransactionDetails_Activity.this, Add_fab_Transaction_Activity.class));
                TransactionDetails_Activity.this.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/
            }
        });
    }

    /*@Override
    public void onPDFDocumentClose(File file) {
        Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
        intent.putExtra("path",path);
        startActivity(intent);
    }*/

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount = 0;
        for (int i = 0; i < transactionlist.size(); i++) {

            String data1 = "";
            String data2 = "";
            String data3 = "";
            String data4 = "";


            if (transactionlist.get(i).getTransactionDate() != null) {
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                // SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                // SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
                try {
                    Date oneWayTripDate;
                    Date oneWayTripDateT;
                    oneWayTripDate = input.parse(transactionlist.get(i).getTransactionDate());  // parse input
                    // oneWayTripDateT = inputT.parse(transactionlist.get(i).getTransactionTime());  // parse input
                    data1 = (output.format(oneWayTripDate));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            if (transactionlist.get(i).getPaymentType() != null) {
                if (transactionlist.get(i).getPaymentType().equals("1")) {
                    data2 = "Cash Payment";
                } else {
                    data2 = "Cash Received";
                }
            }

            data3 = transactionlist.get(i).getTransactionNote();
            data4 = Helper.getFormatPrice(Integer.parseInt(transactionlist.get(i).getAmount()));

            temp.add(new String[]{data1, data2, data3, data4});
        }
        return temp;
    }


    private void getPaymentlist() {

        System.out.println("=========getPaymentlist   " + customer_id);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlist.clear();
                        transactionlistTemp.clear();
                        tvNoPayment.setVisibility(View.GONE);
                        int PaymentTotltal1 = 0;
                        int PaymentTotltal2 = 0;
                        for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                            if (npsnapshot5.child("customer_id").getValue().toString().equals(customer_id)) {
                                //if(npsnapshot5.child("payment_type").getValue().toString().equals("1")){
                                Transcation item1 = new Transcation();
                                item1.setTransactionId(npsnapshot5.child("transaction_id").getValue().toString());
                                item1.setCustomerId(npsnapshot5.child("customer_id").getValue().toString());
                                item1.setPaymentType(npsnapshot5.child("payment_type").getValue().toString());
                                item1.setTransactionDate(npsnapshot5.child("transaction_date").getValue().toString());
                                item1.setTransactionNote(npsnapshot5.child("transaction_note").getValue().toString());
                                item1.setAmount(npsnapshot5.child("amount").getValue().toString());

                                if (item1.getPaymentType().equals("1")) {
                                    PaymentTotltal1 = PaymentTotltal1 + Integer.parseInt(npsnapshot5.child("amount").getValue().toString());
                                    PaymentTotltal = String.valueOf(PaymentTotltal1);
                                }
                                else {
                                    PaymentTotltal2 = PaymentTotltal2 + Integer.parseInt(npsnapshot5.child("amount").getValue().toString());
                                    receivedTotltal = String.valueOf(PaymentTotltal2);
                                }


                                Query queryCustomer = databaseReference.child("Transacation_Customers").orderByKey();
                                queryCustomer.addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshotCustomer : dataSnapshot.getChildren()) {
                                                    if (npsnapshotCustomer.child("id").getValue().toString().equals(customer_id)) {
                                                        String name = npsnapshotCustomer.child("name").getValue().toString();
                                                        item1.setCustomerName(name);
                                                    }
                                                }

                                                Collections.sort(transactionlist, new Comparator<Transcation>() {
                                                    @Override
                                                    public int compare(Transcation o1, Transcation o2) {
                                                        return o1.getTransactionDate().compareTo(o2.getTransactionDate());
                                                    }
                                                }.reversed());

                                                adapterTransPaymentList.notifyDataSetChanged();
                                                tvNoPayment.setVisibility(View.GONE);
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
                                transactionlist.add(item1);
                                transactionlistTemp.add(item1);
                                tvNoPayment.setVisibility(View.GONE);
                                //  }
                            }
                        }

                        if (transactionlist.size() > 0) {
                            rvTransPayment.setVisibility(View.VISIBLE);
                          //  ivTransPaymentPdf.setVisibility(View.VISIBLE);
                            linMainText.setVisibility(View.VISIBLE);
                            tvNoPayment.setVisibility(View.GONE);
                        } else {
                            rvTransPayment.setVisibility(View.GONE);
                           // ivTransPaymentPdf.setVisibility(View.GONE);
                            linMainText.setVisibility(View.GONE);
                            tvNoPayment.setVisibility(View.VISIBLE);
                        }

                        Collections.sort(transactionlist, new Comparator<Transcation>() {
                            @Override
                            public int compare(Transcation o1, Transcation o2) {
                                return o1.getTransactionDate().compareTo(o2.getTransactionDate());
                            }
                        }.reversed());

                        adapterTransPaymentList.notifyDataSetChanged();


                        if (PaymentTotltal.equals("0")) {
                            TransactionDetails_Activity.tvTransCustPayment.setText("--------");
                            tvTotalPay.setText("--------");
                        } else {
                            TransactionDetails_Activity.tvTransCustPayment.setText(getResources().getString(R.string.rupee)+" "+PaymentTotltal);
                            tvTotalPay.setText(getResources().getString(R.string.rupee)+" "+PaymentTotltal);
                        }

                        if (receivedTotltal.equals("0")) {
                            tvTotalReceive.setText("--------");
                        } else {
                            tvTotalReceive.setText(getResources().getString(R.string.rupee)+" "+receivedTotltal);
                        }

                    } else {
                        rvTransPayment.setVisibility(View.GONE);
                        tvNoPayment.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        getPaymentlist();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
