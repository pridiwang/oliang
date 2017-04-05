package net.pridi.oliang.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pierfrancescosoffritti.youtubeplayer.AbstractYouTubeListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;

import net.pridi.oliang.R;
import net.pridi.oliang.activity.MainActivity;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.manager.FullScreenManager;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class ClipFragment extends Fragment {
    PostItemDao dao;
    private YouTubePlayerView youTubePlayerView;
    private FullScreenManager fullScreenManager;
    public ClipFragment() {
        super();
    }

    public static ClipFragment newInstance(PostItemDao dao) {
        ClipFragment fragment = new ClipFragment();
        Bundle args = new Bundle();
        args.putParcelable("dao",dao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        dao=getArguments().getParcelable("dao");
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clip, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        //youTubePlayerView = (YouTubePlayerView) rootView.findViewById(R.id.youTubePlayerView);
        fullScreenManager = new FullScreenManager(getActivity());

        youTubePlayerView = (YouTubePlayerView) rootView.findViewById(R.id.youTubePlayerView);
        youTubePlayerView.initialize(new AbstractYouTubeListener() {

            @Override
            public void onReady() {
                youTubePlayerView.loadVideo(dao.getYt(), 0);
            }

        }, true);
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenManager.enterFullScreen();

                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_36dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.pauseVideo();
                    }
                });
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenManager.exitFullScreen();

                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause_36dp), null);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}
