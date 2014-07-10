package me.nworks.nl.tento;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/** tento 프로토타입에 쓰이는 `ActionBar`를 정의하는 클래스
 *
 */
public class TentoActionBar {

    // ActionBar 를 활성화시킬 activity
    private Activity activity;

    // activity로부터 가져온 ActionBar
    private ActionBar actionBar;

    // 현재 재생 탭
    private ActionBar.Tab currentPlay;

    // 재생 목록 탭
    private ActionBar.Tab playlist;

    /**
     *
     * @param act ActionBar를 활성화 시킬 Activity activity.getActionBar()로 actionBar 를
     *            사용하기위해서 받아옵니다.
     */
    public TentoActionBar(Activity act) {
        activity = act;
        actionBar = activity.getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        initTabs();
    }


    public void initTabs() {
        currentPlay = actionBar.newTab().setText("현재 재생중");
        playlist = actionBar.newTab().setText("재생 목록");
    }


    public void setTabListener(ActionBar.TabListener tabListener) {
        currentPlay.setTabListener(tabListener);
        playlist.setTabListener(tabListener);

        actionBar.addTab(currentPlay);
        actionBar.addTab(playlist);
    }

    /**
     * Fragment에서 스와이프로 탭을 옮겨가면 탭 선택자도 바꾸기위해서 ViewPager를 받아옵니다.
     *
     * @param pager Fragment가 사용하는 ViewPager
     */
    public void setViewPager(ViewPager pager) {
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
    }
}
