package chanathip.gotogether;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationEmailActivity extends AppCompatActivity {
    Button btnsend;
    TextView textinfo;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnsend = (Button) findViewById(R.id.btnResend);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser!=null){
                    firebaseUser.sendEmailVerification();
                    Snackbar snackbar =
                            Snackbar.make(btnsend, "sent verification e-mail", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                    snackbar.show();
                }
            }
        });
        textinfo = (TextView) findViewById(R.id.textView2);
        textinfo.setText("your email (" + firebaseUser.getEmail() + ") is not verification yet,please check your email");
        imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().signOut();
        super.onStop();
    }
    @Override
    protected void onPause(){
        FirebaseAuth.getInstance().signOut();
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}
