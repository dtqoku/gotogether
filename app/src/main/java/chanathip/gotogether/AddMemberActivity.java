package chanathip.gotogether;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class AddMemberActivity extends AppCompatActivity {

    @BindView(R.id.searchViewfriend)
    SearchView _searchViewfriend;
    @BindView(R.id.empty_view)
    TextView empty_view;

    private GroupData groupData;
    private UserData userData;
    private AddMemberAdapter addMemberAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<UserData> friendUserdatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        ButterKnife.bind(this);

        groupData = new GroupData();
        userData = new UserData();
        friendUserdatas = new ArrayList<>();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                groupData.GroupUID = null;
                groupData.Name = null;
                userData.UserUid = null;
                userData.displayname = null;
            } else {
                groupData.GroupUID = extras.getString("GroupUID");
                groupData.Name = extras.getString("GroupName");
                userData.UserUid = extras.getString("UserUid");
                userData.displayname = extras.getString("UserDisplayname");
            }
        } else {
            groupData.GroupUID = (String) savedInstanceState.getSerializable("GroupUID");
            groupData.Name = (String) savedInstanceState.getSerializable("GroupName");
            userData.UserUid = (String) savedInstanceState.getSerializable("UserUid");
            userData.displayname = (String) savedInstanceState.getSerializable("UserDisplayname");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getcurrentuserdata();

    }private void getcurrentuserdata() {
        DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.UserUid);
        currentuserDatabaseReference.keepSynced(true);
        currentuserDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userData.setData(
                                userData.UserUid,
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
                                                    friendUserdata.GroupUid = groupData.GroupUID;
                                                    friendUserdata.GroupName = groupData.Name;

                                                    friendUserdatas.add(friendUserdata);
                                                    updateUI();

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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("GroupUID", groupData.GroupUID);
        savedInstanceState.putString("GroupName", groupData.Name);
        savedInstanceState.putString("UserUid", userData.UserUid);
        savedInstanceState.putString("UserDisplayname", userData.displayname);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateUI() {
        _searchViewfriend = (SearchView) findViewById(R.id.searchViewfriend);
        _searchViewfriend.onActionViewExpanded();
        _searchViewfriend.clearFocus();
        _searchViewfriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addMemberAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addMemberAdapter.filter(newText);
                return false;
            }
        });

        addMemberAdapter = new AddMemberAdapter(this, friendUserdatas, _searchViewfriend, userData);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(addMemberAdapter);

        if (friendUserdatas.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }



    }


}
