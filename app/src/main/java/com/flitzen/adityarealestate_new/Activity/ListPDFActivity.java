package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flitzen.adityarealestate_new.Adapter.PDFAdapter;
import com.flitzen.adityarealestate_new.FileUtils;
import com.flitzen.adityarealestate_new.R;

import java.io.File;
import java.util.ArrayList;

public class ListPDFActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    File dir;
    String property_name = "", property_id = "", customer_id = "", position = "";
    EditText edtSearch;
    ImageView imgClearSearch;
    Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_activity);
        init();
    }

    private void init() {

        lv_pdf = (ListView) findViewById(R.id.lv_pdf);
        edtSearch = findViewById(R.id.edt_search);
        imgClearSearch = findViewById(R.id.img_clear_search);
        btn_add = findViewById(R.id.btn_add);
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//         fn_permission();
        property_name = getIntent().getStringExtra("property_name");
        property_id = getIntent().getStringExtra("property_id");
        customer_id = getIntent().getStringExtra("customer_id");
        position = getIntent().getStringExtra("position");

        Log.d("Position", ""+position + "");

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    filePickerDialog();
                } else {
                    Toast.makeText(ListPDFActivity.this, "Please grand storage permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PdfActivity.class);
                intent.putExtra("position_new", i);
                intent.putExtra("property_name", property_name);
                intent.putExtra("property_id", property_id);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("position", position);
                intent.putExtra("path",fileList);

                startActivity(intent);

                Log.e("Position", i + "");
            }
        });


//        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b)
//                    imgClearSearch.setVisibility(View.VISIBLE);
//                else
//                    imgClearSearch.setVisibility(View.GONE);
//
//            }
//        });
//
//        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        edtSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                obj_adapter.getFilter().filter(s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        imgClearSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edtSearch.setText(null);
//                edtSearch.clearFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//            }
//        });
    }


//    public void filter(String text) {
//        ArrayList<File> temp = new ArrayList();
//        for (File d : fileList) {
//            //or use .equal(text) with you want equal match
//            //use .toLowerCase() for better matches
//            if (d.getName().contains(text)) {
//                temp.add(d);
//            }
//        }
//        //update recyclerview
//        System.out.println("======temp   " + temp.size());
//        obj_adapter.updateList(temp);
//    }


//    @SuppressLint("NewApi")
//    public ArrayList<File> getfile(File dir) {
//        File listFile[] = dir.listFiles();
//

//        Arrays.sort(listFile, Comparator.comparingLong(File::lastModified).reversed());
//        if (listFile != null && listFile.length > 0) {
//            for (int i = 0; i < listFile.length; i++) {
//
//                if (listFile[i].isDirectory()) {
//                    getfile(listFile[i]);
//
//                } else {
//
//                    boolean booleanpdf = false;
//                    if (listFile[i].getName().endsWith(".pdf")) {
//
//                        for (int j = 0; j < fileList.size(); j++) {
//                            if (fileList.get(j).getName().equals(listFile[i].getName())) {
//                                booleanpdf = true;
//                            } else {
//
//                            }
//                        }
//
//                        if (booleanpdf) {
//                            booleanpdf = false;
//                        } else {
//                            fileList.add(listFile[i]);
//
//                        }
//                    }
//                }
//            }
//        }
//        return fileList;
//    }


//    private void fn_permission() {
//        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//
//            if ((ActivityCompat.shouldShowRequestPermissionRationale(ListPDFActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//
//
//            } else {
//                ActivityCompat.requestPermissions(ListPDFActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSIONS);
//
//            }
//        } else {
//            boolean_permission = true;
//
//            getfile(dir);
//            for(int i=0;i<fileList.size();i++){
//                System.out.printf("======File: %-20s Last Modified:" + new Date(fileList.get(i).lastModified()) + "\n", fileList.get(i).getName());
//            }
//            obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
//            lv_pdf.setAdapter(obj_adapter);
//
//        }
//    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSIONS) {
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                boolean_permission = true;
//                getfile(dir);
//
//                obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
//                lv_pdf.setAdapter(obj_adapter);
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
//
//            }
//        }
//
    //keval

    private void filePickerDialog() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, MIME_TYPES_IMAGE_PDF);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(Intent.createChooser(intent, "Select Document"), 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
                    if (null != data.getClipData()) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            Uri uri = data.getClipData().getItemAt(i).getUri();
                            uploadImageToStorage(uri);
                        }
                    } else {
                        Uri uri = data.getData();
                        uploadImageToStorage(uri);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToStorage(Uri uri) {

        if (uri == null) {
            Toast.makeText(this, "Please select other file!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CreateAssignment", "uploadImageToStorage: Uri path -------->>>> " + uri.toString());
        String filePath = FileUtils.getPath(this, uri);
        File selectedFile = new File(filePath);
        if (filePath == null) {
            Toast.makeText(this, "File selection from Google drive is not allowed. Please choose a file from the local device storage.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CreateAssignment", "uploadImageToStorage: File path -------->>>> " + filePath);


        fileList.add(new File(filePath));
        obj_adapter = new PDFAdapter(this, fileList);
        lv_pdf.setAdapter(obj_adapter);
        obj_adapter.notifyDataSetChanged();
     //   clearCache();

//        obj_adapter.setItemClick(new PDFAdapter.setOnItemClick() {
//            @Override
//            public void onItemClick(int position) {
//                Intent intent = new Intent(ListPDFActivity.this, PdfActivity.class);
//                intent.putExtra("position_new", position);
//                intent.putExtra("property_name", property_name);
//                intent.putExtra("property_id", property_id);
//                intent.putExtra("customer_id", customer_id);
//                intent.putExtra("position", position);
//                startActivity(intent);
//            }
//        });
        //  File selectedFile = new File(filePath);

//        String encoded_file = "";
//        try {
//            byte[] bytes = loadFile(selectedFile);
//            encoded_file = Base64.encodeToString(bytes, Base64.DEFAULT);
//            Log.e("ENCODE", encoded_file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Upload_files_items items = new Upload_files_items();

//        items.setFile_decode(encoded_file);
        //   items.setFile(selectedFile);
        //   items.setF_name(selectedFile.getName());
        //   items.setF_extention(FileUtility.getExtension(filePath));
        //   items.setF_size(getReadableFileSize(selectedFile.length()));

        //   itemArray.add(items);

//        mAdapter = new Upload_file_adapter(Upload_files_screen.this, itemArray);
//        lst_upload_file.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Storage ", "Permission is granted");
                return true;
            } else {
                Log.v("Storage ", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Storage ", "Permission is granted");
            return true;
        }
    }

    public static String[] MIME_TYPES_IMAGE_PDF = {
            "application/pdf"
    };

    private void clearCache(){
        FileUtils.deleteCache(this);
    }

}
