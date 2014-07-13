package me.nworks.nl.tento.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;

public class NowPlayingFragment extends Fragment implements View.OnClickListener{

    NowPlayingInterface npi;
    Button btnPause;
    TextView txtTitle;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        Bundle args = getArguments();

        txtTitle =  (TextView) rootView.findViewById(R.id.txtTitle);

        btnPause = (Button) rootView.findViewById(R.id.btnPlayPause); //정지 버튼
        btnPause.setOnClickListener(this);
        setSongInfo();

        if (PlaySongService.mp.isPlaying()) {
            btnPause.setText("Pause");
        } else {
            btnPause.setText("Play");
        }
        return rootView;
    }

    @Override
    public void onClick(View view) { //previous, stop, next 버튼도 구현 예정
        switch (view.getId()){
            case R.id.btnPlayPause:
                    if(!PlaySongService.Title.isEmpty()){
                        npi.playpauseSong();
                    }
                break;
        }
    }

    public void statuschanged(Boolean status) {
        if(status){
            btnPause.setText("Pause");
        }else{
            btnPause.setText("Play");
        }
    }

    public interface NowPlayingInterface{ //MainFragmentActivity와 연결할 인터페이스
        public void playpauseSong();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        npi = (NowPlayingInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }

    public void setSongInfo(){ //음악 정보(앨범사진, 타이틀, 등등) 및 버튼 설정
        txtTitle.setText(PlaySongService.Title);
    }
    public void setbtnText(){
        btnPause.setText("Pause");
    }
}

