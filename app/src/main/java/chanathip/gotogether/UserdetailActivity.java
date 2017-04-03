package chanathip.gotogether;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    @BindView(R.id.lbl_Operation)
    TextView _lbl_Operation;
    @BindView(R.id.cv2)
    CardView _cv2;
    @BindView(R.id.btnunfriend)
    Button btnUnfriend;
    @BindView(R.id.btnchat)
    Button btnChat;


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
                currentViewUserData.userUid = null;
                currentViewUserData.displayname = null;
            } else {
                currentViewUserData.userUid = extras.getString("userUid");
                currentViewUserData.displayname = extras.getString("userDisplayname");
            }
        } else {
            currentViewUserData.userUid = (String) savedInstanceState.getSerializable("userUid");
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
                    currentUserData.userUid = firebaseUser.getUid();
                    DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.userUid);
                    currentuserDatabaseReference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentUserData.setData(
                                            currentUserData.userUid,
                                            dataSnapshot.child("display name").getValue().toString(),
                                            currentUserData.email
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
        DatabaseReference userViewDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.userUid);
        userViewDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Map<String, Object> userdataMap = (Map<String, Object>) dataSnapshot.getValue();

                    currentViewUserData.displayname = String.valueOf(userdataMap.get("display name"));
                    currentViewUserData.email = String.valueOf(userdataMap.get("email"));
                    currentViewUserData.phone = String.valueOf(userdataMap.get("phone"));

                    if(dataSnapshot.child("request").child("friend").getValue() != null){
                        currentViewUserData.FriendStatus = "request";
                    }
                    else if(dataSnapshot.child("friend").child(currentUserData.userUid).getValue() != null){
                        currentViewUserData.FriendStatus = "friend";
                    }
                    else{
                        currentViewUserData.FriendStatus = "notfriend";
                    }

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
        savedInstanceState.putString("userUid", currentViewUserData.userUid);
        savedInstanceState.putString("userDisplayname", currentViewUserData.displayname);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateContectdetail(){
        _displayname.setText(currentViewUserData.displayname);
        _email.setText(currentViewUserData.email);
        if (currentViewUserData.phone.contains("null"))
            _phone.setText("");
        else
            _phone.setText(currentViewUserData.phone);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(currentViewUserData.FriendStatus.equals("request")){
            fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_access_time_black_24dp));
        }
        else if(currentViewUserData.FriendStatus.equals("friend")){
            fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_comment_black_24dp));

            _lbl_Operation.setVisibility(View.VISIBLE);
            _cv2.setVisibility(View.VISIBLE);

            btnUnfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserdetailActivity.this);
                    builder.setMessage("Are you sure to unfriend?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.userUid);
                                    currentuserdatabaseReference.child("friend").child(currentViewUserData.userUid).removeValue();

                                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.userUid);
                                    requestuserdatabaseReference.child("friend").child(currentUserData.userUid).removeValue();

                                    Snackbar snackbar = Snackbar.make(_cv2, "unfriend with " + currentViewUserData.displayname, Snackbar.LENGTH_LONG);
                                    snackbar.show();

                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog,int id){
                                    //cancel
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserdetailActivity.this,PersonChatActivity.class);
                    intent.putExtra("currentChatuserUid",currentViewUserData.userUid);
                    intent.putExtra("currentChatuserDisplayname",currentViewUserData.displayname);
                    intent.putExtra("userUid",currentUserData.userUid);
                    intent.putExtra("UserDisplayname",currentUserData.displayname);
                    startActivity(intent);
                }
            });
        } else if(currentUserData.userUid.equals(currentViewUserData.userUid)){
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentViewUserData.FriendStatus.equals("request")){
                    final FloatingActionButton fabtemp = fab;
                    DatabaseReference currentViewUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.userUid);
                    currentViewUserDatabaseReference.child("request").child("friend").child(currentUserData.userUid).removeValue();

                    Snackbar snackbar = Snackbar.make(_UserdeailCoordinatorLayout, "cancel " + currentViewUserData.displayname + " friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    fabtemp.setImageDrawable(ContextCompat.getDrawable(UserdetailActivity.this,R.drawable.ic_group_add_black_24dp));
                }
                else if(currentViewUserData.FriendStatus.equals("friend")){
                    Intent intent = new Intent(UserdetailActivity.this,PersonChatActivity.class);
                    intent.putExtra("currentChatuserUid",currentViewUserData.userUid);
                    intent.putExtra("currentChatuserDisplayname",currentViewUserData.displayname);
                    intent.putExtra("userUid",currentUserData.userUid);
                    intent.putExtra("UserDisplayname",currentUserData.displayname);
                    startActivity(intent);
                }
                else {
                    gotogetherNotificationManager.sendFriendRequest(currentViewUserData.userUid, currentUserData.displayname);

                    DatabaseReference userViewDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentViewUserData.userUid);
                    userViewDatabaseReference.child("request").child("friend").child(currentUserData.userUid).setValue("true");

                    Snackbar snackbar = Snackbar.make(_UserdeailCoordinatorLayout, "sent friend request to " + currentViewUserData.displayname, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }


}
