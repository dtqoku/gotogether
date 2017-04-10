package chanathip.gotogether;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Created by neetc on 4/5/2017.
 */

public class SocialFragment extends Fragment {
    private class OnUserSocialDetailChange implements ValueEventListener {
        private void updateDataList(List<SocialData> socialDataList, SocialData socialData) {
            boolean isListcontains = false;
            for (SocialData item : socialDataList) {

                if (socialData.Type.equals("Group") && item.groupData.GroupUID.equals(socialData.groupData.GroupUID)) {
                    isListcontains = true;
                    socialDataList.remove(item);
                    break;
                } else if (socialData.Type.equals("FriendList") &&
                        item.userData.userUid.equals(socialData.userData.userUid)) {
                    isListcontains = true;
                    socialDataList.remove(item);
                    break;
                }
            }
            socialDataList.add(socialData);

        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentUserData.setData(
                    currentUserData.userUid,
                    dataSnapshot.child("display name").getValue().toString(),
                    dataSnapshot.child("email").getValue().toString()
            );

            //get group list
            //check group
            groupDatas.clear();
            Map<String, String> groupUserdataMap = (Map<String, String>) dataSnapshot.child("group").getValue();
            if (groupUserdataMap != null) {
                for (HashMap.Entry<String, String> entry : groupUserdataMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    final SocialData groupData = new SocialData();
                    final String GroupUid = key;
                    final String Rank = value;

                    DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(key);
                    groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            groupData.groupData = new GroupData();
                            groupData.groupData.setData(
                                    GroupUid,
                                    String.valueOf(dataSnapshot.child("name").getValue()),
                                    String.valueOf(dataSnapshot.child("description").getValue()),
                                    Rank,
                                    String.valueOf(dataSnapshot.child("settingpoint").getValue()),
                                    String.valueOf(dataSnapshot.child("membercount").getValue()),
                                    currentUserData.userUid
                            );
                            groupData.Type = "Group";
                            groupData.CurrentuserUid = currentUserData.userUid;
                            groupData.CurrentuserDisplayname = currentUserData.displayname;


                            //groupDatas.add(groupData);
                            updateDataList(groupDatas, groupData);


                            updateUI();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            //get friend list
            friendUserdatas.clear();
            Map<String, String> friendUserdataMap = (Map<String, String>) dataSnapshot.child("friend").getValue();
            if (friendUserdataMap != null) {
                for (HashMap.Entry<String, String> entry : friendUserdataMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (value.equals("true")) {
                        final UserData friendUserdata = new UserData();
                        final String Uid = key;

                        DatabaseReference FriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                        FriendDatabaseReference
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        friendUserdata.userUid = Uid;
                                        friendUserdata.displayname = dataSnapshot.child("display name").getValue().toString();
                                        friendUserdata.email = dataSnapshot.child("email").getValue().toString();

                                        DatabaseReference checkUnreadDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.userUid)
                                                .child("messages").child(Uid);
                                        checkUnreadDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> massageMap = (Map<String, Object>) dataSnapshot.getValue();
                                                if (massageMap != null) {
                                                    int unreadcount = 0;
                                                    for (HashMap.Entry<String, Object> entry : massageMap.entrySet()) {
                                                        String key2 = entry.getKey();
                                                        Map<String, String> value2 = (Map<String, String>) entry.getValue();
                                                        if (value2.get("read") != null) {

                                                            if (value2.get("read").equals("unread")) {
                                                                unreadcount = unreadcount + 1;
                                                            }
                                                        }
                                                    }

                                                    friendUserdata.unreadMassage = unreadcount;
                                                } else {
                                                    friendUserdata.unreadMassage = 0;
                                                }

                                                SocialData friendSocialData = new SocialData();
                                                friendSocialData.userData = friendUserdata;
                                                friendSocialData.Type = "FriendList";
                                                friendSocialData.CurrentuserUid = currentUserData.userUid;
                                                friendSocialData.CurrentuserDisplayname = currentUserData.displayname;

                                                //friendUserdatas.add(friendSocialData);
                                                updateDataList(friendUserdatas, friendSocialData);
                                                updateUI();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                }
            }

            updateUI();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SocialAdapter socialAdapter;
    private List<SocialData> friendUserdatas;
    private List<SocialData> socialDatas;
    private List<SocialData> groupDatas;
    private UserData currentUserData;

    private OnUserSocialDetailChange onUserSocialDetailChange;
    private DatabaseReference currentuserDatabaseReference;

    public static SocialFragment newInstance() {
        SocialFragment fragment = new SocialFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_group_detail, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        socialAdapter = new SocialAdapter(context, socialDatas, recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(socialAdapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserData.userUid = firebaseUser.getUid();
        currentUserData.email = firebaseUser.getEmail();

        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    floatingActionMenu.hideMenuButton(true);
                else
                    floatingActionMenu.showMenuButton(true);
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendUserdatas = new ArrayList<>();
        socialDatas = new ArrayList<>();
        currentUserData = new UserData();
        groupDatas = new ArrayList<>();
        onUserSocialDetailChange = new OnUserSocialDetailChange();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.userUid);
        currentuserDatabaseReference.keepSynced(true);
        currentuserDatabaseReference.addValueEventListener(onUserSocialDetailChange);
    }

    @Override
    public void onPause() {
        super.onPause();
        currentuserDatabaseReference.removeEventListener(onUserSocialDetailChange);
    }

    @Override
    public void onStop() {
        super.onStop();
        currentuserDatabaseReference.removeEventListener(onUserSocialDetailChange);
    }

    private void updateUI() {
        socialDatas.clear();

        SocialData socialData = new SocialData();

        if (!groupDatas.isEmpty()) {

            socialData.Type = "Title";
            socialData.titlename = "group";
            socialDatas.add(socialData);
            socialDatas.addAll(groupDatas);
        }

        if (!friendUserdatas.isEmpty()) {
            socialData = new SocialData();
            socialData.Type = "Title";
            socialData.titlename = "Friend";
            socialDatas.add(socialData);
            socialDatas.addAll(friendUserdatas);

        }

        socialAdapter.notifyDataSetChanged();
    }
}
