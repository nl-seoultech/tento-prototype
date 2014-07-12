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
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.txtTitle)).setText("NowPlaying");

        btnPause = (Button) rootView.findViewById(R.id.btnPlayPause); //정지 버튼
        btnPause.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) { //previous, stop, next 버튼도 구현 예정
        switch (view.getId()){
            case R.id.btnPlayPause:
                setBtnText();
                npi.playpauseSong();
                break;
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

    private void setBtnText(){
            if(PlaySongService.mp.isPlaying()){
                btnPause.setText("Play");
            }else {
                btnPause.setText("Pause");
            }
    }
}
