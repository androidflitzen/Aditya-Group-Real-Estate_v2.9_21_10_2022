package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flitzen.adityarealestate_new.Classes.TouchImageView;
import com.flitzen.adityarealestate_new.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Activity_ImageViewer extends AppCompatActivity {
    ImageView ivGallery;
    Toolbar toolbar;
    ImageView ivShare;
    private float mScaleFactor = 1.0f;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        initUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.whiteText1), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ivShare = findViewById(R.id.ivShare);
        ivGallery = (ImageView) findViewById(R.id.ivGallery);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        final String url = getIntent().getStringExtra("img_url");
        Log.d("URL123",""+url);
        Picasso.with(getApplicationContext())
                .load(url)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(ivGallery);


        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    Uri screenshotUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + url);
//
//                    sharingIntent.setType("image/png");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                    startActivity(Intent.createChooser(sharingIntent, "Share image using"));

                shareItem(url);
//                shareImg(url);
            }
        });


    }

    public void shareItem(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, url);
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            ivGallery.setScaleX(mScaleFactor);
            ivGallery.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    private void shareImg(String img_url) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Bitmap image = null;
                    try {
                        URL url = new URL(img_url);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch(IOException e) {
                        System.out.println(e);
                    }
                    try {
                        File file = new File(getExternalCacheDir(),"share.png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("image/png");
                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
