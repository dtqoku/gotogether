package chanathip.gotogether;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
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
 * Created by neetc on 11/8/2016.
 */

public class HomeNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationData> notificationDatas;
    private Context context;
    private View parentView;

    HomeNotificationAdapter(Context context, List<NotificationData> dataset, View view) {
        notificationDatas = dataset;
        this.context = context;
        this.parentView = view;
    }

    public static class ViewHolderTitle extends RecyclerView.ViewHolder {
        public TextView title;

        ViewHolderTitle(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public static class ViewHolderFriendRequest extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frienddetail;
        public ImageView reject;
        public TextView accept;

        ViewHolderFriendRequest(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            reject = (ImageView) view.findViewById(R.id.reject);
            accept = (TextView) view.findViewById(R.id.accept);
            frienddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolderTitle(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_title, parent, false));
            case 1:
                return new ViewHolderFriendRequest(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_friend_request, parent, false));
        }
        return new ViewHolderTitle(LayoutInflater.from(context)
                .inflate(R.layout.recycler_row_title, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NotificationData notificationData = notificationDatas.get(position);
        if (holder instanceof ViewHolderTitle) {
            ViewHolderTitle viewHolderTitle = (ViewHolderTitle) holder;

            viewHolderTitle.title.setText(notificationData.titlename);

        } else if (holder instanceof ViewHolderFriendRequest) {
            ViewHolderFriendRequest viewHolderFriendRequest = (ViewHolderFriendRequest) holder;

            viewHolderFriendRequest.friendname.setText(notificationData.RequestUserdisplayname);
            viewHolderFriendRequest.frienddetail.setText(notificationData.RequestUserFirstname + " " + notificationData.RequestUserLastname);

            viewHolderFriendRequest.friendname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserdetailActivity.class);
                    intent.putExtra("userUid", notificationData.RequestUserUid);
                    intent.putExtra("userDisplayname", notificationData.RequestUserdisplayname);
                    context.startActivity(intent);
                }
            });

            viewHolderFriendRequest.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("friend").child(notificationData.RequestUserUid).setValue("true");
                    currentuserdatabaseReference.child("request").child(notificationData.RequestUserUid).removeValue();

                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.RequestUserUid);
                    requestuserdatabaseReference.child("friend").child(notificationData.CurrentuserUid).setValue("true");

                    GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                    gotogetherNotificationManager.acceptFriendRequest(notificationData.RequestUserUid,notificationData.CurrentuserDisplayname);

                    Snackbar snackbar = Snackbar.make(parentView, "accept "+notificationData.RequestUserdisplayname+" friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        final NotificationData notificationData = notificationDatas.get(position);
        if (notificationData.Type.equals("Title")) {
            return 0;
        } else if (notificationData.Type.equals("FriendRequest")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return notificationDatas.size();
    }
}
