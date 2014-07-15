package me.nworks.nl.tento;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import me.nworks.nl.tento.fragments.NowPlayingFragment;
import me.nworks.nl.tento.fragments.PlaylistFragment;


public class MainFragmentActivity extends TentoFragmentActivity implements PlaylistFragment.PlaylistInterface, NowPlayingFragment.NowPlayingInterface {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Intent intent = new Intent("tento.PlaySongService");
    NowPlayingFragment npf;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentactivity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setViewPager(mViewPager);
        npf = (NowPlayingFragment) mSectionsPagerAdapter.fragments.get(0);

        if(PlaySongService.connect){ //액티비티가 종료되고 다시 켜진 경우 실행중인 서비스에 재연결.
            connectService();
        }
    }

    @Override
    protected void tabSelected(int i) {
        mViewPager.setCurrentItem(i);
    }

    /**
     * NowPlayingFragment.NowPlayingInterface의 구현
     */
    @Override
    public void playpauseSong() {
        intent.putExtra("func", 1);
        startService(intent);
    }

    /**
     * NowPlayingFragment.loopControl 의 구현
     */
    @Override
    public void loopControl(boolean state) {
        intent.putExtra("func", 2);
        intent.putExtra("state", state);
        startService(intent);
    }

    @Override
    public void seekTo(int pos) {
        intent.putExtra("func", 3);
        intent.putExtra("seekTo", pos);
        startService(intent);
    }


    /**
     * PlaylistFragment.PlaylistInterface의 구현
     *
     * @param path mp3 파일의 경로
     */
    @Override
    public void startSong(String path) {

        connectService();
        intent.putExtra("func", 0);
        intent.putExtra("path", path);
        startService(intent);

        npf.setSongInfo();  //NowPlayFragment 화면 갱신
        npf.setbtnText(); //StatusChanged 인터페이스가 액티비티와 연결되는데 걸리는 시간보다 플레이어가 재생되는 시간이 더 빠르기 때문에 넣은 메서드.
        npf.updateProgressBar(); // 위 npf.setbtnText와 같은 이유.
        tabSelected(0); //NowPlayingFragment로 이동

    }

    public void connectService(){
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    public PlaySongService ps;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlaySongService.ServiceBinder binder = (PlaySongService.ServiceBinder)iBinder;
            ps = binder.getService();
            ps.registerInterface(sc);
            PlaySongService.connect = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            PlaySongService.connect = false;
            sc = null;
        }

       private PlaySongService.StatusChanged sc = new PlaySongService.StatusChanged() {
            @Override
            public void statuschanged(Boolean status) {
                npf.statuschanged(status);
            }
        };
    };
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
