package me.nworks.nl.tento;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.nworks.nl.tento.fragments.PlaylistFragment;

public class MenuFragmentActivity extends FragmentActivity {
    MenuPagerAdapter menuPagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuPagerAdapter = new MenuPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(menuPagerAdapter);


        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                int a = tab.getPosition();
                mViewPager.setCurrentItem(a);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab currentPlay = actionBar.newTab().setText("현재 재생중");
        ActionBar.Tab playlist = actionBar.newTab().setText("재생 목록");

        currentPlay.setTabListener(tabListener);
        playlist.setTabListener(tabListener);
        actionBar.addTab(currentPlay);
        actionBar.addTab(playlist);
    }
}

class MenuPagerAdapter extends FragmentStatePagerAdapter {

    public MenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch(i) {
            case 1: {
                fragment = new PlaylistFragment();
            }; break;
            default: {
                fragment = new DemoObjectFragment();
                Bundle args = new Bundle();
                args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
                fragment.setArguments(args);
            }; break;
        };
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

// Instances of this class are fragments representing a single
// object in our collection.
class DemoObjectFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                Integer.toString(args.getInt(ARG_OBJECT)));
        return rootView;
    }
}
