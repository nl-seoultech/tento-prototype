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

    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> path = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.

        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        Bundle args = getArguments();
        Activity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();
        ArrayAdapter<String> listadapter;

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

        ListView listview = (ListView)rootView.findViewById(R.id.playlist_listview);
        listadapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new ListViewItemClickListener());

        Button btnPause = (Button) rootView.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
            }
        });

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
            playSong(path.get(arg2));
        }
    }


    private void playSong(String path){
        try{
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        }catch(Exception e){
        }
    }
}
