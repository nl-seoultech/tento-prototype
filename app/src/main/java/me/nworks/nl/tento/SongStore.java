package me.nworks.nl.tento;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class SongStore {

    /**
     * MediaStore 에서 검색해온 mp3의 정보를 담는 클래스
     */
    public class Song {

        // 안드로이드 MediaStore 에서 mp3의 고유 id
        private String id;

        // mp3의 아티스트
        private String artist;

        // mp3의 제목
        private String title;

        // mp3의 경로
        private String path;

        // mp3 의 display 이름
        private String displayName;

        // mp3의 길이
        private String duration;

        public Song(Cursor cursor) {
            id = cursor.getString(0);
            artist = cursor.getString(1);
            title = cursor.getString(2);
            path = cursor.getString(3);
            displayName = cursor.getString(4);
            duration = cursor.getString(5);
        }

        public String getId() {
            return id;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getPath() {
            return path;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDuration() {
            return duration;
        }
    }

    private static ArrayList<Song> songs;

    private static HashMap<String, Integer> songIndexById;

    private static HashMap<String, Integer> songIndexByPath;

    private Activity activity;

    public SongStore(Activity a) {
        activity = a;
        initializeSongs(false);
    }

    /**
     * songs 에 사용자가 가지고있는 노래 목록을 저장합니다.
     *
     * @param refresh 만약에 true이면 songs 에 노래 목록이 저장되있어도 초기화후 새로 저장합니다.
     */
    public void initializeSongs(boolean refresh) {
        if(refresh || songs == null || songs.isEmpty()) {
            songs = new ArrayList<Song>();
            songIndexById = new HashMap<String, Integer>();
            songIndexByPath = new HashMap<String, Integer>();
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);

            while(cursor.moveToNext()) {
                songs.add(new Song(cursor));
            }

            // 나중에 findSongByPath나 findSongById 같은걸 사용하기위해서 id와 path를 HashMap에 저장해놓습니다.
            for(int i = 0; i < songs.size(); i++) {
                songIndexById.put(songs.get(i).getId(), i);
                songIndexByPath.put(songs.get(i).getPath(), i);
            }
        }
    }

    public Song findSongByPath(String path) {
        return songs.get(songIndexByPath.get(path));
    }

    public Song findSongById(String id) {
        return songs.get(songIndexById.get(id));
    }

    public Song findSongByIndex(int i) {
        return songs.get(i);
    }

    public ArrayList<String> getSongNames() {
        ArrayList<String> names = new ArrayList<String>();

        for(Song s : songs) {
            names.add(String.format("%s - %s", s.getTitle(), s.getArtist()));
        }

        return names;
    }
}
