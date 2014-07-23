package me.nworks.nl.tento;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
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

        private Bitmap artwork;

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

        public void setArtwork(Bitmap bm) {
            artwork = bm;
        }

        public Bitmap getArtwork() {
            return artwork;
        }

    }

    public static ArrayList<Song> songs;

    private static HashMap<String, Integer> songIndexById;

    private static HashMap<String, Integer> songIndexByPath;

    private Activity activity;

    private ContentResolver resolver;

    // 노래 랜덤 재생 상태
    private static boolean random = false;

    // 랜덤 재생 인덱스의 순서를 저장하는 리스트, 실제 songs 리스트는 그대로두고 이 리스트만 섞어서 랜덤 재생곡을 결정합니다.
    private ArrayList<Integer> randomIndecies;

    // findNextOrPrevRandomableIndexById 에서 이전곡을 찾아올때 사용하는 상수
    private int INDEX_PREV = -1;

    // findNextOrPrevRandomableIndexById 에서 다음곡을 찾아올때 사용하는 상수
    private int INDEX_NEXT = 1;

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
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
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
                Song s = new Song(cursor);
                //TODO: 모든 노래 artwork 로딩하고있으므로 메모리 낭비가 있을듯. 필요할때 로딩하는게 괜찮지않을까.
                Uri uri = Uri.parse("content://media/external/audio/media/" + s.getId() + "/albumart");
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = resolver.openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                        s.setArtwork(bm);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                songs.add(s);
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

    public int findIndexById(String id) {
        return songIndexById.get(id);
    }

    /**
     * 랜덤 가능한 다음곡 또는 이전곡의 인덱스를 찾아옵니다.
     *
     * @param id 다음곡 혹은 이전곡을 찾을 곡의 고유 id
     * @param prevOrNext 다음곡을 찾아올건지, 이전곡을 찾아올건지 결정하는 값. 이전곡일때는
     *                   INDEX_PREV 의 값을 사용하여야하고, 다음곡일때는 INDEX_NEXT 값을 사용해야합니다.
     * @return 다음곡 혹은 이전곡의 인덱스
     */
    public int findNextOrPrevRandomableIndexById(String id, int prevOrNext) {
        int prevOrNextIndex;
        int realIndex = findIndexById(id);

        if(random) {
            int currentRandomIndex = randomIndecies.indexOf(realIndex);
            int i = indexCirculationCorrection(currentRandomIndex + prevOrNext);
            prevOrNextIndex = randomIndecies.get(i);
        } else {
            prevOrNextIndex = indexCirculationCorrection(realIndex + prevOrNext);
        }

        return prevOrNextIndex;
    }

    /**
     * 다음곡이나 이전곡을 선택했을때, 유효한 범위안에 들어있지않다면 보정하여 노래 진행을 순환할 수 있도록 합니다.
     * 마지막 곡의 다음곡은 첫번째곡으로 선정되며, 처음곡의 이전곡은 마지막 곡으로 선택됩니다.
     *
     * @param index 보정할 인덱스
     * @return 보정된 인덱스. 마지막 곡 + 1의 인덱스는 처음곡으로, 처음곡 - 1의 인덱스는 마지막곡으로 바꿉니다.
     */
    public int indexCirculationCorrection(int index) {
        int size = songs.size();
        if(index >= size) {
            index -= size;
        } else if(index < 0) {
            index += size;
        }
        return index;
    }

    public boolean isLastSongById(String songId) {
        Song lastSong;
        if(random) {
            int randomIndex  = randomIndecies.get(randomIndecies.size() - 1);
            lastSong = songs.get(randomIndex);
        } else {
            lastSong = songs.get(songs.size() - 1);
        }
        return lastSong.getId().equals(songId);
    }

    public Song findNextSongById(String id) {
        int nextSongIndex = findNextOrPrevRandomableIndexById(id, INDEX_NEXT);
        return songs.get(nextSongIndex);
    }

    public Song findPrevSongById(String id) {
        int prevSongIndex = findNextOrPrevRandomableIndexById(id, INDEX_PREV);
        return songs.get(prevSongIndex);
    }

    public ArrayList<String> getSongNames() {
        ArrayList<String> names = new ArrayList<String>();

        for(Song s : songs) {
            names.add(String.format("%s - %s", s.getTitle(), s.getArtist()));
        }

        return names;
    }

    public void setRandom(boolean r) {
        if(r) {
            randomIndecies = new ArrayList<Integer>();

            for(int i = 0; i < songs.size(); i++) {
                randomIndecies.add(i);
            }
            Collections.shuffle(randomIndecies);
        }
        random = r;
    }

    /**
     * 랜덤 재생 누를 시점에 노래를 랜덤 인덱스의 맨앞으로보내는 메소드.
     *
     * @param songId 현재 재생중인 노래 인덱스
     */
    public void setRandomFirstSong(String songId) {
        int songIndex = songIndexById.get(songId);
        int currentIndex = randomIndecies.indexOf(songIndex);
        randomIndecies.remove(currentIndex);
        ArrayList<Integer> newRandomIndecies = new ArrayList<Integer>();
        newRandomIndecies.add(songIndex);
        for(int i : randomIndecies) {
            newRandomIndecies.add(i);
        }
        randomIndecies = newRandomIndecies;
    }
}
