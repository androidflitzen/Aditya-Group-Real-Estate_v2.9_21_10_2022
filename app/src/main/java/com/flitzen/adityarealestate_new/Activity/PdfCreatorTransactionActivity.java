package com.flitzen.adityarealestate_new.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.RentDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.pdfcreator.activity.PDFCreatorActivity;
import com.flitzen.adityarealestate_new.pdfcreator.utils.PDFUtil;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFBody;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFHeaderView;
import com.flitzen.adityarealestate_new.pdfcreator.views.PDFTableView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFCustomView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFLineSeparatorView;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PdfCreatorTransactionActivity extends PDFCreatorActivity {


    ArrayList<Transcation> transactionlist = new ArrayList<>();
    String customer_name = "";
    String phone_no = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent().hasExtra("paymentList")) {
            customer_name = getIntent().getStringExtra("customer_name");
            phone_no = getIntent().getStringExtra("phone_no");
            transactionlist = (ArrayList<Transcation>) getIntent().getSerializableExtra("paymentList");
        }

        createPDF("test", new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                Toast.makeText(PdfCreatorTransactionActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(PdfCreatorTransactionActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        /*PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = inflater2.inflate(R.layout.header_layout, null);
        PDFCustomView pdfCustomView2=new PDFCustomView(PdfCreatorPlotActivity.this,view2, LinearLayout.LayoutParams.MATCH_PARENT, 90,"4");
        headerView.addView(pdfCustomView2);*/

        return null;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Transaction");
        pdfBody.addView(pdfCompanyNameView);
        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        pdfBody.addView(lineSeparatorView1);
        PDFTextView pdfAddressView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        pdfAddressView.setText("as on " + currentDate);
        pdfBody.addView(pdfAddressView);
        pdfCompanyNameView.getView().setGravity(Gravity.CENTER_HORIZONTAL);
        pdfAddressView.getView().setGravity(Gravity.CENTER_HORIZONTAL);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_layout_for_transaction, null);
        PDFCustomView pdfCustomView = new PDFCustomView(PdfCreatorTransactionActivity.this, view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, customer_name, phone_no);
        pdfBody.addView(pdfCustomView);

        String[] textInTable = {"Date", "Customer Name", "Remarks", "Amount"};

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());

        /*for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextView.setText(s);
            tableHeader.addToRow(pdfTextView);
        }*/

        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
       /* for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            //pdfTextView.setText("Row 1 : " + s);
            pdfTextView.setText("Row 1 : " + s);
            tableRowView1.addToRow(pdfTextView);
        }*/

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);
        int finalTotalAmount = 0;
        for (int i = 0; i < transactionlist.size(); i++) {
            // Create 10 rows
            PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
            for (int j = 0; j < 4; j++) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                if (j == 0) {

                    if (transactionlist.get(i).getTransactionDate() != null) {
                        if (transactionlist.get(i).getTransactionTime() != null) {
                            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
                            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                            SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
                            try {
                                Date oneWayTripDate;
                                Date oneWayTripDateT;
                                oneWayTripDate = input.parse(transactionlist.get(i).getTransactionDate());  // parse input
                                oneWayTripDateT = inputT.parse(transactionlist.get(i).getTransactionTime());  // parse input
                                pdfTextView.setText(output.format(oneWayTripDate) + " " + outputT.format(oneWayTripDateT));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            pdfTextView.setText(output.format(""));
                            tableRowView.addToRow(pdfTextView);
                        }
                    } else {
                        tableRowView.addToRow(pdfTextView);
                    }


                } else if (j == 1) {
                    if (transactionlist.get(i).getPaymentType() != null) {
                        pdfTextView.setText(transactionlist.get(i).getPaymentType());
                        tableRowView.addToRow(pdfTextView);
                    } else {
                        pdfTextView.setText("");
                        tableRowView.addToRow(pdfTextView);
                    }

                } else if (j == 2) {
                    if (transactionlist.get(i).getTransactionNote() != null) {
                        pdfTextView.setText(transactionlist.get(i).getTransactionNote());
                        tableRowView.addToRow(pdfTextView);
                    } else {
                        pdfTextView.setText("");
                        tableRowView.addToRow(pdfTextView);
                    }

                } else if (j == 3) {
                    PDFTextView pdfTextViewR = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P, 0);
                    pdfTextViewR.setText("???" + Helper.getFormatPrice(Integer.parseInt(transactionlist.get(i).getAmount())));
                    finalTotalAmount = finalTotalAmount + Integer.parseInt(transactionlist.get(i).getAmount());
                    tableRowView.addToRow(pdfTextViewR);
                }

            }
           /* for (String s : textInTable) {
                PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextView.setText("Row " + (i + 2) + ": " + s);
                tableRowView.addToRow(pdfTextView);
            }*/
            tableView.addRow(tableRowView);
            PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
            tableView.addSeparatorRow(lineSeparatorView3);

        }
        pdfBody.addView(tableView);

        LayoutInflater inflaterFinal = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewFinal = inflaterFinal.inflate(R.layout.final_total_layout, null);
        PDFCustomView pdfCustomViewFinal = new PDFCustomView(PdfCreatorTransactionActivity.this, viewFinal, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, finalTotalAmount);
        pdfBody.addView(pdfCustomViewFinal);


      /*  LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater1.inflate(R.layout.table_layout, null);
        PDFCustomView pdfCustomView1=new PDFCustomView(PdfCreatorPlotActivity.this,view1, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,"2");
        pdfBody.addView(pdfCustomView1);*/

       /* LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = inflater2.inflate(R.layout.list_layout, null);
        PDFCustomView pdfCustomView2=new PDFCustomView(PdfCreatorPlotActivity.this,view2, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,arrayListPlotPayment);
        pdfBody.addView(pdfCustomView2);*/

        return pdfBody;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
        Uri pdfUri = Uri.fromFile(savedPDFFile);

        Intent intentPdfViewer = new Intent(PdfCreatorTransactionActivity.this, PdfViewerActivity.class);
        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);

        startActivity(intentPdfViewer);
    }
}
