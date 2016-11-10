package chanathip.gotogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserdetailActivity extends AppCompatActivity {
    private UserData currentViewUserData;
    private UserData currentUserData;
    private GotogetherNotificationManager gotogetherNotificationManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @BindView(R.id.txtdisplayname)
    TextView _displayname;
    @BindView(R.id.txtFirstname)
    TextView _firstname;
    @BindView(R.id.txtLastname)
    TextView _lastname;
    @BindView(R.id.txtEmail)
    TextView _email;
    @BindView(R.id.txtPhone)
    TextView _phone;
    @BindView(R.id.UserdeailCoordinatorLayout)
    View _UserdeailCoordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        currentViewUserData = new UserData();
        currentUserData = new UserData();
        gotogetherNotificationManager = new GotogetherNotificationManager(this);

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                currentViewUserData.UserUid = null;
                currentViewUserData.displayname = null;
            } else {
                currentViewUserData.UserUid = extras.getString("userUid");
                currentViewUserData.displayname = extras.getString("userDisplayname");
            }
        } else {
            currentViewUserData.UserUid = (String) savedInstanceState.getSerializable("userUid");
            currentViewUserData.displayname = (String) savedInstanceState.getSerializable("userDisplayname");
        }

        toolbar.setTitle(currentViewUserData.displayname);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get currentuserdata
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    currentUserData.UserUid = firebaseUser.getUid();
                    DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.UserUid);
                    currentuserDatabaseReference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentUserData.setData(
                                            currentUserData.UserUid,
                                            dataSnapshot.child("First name").getValue().toString(),
                                            dataSnapshot.child("Last name").getValue().toString(),
                                            dataSnapshot.child("display name").getValue().toString(),
                                            currentUserData.Email,
                                            dataSnapshot.child("Phone").getValue().toString()
                                    );
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                else{
                    Intent intent = new Intent(UserdetailActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);

        //get current view userdetail
        DatabaseReference userViewDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.UserUid);
        userViewDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Map<String, Object> userdataMap = (Map<String, Object>) dataSnapshot.getValue();

                    currentViewUserData.displayname = String.valueOf(userdataMap.get("display name"));
                    currentViewUserData.Firstname = String.valueOf(userdataMap.get("First name"));
                    currentViewUserData.Lastname = String.valueOf(userdataMap.get("Last name"));
                    currentViewUserData.Email = String.valueOf(userdataMap.get("email"));
                    currentViewUserData.Phone = String.valueOf(userdataMap.get("phone"));
                    //currentViewUserData.FriendList = new ArrayList<>(userdataMap.get("friend"));

                    updateContectdetail();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("userdetailView", databaseError.toString());
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("userUid", currentViewUserData.UserUid);
        savedInstanceState.putString("userDisplayname", currentViewUserData.displayname);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateContectdetail(){
        _displayname.setText(currentViewUserData.displayname);
        _firstname.setText(currentViewUserData.Firstname);
        _lastname.setText(currentViewUserData.Lastname);
        _email.setText(currentViewUserData.Email);
        if (currentViewUserData.Phone.contains("null"))
            _phone.setText("");
        else
            _phone.setText(currentViewUserData.Phone);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotogetherNotificationManager.sendFriendRequest(currentViewUserData.UserUid,currentUserData.displayname);

                DatabaseReference userViewDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.UserUid);
                userViewDatabaseReference.child("request").child("friend").child(currentUserData.UserUid).setValue("true");

                Snackbar snackbar = Snackbar.make(_UserdeailCoordinatorLayout, "sent friend request to " + currentViewUserData.displayname, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }


}
