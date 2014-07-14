package me.nworks.nl.tento.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import me.nworks.nl.tento.PlaySongService;
import me.nworks.nl.tento.R;

public class NowPlayingFragment extends Fragment implements View.OnClickListener {


    /**
     * MainFragmentActivity와 연결할 인터페이스
     */
    public interface NowPlayingInterface {

        /**
         * PlaySongService 에서 playpauseSong 을 연결하기위해서 사용
         */
        public void playpauseSong();

        /**
         * PlaySongService 에서 loopControl 을 연결하기위해서 사용
         */
        public void loopControl(boolean state);

    }

    private NowPlayingInterface npi;

    private Button btnPause;

    private TextView txtTitle;

    private CheckBox checkboxRepeat;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
        txtTitle =  (TextView) rootView.findViewById(R.id.txtTitle);
        btnPause = (Button) rootView.findViewById(R.id.btnPlayPause); //정지 버튼
        checkboxRepeat = (CheckBox) rootView.findViewById(R.id.checkboxRepeat);

        btnPause.setOnClickListener(this);
        checkboxRepeat.setOnClickListener(this);

        setSongInfo();

        if (PlaySongService.mp.isPlaying()) {
            btnPause.setText("Pause");
        } else {
            btnPause.setText("Play");
        }

        return rootView;
    }

    /**
     * TODO: previous, stop, next 버튼도 구현 예정
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlayPause:
                if(!PlaySongService.Title.isEmpty()){
                    npi.playpauseSong();
                }
                break;
            case R.id.checkboxRepeat:
                npi.loopControl(checkboxRepeat.isChecked());
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        npi = (NowPlayingInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }

    /**
     * 음악 정보(앨범사진, 타이틀, 등등) 및 버튼 설정
     */
    public void setSongInfo() {
        txtTitle.setText(PlaySongService.Title);
    }

    public void setbtnText() {
        btnPause.setText("Pause");
    }
}

