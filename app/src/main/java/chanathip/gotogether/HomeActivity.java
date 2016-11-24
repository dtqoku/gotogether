package chanathip.gotogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserData currentUserData;
    private DatabaseReference databaseReference;
    private List<NotificationData> notificationDatas;
    private List<NotificationData> friendRequestNotificationDatas;
    private List<NotificationData> unreadMassageNotificationDatas;
    private List<NotificationData> groupRequestNotificationDatas;
    private RecyclerView.LayoutManager layoutManager;
    private HomeNotificationAdapter homeNotificationAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        currentUserData = new UserData();
        notificationDatas = new ArrayList<>();
        friendRequestNotificationDatas = new ArrayList<>();
        unreadMassageNotificationDatas = new ArrayList<>();
        groupRequestNotificationDatas = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    currentUserData.UserUid = firebaseUser.getUid();
                    currentUserData.Email = firebaseUser.getEmail();

                    getcurrentuserdata();
                } else {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        notificationDatas.clear();
        navigationView.setCheckedItem(R.id.nav_home);

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // this
        } else if (id == R.id.nav_group) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_friend) {
            Intent intent = new Intent(this, FriendActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_accountsetting) {

        } else if (id == R.id.nav_appseting) {

        } else if (id == R.id.nav_logout) {
            GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(this);
            gotogetherNotificationManager.deleteToken(firebaseAuth.getCurrentUser().getUid());
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getcurrentuserdata() {
        DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.UserUid);
        currentuserDatabaseReference.keepSynced(true);
        currentuserDatabaseReference
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentUserData.setData(
                                        currentUserData.UserUid,
                                        dataSnapshot.child("First name").getValue().toString(),
                                        dataSnapshot.child("Last name").getValue().toString(),
                                        dataSnapshot.child("display name").getValue().toString(),
                                        dataSnapshot.child("email").getValue().toString(),
                                        dataSnapshot.child("Phone").getValue().toString()
                                );


                                //check friend request
                                friendRequestNotificationDatas.clear();
                                Map<String, String> FriendRequestMap = (Map<String, String>) dataSnapshot.child("request").child("friend").getValue();
                                if (FriendRequestMap != null) {
                                    for (HashMap.Entry<String, String> entry : FriendRequestMap.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();

                                        if (value.equals("true")) {
                                            final NotificationData friendRequestNotificationData = new NotificationData();

                                            DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                            requestFriendDatabaseReference
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            friendRequestNotificationData.RequestUserdisplayname = dataSnapshot.child("display name").getValue().toString();
                                                            friendRequestNotificationData.RequestUserLastname = dataSnapshot.child("Last name").getValue().toString();
                                                            friendRequestNotificationData.RequestUserFirstname = dataSnapshot.child("First name").getValue().toString();
                                                            friendRequestNotificationData.Type = "FriendRequest";
                                                            friendRequestNotificationData.RequestUserUid = dataSnapshot.getKey();
                                                            friendRequestNotificationData.CurrentuserUid = currentUserData.UserUid;
                                                            friendRequestNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                                            friendRequestNotificationDatas.add(friendRequestNotificationData);
                                                            updateNotificationdata();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                }

                                //check group request
                                groupRequestNotificationDatas.clear();
                                Map<String, String> GroupRequestMap = (Map<String, String>) dataSnapshot.child("request").child("group").getValue();
                                if (GroupRequestMap != null) {
                                    for (HashMap.Entry<String, String> entry : GroupRequestMap.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();

                                        if (value.equals("true")) {
                                            final NotificationData groupRequestNotificationData = new NotificationData();

                                            DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(key);
                                            requestFriendDatabaseReference
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            groupRequestNotificationData.RequestGroupname = dataSnapshot.child("name").getValue().toString();
                                                            groupRequestNotificationData.RequestGroupdetail = dataSnapshot.child("description").getValue().toString();
                                                            groupRequestNotificationData.Type = "GroupRequest";
                                                            groupRequestNotificationData.RequestGroupUid = dataSnapshot.getKey();
                                                            groupRequestNotificationData.CurrentuserUid = currentUserData.UserUid;
                                                            groupRequestNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                                            groupRequestNotificationDatas.add(groupRequestNotificationData);
                                                            updateNotificationdata();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                }

                                //check unread chat
                                unreadMassageNotificationDatas.clear();
                                Map<String, Object> massageMap = (Map<String, Object>) dataSnapshot.child("messages").getValue();
                                if (massageMap != null) {
                                    for (HashMap.Entry<String, Object> entry : massageMap.entrySet()) {
                                        String key = entry.getKey();
                                        Map<String, Object> value = (Map<String, Object>) entry.getValue();
                                        int unreadcount = 0;
                                        String sendermessage = "nomessage";

                                        for (HashMap.Entry<String, Object> entry2 : value.entrySet()) {
                                            String key2 = entry2.getKey();
                                            Map<String, String> value2 = (Map<String, String>) entry2.getValue();

                                            if (value2.get("read").equals("unread")) {
                                                unreadcount = unreadcount + 1;
                                                sendermessage = value2.get("message");
                                            }
                                        }


                                        if (unreadcount != 0) {
                                            final NotificationData unreadMassageNotificationData = new NotificationData();
                                            final int unreadcountdata = unreadcount;
                                            final String senderlastmessagedata = sendermessage;

                                            DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                            requestFriendDatabaseReference
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            unreadMassageNotificationData.SenderDisplayname = dataSnapshot.child("display name").getValue().toString();
                                                            unreadMassageNotificationData.Unreadcount = unreadcountdata;
                                                            unreadMassageNotificationData.SenderLastmessage = senderlastmessagedata;
                                                            unreadMassageNotificationData.Type = "Unread";
                                                            unreadMassageNotificationData.SenderUid = dataSnapshot.getKey();
                                                            unreadMassageNotificationData.CurrentuserUid = currentUserData.UserUid;
                                                            unreadMassageNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                                            unreadMassageNotificationDatas.add(unreadMassageNotificationData);
                                                            updateNotificationdata();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                }

                                updateUI();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }

                );
    }

    private void updateNotificationdata() {
        notificationDatas.clear();

        NotificationData notificationData = new NotificationData();
        notificationData.Type = "Title";
        notificationData.titlename = "Friend Request";
        notificationDatas.add(notificationData);
        notificationDatas.addAll(friendRequestNotificationDatas);

        notificationData = new NotificationData();
        notificationData.Type = "Title";
        notificationData.titlename = "Group Request";
        notificationDatas.add(notificationData);
        notificationDatas.addAll(groupRequestNotificationDatas);

        notificationData = new NotificationData();
        notificationData.Type = "Title";
        notificationData.titlename = "Unread Massage";
        notificationDatas.add(notificationData);
        notificationDatas.addAll(unreadMassageNotificationDatas);

        homeNotificationAdapter.notifyDataSetChanged();

    }

    private void updateUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView _showuser = (TextView) header.findViewById(R.id.txtshowuser);
        TextView _showuserEmail = (TextView) header.findViewById(R.id.txtShowuserEmail);
        _showuser.setText(currentUserData.displayname);
        _showuserEmail.setText(currentUserData.Email);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        homeNotificationAdapter = new HomeNotificationAdapter(this, notificationDatas, recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homeNotificationAdapter);

        updateNotificationdata();
    }
}
