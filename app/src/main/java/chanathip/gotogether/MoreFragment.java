package chanathip.gotogether;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by neetc on 4/5/2017.
 */

public class MoreFragment extends Fragment {
    private Context context;
    private CardView cv_exit;
    private Switch locationSwitch;

    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cv_exit = (CardView) view.findViewById(R.id.cv_exit);
        cv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                gotogetherNotificationManager.deleteToken(FirebaseAuth.getInstance().getCurrentUser().getUid());

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference userLocationStatusRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status");
        userLocationStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("active")) {
                    locationSwitch.setChecked(true);
                } else
                    locationSwitch.setChecked(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        locationSwitch = (Switch) view.findViewById(R.id.switch_location);
        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationSwitch.isChecked()) {
                    ((onSharedLocationListener) context).onSharedLocationChange(true, locationSwitch);
                } else {
                    ((onSharedLocationListener) context).onSharedLocationChange(false, locationSwitch);
                }
            }
        });
    }

    public interface onSharedLocationListener {
        void onSharedLocationChange(boolean isShared, Switch locationSwitch);
    }
}
