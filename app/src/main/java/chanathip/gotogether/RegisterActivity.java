package chanathip.gotogether;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserData userData;
    private DatabaseReference databaseReference;

    @BindView(R.id.textinputlayout_txtPass)
    TextInputLayout _password;
    @BindView(R.id.textinputlayout_txtPass2)
    TextInputLayout _password2;
    @BindView(R.id.textinputlayout_txtUser)
    TextInputLayout _email;
    @BindView(R.id.textinputlayout_txtDisplayname)
    TextInputLayout _displayname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v2);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userData.displayname)
                            .build();

                    firebaseUser.updateProfile(userProfileChangeRequest);

                    DatabaseReference userdetail = databaseReference.child(firebaseUser.getUid());
                    userdetail.child("display name").setValue(userData.displayname);
                    userdetail.child("phone").setValue(userData.phone);
                    userdetail.child("email").setValue(firebaseUser.getEmail());

                    onBackPressed();
                }
            }
        };

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        userData = new UserData();

    }

    @Override
    protected void onStart() {
        super.onStart();

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
    protected void onPause(){
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.btnSubmit)
    public void submit() {
        userData.password = _password.getEditText().getText().toString();
        userData.email = _email.getEditText().getText().toString();
        userData.displayname = _displayname.getEditText().getText().toString();
        if (CheckUserData()) {

            final DialogConect dialogConect;
            dialogConect = new DialogConect(this);
            dialogConect.setTitle("Registering...");
            dialogConect.setMessage("Please wait");
            dialogConect.setCancelable(false);
            dialogConect.setCanceledOnTouchOutside(false);
            dialogConect.show();

            firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialogConect.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogConect.dismiss();
                            Log.d("create user fail",e.toString());
                            Snackbar snackbar = Snackbar.make(_displayname, "Authentication failed ," + e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(Color.WHITE);
                            snackbar.getView().setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    });

        }
        _password2.getEditText().setText("");
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

    private boolean CheckUserData() {
        _password.setErrorEnabled(false);
        _password2.setErrorEnabled(false);
        _email.setErrorEnabled(false);
        _displayname.setErrorEnabled(false);
        String password2 = _password2.getEditText().getText().toString();
        if (userData.password.length() == 0) {
            _password.setError("please type password");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Please input password", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (password2.length() == 0) {
            _password2.setError("please type password again");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Please input password again", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.password.length() < 8 || userData.password.length() > 12) {
            _password.setError("password should be 8-12 character");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Password must be 8-12 character", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (!userData.password.equals(password2)) {
            _password.setError("password not match");
            _password2.setError("password not match");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Password not match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.displayname.length() == 0) {
            _displayname.setError("please type your display name");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Please input display name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.email.length() == 0) {
            _email.setError("please type your e-mail");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "Please input E-mail", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (!userData.email.contains("@")) {
            _email.setError("e-mail not valid");
            Snackbar snackbar =
                    Snackbar.make(_displayname, "e-mail not valid", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else {
            _password.setErrorEnabled(false);
            _password2.setErrorEnabled(false);
            _email.setErrorEnabled(false);
            _displayname.setErrorEnabled(false);
        }
        return true;
    }
}
