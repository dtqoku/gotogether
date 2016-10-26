package chanathip.gotogether;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFriendActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.searchViewfriend)
    SearchView _searchViewfriend;
    @BindView(R.id.empty_view)
    TextView _empty_view;
    @BindView(R.id.activity_add_friend)
    View _activity_add_friend;

    private RecyclerView.LayoutManager layoutManager;
    private AddFriendListAdapter addFriendListAdapter;
    private List<UserData> userDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        ButterKnife.bind(this);

        updatesearchlist();
    }
    public void onSearchComplete(){
        if (addFriendListAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            _empty_view.setVisibility(View.VISIBLE);

            Snackbar snackbar = Snackbar.make(_activity_add_friend,"not found that displayname",Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            _empty_view.setVisibility(View.GONE);
        }

    }

    private void updatesearchlist(){
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        userDatas = new ArrayList<>();


        addFriendListAdapter = new AddFriendListAdapter(this,userDatas, _activity_add_friend);
        recyclerView.setAdapter(addFriendListAdapter);


        _searchViewfriend.onActionViewExpanded();
        _searchViewfriend.clearFocus();
        _searchViewfriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                addFriendListAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
