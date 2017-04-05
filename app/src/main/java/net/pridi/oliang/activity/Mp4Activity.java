package net.pridi.oliang.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;


public class Mp4Activity extends AppCompatActivity implements EasyVideoCallback {
    private EasyVideoPlayer player;
    PostItemDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MP4"," on created ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4);
        Bundle args = new Bundle();
        Log.d("MP4"," new bundel4  ");
        args.putParcelable("dao",dao);
        Log.d("MP4"," put parcelable  ");
        dao =getIntent().getParcelableExtra("dao");
        Log.d("MP4"," get Intent  ");
        Log.d("MP4"," dao mp4 t  "+dao.getMp4());
        if(dao.getMp4() == null) return;
        initInstance(dao);

    }

    private void initInstance(PostItemDao dao) {

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
        Log.d("EVP-Sample", "onPreparing()");
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onPrepared()");
    }

    @Override
    public void onBuffering(int percent) {
        Log.d("EVP-Sample", "onBuffering(): " + percent + "%");
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        Log.d("EVP-Sample", "onError(): " + e.getMessage());
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        Log.d("EVP-Sample", "onCompletion()");
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        Toast.makeText(this, "Retry", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickVideoFrame(EasyVideoPlayer player) {
        Toast.makeText(this, "Click video frame.", Toast.LENGTH_SHORT).show();

    }

}
