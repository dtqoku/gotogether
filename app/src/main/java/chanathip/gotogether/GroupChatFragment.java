package chanathip.gotogether;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by neetc on 11/15/2016.
 */

public class GroupChatFragment extends Fragment {
    private UserData userData;
    private GroupData groupData;
    private RecyclerView recyclerView;
    private Context context;
    private RecyclerView.LayoutManager layoutManager;
    private ChatAdapter chatAdapter;
    private List<UserMessage> userMessages;
    private DatabaseReference groupChatDatabaseReference;
    private ValueEventListener groupChatDatabaseReferenceChildEventListener;

    public GroupChatFragment() {

    }

    public static GroupChatFragment newInstance(String GroupUID, String GroupName, String UserUid) {
        GroupChatFragment fragment = new GroupChatFragment();
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
        userMessages = new ArrayList<>();

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
    public void onStart() {
        super.onStart();

        groupChatDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("groups").child(groupData.GroupUID).child("messages");
        groupChatDatabaseReference.addValueEventListener(groupChatDatabaseReferenceChildEventListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        groupChatDatabaseReference.removeEventListener(groupChatDatabaseReferenceChildEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        Button submitChat = (Button) view.findViewById(R.id.submit);
        TextInputLayout txt_submit_chat = (TextInputLayout) view.findViewById(R.id.txt_submit_chat);

        chatAdapter = new ChatAdapter(context, userMessages, recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        final TextInputLayout txt_submit_chattemp = txt_submit_chat;

        submitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now;
                Locale locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = getResources().getConfiguration().getLocales().get(0);
                } else {
                    locale = getResources().getConfiguration().locale;
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", locale);

                Calendar calendar = new GregorianCalendar();
                now = dateFormat.format(calendar.getTime());

                DatabaseReference submitChatDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("groups").child(groupData.GroupUID).child("messages").child(userData.userUid).child(now);
                if (txt_submit_chattemp.getEditText().getText().toString().length() != 0) {
                    submitChatDatabaseReference.child("message").setValue(txt_submit_chattemp.getEditText().getText().toString());

                    txt_submit_chattemp.getEditText().setText("");
                    View view = ((GroupHomeActivity) context).getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    layoutManager.scrollToPosition(userMessages.size() - 1);

                    GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                    gotogetherNotificationManager.sendGroupChat(groupData.GroupUID, groupData.Name);
                }
            }
        });

        groupChatDatabaseReferenceChildEventListener = new ValueEventListener() {
            private void updateData(List<UserMessage> userMessageList, UserMessage userMessage) {
                for (UserMessage item : userMessageList) {
                    if (item.time.equals(userMessage.time) && item.sender.equals(userMessage.sender)) {
                        userMessageList.remove(item);
                        break;
                    }
                }
                userMessageList.add(userMessage);
            }
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, HashMap> ChatUserdataMap = (HashMap<String, HashMap>) dataSnapshot.getValue();

                    for (HashMap.Entry<String, HashMap> entry : ChatUserdataMap.entrySet()) {
                        String key = entry.getKey();
                        HashMap<String, HashMap> value = entry.getValue();
                        for (HashMap.Entry<String, HashMap> entry2 : value.entrySet()) {
                            HashMap<String, String> value2 = entry2.getValue();
                            final UserMessage userMessage = new UserMessage();

                            userMessage.message = value2.get("message");

                            Locale locale;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                locale = getResources().getConfiguration().getLocales().get(0);
                            } else {
                                locale = getResources().getConfiguration().locale;
                            }
                            DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", locale);
                            try {
                                userMessage.calendar = Calendar.getInstance();
                                userMessage.calendar.setTime(dateFormat.parse(entry2.getKey()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            DateFormat dateFormat1 = new SimpleDateFormat("dd/MM HH:mm:ss", locale);
                            userMessage.time = dateFormat1.format(userMessage.calendar.getTime());
                            userMessage.readstatus = "notneed";

                            String userUid;
                            if (key.equals(userData.userUid)) {
                                userMessage.Type = "self";
                                userUid = userData.userUid;
                            } else {
                                userMessage.Type = "notself";
                                userUid = key;
                            }
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(userUid).child("display name");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    userMessage.sender = dataSnapshot.getValue().toString();

                                    updateData(userMessages, userMessage);
                                    Collections.sort(userMessages);
                                    chatAdapter.notifyDataSetChanged();

                                    layoutManager.scrollToPosition(userMessages.size() - 1);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }
}
