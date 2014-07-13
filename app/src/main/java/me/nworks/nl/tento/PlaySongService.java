package me.nworks.nl.tento;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaySongService extends Service {

    public static MediaPlayer mp = new MediaPlayer();

    public static String Title=""; //음악 파일 이름
    public static Boolean connect = false;
    private StatusChanged sc;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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
        }catch(Exception e){
        }
    }
    private void playpauseSong(){
        if(mp.isPlaying()){
            mp.pause();
            sc.statuschanged(false);
        }else{
            mp.start();
            sc.statuschanged(true);
        }
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
