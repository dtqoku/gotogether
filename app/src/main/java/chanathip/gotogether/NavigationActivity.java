package chanathip.gotogether;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private UserData userData;
    private GroupData groupData;
    private UiSettings uiSettings;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 123;
    private GoogleApiClient googleApiClient;
    private List<GroupDetailData> memberDatas;
    private Button setmeetingpoint;
    private Button btnok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setmeetingpoint = (Button) findViewById(R.id.btnSubmit);
        btnok = (Button) findViewById(R.id.btnok);

        userData = new UserData();
        groupData = new GroupData();
        memberDatas = new ArrayList<>();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                groupData.GroupUID = null;
                groupData.Name = null;
                userData.UserUid = null;
            } else {
                groupData.GroupUID = extras.getString("GroupUID");
                groupData.Name = extras.getString("GroupName");
                userData.UserUid = extras.getString("UserUid");
            }
        } else {
            groupData.GroupUID = (String) savedInstanceState.getSerializable("GroupUID");
            groupData.Name = (String) savedInstanceState.getSerializable("GroupName");
            userData.UserUid = (String) savedInstanceState.getSerializable("UserUid");
        }
        setTitle(groupData.Name + "'s map");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        setmeetingpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(0, 0)).title("Meeting point");
                final Marker marker1 = mMap.addMarker(marker);

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        marker1.setPosition(latLng);
                    }
                });
                setmeetingpoint.setVisibility(View.GONE);
                btnok.setVisibility(View.VISIBLE);

                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //LatLng latLng = marker1.getPosition();
                        LatLng latLng = marker1.getPosition();
                        

                        btnok.setVisibility(View.GONE);
                        setmeetingpoint.setVisibility(View.VISIBLE);
                    }
                });
            }
        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.UserUid);
        currentuserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData.setData(
                        userData.UserUid,
                        String.valueOf(dataSnapshot.child("First name").getValue()),
                        String.valueOf(dataSnapshot.child("Last name").getValue()),
                        String.valueOf(dataSnapshot.child("display name").getValue()),
                        String.valueOf(dataSnapshot.child("email").getValue()),
                        String.valueOf(dataSnapshot.child("Phone").getValue())
                );
                userData.Status = String.valueOf(dataSnapshot.child("status"));
                userData.rank = String.valueOf(dataSnapshot.child("group").child(groupData.GroupUID).getValue());
                if (!userData.rank.equals("leader")) {
                    setmeetingpoint.setVisibility(View.GONE);
                }

                final DatabaseReference groupDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                groupDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupData.setData(
                                groupData.GroupUID,
                                String.valueOf(dataSnapshot.child("name").getValue()),
                                String.valueOf(dataSnapshot.child("description").getValue()),
                                userData.rank,
                                String.valueOf(dataSnapshot.child("settingpoint").getValue()),
                                String.valueOf(dataSnapshot.child("membercount").getValue()),
                                userData.UserUid
                        );

                        Map<String, String> memberMap = (Map<String, String>) dataSnapshot.child("member").getValue();
                        if (memberMap != null) {
                            for (HashMap.Entry<String, String> entry : memberMap.entrySet()) {
                                String key = entry.getKey();
                                String valve = entry.getValue();

                                final String keyData = key;
                                final String valveData = valve;

                                final DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserData memberData = new UserData();
                                        memberData.setData(
                                                keyData,
                                                String.valueOf(dataSnapshot.child("First name").getValue()),
                                                String.valueOf(dataSnapshot.child("Last name").getValue()),
                                                String.valueOf(dataSnapshot.child("display name").getValue()),
                                                String.valueOf(dataSnapshot.child("email").getValue()),
                                                String.valueOf(dataSnapshot.child("Phone").getValue())
                                        );
                                        memberData.rank = valveData;
                                        memberData.Status = String.valueOf(dataSnapshot.child("status").getValue());

                                        if (memberData.Status.equals("active")) {
                                            memberData.LocationLat = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
                                            memberData.LocationLng = Double.valueOf(String.valueOf(dataSnapshot.child("lng").getValue()));
                                        }

                                        GroupDetailData groupDetailData = new GroupDetailData();
                                        groupDetailData.member = memberData;

                                        //memberDatas.add(groupDetailData);
                                        if (!memberData.UserUid.equals(userData.UserUid) && memberData.Status.equals("active")) {
                                            LatLng sydney = new LatLng(memberData.LocationLat, memberData.LocationLng);
                                            mMap.addMarker(new MarkerOptions().position(sydney).title(memberData.displayname));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("GroupUID", groupData.GroupUID);
        savedInstanceState.putString("GroupName", groupData.Name);
        savedInstanceState.putString("UserUid", userData.UserUid);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onStart() {
        super.onStart();
        memberDatas.clear();

        // Connect to Google API Client
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.UserUid);
        currentUserDatabaseReference.child("lat").removeValue();
        currentUserDatabaseReference.child("lng").removeValue();
        currentUserDatabaseReference.child("status").setValue("notactive");

        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when location provider not available
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // Do something when got new current location
        LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(me).title("me"));
        if (!userData.Status.equals("active")) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 16));
            userData.Status = "active";
        }
        DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.UserUid);
        currentUserDatabaseReference.child("lat").setValue(location.getLatitude());
        currentUserDatabaseReference.child("lng").setValue(location.getLongitude());
        currentUserDatabaseReference.child("status").setValue("active");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
