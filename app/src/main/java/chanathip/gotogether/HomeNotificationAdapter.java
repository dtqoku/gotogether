package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

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

    public static class ViewHolderGroup extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupdescription;
        public ImageView _overflow;
        public ImageView _leader;
        public View _line;
        public ImageView _target_status;
        public Button _btn_get_target_location;

        public ViewHolderGroup(View view) {
            super(view);

            groupname = (TextView) view.findViewById(R.id.txtgroupname);
            groupdescription = (TextView) view.findViewById(R.id.txtgroupdescription);
            _overflow = (ImageView) view.findViewById(R.id.overflow);
            _leader = (ImageView) view.findViewById(R.id.leader);
            _target_status = (ImageView) view.findViewById(R.id.target_status);
            _line = (View) view.findViewById(R.id.line);
            _btn_get_target_location = (Button) view.findViewById(R.id.btn_get_target_location);
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
            case 4:
                return new ViewHolderGroup(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_group, parent, false));
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
            viewHolderFriendRequest.frienddetail.setText(notificationData.requestEmail);

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
                    intent.putExtra("userUid",notificationData.CurrentuserUid);
                    context.startActivity(intent);
                }
            });

            viewHolderGroupRequest.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("request").child("group").child(notificationData.RequestGroupUid).removeValue();
                    currentuserdatabaseReference.child("group").child(notificationData.RequestGroupUid).setValue("member");

                    DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(notificationData.RequestGroupUid);
                    groupdatabaseReference.child("invite").child(notificationData.CurrentuserUid).removeValue();
                    groupdatabaseReference.child("member").child(notificationData.CurrentuserUid).setValue("member");
                    final DatabaseReference databaseReference = groupdatabaseReference;
                    groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int membercount = Integer.valueOf(String.valueOf(dataSnapshot.child("membercount").getValue()));
                            membercount = membercount+1;
                            databaseReference.child("membercount").setValue(membercount);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    FirebaseMessaging.getInstance().subscribeToTopic(notificationData.RequestGroupUid);

                    GotogetherNotificationManager gotogetherNotificationManager = new GotogetherNotificationManager(context);
                    gotogetherNotificationManager.acceptGroupRequest(notificationData.RequestGroupUid, notificationData.CurrentuserDisplayname);

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "accept " + notificationData.RequestGroupname + " group request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            viewHolderGroupRequest.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.CurrentuserUid);
                    currentuserdatabaseReference.child("request").child("group").child(notificationData.RequestGroupUid).removeValue();

                    DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(notificationData.RequestGroupUid);
                    groupdatabaseReference.child("invite").child(notificationData.CurrentuserUid).removeValue();

                    notificationDatas.remove(positiontemp);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar.make(parentView, "reject " + notificationData.RequestGroupname + " group request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
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
                    intent.putExtra("userUid",notificationData.CurrentuserUid);
                    intent.putExtra("UserDisplayname",notificationData.CurrentuserDisplayname);
                    context.startActivity(intent);
                }
            });

            viewHolderUnread.ic_nofication_count.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary));

        } else if (holder instanceof ViewHolderGroup){
            ViewHolderGroup viewHolderGroup = (ViewHolderGroup) holder;

            viewHolderGroup.groupname.setText(notificationData.groupData.Name + " (" + String.valueOf(notificationData.groupData.Membercount) + ")");
            viewHolderGroup.groupdescription.setText(notificationData.groupData.Description);

            final ViewHolderGroup viewHolderGrouptemp = viewHolderGroup;
            viewHolderGroup._overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showpopupmenu(viewHolderGrouptemp._overflow, notificationData);
                }
            });

            if (notificationData.groupData.isleader()) {
                viewHolderGroup._leader.setVisibility(View.VISIBLE);
            } else {
                viewHolderGroup._leader.setVisibility(View.GONE);
            }

            if (notificationData.groupData.isMeetingPointSet()) {
                viewHolderGroup._target_status.setVisibility(View.VISIBLE);
                viewHolderGroup._line.setVisibility(View.VISIBLE);
                viewHolderGroup._btn_get_target_location.setVisibility(View.VISIBLE);
            } else {
                viewHolderGroup._target_status.setVisibility(View.GONE);
                viewHolderGroup._line.setVisibility(View.GONE);
                viewHolderGroup._btn_get_target_location.setVisibility(View.GONE);
            }

            viewHolderGroup.groupname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,GroupHomeActivity.class);
                    intent.putExtra("GroupUID",notificationData.groupData.GroupUID);
                    intent.putExtra("GroupName",notificationData.groupData.Name);
                    intent.putExtra("userUid",notificationData.groupData.thisUserUid);
                    context.startActivity(intent);
                }
            });
        }
    }
    private void showpopupmenu(View view, NotificationData notificationData) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_group, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(notificationData));
        popupMenu.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        NotificationData notificationData;

        MyMenuItemClickListener(NotificationData notificationData) {
            this.notificationData = notificationData;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_leave:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure to leave this group?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //leave group
                                    if (notificationData.groupData.isleader() && notificationData.groupData.Membercount > 1) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("you cannot leave group if you are leader or not last one in group")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //do nothing
                                                    }
                                                });
                                        AlertDialog alertDialog2 = builder.create();
                                        alertDialog2.show();
                                    } else if (notificationData.groupData.rank.equals("leader")) {
                                        //leave group as leader
                                        DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(notificationData.groupData.GroupUID);
                                        groupdatabaseReference.removeValue();

                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(notificationData.groupData.GroupUID).removeValue();

                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(notificationData.groupData.GroupUID);

                                        notificationDatas.remove(notificationData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave" + notificationData.groupData.Name, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    } else {
                                        //just leave
                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(notificationData.groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(notificationData.groupData.GroupUID).removeValue();

                                        DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(notificationData.groupData.GroupUID);
                                        groupdatabaseReference.child("member").child(notificationData.groupData.thisUserUid).removeValue();
                                        final DatabaseReference databaseReference = groupdatabaseReference;
                                        groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int membercount = Integer.valueOf(String.valueOf(dataSnapshot.child("membercount").getValue()));
                                                membercount = membercount-1;
                                                databaseReference.child("membercount").setValue(membercount);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(notificationData.groupData.GroupUID);

                                        notificationDatas.remove(notificationData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave " + notificationData.groupData.Name, Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                    }


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //cancel
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
            }
            return false;
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
        } else if (notificationData.Type.equals("Group")) {
            return 4;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return notificationDatas.size();
    }
}
