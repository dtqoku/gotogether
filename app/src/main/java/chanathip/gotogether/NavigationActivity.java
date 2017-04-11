package chanathip.gotogether;


import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 123;
    private GoogleApiClient googleApiClient;
    private List<UserData> memberDatas;
    private Button setmeetingpoint;
    private Button btnok;
    private Button btnClear;
    private Marker selfMarker;
    private Marker meetPointMarker;

    private DatabaseReference usersDatabaseReference;
    private List<ValueEventListener> userValueEventListenerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setmeetingpoint = (Button) findViewById(R.id.btnSubmit);
        btnok = (Button) findViewById(R.id.btnok);
        btnClear = (Button) findViewById(R.id.btnclear);

        userData = new UserData();
        groupData = new GroupData();
        memberDatas = new ArrayList<>();
        userValueEventListenerList = new ArrayList<>();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                groupData.GroupUID = null;
                groupData.Name = null;
                userData.userUid = null;
            } else {
                groupData.GroupUID = extras.getString("GroupUID");
                groupData.Name = extras.getString("GroupName");
                userData.userUid = extras.getString("userUid");
            }
        } else {
            groupData.GroupUID = (String) savedInstanceState.getSerializable("GroupUID");
            groupData.Name = (String) savedInstanceState.getSerializable("GroupName");
            userData.userUid = (String) savedInstanceState.getSerializable("userUid");
        }
        setTitle(groupData.Name + "'s map");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setmeetingpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetPointMarker == null) {
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Meet Point")
                            .icon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_beenhere_black_48dp, null)));
                    meetPointMarker = mMap.addMarker(marker);
                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        meetPointMarker.setPosition(latLng);
                    }
                });
                setmeetingpoint.setVisibility(View.INVISIBLE);
                btnok.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);

                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setOnMapClickListener(null);
                        LatLng latLng = meetPointMarker.getPosition();

                        DatabaseReference currentgroup = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                        currentgroup.child("settingpoint").setValue("active");
                        currentgroup.child("lat").setValue(latLng.latitude);
                        currentgroup.child("lng").setValue(latLng.longitude);

                        GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(NavigationActivity.this);
                        gotogetherNotificationManager.meetPointSetted(groupData.GroupUID, groupData.Name);

                        btnok.setVisibility(View.GONE);
                        btnClear.setVisibility(View.GONE);
                        setmeetingpoint.setVisibility(View.VISIBLE);
                    }
                });

                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.setOnMapClickListener(null);
                        meetPointMarker.remove();

                        DatabaseReference currentgroup = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                        currentgroup.child("settingpoint").setValue("notactive");
                        currentgroup.child("lat").removeValue();
                        currentgroup.child("lng").removeValue();

                        btnok.setVisibility(View.GONE);
                        btnClear.setVisibility(View.GONE);
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

        DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.userUid);
        currentuserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData.setData(
                        userData.userUid,
                        String.valueOf(dataSnapshot.child("display name").getValue()),
                        String.valueOf(dataSnapshot.child("email").getValue())
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
                                userData.userUid
                        );

                        if (groupData.isMeetingPointSet()) {
                            LatLng meetpoint = new LatLng(
                                    Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue())),
                                    Double.valueOf(String.valueOf(dataSnapshot.child("lng").getValue())));
                            meetPointMarker = mMap.addMarker(new MarkerOptions()
                                    .position(meetpoint)
                                    .title("Meet Point")
                                    .icon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_beenhere_black_48dp, null)))
                            );

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meetpoint, 16));
                        }

                        Map<String, String> memberMap = (Map<String, String>) dataSnapshot.child("member").getValue();
                        if (memberMap != null) {
                            for (HashMap.Entry<String, String> entry : memberMap.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                if (key.equals(userData.userUid)) {
                                    continue;
                                }

                                final String keyData = key;
                                final String valueData = value;

                                usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                ValueEventListener userEventListener = usersDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserData memberData = new UserData();
                                        memberData.setData(
                                                keyData,
                                                String.valueOf(dataSnapshot.child("display name").getValue()),
                                                String.valueOf(dataSnapshot.child("email").getValue())
                                        );
                                        memberData.rank = valueData;
                                        memberData.Status = String.valueOf(dataSnapshot.child("status").getValue());

                                        if (memberData.Status.equals("active")) {
                                            memberData.LocationLat = Double.valueOf(String.valueOf(dataSnapshot.child("lat").getValue()));
                                            memberData.LocationLng = Double.valueOf(String.valueOf(dataSnapshot.child("lng").getValue()));

                                            boolean isMemberExistInMemberDatas = false;
                                            Marker oldMarker = null;
                                            for (UserData member : memberDatas) {
                                                if (member.userUid.equals(memberData.userUid)) {
                                                    isMemberExistInMemberDatas = true;
                                                    oldMarker = member.marker;
                                                }
                                            }
                                            if (!isMemberExistInMemberDatas) {
                                                LatLng position = new LatLng(memberData.LocationLat, memberData.LocationLng);
                                                memberData.marker = mMap.addMarker(new MarkerOptions()
                                                        .position(position)
                                                        .title(memberData.displayname)
                                                        .icon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_person_pin_circle_black_48dp, null)))
                                                );

                                                memberDatas.add(memberData);
                                            } else {
                                                if(oldMarker != null){
                                                    animateMarker(oldMarker, new LatLng(memberData.LocationLat, memberData.LocationLng), false);
                                                }
                                            }
                                        } else {
                                            for (UserData member : memberDatas) {
                                                if (member.userUid.equals(memberData.userUid)) {
                                                    member.marker.remove();
                                                    memberDatas.remove(member);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                userValueEventListenerList.add(userEventListener);
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
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        savedInstanceState.putString("userUid", userData.userUid);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);

            uiSettings = mMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
        }
    }

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

        for (ValueEventListener valueEventListener:userValueEventListenerList){
            usersDatabaseReference.removeEventListener(valueEventListener);
        }

        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability.isLocationAvailable()) {
                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10000);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            } else {
                // Do something when location provider not available
                Snackbar snackbar = Snackbar.make(btnok, "turn on your location for share yourself position", Snackbar.LENGTH_INDEFINITE)
                        .setAction("refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavigationActivity.this.recreate();
                            }
                        });
                snackbar.show();
            }

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // Do something when got new current location
        LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
        if (selfMarker == null) {
            selfMarker = mMap.addMarker(new MarkerOptions()
                    .position(me)
                    .title("me")
                    .icon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_place_black_48dp, null)))
            );

        } else {
            animateMarker(selfMarker, me, false);
        }
        if (!userData.Status.equals("active")) {
            if (meetPointMarker == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 16));

            }
            userData.Status = "active";
        }

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
