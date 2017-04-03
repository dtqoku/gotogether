package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neetc on 11/15/2016.
 */

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.ViewHolder> {
    private List<UserData> userDatas;
    private List<UserData> userDatasfromdb;
    private Context mContext;
    private View parentView;
    private UserData currentUserdata;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;

        public ViewHolder(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public AddMemberAdapter(Context context, List<UserData> dataset, View view, UserData userData) {
        userDatasfromdb = dataset;
        userDatas = new ArrayList<>(userDatasfromdb);
        mContext = context;
        this.parentView = view;
        this.currentUserdata = userData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_row_add_member, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final UserData userData = userDatas.get(position);

        viewHolder.friendname.setText(userData.displayname);
        viewHolder.frineddetail.setText(userData.email);

        viewHolder.friendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserdetailActivity.class);
                intent.putExtra("userUid", userData.userUid);
                intent.putExtra("userDisplayname", userData.displayname);
                mContext.startActivity(intent);
            }
        });
        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do you want to invite " + userData.displayname + " to group?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //invite
                                DatabaseReference inviteUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.userUid)
                                        .child("request").child("group");
                                inviteUserDatabaseReference.child(userData.GroupUid).setValue("true");

                                DatabaseReference memberDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups")
                                        .child(userData.GroupUid).child("invite");
                                memberDatabaseReference.child(userData.userUid).setValue("true");

                                GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(mContext);
                                gotogetherNotificationManager.sendInvitetoGroup(userData.userUid,userData.GroupName);

                                ((AddMemberActivity)mContext).onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //cancel
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void filter(String text) {
        userDatas.clear();
        if (text.isEmpty()) {
            userDatas.addAll(userDatasfromdb);
        } else {
            text = text.toLowerCase();
            for (UserData item : userDatasfromdb) {
                if (item.displayname.toLowerCase().contains(text)) {
                    userDatas.add(item);
                }
            }
        }
        notifyDataSetChanged();

    }
}
