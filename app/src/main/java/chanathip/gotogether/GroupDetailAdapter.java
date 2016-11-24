package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by neetc on 11/15/2016.
 */

public class GroupDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GroupDetailData> groupDetailDatas;
    private Context context;
    private View parentView;

    GroupDetailAdapter(Context context, List<GroupDetailData> dataset, View view) {
        groupDetailDatas = dataset;
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

    public static class ViewHolderGroupdetail extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupdescription;

        ViewHolderGroupdetail(View view) {
            super(view);

            groupname = (TextView) view.findViewById(R.id.txtname);
            groupdescription = (TextView) view.findViewById(R.id.txtgroupdescription);
        }
    }

    public static class ViewHolderMember extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;
        public ImageView ic_nofication_count;
        public TextView nofication_count;
        public ImageView ic_leader;

        public ViewHolderMember(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            ic_nofication_count = (ImageView) view.findViewById(R.id.ic_nofication_count);
            nofication_count = (TextView) view.findViewById(R.id.nofication_count);
            ic_leader = (ImageView) view.findViewById(R.id.leader);
        }
    }

    public static class ViewHolderAddMember extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolderAddMember(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cv);
        }
    }
    public static class ViewHolderInvite extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;

        public ViewHolderInvite(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolderTitle(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_title, parent, false));
            case 1:
                return new ViewHolderGroupdetail(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_groupdetail, parent, false));
            case 2:
                return new ViewHolderMember(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_friend, parent, false));
            case 3:
                return new ViewHolderAddMember(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_invite_member, parent, false));
            case 4:
                return new ViewHolderInvite(LayoutInflater.from(context)
                        .inflate(R.layout.recycler_row_groupdetail_invite,parent,false));
        }
        return new ViewHolderTitle(LayoutInflater.from(context)
                .inflate(R.layout.recycler_row_title, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GroupDetailData groupDetailData = groupDetailDatas.get(position);
        if (holder instanceof ViewHolderTitle) {
            ViewHolderTitle viewHolderTitle = (ViewHolderTitle) holder;

            viewHolderTitle.title.setText(groupDetailData.titlename);

        } else if (holder instanceof ViewHolderGroupdetail) {
            ViewHolderGroupdetail viewHolderFriendRequest = (ViewHolderGroupdetail) holder;

            viewHolderFriendRequest.groupname.setText(groupDetailData.groupname);
            viewHolderFriendRequest.groupdescription.setText(groupDetailData.groupDescription);
        } else if (holder instanceof ViewHolderMember) {
            final ViewHolderMember viewHolderMember = (ViewHolderMember) holder;

            viewHolderMember.friendname.setText(groupDetailData.member.displayname);
            viewHolderMember.frineddetail.setText(groupDetailData.member.Firstname + " " + groupDetailData.member.Lastname);
            viewHolderMember.nofication_count.setVisibility(View.GONE);
            viewHolderMember.ic_nofication_count.setVisibility(View.GONE);

            if (groupDetailData.member.rank.equals("leader")) {
                viewHolderMember.ic_leader.setVisibility(View.VISIBLE);
            }
            if (!groupDetailData.Rank.equals("leader")) {
                viewHolderMember.overflow.setVisibility(View.GONE);
            }
            if (groupDetailData.CurrentuserUid.equals(groupDetailData.member.UserUid)) {
                viewHolderMember.overflow.setVisibility(View.GONE);
            }

            viewHolderMember.friendname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserdetailActivity.class);
                    intent.putExtra("userUid", groupDetailData.member.UserUid);
                    intent.putExtra("userDisplayname", groupDetailData.member.displayname);
                    context.startActivity(intent);
                }
            });
            viewHolderMember.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showpopupmenu(viewHolderMember.overflow, groupDetailData);
                }
            });
        } else if(holder instanceof ViewHolderAddMember){
            final ViewHolderAddMember viewHolderAddMember = (ViewHolderAddMember) holder;

            viewHolderAddMember.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddMemberActivity.class);
                    intent.putExtra("GroupUID", groupDetailData.GroupUid);
                    intent.putExtra("GroupName", groupDetailData.groupname);
                    intent.putExtra("UserUid", groupDetailData.CurrentuserUid);
                    intent.putExtra("UserDisplayname", groupDetailData.CurrentuserDisplayname);
                    context.startActivity(intent);
                }
            });
        } else if(holder instanceof ViewHolderInvite){
            final ViewHolderInvite viewHolderInvite = (ViewHolderInvite) holder;

            viewHolderInvite.friendname.setText(groupDetailData.member.displayname);
            viewHolderInvite.frineddetail.setText(groupDetailData.member.Firstname + " " + groupDetailData.member.Lastname);
            if (!groupDetailData.Rank.equals("leader")) {
                viewHolderInvite.overflow.setVisibility(View.GONE);
            }
            viewHolderInvite.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want cancel " + groupDetailData.member.displayname + "'s invite to group?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //invite
                                    DatabaseReference inviteUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(groupDetailData.member.UserUid)
                                            .child("request").child("group");
                                    inviteUserDatabaseReference.child(groupDetailData.GroupUid).removeValue();

                                    DatabaseReference memberDatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups")
                                            .child(groupDetailData.GroupUid).child("invite");
                                    memberDatabaseReference.child(groupDetailData.member.UserUid).removeValue();

                                    groupDetailDatas.remove(groupDetailData);
                                    notifyDataSetChanged();

                                    Snackbar snackbar = Snackbar.make(parentView, "cancel " + groupDetailData.member.displayname + "'s invite to group", Snackbar.LENGTH_LONG);
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
                }
            });
        }
    }

    private void showpopupmenu(View view, GroupDetailData groupDetailData) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_member_leader, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(groupDetailData));
        popupMenu.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        GroupDetailData groupDetailData;

        MyMenuItemClickListener(GroupDetailData groupDetailData) {
            this.groupDetailData = groupDetailData;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chat:
                    return true;
            }
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        final GroupDetailData groupDetailData = groupDetailDatas.get(position);
        if (groupDetailData.Type.equals("title")) {
            return 0;
        } else if (groupDetailData.Type.equals("detail")) {
            return 1;
        } else if (groupDetailData.Type.equals("member")) {
            return 2;
        } else if (groupDetailData.Type.equals("addmember")) {
            return 3;
        } else if (groupDetailData.Type.equals("invite")) {
            return 4;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return groupDetailDatas.size();
    }
}
