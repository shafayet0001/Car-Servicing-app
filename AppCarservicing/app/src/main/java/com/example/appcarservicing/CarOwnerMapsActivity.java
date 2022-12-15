package com.example.appcarservicing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarOwnerMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button mRequest;
    private LatLng pickupLocation,requestService;
    private Boolean requestBol = false;
    private Marker pickupMarker;
    private LinearLayout mcarWorkshopOwnerInfo;
    private TextView mcarWorkshopOwnerName, mcarWorkshopOwnerPhone;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_owner_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mcarWorkshopOwnerInfo = (LinearLayout) findViewById(R.id.carWorkshopOwnerInfo);
        mcarWorkshopOwnerName = (TextView) findViewById(R.id.carWorkshopOwnerName);
        mcarWorkshopOwnerPhone = (TextView) findViewById(R.id.carWorkshopOwnerPhone);
        Button mLogout = (Button) findViewById(R.id.logout);
        mRequest = (Button) findViewById(R.id.request);
        mLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CarOwnerMapsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        mRequest.setOnClickListener(v -> {

                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CarOwnerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("I'm Here").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup)));
                mRequest.setText("Getting your Workshop....");

                getClosestcarWorkshopOwner();
           // }
        });

    }
    private int radius = 1;
    private Boolean carWorkshopOwnerFound = false;
    private String carWorkshopOwnerFoundID;
    GeoQuery geoQuery;
    private void getClosestcarWorkshopOwner(){
        DatabaseReference carWorkshopOwnerLocation = FirebaseDatabase.getInstance().getReference().child("CarWorkshopOwnerAvailable");
        GeoFire geoFire = new GeoFire(carWorkshopOwnerLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!carWorkshopOwnerFound && requestBol){
                    DatabaseReference mCarOwnerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner").child(key);
                    mCarOwnerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                       // @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map carWorkshopOwnerMap = (Map) dataSnapshot.getValue();
                                //Map<String, Object> carWorkshopOwnerMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (carWorkshopOwnerFound){
                                    return;
                                }

                                assert carWorkshopOwnerMap != null;
                                if(Objects.equals(carWorkshopOwnerMap.get("service"), requestService)){
                                    carWorkshopOwnerFound = true;
                                    carWorkshopOwnerFoundID = dataSnapshot.getKey();
                                    DatabaseReference carWorkshopOwnerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner").child(carWorkshopOwnerFoundID).child("CarOwnerRequest");
                                    String CarOwnerId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    //HashMap<String, Object> map = new HashMap<>();
                                    HashMap map = new HashMap();
                                    map.put("carownerId", CarOwnerId);
                                    carWorkshopOwnerRef.updateChildren(map);
                                    getcarWorkshopOwnerLocation();
                                    getcarWorkshopOwnerInfo();
                                    mRequest.setText("Looking for carWorkshopOwner Location....");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!carWorkshopOwnerFound)
                {
                    radius++;
                    getClosestcarWorkshopOwner();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private Marker mcarWorkshopOwnerMarker;
    private DatabaseReference carWorkshopOwnerLocationRef;
    private ValueEventListener carWorkshopOwnerLocationRefListener;
    private void getcarWorkshopOwnerLocation(){
        carWorkshopOwnerLocationRef = FirebaseDatabase.getInstance().getReference().child("CarWorkshopOwnerWorking").child(carWorkshopOwnerFoundID).child("l");
        carWorkshopOwnerLocationRefListener = carWorkshopOwnerLocationRef.addValueEventListener(new ValueEventListener() {
            //@SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    assert map != null;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng carWorkshopOwnerLatLng = new LatLng(locationLat,locationLng);
                    if(mcarWorkshopOwnerMarker != null){
                        mcarWorkshopOwnerMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);
                    Location loc2 = new Location("");
                    loc2.setLatitude(carWorkshopOwnerLatLng.latitude);
                    loc2.setLongitude(carWorkshopOwnerLatLng.longitude);
                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("carWorkshop's Here");
                    }else{
                        mRequest.setText("carWorkshop Found: " + String.valueOf(distance));
                    }
                    mcarWorkshopOwnerMarker = mMap.addMarker(new MarkerOptions().position(carWorkshopOwnerLatLng).title("your CarWorkshop").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getcarWorkshopOwnerInfo(){
        mcarWorkshopOwnerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCarOwnerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner").child(carWorkshopOwnerFoundID);
        mCarOwnerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    dataSnapshot.child("name");
                    mcarWorkshopOwnerName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    dataSnapshot.child("phone");
                    mcarWorkshopOwnerPhone.setText(Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        carWorkshopOwnerLocationRef.removeEventListener(carWorkshopOwnerLocationRefListener);

        if (carWorkshopOwnerFoundID != null){
            DatabaseReference carWorkshopOwnerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("CarWorkshopOwner").child(carWorkshopOwnerFoundID).child("carownerRequest");
            carWorkshopOwnerRef.removeValue();
            carWorkshopOwnerFoundID = null;

        }
        carWorkshopOwnerFound = false;
        radius = 1;
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carownerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mcarWorkshopOwnerMarker != null){
            mcarWorkshopOwnerMarker.remove();
        }
        mRequest.setText("call");
        mcarWorkshopOwnerInfo.setVisibility(View.GONE);
        mcarWorkshopOwnerName.setText("");
        mcarWorkshopOwnerPhone.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carownerRequest").child(userId);
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));


                    }
                }
            }

    };
    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CarOwnerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CarOwnerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    boolean getcarWorkshopOwnerAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getcarWorkshopOwnerAround(){
        getcarWorkshopOwnerAroundStarted = true;
        DatabaseReference carWorkshopOwnerLocation = FirebaseDatabase.getInstance().getReference().child("CarWorkshopOwnerAvailable");

        GeoFire geoFire = new GeoFire(carWorkshopOwnerLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(Objects.equals(markerIt.getTag(), key))
                        return;
                }
                LatLng carWorkshopOwnerLocation = new LatLng(location.latitude, location.longitude);
                Marker mcarWorkshopOwnerMarker = mMap.addMarker(new MarkerOptions().position(carWorkshopOwnerLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
                mcarWorkshopOwnerMarker.setTag(key);
                markers.add(mcarWorkshopOwnerMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(Objects.equals(markerIt.getTag(), key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(Objects.equals(markerIt.getTag(), key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}