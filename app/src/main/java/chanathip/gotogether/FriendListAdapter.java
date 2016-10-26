package chanathip.gotogether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frineddetail;

        public ViewHolder(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            frineddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
        }
    }

    public FriendListAdapter(Context context, List<UserData> dataset,View view) {
        userDatasfromdb = dataset;
        userDatas = new ArrayList<>(userDatasfromdb);
        mContext = context;
        this.parentView = view;
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
                if(item.Firstname.toLowerCase().contains(text) || item.Lastname.toLowerCase().contains(text)){
                    userDatas.add(item);
                }
            }
        }
        notifyDataSetChanged();

    }
}