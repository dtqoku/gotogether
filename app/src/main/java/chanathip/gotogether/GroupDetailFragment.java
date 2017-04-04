package chanathip.gotogether;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Created by neetc on 11/15/2016.
 */

public class GroupDetailFragment extends Fragment {
    private UserData userData;
    private GroupData groupData;
    private List<GroupDetailData> groupDetailDatas;
    private RecyclerView.LayoutManager layoutManager;
    private GroupDetailAdapter groupDetailAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private List<GroupDetailData> memberDatas;
    private List<GroupDetailData> inviteDatas;


    public GroupDetailFragment() {

    }

    public static GroupDetailFragment newInstance(String GroupUID, String GroupName, String UserUid) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GroupUID", GroupUID);
        bundle.putString("GroupName", GroupName);
        bundle.putString("userUid", UserUid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userData = new UserData();
        groupData = new GroupData();
        groupDetailDatas = new ArrayList<>();
        memberDatas = new ArrayList<>();
        inviteDatas = new ArrayList<>();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getArguments();
            if (extras == null) {
                groupData.GroupUID = null;
                groupData.Name = null;
                userData.userUid = null;
            } else {
                groupData.GroupUID = extras.getString("GroupUID");
                groupData.Name = extras.getString("GroupName");
                userData.userUid = extras.getString("userUid");
            }
        } else {
            groupData.GroupUID = (String) savedInstanceState.getSerializable("GroupUID");
            groupData.Name = (String) savedInstanceState.getSerializable("GroupName");
            userData.userUid = (String) savedInstanceState.getSerializable("userUid");
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        memberDatas.clear();
        inviteDatas.clear();

        //get current userdata
        DatabaseReference currentuserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.userUid);
        currentuserDatabaseReference.keepSynced(true);
        currentuserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userData.setData(
                        userData.userUid,
                        String.valueOf(dataSnapshot.child("display name").getValue()),
                        String.valueOf(dataSnapshot.child("email").getValue())
                );

                userData.rank = String.valueOf(dataSnapshot.child("group").child(groupData.GroupUID).getValue());

                final DatabaseReference groupDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                groupDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupData.setData(
                                groupData.GroupUID,
                                String.valueOf(dataSnapshot.child("name").getValue()),
                                String.valueOf(dataSnapshot.child("description").getValue()),
                                userData.rank,
                                String.valueOf(dataSnapshot.child("settingpoint").getValue()),
                                String.valueOf(dataSnapshot.child("membercount").getValue()),
                                userData.userUid
                        );

                        Map<String, String> memberMap = (Map<String, String>) dataSnapshot.child("member").getValue();
                        if (memberMap != null) {
                            for (HashMap.Entry<String, String> entry : memberMap.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                final String keyData = key;
                                final String valueData = value;

                                DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserData memberData = new UserData();
                                        memberData.setData(
                                                keyData,
                                                String.valueOf(dataSnapshot.child("display name").getValue()),
                                                String.valueOf(dataSnapshot.child("email").getValue())
                                        );
                                        memberData.rank = valueData;

                                        GroupDetailData groupDetailData = new GroupDetailData();
                                        groupDetailData.Type = "member";
                                        groupDetailData.CurrentuserUid = userData.userUid;
                                        groupDetailData.CurrentuserDisplayname = userData.displayname;
                                        groupDetailData.Rank = userData.rank;
                                        groupDetailData.GroupUid = groupData.GroupUID;
                                        groupDetailData.CurrentuserUid = userData.userUid;
                                        groupDetailData.member = memberData;

                                        memberDatas.add(groupDetailData);
                                        updateData();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        Map<String, String> inviteMap = (Map<String, String>) dataSnapshot.child("invite").getValue();
                        if (inviteMap != null) {
                            for (HashMap.Entry<String, String> entry : inviteMap.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();

                                final String keyData = key;
                                final String valueData = value;

                                DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(key);
                                usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserData memberData = new UserData();
                                        memberData.setData(
                                                keyData,
                                                String.valueOf(dataSnapshot.child("display name").getValue()),
                                                String.valueOf(dataSnapshot.child("email").getValue())
                                        );
                                        memberData.rank = valueData;

                                        GroupDetailData groupDetailData = new GroupDetailData();
                                        groupDetailData.Type = "invite";
                                        groupDetailData.CurrentuserUid = userData.userUid;
                                        groupDetailData.CurrentuserDisplayname = userData.displayname;
                                        groupDetailData.Rank = userData.rank;
                                        groupDetailData.GroupUid = groupData.GroupUID;
                                        groupDetailData.CurrentuserUid = userData.userUid;
                                        groupDetailData.member = memberData;

                                        inviteDatas.add(groupDetailData);
                                        updateData();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        updateData();
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

    private void updateData() {
        groupDetailDatas.clear();

        GroupDetailData groupDetailData = new GroupDetailData();

        groupDetailData.Type = "detail";
        groupDetailData.CurrentuserUid = userData.userUid;
        groupDetailData.CurrentuserDisplayname = userData.displayname;
        groupDetailData.Rank = userData.rank;
        groupDetailData.GroupUid = groupData.GroupUID;
        groupDetailData.groupname = groupData.Name;
        groupDetailData.groupDescription = groupData.Description;

        groupDetailDatas.add(groupDetailData);

        groupDetailData = new GroupDetailData();
        groupDetailData.Type = "title";
        groupDetailData.CurrentuserUid = userData.userUid;
        groupDetailData.CurrentuserDisplayname = userData.displayname;
        groupDetailData.Rank = userData.rank;
        groupDetailData.GroupUid = groupData.GroupUID;
        groupDetailData.CurrentuserUid = userData.userUid;
        groupDetailData.titlename = "Member";

        groupDetailDatas.add(groupDetailData);

        groupDetailDatas.addAll(memberDatas);

        groupDetailDatas.addAll(inviteDatas);

        if(userData.rank.equals("leader")){
            groupDetailData = new GroupDetailData();
            groupDetailData.Type = "addmember";
            groupDetailData.CurrentuserUid = userData.userUid;
            groupDetailData.CurrentuserDisplayname = userData.displayname;
            groupDetailData.Rank = userData.rank;
            groupDetailData.GroupUid = groupData.GroupUID;
            groupDetailData.CurrentuserUid = userData.userUid;
            groupDetailData.groupname = groupData.Name;

            groupDetailDatas.add(groupDetailData);
        }

        groupDetailAdapter.notifyDataSetChanged();

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

        groupDetailAdapter = new GroupDetailAdapter(context,groupDetailDatas,recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(groupDetailAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("GroupUID", groupData.GroupUID);
        savedInstanceState.putString("GroupName", groupData.Name);
        savedInstanceState.putString("userUid", userData.userUid);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
