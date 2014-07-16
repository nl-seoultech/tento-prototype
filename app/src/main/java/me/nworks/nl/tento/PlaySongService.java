package me.nworks.nl.tento;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.CheckBox;

import java.util.Timer;
import java.util.TimerTask;

import me.nworks.nl.tento.fragments.NowPlayingFragment;

public class PlaySongService extends Service {

    public static MediaPlayer mp = new MediaPlayer();
    public static String Title = ""; // 음악 파일 이름
    public static String SongId; // 음악 파일에 부여되는 고유 ID  MediaStore.Audio.Media._ID
    public static Boolean connect = false;
    private StatusChanged sc;
    private ServiceTimeTask stimetask;
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startTimer();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("func", 0)){
            case 0:
                startSong(intent.getStringExtra("path"));
                break;
            case 1:
                playpauseSong();
                break;
            case 2:
                loopControl(intent.getBooleanExtra("state", false));
                break;
            case 3:
                seekTo(intent.getIntExtra("seekTo", 0));
                break;
            case 4:
                changeSong(intent.getStringExtra("id"), intent.getStringExtra("path"));
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSong(String path){
        try{
            mp.reset(); //플레이어 초기화
            mp.setDataSource(path); //경로 설정
            mp.prepare();
            mp.start();
            sc.statuschanged(true);
            if(timer!=null){
                cancelTimer();
            }
        } catch(Exception e) {
        }
    }

    private void playpauseSong(){
        if(mp.isPlaying()){
            mp.pause();
            sc.statuschanged(false);
            startTimer();
        }else{
            mp.start();
            sc.statuschanged(true);
            cancelTimer();
        }
    }

    /**
     * 음악 정지 혹은 종료시 1분 후 서비스 자동 종료
     */
    private class ServiceTimeTask extends TimerTask{

        @Override
        public void run() {
            stopSelf();
        }
    }

    private void startTimer(){

        timer = new Timer();
        stimetask = new ServiceTimeTask();
        timer.schedule(stimetask, 60000);
    }

    private void cancelTimer(){
        timer.cancel();
        timer = null;
        stimetask = null;
    }

    /**
     * 체크 박스를 이용한 재생중인 음악 한곡 반복.
     */
    public void loopControl(boolean state){
        mp.setLooping(state);
    }

    /**
     * 노래 재생위치를 변경합니다
     *
     * @param pos 변경할 노래위치
     */
    public void seekTo(int pos) {
        mp.seekTo(pos);
    }

    public void changeSong(String id, String path) {
        SongId = id;
        Title = path;
        NowPlayingFragment.setSongInfo();
        startSong(path);
    }

    public class ServiceBinder extends Binder{
        public PlaySongService getService(){
            return PlaySongService.this;
        }
    }

    private final IBinder binder = new ServiceBinder();

    public interface StatusChanged{
        public void statuschanged(Boolean status); //Playing = true; Pause = false
    }

    public void registerInterface(StatusChanged _sc){
        this.sc = _sc;
    }

}



