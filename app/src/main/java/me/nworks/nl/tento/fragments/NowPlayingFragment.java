package me.nworks.nl.tento.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;
import me.nworks.nl.tento.SongStore;
import me.nworks.nl.tento.TentoAPI;
import me.nworks.nl.tento.soundfile.CheapSoundFile;

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

        /**
         * MainFragmentActivity에서 changeSong 을 연결하기 위해서 사용
         * @param song
         */
        public void changeSong(SongStore.Song song);
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

    private Button btnAnalyze;

    private TextView txtTitle;

    private CheckBox checkboxRepeat;

    private CheckBox checkboxRandom;

    private CheckBox checkboxRepeatAll;

    private ImageView imgAlbumArt;

    private View rootView;

    private SeekBar seekbarSong;

    private Button btnNextSong;

    private Button btnPreviousSong;

    // UpdateProgressTime 를 관리하기위해서 사용하는 핸들러
    private Handler handler = new Handler();

    private SongStore songStore;

    private UpdateProgressTime updateProgressTime = new UpdateProgressTime();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        txtTitle =  (TextView) rootView.findViewById(R.id.txtTitle);
        btnPause = (Button) rootView.findViewById(R.id.btnPlayPause); //정지 버튼
        btnAnalyze = (Button) rootView.findViewById(R.id.btnAnalyze);
        checkboxRepeat = (CheckBox) rootView.findViewById(R.id.checkboxRepeat);
        checkboxRandom = (CheckBox) rootView.findViewById(R.id.checkboxRandom);
        checkboxRepeatAll = (CheckBox) rootView.findViewById(R.id.checkboxRepeatAll);
        imgAlbumArt = (ImageView) rootView.findViewById(R.id.imgAlbumArt);
        seekbarSong = (SeekBar) rootView.findViewById(R.id.seekbarSong);
        btnNextSong = (Button) rootView.findViewById(R.id.btnNext);
        btnPreviousSong = (Button) rootView.findViewById(R.id.btnPrev);
        songStore = new SongStore(getActivity());

        btnPause.setOnClickListener(this);
        btnAnalyze.setOnClickListener(this);
        checkboxRepeat.setOnClickListener(this);
        checkboxRepeatAll.setOnClickListener(this);
        checkboxRandom.setOnClickListener(this);
        seekbarSong.setOnSeekBarChangeListener(this);
        btnNextSong.setOnClickListener(this);
        btnPreviousSong.setOnClickListener(this);

        setSongInfo();
        if (PlaySongService.mp.isPlaying()) {
            btnPause.setText("Pause");
        } else {
            btnPause.setText("Play");
        }

        return rootView;
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(PlaySongService.SongId == null) return;
        switch (view.getId()) {
            case R.id.btnPlayPause:
                if(!PlaySongService.Title.isEmpty()){
                    npi.playpauseSong();
                }
                break;
            case R.id.checkboxRepeat:
                npi.loopControl(checkboxRepeat.isChecked());
                break;
            case R.id.checkboxRandom:
                {
                    songStore.setRandom(checkboxRandom.isChecked());
                    if(PlaySongService.SongId != null) {
                        songStore.setRandomFirstSong(PlaySongService.SongId);
                    }
                }
                break;
            case R.id.btnNext:
                {
                    if(PlaySongService.SongId != null) {
                        changeNextSong();
                    }
                }
                break;
            case R.id.btnPrev:
                {
                    if(PlaySongService.SongId != null) {
                        SongStore.Song song = songStore.findPrevSongById(PlaySongService.SongId);
                            npi.changeSong(song);
                    }
                }
                break;
            case R.id.btnAnalyze:
                postSongFrame();
                break;
        }
    }
    private void postSongFrame() {
        mLoadingLastUpdateTime = System.currentTimeMillis();
        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) {
                    Log.d("hello", String.valueOf(fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return true;
            }
        };
        final SongStore.Song song = songStore.findSongById(PlaySongService.SongId);
        final String songPath = song.getPath();
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(songPath, listener);

                } catch (final Exception e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finishLoadSoundFile();
                    }
                });
            }
        }.start();
    }

    private void finishLoadSoundFile() {
        TentoAPI api = new TentoAPI();
        CheapSoundFile my = mSoundFile;
        String payload = "{\"frames\": [";
        for(int f : mSoundFile.getFrameGains())  {
            payload += String.valueOf(f) + ",";
        }
        payload += "]}";
        api.postJSON("/analyze_songs/", payload, new TentoAPI.TentoCallback<String>() {
            @Override
            public void success(String result) {
                Log.d("hello", result);
            }

            @Override
            public void error(String message) {
                Log.d("hello", message);
            }
        });
    }

    private long mLoadingLastUpdateTime;
    private CheapSoundFile mSoundFile;

    /**
     * MainFragmentActivity 의 sc ( PlaySongService.StatusChagned ) 구현에서 연결되는 메소드
     * status 는 다음과 같은 상태로 정의됩니다.
     *  - StatusChanged.PLAY : 재생중
     *  - StatusChanged.PAUSE : 일시 정지중
     *  - StatusChanged.CHANGE : 노래 변경
     *
     * @param status 다음 값중 하나를 가집니다. StatusChanged.PLAY, StatusChanged.PAUSE,
     *               StatusChanged.CHANGE.
     */
    public void statusChanged(int status) {
        switch (status) {
            case PlaySongService.StatusChanged.PAUSE: {
                // 노래가 재생중이면 버튼은 "재생"이 되야함
                btnPause.setText("Play");
                stopProgressBar();
            }
            break;
            case PlaySongService.StatusChanged.PLAY: {
                // 노래가 재생중이면 버튼은 "일시정지"가 되야함
                btnPause.setText("Pause");
                final SongStore.Song song = songStore.findSongById(PlaySongService.SongId);
                updateProgressBar();
            }
            break;
            case PlaySongService.StatusChanged.CHANGE: {
                setSongInfo();
            }
            break;
            case PlaySongService.StatusChanged.AUTO_NEXT: {
                changeNextSong();
            }
            break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        npi = (NowPlayingInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }

    /**
     * onClick과 statusChanged에서 쓰이는 부분의 중복 제거를 위해 만듦
     */
    public void changeNextSong() {
        SongStore.Song song = songStore.findNextSongById(PlaySongService.SongId);
        if (!songStore.isLastSongById(PlaySongService.SongId) || checkboxRepeatAll.isChecked()) {
            npi.changeSong(song);
        }
    }

    /**
     * 음악 정보(앨범사진, 타이틀, 등등) 및 버튼 설정
     */
    public void setSongInfo() {
        if(PlaySongService.SongId != null) {
            SongStore.Song song = songStore.findSongById(PlaySongService.SongId);
            imgAlbumArt.setImageBitmap(song.getArtwork());
            txtTitle.setText(song.getTitle());
        }
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

