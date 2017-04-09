package chanathip.gotogether;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivityV2 extends AppCompatActivity {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    FloatingActionMenu fabmenu;
    com.github.clans.fab.FloatingActionButton fabnewfriend;
    com.github.clans.fab.FloatingActionButton fabnewgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_v2);
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        fabmenu = (FloatingActionMenu) findViewById(R.id.fab);
        fabnewfriend = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fabnewfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivityV2.this,AddFriendActivity.class);
                startActivity(intent);
            }
        });
        fabnewgroup = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fabnewgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivityV2.this, CreateNewGroupActivity.class);
                intent.putExtra("userUid", currentUserUid);
                startActivity(intent);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle("Notification");
                        fabmenu.showMenuButton(true);
                        break;
                    case 1:
                        setTitle("Social");
                        fabmenu.showMenuButton(true);
                        break;
                    case 2:
                        setTitle("More");
                        fabmenu.hideMenuButton(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group_white_120dp);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_assistant_photo_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_more_horiz_white_24dp);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
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
            View rootView = inflater.inflate(R.layout.fragment_home_activity_v2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return HomeNotificationFragment.newInstance();
            }
            else if(position == 1){
                return SocialFragment.newInstance();
            } else if(position == 2){
                return  MoreFragment.newInstance();
            }
                return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
