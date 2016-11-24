package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neetc on 11/14/2016.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<GroupData> groupdatas;
    private List<GroupData> groupdatasfromdb;
    private Context mContext;
    private View parentView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupname;
        public TextView groupdescription;
        public ImageView _overflow;
        public ImageView _leader;
        public View _line;
        public ImageView _target_status;
        public Button _btn_get_target_location;

        public ViewHolder(View view) {
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

    public GroupAdapter(Context context, List<GroupData> dataset, View view) {
        groupdatasfromdb = dataset;
        groupdatas = new ArrayList<>(groupdatasfromdb);
        mContext = context;
        this.parentView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_row_group, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final GroupData groupData = groupdatas.get(position);

        viewHolder.groupname.setText(groupData.Name + " (" + String.valueOf(groupData.Membercount) + ")");
        viewHolder.groupdescription.setText(groupData.Description);

        viewHolder._overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopupmenu(viewHolder._overflow, groupData);
            }
        });

        if (groupData.isleader()) {
            viewHolder._leader.setVisibility(View.VISIBLE);
        } else {
            viewHolder._leader.setVisibility(View.GONE);
        }

        if (groupData.isMeetingPointSet()) {
            viewHolder._target_status.setVisibility(View.VISIBLE);
            viewHolder._line.setVisibility(View.VISIBLE);
            viewHolder._btn_get_target_location.setVisibility(View.VISIBLE);
        } else {
            viewHolder._target_status.setVisibility(View.GONE);
            viewHolder._line.setVisibility(View.GONE);
            viewHolder._btn_get_target_location.setVisibility(View.GONE);
        }

        viewHolder.groupname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,GroupHomeActivity.class);
                intent.putExtra("GroupUID",groupData.GroupUID);
                intent.putExtra("GroupName",groupData.Name);
                intent.putExtra("UserUid",groupData.thisUserUid);
                mContext.startActivity(intent);
            }
        });
    }

    private void showpopupmenu(View view, GroupData groupData) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_group, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(groupData));
        popupMenu.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        GroupData groupData;

        MyMenuItemClickListener(GroupData groupData) {
            this.groupData = groupData;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_leave:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Are you sure to leave this group?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //leave group
                                    if (groupData.isleader() && groupData.Membercount > 1) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setMessage("you cannot leave group if you are leader or not last one in group")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        ((GroupActivity) mContext).recreate();
                                                    }
                                                });
                                        AlertDialog alertDialog2 = builder.create();
                                        alertDialog2.show();
                                    } else if (groupData.rank.equals("leader")) {
                                        //leave group as leader
                                        DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                                        groupdatabaseReference.removeValue();

                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(groupData.GroupUID).removeValue();

                                        groupdatas.remove(groupData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave" + groupData.Name, Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    } else {
                                        //just leave
                                        DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(groupData.thisUserUid);
                                        currentuserdatabaseReference.child("group").child(groupData.GroupUID).removeValue();

                                        groupdatas.remove(groupData);
                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar.make(parentView, "leave" + groupData.Name, Snackbar.LENGTH_LONG);
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
    public int getItemCount() {
        return groupdatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void filter(String text) {
        groupdatas.clear();
        Log.d("test", text);
        if (text.isEmpty()) {
            groupdatas.addAll(groupdatasfromdb);
            Log.d("test", "asjdhkajshd");
        } else {
            text = text.toLowerCase();
            for (GroupData item : groupdatasfromdb) {
                if (item.Name.toLowerCase().contains(text) || item.Description.toLowerCase().contains(text)) {
                    groupdatas.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
