package me.nworks.nl.tento.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;


public class PlaylistFragment extends Fragment{

    private ArrayList<String> list = new ArrayList<String>(); //음악 파일 목록을 담을 배열
    private ArrayList<String> path = new ArrayList<String>(); //음악 파일의 경로를 담을 배열

    private PlaylistInterface pi;
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
            PlaySongService.file = path.get(arg2);
            pi.startSong(path.get(arg2));

        }
    }

    public interface PlaylistInterface{ //MainFragmentActivity와 연결할 인터페이스
        public void startSong(String path);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pi = (PlaylistInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }
}
