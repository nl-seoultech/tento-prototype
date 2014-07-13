package me.nworks.nl.tento.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;
import me.nworks.nl.tento.SongStore;

public class PlaylistFragment extends Fragment {

    private PlaylistInterface pi;

    private MediaPlayer mp = new MediaPlayer();  //음악 플레이어

    private SongStore songStore;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        Bundle args = getArguments();
        Activity activity = getActivity();
        ArrayAdapter<String> listadapter; //Listview인 list와 list배열을 연결할 어댑터
        songStore = new SongStore(activity);
        ListView listview = (ListView)rootView.findViewById(R.id.listPlaylist);
        listadapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_list_item_1,
                songStore.getSongNames());
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new ListViewItemClickListener()); //리스트 클릭 이벤트

        return rootView;
    }
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            String path = songStore.findSongByIndex(arg2).getPath();
            PlaySongService.Title = path;
            pi.startSong(path); //재생 메서드에 선택된 음악 파일 경로 넘김.
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
