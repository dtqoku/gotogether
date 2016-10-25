package chanathip.gotogether;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserData userData;
    private DatabaseReference databaseReference;

    @BindView(R.id.textinputlayout_txtPassword)
    TextInputLayout _password;
    @BindView(R.id.textinputlayout_txtPassword2)
    TextInputLayout _password2;
    @BindView(R.id.textinputlayout_txtFirstname)
    TextInputLayout _firstname;
    @BindView(R.id.textinputlayout_txtLastname)
    TextInputLayout _lastname;
    @BindView(R.id.textinputlayout_txtEmail)
    TextInputLayout _email;
    @BindView(R.id.textinputlayout_txtphone)
    TextInputLayout _phone;
    @BindView(R.id.textinputlayout_txtdisplayname)
    TextInputLayout _displayname;
    @BindView(R.id.RegisterCoordinatorLayout)
    View _registerCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                    DatabaseReference userdetail = databaseReference.child(userData.Emailwithoutadd);
                    userdetail.child("First name").setValue(userData.Firstname);
                    userdetail.child("Last name").setValue(userData.Lastname);
                    userdetail.child("display name").setValue(userData.displayname);
                    userdetail.child("Phone").setValue(userData.Phone);

                    // TODO: 10/25/2016 after create user
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

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.btnSubmit)
    public void submit() {
        userData.password = _password.getEditText().getText().toString();
        userData.Firstname = _firstname.getEditText().getText().toString();
        userData.Lastname = _lastname.getEditText().getText().toString();
        userData.Email = _email.getEditText().getText().toString();
        userData.Phone = _phone.getEditText().getText().toString();
        userData.displayname = _displayname.getEditText().getText().toString();
        if (CheckUserData()) {
            Snackbar snackbar = Snackbar.make(_registerCoordinatorLayout, "Register...", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();

            CheckEmailDuplicate();

        }
        _password2.getEditText().setText("");
    }

    private boolean CheckUserData() {
        _password.setErrorEnabled(false);
        _password2.setErrorEnabled(false);
        _firstname.setErrorEnabled(false);
        _lastname.setErrorEnabled(false);
        _email.setErrorEnabled(false);
        _displayname.setErrorEnabled(false);
        String password2 = _password2.getEditText().getText().toString();
        if (userData.password.length() == 0) {
            _password.setError("please type password");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input password", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (password2.length() == 0) {
            _password2.setError("please type password again");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input password again", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.password.length() < 8 || userData.password.length() > 12) {
            _password.setError("password should be 8-12 character");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Password must be 8-12 character", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (!userData.password.equals(password2)) {
            _password.setError("password not match");
            _password2.setError("password not match");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Password not match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.Firstname.length() == 0) {
            _firstname.setError("please type your First name");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input your First name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.Lastname.length() == 0) {
            _lastname.setError("please type your Last name");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input Last name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.displayname.length() == 0) {
            _displayname.setError("please type your display name");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input display name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.Email.length() == 0) {
            _email.setError("please type your e-mail");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "Please input E-mail", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (!userData.Email.contains("@")) {
            _email.setError("e-mail not valid");
            Snackbar snackbar =
                    Snackbar.make(_registerCoordinatorLayout, "e-mail not valid", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else {
            _password.setErrorEnabled(false);
            _password2.setErrorEnabled(false);
            _firstname.setErrorEnabled(false);
            _lastname.setErrorEnabled(false);
            _email.setErrorEnabled(false);
            _displayname.setErrorEnabled(false);
        }
        return true;
    }

    private void CheckEmailDuplicate() {
        userData.Emailwithoutadd = (userData.Email.split("@"))[0];

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(userData.Emailwithoutadd)) {
                    firebaseAuth.createUserWithEmailAndPassword(userData.Email, userData.password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Snackbar snackbar =
                                                Snackbar.make(_registerCoordinatorLayout, "Authentication failed.", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null);
                                        snackbar.show();
                                    }
                                }
                            });
                } else {
                    Snackbar snackbar = Snackbar.make(_registerCoordinatorLayout, "Authentication failed,Email Duplicate.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
