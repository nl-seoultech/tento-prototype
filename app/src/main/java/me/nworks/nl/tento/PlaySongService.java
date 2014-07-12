package me.nworks.nl.tento;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlaySongService extends Service {

    public static MediaPlayer mp = new MediaPlayer();
    public static String file="";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("func",0)){
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
        }catch(Exception e){
        }
    }
    private void playpauseSong(){
        if(mp.isPlaying()){
            mp.pause();
        }else{
            mp.start();
        }
    }
}
