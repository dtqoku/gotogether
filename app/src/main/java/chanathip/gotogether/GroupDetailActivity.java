package chanathip.gotogether;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class GroupDetailActivity extends AppCompatActivity {
    private UserData userData;
    private GroupData groupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        userData = new UserData();
        groupData = new GroupData();

        //get information from bundle
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                groupData.GroupUID = null;
                groupData.Name = null;
                userData.UserUid = null;
            } else {
                groupData.GroupUID = extras.getString("GroupUID");
                groupData.Name = extras.getString("GroupName");
                userData.UserUid = extras.getString("UserUid");
            }
        } else {
            groupData.GroupUID = (String) savedInstanceState.getSerializable("GroupUID");
            groupData.Name = (String) savedInstanceState.getSerializable("GroupName");
            userData.UserUid = (String) savedInstanceState.getSerializable("UserUid");
        }
        setTitle(groupData.Name + " Detail");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        GroupDetailFragment myFragment = GroupDetailFragment.newInstance(groupData.GroupUID,groupData.Name,userData.UserUid);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, myFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("GroupUID", groupData.GroupUID);
        savedInstanceState.putString("GroupName", groupData.Name);
        savedInstanceState.putString("UserUid", userData.UserUid);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
