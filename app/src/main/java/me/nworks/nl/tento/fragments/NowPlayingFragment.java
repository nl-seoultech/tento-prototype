package me.nworks.nl.tento.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;
import me.nworks.nl.tento.SongStore;

public class NowPlayingFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {

    /**
     * MainFragmentActivity와 연결할 인터페이스
     */
    public interface NowPlayingInterface {

        /**
         * PlaySongService 에서 playpauseSong 을 연결하기위해서 사용
         */
        public void playpauseSong();

        /**
         * PlaySongService 에서 loopControl 을 연결하기위해서 사용
         */
        public void loopControl(boolean state);

        /**
         * PlaySongService 에서 seekTo 을 연결하기위해서 사용
         */
        public void seekTo(int pos);

    }

    /**
     * seekbarSong 의 progress 값을 500ms 마다 바꾸기위해서 필요한 클래스
     */
    private class UpdateProgressTime implements Runnable {

        @Override
        public void run() {
            long totalDuration = PlaySongService.mp.getDuration();
            long currentDuration = PlaySongService.mp.getCurrentPosition();
            int pos = (int) ((currentDuration / (double) totalDuration) * 100);
            seekbarSong.setProgress(pos);
            handler.postDelayed(this, 500);
        }
    }

    private NowPlayingInterface npi;

    private Button btnPause;

    private TextView txtTitle;

    private CheckBox checkboxRepeat;

    private ImageView imgAlbumArt;

    private View rootView;

    private SeekBar seekbarSong;

    // UpdateProgressTime 를 관리하기위해서 사용하는 핸들러
    private Handler handler = new Handler();

    private UpdateProgressTime updateProgressTime = new UpdateProgressTime();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        txtTitle =  (TextView) rootView.findViewById(R.id.txtTitle);
        btnPause = (Button) rootView.findViewById(R.id.btnPlayPause); //정지 버튼
        checkboxRepeat = (CheckBox) rootView.findViewById(R.id.checkboxRepeat);
        imgAlbumArt = (ImageView) rootView.findViewById(R.id.imgAlbumArt);
        seekbarSong = (SeekBar) rootView.findViewById(R.id.seekbarSong);

        btnPause.setOnClickListener(this);
        checkboxRepeat.setOnClickListener(this);
        seekbarSong.setOnSeekBarChangeListener(this);

        setSongInfo();
        if (PlaySongService.mp.isPlaying()) {
            btnPause.setText("Pause");
        } else {
            btnPause.setText("Play");
        }

        return rootView;
    }

    /**
     * TODO: previous, stop, next 버튼도 구현 예정
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlayPause:
                if(!PlaySongService.Title.isEmpty()){
                    npi.playpauseSong();
                }
                break;
            case R.id.checkboxRepeat:
                npi.loopControl(checkboxRepeat.isChecked());
                break;
        }
    }

    /**
     * MainFragmentActivity 의 sc ( PlaySongService.StatusChagned ) 구현에서 연결되는 메소드
     * status 가 true 이면 재생중인 상태이고 false 이면 일시정지상태입니다.
     *
     * @param status
     */
    public void statuschanged(Boolean status) {
        if(status){
            btnPause.setText("Pause");
            updateProgressBar();
        } else {
            btnPause.setText("Play");
            stopProgressBar();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        npi = (NowPlayingInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }

    /**
     * 음악 정보(앨범사진, 타이틀, 등등) 및 버튼 설정
     */
    public void setSongInfo() {
        SongStore s = new SongStore(getActivity());
        if(PlaySongService.SongId != null) {
            SongStore.Song song = s.findSongById(PlaySongService.SongId);
            imgAlbumArt.setImageBitmap(song.getArtwork());
        }
        txtTitle.setText(PlaySongService.Title); // song.getTitle() 해도 괜찮을듯
    }

    public void setbtnText() {
        btnPause.setText("Pause");
    }

    /**
     * UpdateProgressTime 을 500ms 후에 작동하게합니다.
     * UpdateProgressTime 내부에서도 handler.postDelayed(this, 500) 을 실행하기때문에 handler 에서
     * 콜백 함수들이 삭제되기전까진 계속 실행하게됩니다.
     */
    public void updateProgressBar() {
        handler.postDelayed(updateProgressTime, 500);
    }

    /**
     * UpdateProgressTime 을 handler 의 콜백함수에서 제외함으로써 seekbarSong 의 업데이트가 중지됩니다.
     */
    public void stopProgressBar() {
        handler.removeCallbacks(updateProgressTime);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    /**
     * 유저가 SeekBar 를 누르기시작하면 위치를 바꾸고싶어하는 것이므로 일단 SeekBar 의 진행을 멈춥니다.
     *
     * @param seekBar 연결시킨 SeekBar, 이 클래스에서 선언된 seekbarSong
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        stopProgressBar();
    }

    /**
     * 유저가 SeekBar 로 위치를 찾다가 놓으면 그 위치부터 재생하고싶어하는 것이므로 노래를 그 위치부터 재생하도록 합니다.
     *
     * @param seekBar 연결시킨 SeekBar, 이 클래스에서 선언된 seekbarSong
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        stopProgressBar();
        long totalDuration = PlaySongService.mp.getDuration();
        int pos = (int) (totalDuration * (seekBar.getProgress() / 100.0));
        npi.seekTo(pos);
        updateProgressBar();
    }
}

