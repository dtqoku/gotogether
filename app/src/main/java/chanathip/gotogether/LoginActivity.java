package chanathip.gotogether;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserData userData;
    private DatabaseReference databaseReference;

    @BindView(R.id.textinputlayout_txtUser)
    TextInputLayout _username;
    @BindView(R.id.textinputlayout_txtPass)
    TextInputLayout _password;
    @BindView(R.id.LoginCoordinatorLayout)
    View _loginCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    NotificationManager notificationManager = new NotificationManager(LoginActivity.this);
                    notificationManager.updateToken(firebaseUser.getUid(), FirebaseInstanceId.getInstance().getToken());
                    
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
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

    @OnClick(R.id.btnLogin)
    public void Login() {
        userData.Email = _username.getEditText().getText().toString();
        userData.password = _password.getEditText().getText().toString();
        if (CheckUserData()) {

            final DialogConect dialogConect;
            dialogConect = new DialogConect(this);
            dialogConect.setTitle("Login...");
            dialogConect.setMessage("Please wait");
            dialogConect.setCancelable(false);
            dialogConect.setCanceledOnTouchOutside(false);
            dialogConect.show();

            firebaseAuth.signInWithEmailAndPassword(userData.Email, userData.password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialogConect.dismiss();
                            if (task.isSuccessful()) {

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("login fail", e.toString());
                            Snackbar snackbar =
                                    Snackbar.make(_loginCoordinatorLayout, "Authentication failed, check your email and password.", Snackbar.LENGTH_LONG)
                                            .setActionTextColor(Color.WHITE);
                            snackbar.getView().setBackgroundColor(Color.RED);
                            snackbar.show();
                            dialogConect.dismiss();
                        }
                    });
        }
    }

    @OnClick(R.id.btnregis)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean CheckUserData() {
        if (userData.Email.length() == 0) {
            _username.setError("please type username");
            Snackbar snackbar =
                    Snackbar.make(_loginCoordinatorLayout, "Please input username", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else if (userData.password.length() == 0) {
            _password.setError("please type password");
            Snackbar snackbar =
                    Snackbar.make(_loginCoordinatorLayout, "Please input password", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
            snackbar.show();
            return false;
        } else {
            _username.setErrorEnabled(false);
            _password.setErrorEnabled(false);
        }
        return true;
    }

}
