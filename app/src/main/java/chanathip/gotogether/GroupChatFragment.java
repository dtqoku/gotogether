package chanathip.gotogether;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by neetc on 11/15/2016.
 */

public class GroupChatFragment extends Fragment {
    public GroupChatFragment(){

    }

    public static GroupChatFragment newInstance() {
        GroupChatFragment fragment = new GroupChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);
        return rootView;
    }
}
