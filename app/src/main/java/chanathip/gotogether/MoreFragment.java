package chanathip.gotogether;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by neetc on 4/5/2017.
 */

public class MoreFragment extends Fragment {
    private Context context;
    private CardView cv_exit;

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
    }
}
