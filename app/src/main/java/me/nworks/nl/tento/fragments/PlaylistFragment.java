package me.nworks.nl.tento.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import me.nworks.nl.tento.R;


public class PlaylistFragment extends Fragment{

    private ArrayList<String> list = new ArrayList<String>(); //음악 파일 목록을 담을 배열
    private ArrayList<String> path = new ArrayList<String>(); //음악 파일의 경로를 담을 배열
    private MediaPlayer mp = new MediaPlayer(); //음악 플레이어
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        Bundle args = getArguments();
        Activity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();
        ArrayAdapter<String> listadapter; //Listview인 list와 list배열을 연결할 어댑터

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        ListView listview = (ListView)rootView.findViewById(R.id.listPlaylist);
        listadapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new ListViewItemClickListener()); //리스트 클릭 이벤트

        Button btnPause = (Button) rootView.findViewById(R.id.btnPause); //정지 버틈
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
            }
        }); //정지 버튼 이벤트

           while(cursor.moveToNext()) {
               list.add(cursor.getString(1)+" - "+ cursor.getString(2));
               path.add(cursor.getString(3));
        }
        return rootView;
    }
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            // TODO Auto-generated method stub
            playSong(path.get(arg2)); //재생 메서드에 선택된 음악 파일 경로 넘김.

        }
    }


    private void playSong(String path){
        try{
            mp.reset(); //플레이어 초기화
            mp.setDataSource(path); //경로 설정
            mp.prepare();
            mp.start();
        }catch(Exception e){
        }
    }
}
