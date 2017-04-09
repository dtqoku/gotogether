package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
 * Created by neetc on 4/5/2017.
 */

public class SocialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SocialData> socialDatas;
    private Context context;
    private View parentView;

    SocialAdapter(Context context, List<SocialData> dataset, View view) {
        socialDatas = dataset;
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

    public static class ViewHolderFriendList extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;
        public ImageView ic_nofication_count;
        public TextView nofication_count;

        ViewHolderFriendList(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            ic_nofication_count = (ImageView) view.findViewById(R.id.ic_nofication_count);
            nofication_count = (TextView) view.findViewById(R.id.nofication_count);
        }
    }

    public static class ViewHolderGroup extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupdescription;
        public ImageView _overflow;
        public ImageView _leader;
        public ImageView _target_status;
        public Button _btn_get_target_location;

        public ViewHolderGroup(View view) {
            super(view);

            groupname = (TextView) view.findViewById(R.id.txtgroupname);
            groupdescription = (TextView) view.findViewById(R.id.txtgroupdescription);
            _overflow = (ImageView) view.findViewById(R.id.overflow);
            _leader = (ImageView) view.findViewById(R.id.leader);
            _target_status = (ImageView) view.findViewById(R.id.target_status);
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
                return new ViewHolderFriendList(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_friend, parent, false));
            case 2:
                return new ViewHolderGroup(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_group, parent, false));
            /*case 3:
                return new ViewHolderGroupRequest(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_request, parent, false));
            case 4:
                return new ViewHolderGroup(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_group, parent, false));*/
            default:
                return new ViewHolderTitle(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_title, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SocialData socialData = socialDatas.get(position);
        final int positiontemp = position;
        if (holder instanceof ViewHolderTitle) {
            ViewHolderTitle viewHolderTitle = (ViewHolderTitle) holder;

            viewHolderTitle.title.setText(socialData.titlename);

        } else if (holder instanceof ViewHolderFriendList) {
            final ViewHolderFriendList viewHolderFriendList = (ViewHolderFriendList) holder;

            viewHolderFriendList.friendname.setText(socialData.userData.displayname);
            viewHolderFriendList.frineddetail.setText(socialData.userData.email);
            viewHolderFriendList.nofication_count.setText(String.valueOf(socialData.userData.unreadMassage));
            if (socialData.userData.unreadMassage != 0) {
                viewHolderFriendList.ic_nofication_count.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            }

            viewHolderFriendList.friendname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserdetailActivity.class);
                    intent.putExtra("userUid", socialData.userData.userUid);
                    intent.putExtra("userDisplayname", socialData.userData.displayname);
                    context.startActivity(intent);
                }
            });
            viewHolderFriendList.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFriendPopUpmenu(viewHolderFriendList.overflow, socialData);
                }
            });
        } else if (holder instanceof ViewHolderGroup) {
            ViewHolderGroup viewHolderGroup = (ViewHolderGroup) holder;

            viewHolderGroup.groupname.setText(socialData.groupData.Name + " (" + String.valueOf(socialData.groupData.Membercount) + ")");
            viewHolderGroup.groupdescription.setText(socialData.groupData.Description);

            final ViewHolderGroup viewHolderGrouptemp = viewHolderGroup;
            viewHolderGroup._overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGroupPopUpmenu(viewHolderGrouptemp._overflow, socialData);
                }
            });

            if (socialData.groupData.isleader()) {
                viewHolderGroup._leader.setVisibility(View.VISIBLE);
            } else {
                viewHolderGroup._leader.setVisibility(View.GONE);
            }

            if (socialData.groupData.isMeetingPointSet()) {
                viewHolderGroup._target_status.setVisibility(View.VISIBLE);
                viewHolderGroup._btn_get_target_location.setVisibility(View.VISIBLE);
                viewHolderGroup._btn_get_target_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, NavigationActivity.class);
                        intent.putExtra("GroupUID", socialData.groupData.GroupUID);
                        intent.putExtra("GroupName", socialData.groupData.Name);
                        intent.putExtra("userUid", socialData.groupData.thisUserUid);
                        context.startActivity(intent);
                    }
                });
            } else {
                viewHolderGroup._target_status.setVisibility(View.GONE);
                viewHolderGroup._btn_get_target_location.setVisibility(View.GONE);
            }

            viewHolderGroup.groupname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GroupHomeActivity.class);
                    intent.putExtra("GroupUID", socialData.groupData.GroupUID);
                    intent.putExtra("GroupName", socialData.groupData.Name);
                    intent.putExtra("userUid", socialData.groupData.thisUserUid);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void showFriendPopUpmenu(View view, SocialData socialData) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_friend, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new FriendMenuItemClickListener(socialData));
        popupMenu.show();
    }

    private void showGroupPopUpmenu(View view, SocialData socialData) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_group, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new GroupMenuItemClickListener(socialData));
        popupMenu.show();
    }

    private class FriendMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        SocialData socialData;


        FriendMenuItemClickListener(SocialData socialData) {
            this.socialData = socialData;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat:
                    Intent intent = new Intent(context, PersonChatActivity.class);
                    intent.putExtra("currentChatuserUid", socialData.userData.userUid);
                    intent.putExtra("currentChatuserDisplayname", socialData.userData.displayname);
                    intent.putExtra("userUid", socialData.CurrentuserUid);
                    intent.putExtra("UserDisplayname", socialData.CurrentuserDisplayname);
                    context.startActivity(intent);
                    return true;
                case R.id.unfriend:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure to unfriend?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(socialData.CurrentuserUid);
                                    currentuserdatabaseReference.child("friend").child(socialData.userData.userUid).removeValue();

                                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(socialData.userData.userUid);
                                    requestuserdatabaseReference.child("friend").child(socialData.CurrentuserUid).removeValue();

                                    socialDatas.remove(socialData);
                                    notifyDataSetChanged();

                                    Snackbar snackbar = Snackbar.make(parentView, "unfriend with " + socialData.userData.displayname, Snackbar.LENGTH_LONG);
                                    snackbar.show();
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
    private class GroupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        SocialData socialData;

        GroupMenuItemClickListener(SocialData socialData) {
            this.socialData = socialData;
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
                                    if (socialData.groupData.isleader() && socialData.groupData.Membercount > 1) {
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
                                    } else if (socialData.groupData.rank.equals("leader")) {
                                        //leave group as leader

                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(socialData.groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(socialData.groupData.GroupUID).removeValue();

                                        DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(socialData.groupData.GroupUID);
                                        groupdatabaseReference.removeValue();

                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(socialData.groupData.GroupUID);

                                        socialDatas.remove(socialData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave" + socialData.groupData.Name, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    } else {
                                        //just leave
                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(socialData.groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(socialData.groupData.GroupUID).removeValue();

                                        DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(socialData.groupData.GroupUID);
                                        groupdatabaseReference.child("member").child(socialData.groupData.thisUserUid).removeValue();
                                        final DatabaseReference databaseReference = groupdatabaseReference;
                                        groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int membercount = Integer.valueOf(String.valueOf(dataSnapshot.child("membercount").getValue()));
                                                membercount = membercount - 1;
                                                databaseReference.child("membercount").setValue(membercount);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(socialData.groupData.GroupUID);

                                        socialDatas.remove(socialData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave  " + socialData.groupData.Name, Snackbar.LENGTH_LONG);
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
        final SocialData socialData = socialDatas.get(position);
        if (socialData.Type.equals("Title")) {
            return 0;
        } else if (socialData.Type.equals("FriendList")) {
            return 1;
        } else if (socialData.Type.equals("Group")) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return socialDatas.size();
    }

}
