package chanathip.gotogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class FriendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.searchViewfriend)
    SearchView _searchViewfriend;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.empty_view)
    TextView empty_view;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserData currentUserData;
    private List<UserData> friendUserdatas;
    private DatabaseReference databaseReference;
    private FriendListAdapter friendListAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        ButterKnife.bind(this);

        currentUserData = new UserData();
        friendUserdatas = new ArrayList<>();

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
                    Intent intent = new Intent(FriendActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        friendUserdatas.clear();
        navigationView.setCheckedItem(R.id.nav_friend);

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
            super.onBackPressed();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_group) {
            Intent intent = new Intent(this, GroupActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_friend) {
            //this
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
        currentuserDatabaseReference.addListenerForSingleValueEvent(
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


                        Map<String, String> friendUserdataMap = (Map<String, String>) dataSnapshot.child("friend").getValue();
                        if (friendUserdataMap != null) {
                            for (HashMap.Entry<String, String> entry : friendUserdataMap.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                if (value.equals("true")) {
                                    final UserData friendUserdata = new UserData();
                                    final String Uid = key;

                                    DatabaseReference FriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                    FriendDatabaseReference
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    friendUserdata.UserUid = Uid;
                                                    friendUserdata.Firstname = dataSnapshot.child("First name").getValue().toString();
                                                    friendUserdata.Lastname = dataSnapshot.child("Last name").getValue().toString();
                                                    friendUserdata.displayname = dataSnapshot.child("display name").getValue().toString();
                                                    friendUserdata.Email = dataSnapshot.child("email").getValue().toString();
                                                    friendUserdata.Phone = dataSnapshot.child("Phone").getValue().toString();

                                                    DatabaseReference checkUnreadDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.UserUid)
                                                            .child("messages").child(Uid);
                                                    checkUnreadDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Map<String, Object> massageMap = (Map<String, Object>) dataSnapshot.getValue();
                                                            if (massageMap != null) {
                                                                int unreadcount = 0;
                                                                for (HashMap.Entry<String, Object> entry : massageMap.entrySet()) {
                                                                    String key2 = entry.getKey();
                                                                    Map<String, String> value2 = (Map<String, String>) entry.getValue();

                                                                    if (value2.get("read").equals("unread")) {
                                                                        unreadcount = unreadcount + 1;
                                                                    }
                                                                }

                                                                friendUserdata.unreadMassage = unreadcount;
                                                            } else {
                                                                friendUserdata.unreadMassage = 0;
                                                            }

                                                            friendUserdatas.add(friendUserdata);
                                                            updateUI();
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

    private void updateUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendActivity.this, AddFriendActivity.class);
                startActivity(intent);
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


        _searchViewfriend = (SearchView) findViewById(R.id.searchViewfriend);
        _searchViewfriend.onActionViewExpanded();
        _searchViewfriend.clearFocus();
        _searchViewfriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                friendListAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendListAdapter.filter(newText);
                return false;
            }
        });

        friendListAdapter = new FriendListAdapter(this, friendUserdatas, _searchViewfriend, currentUserData);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(friendListAdapter);
        final FloatingActionButton fabscroll = fab;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fabscroll.hide();
                else
                    fabscroll.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (friendUserdatas.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }



    }
}
