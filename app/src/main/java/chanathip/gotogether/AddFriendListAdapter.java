package chanathip.gotogether;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neetc on 10/26/2016.
 */

public class AddFriendListAdapter extends RecyclerView.Adapter<AddFriendListAdapter.ViewHolder>{
    private List<UserData> userDatas;
    private List<UserData> userDatasfromdb;
    private Context context;
    private View parentView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendname;
        public TextView frienddetail;
        public TextView noficationcount;
        public ImageView iconnoficationcount;
        public ImageView overflow;

        public ViewHolder(View view) {
            super(view);

            friendname = (TextView) view.findViewById(R.id.txtfriendname);
            noficationcount = (TextView) view.findViewById(R.id.nofication_count);
            iconnoficationcount = (ImageView) view.findViewById(R.id.ic_nofication_count);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            frienddetail = (TextView) view.findViewById(R.id.txtfrienddetail);
        }
    }

    public AddFriendListAdapter(Context context, List<UserData> dataset,View view) {
        userDatasfromdb = dataset;
        userDatas = new ArrayList<>(userDatasfromdb);
        this.context = context;
        this.parentView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_row_friend, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final UserData userData = userDatas.get(position);
        viewHolder.friendname.setText(userData.displayname);
        viewHolder.frienddetail.setText(userData.Firstname + " " + userData.Lastname);

        viewHolder.noficationcount.setVisibility(View.GONE);
        viewHolder.iconnoficationcount.setVisibility(View.GONE);
        viewHolder.overflow.setVisibility(View.GONE);
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
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersDatabaseReference.orderByChild("display name").equalTo(text).limitToFirst(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    userDatas.clear();
                    Map<String, Object> searchUserdataMap = (Map<String, Object>) dataSnapshot.getValue();

                    for (HashMap.Entry<String,Object> entry : searchUserdataMap.entrySet()) {
                        String key = entry.getKey();
                        Map<String,String> value = (Map<String,String>)entry.getValue();
                        UserData userData = new UserData();

                        userData.UserUid = key;
                        userData.displayname = value.get("display name");
                        userData.Firstname = value.get("First name");
                        userData.Lastname = value.get("Last name");

                        userDatas.add(userData);
                    }
                }
                else{
                    userDatas.clear();
                }
                notifyDataSetChanged();
                ((AddFriendActivity)context).onSearchComplete();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("queryworking", databaseError.toString());
            }
        });
    }


}