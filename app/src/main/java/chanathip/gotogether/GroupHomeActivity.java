package chanathip.gotogether;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class GroupHomeActivity extends AppCompatActivity {
    private UserData userData;
    private GroupData groupData;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home);

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
        setTitle(groupData.Name);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.leave:
                DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                groupdatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupData.rank = String.valueOf(dataSnapshot.child("member").child(userData.UserUid).getValue());
                        groupData.Membercount = Integer.valueOf(String.valueOf(dataSnapshot.child("membercount").getValue()));

                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupHomeActivity.this);
                        builder.setMessage("Are you sure to leave this group?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //leave group
                                        if (groupData.rank.equals("leader") && groupData.Membercount > 1) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupHomeActivity.this);
                                            builder.setMessage("you cannot leave group if you are leader or not last one in group")
                                                    .setCancelable(false)
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            //do nothing
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

                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(groupData.GroupUID);

                                            onBackPressed();
                                        } else {
                                            //just leave
                                            DatabaseReference currentuserdatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userData.UserUid);
                                            currentuserdatabaseReference.child("group").child(groupData.GroupUID).removeValue();

                                            DatabaseReference groupdatabaseReference = FirebaseDatabase.getInstance().getReference().child("groups").child(groupData.GroupUID);
                                            groupdatabaseReference.child("member").child(userData.UserUid).removeValue();
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

                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(groupData.GroupUID);


                                            onBackPressed();

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;
            case R.id.map:
                Intent intent = new Intent(this, NavigationActivity.class);
                intent.putExtra("GroupUID", groupData.GroupUID);
                intent.putExtra("GroupName", groupData.Name);
                intent.putExtra("UserUid", userData.UserUid);
                startActivity(intent);
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_home, menu);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return GroupChatFragment.newInstance(groupData.GroupUID,
                            groupData.Name,
                            userData.UserUid);
                case 1:
                    return GroupDetailFragment.newInstance(groupData.GroupUID,
                            groupData.Name,
                            userData.UserUid);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show  pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "chat";
                case 1:
                    return "info";
            }
            return null;
        }
    }
}
