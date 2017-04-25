package net.pridi.oliang.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;


public class Mp4Activity extends AppCompatActivity implements EasyVideoCallback {
    private EasyVideoPlayer player;
    PostItemDao dao;
    ProgressBar progressBar;
    final static String TAG ="Mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG," on created ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4);
        Bundle args = new Bundle();
        Log.d(TAG," new bundel4  ");
        args.putParcelable("dao",dao);
        Log.d(TAG," put parcelable  ");
        dao =getIntent().getParcelableExtra("dao");
        Log.d(TAG," get Intent  ");
        Log.d(TAG," dao mp4 t  "+dao.getMp4());
        if(dao.getMp4() == null) return;
        initInstance(dao);

    }

    private void initInstance(PostItemDao dao) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        player = (EasyVideoPlayer) findViewById(R.id.player);
        assert player != null;
        player.setCallback(this);

        // All further configuration is done from the XML layout.
        Uri uri= Uri.parse(dao.getMp4());
        player.setSource(uri);
        player.setAutoPlay(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        Log.d(TAG, "onPreparing()");
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        Log.d(TAG, "onPrepared()");
    }

    @Override
    public void onBuffering(int percent) {
        Log.d(TAG, "onBuffering(): " + percent + "%");
        progressBar.setProgress(percent);
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        Log.d(TAG, "onError(): " + e.getMessage());
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        Log.d(TAG, "onCompletion()");
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        Toast.makeText(this, "Retry", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        //Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {
        //Toast.makeText(this, "Click video frame.", Toast.LENGTH_SHORT).show();
        if (player.isPlaying()){
            player.pause();

        }else{
            player.start();
        }

    }

}
