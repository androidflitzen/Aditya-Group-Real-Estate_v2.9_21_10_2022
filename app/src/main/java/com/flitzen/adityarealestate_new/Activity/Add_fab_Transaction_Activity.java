package com.flitzen.adityarealestate_new.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Transaction_List;
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Fragment.ActionBottomDialogFragment;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Add_fab_Transaction_Activity extends AppCompatActivity implements ActionBottomDialogFragment.ItemClickListener{


    @BindView(R.id.tvTransCompanySpn)
    TextView tvTransCompanySpn;
    @BindView(R.id.rvTranCompnyspn)
    RelativeLayout rvTranCompnyspn;
    @BindView(R.id.tvTransTypespn)
    TextView tvTransTypespn;
    @BindView(R.id.rvTransTypespn)
    RelativeLayout rvTransTypespn;
    @BindView(R.id.etTrans_Date)
    TextView etTransDate;
    @BindView(R.id.etTrans_Amount)
    EditText etTransAmount;
    @BindView(R.id.etTrans_Note)
    EditText etTransNote;
    @BindView(R.id.btn_TransAdd)
    TextView btnTransAdd;
    @BindView(R.id.btn_add_share_payment)
    Button btn_add_share_payment;

    Context context;
    API webapi;
    ProgressDialog prd;
    String cust_id = "",typeid = "0";

    Adapter_Transaction_List adapterTransactionList;
  //  ArrayList<Item_Sites_List> transactionlist = new ArrayList<>();

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();
    @BindView(R.id.tvTransType1)
    TextView tvTransType1;

    String customer_name,customer_id,type1="Cash Received",type2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fab__transaction_);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(Html.fromHtml("Add Received "));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = Add_fab_Transaction_Activity.this;



        customer_id = getIntent().getStringExtra("customer_id");
        customer_name = getIntent().getStringExtra("customer_name");




        tvTransCompanySpn.setText(customer_name);
        tvTransTypespn.setText(type1);



      /*  rvTranCompnyspn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getCompanyapi(tvTransCompanySpn);

            }
        });*/

        rvTransTypespn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {
                        "Cash Received"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Add_fab_Transaction_Activity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        tvTransTypespn.setText(items[item]);
                        tvTransType1.setTextColor(getResources().getColor(R.color.blackText1));
                      //  typeid = "0";



                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        etTransDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etTransDate.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                etTransDate.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                etTransDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(Add_fab_Transaction_Activity.this, etTransDate);
                    }
                });

            }
        });


        btnTransAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvTransCompanySpn.getText().toString().equals("")){
                    tvTransCompanySpn.setError("Select Party Name");
                    tvTransCompanySpn.requestFocus();
                    return;
                }else if (tvTransTypespn.getText().toString().equals("")) {
                    tvTransTypespn.setError("Select Payment Type");
                    tvTransTypespn.requestFocus();
                    return;
                }else if (etTransDate.getText().toString().equals("")) {
                    etTransDate.setError("Select Payment Date");
                    etTransDate.requestFocus();
                    return;
                }else if (etTransAmount.getText().toString().equals("")) {
                    etTransAmount.setError("Select Payment Date");
                    etTransAmount.requestFocus();
                    return;
                }else {

                    Utils.showLog("cust_id" +cust_id+typeid+etTransNote.getText().toString().trim() + etTransDate.getText().toString().trim() + etTransAmount.getText().toString().trim());
                    addTransactionApi(customer_id,typeid,etTransDate.getTag().toString().trim(),etTransAmount.getText().toString().trim(),etTransNote.getText().toString().trim(),0);
                }
            }
        });

        btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvTransCompanySpn.getText().toString().equals("")){
                    tvTransCompanySpn.setError("Select Party Name");
                    tvTransCompanySpn.requestFocus();
                    return;
                }else if (tvTransTypespn.getText().toString().equals("")) {
                    tvTransTypespn.setError("Select Payment Type");
                    tvTransTypespn.requestFocus();
                    return;
                }else if (etTransDate.getText().toString().equals("")) {
                    etTransDate.setError("Select Payment Date");
                    etTransDate.requestFocus();
                    return;
                }else if (etTransAmount.getText().toString().equals("")) {
                    etTransAmount.setError("Select Payment Date");
                    etTransAmount.requestFocus();
                    return;
                }else {

                    Utils.showLog("cust_id" +cust_id+typeid+etTransNote.getText().toString().trim() + etTransDate.getText().toString().trim() + etTransAmount.getText().toString().trim());
                    addTransactionApi(customer_id,typeid,etTransDate.getTag().toString().trim(),etTransAmount.getText().toString().trim(),etTransNote.getText().toString().trim(),1);
                }
            }
        });


    }

    private void addTransactionApi(final String customer_id,final String typeid, final String date, final String amount,final String note,int checkButton) {


        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Transactions").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customer_id);
        map.put("payment_type", typeid);
        map.put("transaction_date", date);
        map.put("amount", amount);
        map.put("transaction_note", note);
        map.put("transaction_id", key);
        map.put("last_updated", date+" "+"00:00:00");
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        map.put("created_at", date+" "+currentTime);


        rootRef.child("Transactions").child(key).setValue(map).addOnCompleteListener(Add_fab_Transaction_Activity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                if (checkButton == 1) {
                    if (appInstalledOrNot() == 0) {
                        if (checkButton == 1) {
                            sendMessage(amount, "com.whatsapp");
                        }
                    } else if (appInstalledOrNot() == 1) {
                        if (checkButton == 1) {
                            sendMessage(amount, "com.whatsapp.w4b");
                        }
                    } else if (appInstalledOrNot() == 2) {
                        if (checkButton == 1) {
                            sendMessage(amount, "both");
                        }
                    }
                }
                new CToast(Add_fab_Transaction_Activity.this).simpleToast("Payment added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                finish();
            }

        }).addOnFailureListener(Add_fab_Transaction_Activity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Add_fab_Transaction_Activity.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendMessage(String amount, String pkg) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text="";
        text = "Dear  '"+customer_name+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";

        if (pkg.equalsIgnoreCase("both")) {
            showBottomSheet(text);
        } else {
            waIntent.setPackage(pkg);
            if (waIntent != null) {
                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, text));
            } else {
                Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                        .show();
            }
        }
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

    private void getCompanyapi(final TextView view) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }

                        if (!view.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!view.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);

                        } else {
                            customerDialog(view);
                        }

                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);


    }

    private void customerDialog(final TextView textView) {

        LayoutInflater localView = LayoutInflater.from(context);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);
        final TextView tvAddNewCustomer = (TextView) promptsView.findViewById(R.id.tvAddNewCustomer);

        tvAddNewCustomer.setVisibility(View.GONE);

        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

        edtSearchLocation.setHint("Search Customer");
        for (int i = 0; i < itemListCustomer.size(); i++) {
            arrayListTemp.add(itemListCustomer.get(i).getName());
            arrayListId.add(itemListCustomer.get(i).getId());
        }


        list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemListCustomer.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListCustomer.get(j).getName().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListCustomer.get(j).getName());
                            arrayListId.add(itemListCustomer.get(j).getId());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();
                    for (int i = 0; i < itemListCustomer.size(); i++) {
                        arrayListTemp.add(itemListCustomer.get(i).getName());
                        arrayListId.add(itemListCustomer.get(i).getId());
                    }
                    list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

     /*   list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                OpenAddNewCustomer(textView);
                alertDialog.dismiss();
            }

        });
        */


        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // OpenAddNewCustomer(textView);
                textView.setText(arrayListTemp.get(position));
                textView.setTag(arrayListId.get(position));
                cust_id = arrayListId.get(position);
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


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
