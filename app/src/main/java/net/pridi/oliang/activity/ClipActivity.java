package net.pridi.oliang.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.pierfrancescosoffritti.youtubeplayer.AbstractYouTubeListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.YouTubePlayerView;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.fragment.ClipFragment;
import net.pridi.oliang.fragment.DetailFragment;
import net.pridi.oliang.fragment.MainFragment;
import net.pridi.oliang.manager.FullScreenManager;

/**
 * Created by pridi on 31-Mar-17.
 */

public class ClipActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    Toolbar toolbar;
    PostItemDao dao;
    private YouTubePlayerView youTubePlayerView;
    private FullScreenManager fullScreenManager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        Bundle args = new Bundle();
        args.putParcelable("dao",dao);
        dao =getIntent().getParcelableExtra("dao");
        initInstance(dao);
    }

    private void initInstance(PostItemDao dao) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //showToast(" initialized instance YouTube ID : " +dao.getYt());

        showYoutube(dao.getYt());
    }
    private void showYoutube(final String youTubeId){
        //showToast(" loading youtube id " +youTubeId);

        fullScreenManager = new FullScreenManager(this);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
        youTubePlayerView.initialize(new AbstractYouTubeListener() {

            @Override
            public void onReady() {
                youTubePlayerView.loadVideo(youTubeId, 0);
            }

        }, true);

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenManager.enterFullScreen();

                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(ClipActivity.this, R.drawable.ic_pause_36dp), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youTubePlayerView.pauseVideo();
                    }
                });
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenManager.exitFullScreen();

                youTubePlayerView.setCustomActionRight(ContextCompat.getDrawable(ClipActivity.this, R.drawable.ic_pause_36dp), null);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, youTubeId);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        youTubePlayerView.release();
    }
    private void showToast(String text){
        Toast.makeText(getBaseContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }
}
