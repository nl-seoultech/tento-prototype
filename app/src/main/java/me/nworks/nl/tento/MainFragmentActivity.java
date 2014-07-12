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

import java.util.ArrayList;

import me.nworks.nl.tento.fragments.NowPlayingFragment;
import me.nworks.nl.tento.fragments.PlaylistFragment;


public class MainFragmentActivity extends TentoFragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentactivity_main);

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
 * Fragment를 ViewPager에 적용시키기위한 아답터.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    // Tab에 들어가는 Fragment를 담는 ArrayList
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        fragments.add(new NowPlayingFragment());
        fragments.add(new PlaylistFragment());
    }

    /**
     *
     * @param position tab이 선택된 위치
     * @return 선택된 Fragment
     */
    @Override
    public Fragment getItem(int position) {
        // 만약에 이상한숫자가 들어온다면 0으로 position을 바꿈. 이거없으면 ArrayIndexOutOfBound 날수있음
        if(position > fragments.size()) {
            position = 0;
        }

        Fragment fragment = fragments.get(position);
        Bundle args = new Bundle();
        args.putInt("position", position + 1); // tab의 인덱스는 항상 position으로 Bundle에 넘김.
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
