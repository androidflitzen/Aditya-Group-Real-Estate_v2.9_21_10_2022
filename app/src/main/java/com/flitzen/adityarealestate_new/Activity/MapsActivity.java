package com.flitzen.adityarealestate_new.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchView;
    TextView tvSave, tvShare;
    ProgressDialog prd;
    ImageView iv_back;
    LatLng plotLocation = null;
    LatLng newLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchView = (SearchView) findViewById(R.id.idSearchView);
        tvSave = findViewById(R.id.tvSave);
        tvShare = findViewById(R.id.tvShare);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    } catch (Exception e) {
                        e.printStackTrace();
                        new CToast(MapsActivity.this).simpleToast("Location not exits", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newLocation != null) {
                    addLocation(newLocation.latitude, newLocation.longitude);
                }
            }
        });

        try {
            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        String uri = "http://maps.google.com/maps?daddr=" + plotLocation.latitude + "," + plotLocation.longitude;
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, uri);
                        startActivity(Intent.createChooser(sharingIntent, "Share With?"));
                    } catch (Exception e) {
                        new CToast(MapsActivity.this).simpleToast("Location not saved!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    }
                }
            });
        } catch (Exception e) {
            new CToast(MapsActivity.this).simpleToast("Location not saved!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(10);
        LatLng rajkot = new LatLng(22.308155, 70.800705);
//        mMap.addMarker(new MarkerOptions()
//                .position(rajkot)
//                .title("Marker in Rajkot"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rajkot));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                newLocation = latLng;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(newLocation);
                markerOptions.title("New Location");

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                String uri = "http://maps.google.com/maps?daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share With?"));

                return false;
            }
        });


        checkForLocation();
    }

    public void checkForLocation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByChild("id").equalTo(Aditya.ID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String plotNodeKey = data.getKey();
                        String plot_no = data.child("plot_no").getValue().toString();

                        Log.d("TAG", "data: " + data.toString());
                        Log.d("TAG", "key: " + plotNodeKey);
                        Log.d("TAG", "plot_no: " + plot_no);

                        if (data.child("latitude").exists() && data.child("longitude").exists()) {
                            String latitude = data.child("latitude").getValue().toString();
                            String longitude = data.child("longitude").getValue().toString();

                            Log.d("TAG", "latitude: " + latitude);
                            Log.d("TAG", "longitude: " + longitude);

                            mMap.clear();

                            plotLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            mMap.addMarker(new MarkerOptions().position(plotLocation).title("Plot No: " + plot_no));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(plotLocation));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addLocation(double latitude, double longitude) {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByChild("id").equalTo(Aditya.ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String plotNodeKey = data.getKey();

                        Map<String, Object> map = new HashMap<>();
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        databaseReference.child("Plots").child(plotNodeKey).updateChildren(map).addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                hidePrd();
                                new CToast(MapsActivity.this).simpleToast("Location saved successfully.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                            }
                        }).addOnFailureListener(MapsActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hidePrd();
                                new CToast(MapsActivity.this).simpleToast("Location not saved!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hidePrd();
                new CToast(MapsActivity.this).simpleToast("Location not saved!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
            }
        });
    }

    public void showPrd() {
        prd = new ProgressDialog(MapsActivity.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    //
    public void hidePrd() {
        prd.dismiss();
    }

}