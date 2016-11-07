package chanathip.gotogether;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
    private DatabaseReference databaseReference;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetail);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        currentViewUserData = new UserData();

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
    }
}
