package me.nworks.nl.tento;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.nworks.nl.tento.fragments.PlaylistFragment;


public class MenuFragmentActivity extends TentoFragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setViewPager(mViewPager);
    }

    @Override
    protected void tabSelected(int i) {
        mViewPager.setCurrentItem(i);
    }

}

/**
 * 아무 의미없는 더미 Fragment 내용 표시할만한게없을때 숫자로 프래그먼트를 채웁니다.
 */
class DemoObjectFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                Integer.toString(args.getInt(ARG_OBJECT)));
        return rootView;
    }
}


/**
 * Fragment를 ViewPager에 적용시키기위한 아답터.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     *
     * @param position tab이 선택된 위치
     * @return 선택된 Fragment
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 1: {
                fragment = new PlaylistFragment();
            };
            break;
            default: {
                fragment = new DemoObjectFragment();
                Bundle args = new Bundle();
                args.putInt(DemoObjectFragment.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
            }
            ;
            break;
        }
        ;
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
