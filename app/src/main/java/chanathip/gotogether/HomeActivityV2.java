package chanathip.gotogether;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivityV2 extends AppCompatActivity implements MoreFragment.onSharedLocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    FloatingActionMenu fabmenu;
    com.github.clans.fab.FloatingActionButton fabnewfriend;
    com.github.clans.fab.FloatingActionButton fabnewgroup;
    Switch locationSwitch;
    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_v2);
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        fabmenu = (FloatingActionMenu) findViewById(R.id.fab);
        fabnewfriend = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fabnewfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivityV2.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });
        fabnewgroup = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fabnewgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivityV2.this, CreateNewGroupActivity.class);
                intent.putExtra("userUid", currentUserUid);
                startActivity(intent);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle("Notification");
                        fabmenu.showMenuButton(true);
                        break;
                    case 1:
                        setTitle("Social");
                        fabmenu.showMenuButton(true);
                        break;
                    case 2:
                        setTitle("More");
                        fabmenu.hideMenuButton(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group_white_120dp);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_assistant_photo_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_more_horiz_white_24dp);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            connectToLocationServices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationSwitch.setChecked(true);
                    connectToLocationServices();
                } else {
                    locationSwitch.setChecked(false);
                    Snackbar snackbar = Snackbar.make(fabmenu, "need you permission!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("refresh", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestLocationPermission();
                                }
                            });
                    snackbar.show();
                }

                break;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ACCESS_FINE_LOCATION);

    }

    private void connectToLocationServices() {
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            Snackbar snackbar = Snackbar.make(fabmenu, "turn on your location for share yourself position", Snackbar.LENGTH_INDEFINITE)
                    .setAction("refresh", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connectToLocationServices();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onSharedLocationChange(boolean isShared, Switch locationSwitch) {
        this.locationSwitch = locationSwitch;
        if (isShared) {
            Snackbar snackbar = Snackbar.make(fabmenu, "shared your location ", Snackbar.LENGTH_LONG);
            snackbar.show();
            googleApiClient.connect();
        } else {
            Snackbar snackbar = Snackbar.make(fabmenu, "close shared your location ", Snackbar.LENGTH_LONG);
            snackbar.show();
            googleApiClient.disconnect();

            DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            currentUserDatabaseReference.child("status").setValue("notactive");
            currentUserDatabaseReference.child("lat").removeValue();
            currentUserDatabaseReference.child("lng").removeValue();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserDatabaseReference.child("lat").setValue(location.getLatitude());
        currentUserDatabaseReference.child("lng").setValue(location.getLongitude());
        currentUserDatabaseReference.child("status").setValue("active");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar snackbar = Snackbar.make(fabmenu, "Cannot connect to googleApi!", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            currentUserDatabaseReference.child("status").setValue("notactive");
            currentUserDatabaseReference.child("lat").removeValue();
            currentUserDatabaseReference.child("lng").removeValue();
        }

        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return HomeNotificationFragment.newInstance();
            }
            else if(position == 1){
                return SocialFragment.newInstance();
            } else if(position == 2){
                return  MoreFragment.newInstance();
            }
            return MoreFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
