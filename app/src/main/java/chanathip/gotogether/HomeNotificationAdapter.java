package chanathip.gotogether;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static class ViewHolderUnread extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;
        public TextView nofication_count;
        public CardView cv;
        public ImageView ic_nofication_count;

        public ViewHolderUnread(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            nofication_count = (TextView) view.findViewById(R.id.nofication_count);
            cv = (CardView) view.findViewById(R.id.cv);
            ic_nofication_count = (ImageView) view.findViewById(R.id.ic_nofication_count);
        }
    }

    public static class ViewHolderGroupRequest extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupdetail;
        public ImageView reject;
        public TextView accept;

        ViewHolderGroupRequest(View view) {
            super(view);

            groupname = (TextView) view.findViewById(R.id.txtfriendname);
            reject = (ImageView) view.findViewById(R.id.reject);
            accept = (TextView) view.findViewById(R.id.accept);
            groupdetail = (TextView) view.findViewById(R.id.txtfrienddetail);
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
                        .inflate(R.layout.recycler_row_request, parent, false));
            case 2:
                return new ViewHolderUnread(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_friend, parent, false));
            case 3:
                return new ViewHolderGroupRequest(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_request, parent, false));
            default:
                return new ViewHolderTitle(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_title, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NotificationData notificationData = notificationDatas.get(position);
        final int positiontemp = position;
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
                    currentuserdatabaseReference.child("request").child("friend").child(notificationData.RequestUserUid).removeValue();

                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.RequestUserUid);
                    requestuserdatabaseReference.child("friend").child(notificationData.CurrentuserUid).setValue("true");
                    requestuserdatabaseReference.child("request").child("friend").child(notificationData.CurrentuserDisplayname).removeValue();

                    GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                    gotogetherNotificationManager.acceptFriendRequest(notificationData.RequestUserUid, notificationData.CurrentuserDisplayname);

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "accept " + notificationData.RequestUserdisplayname + " friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            viewHolderFriendRequest.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("request").child("friend").child(notificationData.RequestUserUid).removeValue();

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "reject " + notificationData.RequestUserdisplayname + " friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        } else if (holder instanceof ViewHolderGroupRequest) {
            ViewHolderGroupRequest viewHolderGroupRequest = (ViewHolderGroupRequest) holder;

            viewHolderGroupRequest.groupname.setText(notificationData.RequestGroupname);
            viewHolderGroupRequest.groupdetail.setText(notificationData.RequestGroupdetail);

            viewHolderGroupRequest.groupname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GroupDetailActivity.class);
                    intent.putExtra("GroupUID",notificationData.RequestGroupUid);
                    intent.putExtra("GroupName",notificationData.RequestGroupname);
                    intent.putExtra("UserUid",notificationData.CurrentuserUid);
                    context.startActivity(intent);
                }
            });/*

            viewHolderGroupRequest.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("friend").child(notificationData.RequestUserUid).setValue("true");
                    currentuserdatabaseReference.child("request").child("friend").child(notificationData.RequestUserUid).removeValue();

                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.RequestUserUid);
                    requestuserdatabaseReference.child("friend").child(notificationData.CurrentuserUid).setValue("true");
                    requestuserdatabaseReference.child("request").child("friend").child(notificationData.CurrentuserDisplayname).removeValue();

                    GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                    gotogetherNotificationManager.acceptFriendRequest(notificationData.RequestUserUid, notificationData.CurrentuserDisplayname);

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "accept " + notificationData.RequestUserdisplayname + " friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            viewHolderGroupRequest.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("request").child("friend").child(notificationData.RequestUserUid).removeValue();

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "reject " + notificationData.RequestUserdisplayname + " friend request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });*/
        } else if (holder instanceof ViewHolderUnread){
            ViewHolderUnread viewHolderUnread = (ViewHolderUnread) holder;

            viewHolderUnread.friendname.setText(notificationData.SenderDisplayname);
            viewHolderUnread.frineddetail.setText(notificationData.SenderLastmessage);
            viewHolderUnread.nofication_count.setText(String.valueOf(notificationData.Unreadcount));

            viewHolderUnread.overflow.setVisibility(View.GONE);

            viewHolderUnread.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PersonChatActivity.class);
                    intent.putExtra("currentChatuserUid",notificationData.SenderUid);
                    intent.putExtra("currentChatuserDisplayname",notificationData.SenderDisplayname);
                    intent.putExtra("UserUid",notificationData.CurrentuserUid);
                    intent.putExtra("UserDisplayname",notificationData.CurrentuserDisplayname);
                    context.startActivity(intent);
                }
            });

            viewHolderUnread.ic_nofication_count.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));

        }
    }

    @Override
    public int getItemViewType(int position) {
        final NotificationData notificationData = notificationDatas.get(position);
        if (notificationData.Type.equals("Title")) {
            return 0;
        } else if (notificationData.Type.equals("FriendRequest")) {
            return 1;
        } else if (notificationData.Type.equals("Unread")) {
            return 2;
        } else if (notificationData.Type.equals("GroupRequest")) {
            return 3;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return notificationDatas.size();
    }
}
