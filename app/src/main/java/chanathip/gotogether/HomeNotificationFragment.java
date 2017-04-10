package chanathip.gotogether;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neetc on 4/5/2017.
 */

public class HomeNotificationFragment extends Fragment {
    private class OnNotificationChange implements ValueEventListener {
        private void updateDataList(List<NotificationData> notificationDataList, NotificationData notificationData) {
            for (NotificationData item : notificationDataList) {
                if (notificationData.Type.equals("Group") && item.groupData.GroupUID.equals(notificationData.groupData.GroupUID)) {
                    notificationDataList.remove(item);
                    break;
                }
            }
            notificationDataList.add(notificationData);
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentUserData.setData(
                    currentUserData.userUid,
                    dataSnapshot.child("display name").getValue().toString(),
                    dataSnapshot.child("email").getValue().toString()
            );


            //check friend request
            friendRequestNotificationDatas.clear();
            Map<String, String> FriendRequestMap = (Map<String, String>) dataSnapshot.child("request").child("friend").getValue();
            if (FriendRequestMap != null) {
                for (HashMap.Entry<String, String> entry : FriendRequestMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (value.equals("true")) {
                        final NotificationData friendRequestNotificationData = new NotificationData();

                        DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                        requestFriendDatabaseReference
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        friendRequestNotificationData.RequestUserdisplayname = dataSnapshot.child("display name").getValue().toString();
                                        friendRequestNotificationData.Type = "FriendRequest";
                                        friendRequestNotificationData.RequestUserUid = dataSnapshot.getKey();
                                        friendRequestNotificationData.requestEmail = dataSnapshot.child("email").getValue().toString();
                                        friendRequestNotificationData.CurrentuserUid = currentUserData.userUid;
                                        friendRequestNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                        friendRequestNotificationDatas.add(friendRequestNotificationData);
                                        updateNotificationdata();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            //check group request
            groupRequestNotificationDatas.clear();
            Map<String, String> GroupRequestMap = (Map<String, String>) dataSnapshot.child("request").child("group").getValue();
            if (GroupRequestMap != null) {
                for (HashMap.Entry<String, String> entry : GroupRequestMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (value.equals("true")) {
                        final NotificationData groupRequestNotificationData = new NotificationData();

                        DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(key);
                        requestFriendDatabaseReference
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        groupRequestNotificationData.RequestGroupname = dataSnapshot.child("name").getValue().toString();
                                        groupRequestNotificationData.RequestGroupdetail = dataSnapshot.child("description").getValue().toString();
                                        groupRequestNotificationData.Type = "GroupRequest";
                                        groupRequestNotificationData.RequestGroupUid = dataSnapshot.getKey();
                                        groupRequestNotificationData.CurrentuserUid = currentUserData.userUid;
                                        groupRequestNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                        groupRequestNotificationDatas.add(groupRequestNotificationData);
                                        updateNotificationdata();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            //check unread chat
            unreadMassageNotificationDatas.clear();
            Map<String, Object> massageMap = (Map<String, Object>) dataSnapshot.child("messages").getValue();
            if (massageMap != null) {
                for (HashMap.Entry<String, Object> entry : massageMap.entrySet()) {
                    String key = entry.getKey();
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    int unreadcount = 0;
                    String sendermessage = "nomessage";

                    for (HashMap.Entry<String, Object> entry2 : value.entrySet()) {
                        String key2 = entry2.getKey();
                        Map<String, String> value2 = (Map<String, String>) entry2.getValue();

                        if (value2.get("read") != null) {
                            if (value2.get("read").equals("unread")) {
                                unreadcount = unreadcount + 1;
                                sendermessage = value2.get("message");
                            }
                        }
                    }


                    if (unreadcount != 0) {
                        final NotificationData unreadMassageNotificationData = new NotificationData();
                        final int unreadcountdata = unreadcount;
                        final String senderlastmessagedata = sendermessage;

                        DatabaseReference requestFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                        requestFriendDatabaseReference
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        unreadMassageNotificationData.SenderDisplayname = dataSnapshot.child("display name").getValue().toString();
                                        unreadMassageNotificationData.Unreadcount = unreadcountdata;
                                        unreadMassageNotificationData.SenderLastmessage = senderlastmessagedata;
                                        unreadMassageNotificationData.Type = "Unread";
                                        unreadMassageNotificationData.SenderUid = dataSnapshot.getKey();
                                        unreadMassageNotificationData.CurrentuserUid = currentUserData.userUid;
                                        unreadMassageNotificationData.CurrentuserDisplayname = currentUserData.displayname;


                                        unreadMassageNotificationDatas.add(unreadMassageNotificationData);
                                        updateNotificationdata();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            //check group meeting point active
            groupNotificationDatas.clear();
            Map<String, String> groupUserdataMap = (Map<String, String>) dataSnapshot.child("group").getValue();
            if (groupUserdataMap != null) {
                for (HashMap.Entry<String, String> entry : groupUserdataMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    final NotificationData groupNotificationData = new NotificationData();
                    final String GroupUid = key;
                    final String Rank = value;

                    DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(key);
                    groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            groupNotificationData.groupData = new GroupData();
                            groupNotificationData.groupData.setData(
                                    GroupUid,
                                    String.valueOf(dataSnapshot.child("name").getValue()),
                                    String.valueOf(dataSnapshot.child("description").getValue()),
                                    Rank,
                                    String.valueOf(dataSnapshot.child("settingpoint").getValue()),
                                    String.valueOf(dataSnapshot.child("membercount").getValue()),
                                    currentUserData.userUid
                            );
                            groupNotificationData.Type = "Group";
                            groupNotificationData.CurrentuserUid = currentUserData.userUid;
                            groupNotificationData.CurrentuserDisplayname = currentUserData.displayname;

                            if(groupNotificationData.groupData.isMeetingPointSet()){
                                //groupNotificationDatas.add(groupNotificationData);
                                updateDataList(groupNotificationDatas,groupNotificationData);
                            }

                            updateNotificationdata();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private HomeNotificationAdapter homeNotificationAdapter;
    private TextView emptyView;

    private UserData currentUserData;

    private List<NotificationData> notificationDatas;
    private List<NotificationData> friendRequestNotificationDatas;
    private List<NotificationData> unreadMassageNotificationDatas;
    private List<NotificationData> groupRequestNotificationDatas;
    private List<NotificationData> groupNotificationDatas;

    private OnNotificationChange onNotificationChange;
    private DatabaseReference currentuserDatabaseReference;

    public static HomeNotificationFragment newInstance() {
        HomeNotificationFragment fragment = new HomeNotificationFragment();
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
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        homeNotificationAdapter = new HomeNotificationAdapter(context, notificationDatas, recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homeNotificationAdapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserData.userUid = firebaseUser.getUid();
        currentUserData.email = firebaseUser.getEmail();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationDatas = new ArrayList<>();
        friendRequestNotificationDatas = new ArrayList<>();
        unreadMassageNotificationDatas = new ArrayList<>();
        groupRequestNotificationDatas = new ArrayList<>();
        groupNotificationDatas = new ArrayList<>();
        currentUserData = new UserData();
        onNotificationChange = new OnNotificationChange();
    }

    @Override
    public void onStart() {
        super.onStart();
        currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserData.userUid);
        currentuserDatabaseReference.addValueEventListener(onNotificationChange);
    }

    @Override
    public void onStop() {
        super.onStop();

        currentuserDatabaseReference.removeEventListener(onNotificationChange);
    }

    @Override
    public void onPause() {
        super.onPause();
        currentuserDatabaseReference.removeEventListener(onNotificationChange);
    }

    private void updateNotificationdata() {
        notificationDatas.clear();

        NotificationData notificationData = new NotificationData();

        if (!friendRequestNotificationDatas.isEmpty()) {
            notificationData.Type = "Title";
            notificationData.titlename = "Friend Request";
            notificationDatas.add(notificationData);
            notificationDatas.addAll(friendRequestNotificationDatas);
        }

        if (!groupRequestNotificationDatas.isEmpty()) {
            notificationData = new NotificationData();
            notificationData.Type = "Title";
            notificationData.titlename = "Group Request";
            notificationDatas.add(notificationData);
            notificationDatas.addAll(groupRequestNotificationDatas);
        }
        if (!unreadMassageNotificationDatas.isEmpty()) {
            notificationData = new NotificationData();
            notificationData.Type = "Title";
            notificationData.titlename = "Unread Massage";
            notificationDatas.add(notificationData);
            notificationDatas.addAll(unreadMassageNotificationDatas);
        }
        if (!groupNotificationDatas.isEmpty()) {
            notificationData = new NotificationData();
            notificationData.Type = "Title";
            notificationData.titlename = "Meet point set!";
            notificationDatas.add(notificationData);
            notificationDatas.addAll(groupNotificationDatas);
        }

        if (notificationDatas.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        homeNotificationAdapter.notifyDataSetChanged();

    }

}
