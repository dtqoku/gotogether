package chanathip.gotogether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neetc on 10/26/2016.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{
    private List<UserData> userDatas;
    private List<UserData> userDatasfromdb;
    private Context mContext;
    private View parentView;
    private UserData currentUserdata;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;
        public ImageView overflow;
        public ImageView ic_nofication_count;
        public TextView nofication_count;

        public ViewHolder(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            ic_nofication_count = (ImageView) view.findViewById(R.id.ic_nofication_count);
            nofication_count = (TextView) view.findViewById(R.id.nofication_count);
        }
    }

    public FriendListAdapter(Context context, List<UserData> dataset,View view,UserData userData) {
        userDatasfromdb = dataset;
        userDatas = new ArrayList<>(userDatasfromdb);
        mContext = context;
        this.parentView = view;
        this.currentUserdata = userData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_row_friend, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final UserData userData = userDatas.get(position);

        viewHolder.friendname.setText(userData.displayname);
        viewHolder.frineddetail.setText(userData.email);
        viewHolder.nofication_count.setText(String.valueOf(userData.unreadMassage));
        if(userData.unreadMassage != 0){
            viewHolder.ic_nofication_count.setColorFilter(ContextCompat.getColor(mContext,R.color.colorPrimary));
        }

        viewHolder.friendname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,UserdetailActivity.class);
                intent.putExtra("userUid",userData.userUid);
                intent.putExtra("userDisplayname",userData.displayname);
                mContext.startActivity(intent);
            }
        });
        viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopupmenu(viewHolder.overflow,userData);
            }
        });
    }
    private void showpopupmenu(View view,UserData userData){
        PopupMenu popupMenu = new PopupMenu(mContext,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_friend, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(userData));
        popupMenu.show();
    }
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
        UserData userData;
        SharedPreferences sharedPreferences;

        MyMenuItemClickListener(UserData userData){
            this.userData = userData;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.chat :
                    Intent intent = new Intent(mContext,PersonChatActivity.class);
                    intent.putExtra("currentChatuserUid",userData.userUid);
                    intent.putExtra("currentChatuserDisplayname",userData.displayname);
                    intent.putExtra("userUid",currentUserdata.userUid);
                    intent.putExtra("UserDisplayname",currentUserdata.displayname);
                    mContext.startActivity(intent);
                    return true;
                case R.id.unfriend :
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Are you sure to unfriend?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserdata.userUid);
                                    currentuserdatabaseReference.child("friend").child(userData.userUid).removeValue();

                                    DatabaseReference requestuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.userUid);
                                    requestuserdatabaseReference.child("friend").child(currentUserdata.userUid).removeValue();

                                    userDatas.remove(userData);
                                    notifyDataSetChanged();

                                    Snackbar snackbar = Snackbar.make(parentView, "unfriend with " + userData.displayname, Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog,int id){
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
        return userDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void filter(String text) {userDatas.clear();
        if(text.isEmpty()){
            userDatas.addAll(userDatasfromdb);
        } else{
            text = text.toLowerCase();
            for(UserData item: userDatasfromdb){
                if(item.displayname.toLowerCase().contains(text)){
                    userDatas.add(item);
                }
            }
        }
        notifyDataSetChanged();

    }
}