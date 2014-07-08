package me.nworks.nl.tento.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import me.nworks.nl.tento.R;


public class PlaylistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        Bundle args = getArguments();
        Activity activity = getActivity();
        ContentResolver resolver = activity.getContentResolver();
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
        TableLayout playlistTable = (TableLayout) rootView.findViewById(R.id.playlist_table);
        while(cursor.moveToNext()) {
            TableRow row = new TableRow(activity);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView title = new TextView(activity);
            TextView artist = new TextView(activity);
            title.setText(cursor.getString(1));
            artist.setText(cursor.getString(2));

            row.addView(title);
            row.addView(artist);
            playlistTable.addView(row);
            Log.d("hello", cursor.getString(0) + "||"
                    + cursor.getString(1) + "||"
                    + cursor.getString(2) + "||"
                    + cursor.getString(3) + "||"
                    + cursor.getString(4) + "||"
                    + cursor.getString(5));
        }
        return rootView;
    }

}
