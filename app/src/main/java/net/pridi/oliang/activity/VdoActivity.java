package net.pridi.oliang.activity;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;

import java.io.IOException;

public class VdoActivity extends AppCompatActivity {
    SimpleExoPlayerView vdoPlayer;
    PostItemDao dao;
    String mp4;
    final static String TAG ="Vdo";
    Button btnBack;
    Toolbar toolbar;
    SimpleExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vdo);
        Bundle args = new Bundle();
        Log.d(TAG," new bundel4  ");
        args.putParcelable("dao",dao);
        Log.d(TAG," put parcelable  ");
        dao =getIntent().getParcelableExtra("dao");
        Log.d(TAG," get Intent  ");
        Log.d(TAG," dao mp4  "+dao.getMp4());
        if(dao.getMp4() == null) return;
        mp4=dao.getMp4();
        mp4=mp4.replace("/media/",":8081/vod/oliang/")+"/playlist.m3u8";
        Log.d(TAG," hls "+mp4);
        initInstance();
    }

    private void initInstance() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vdoPlayer = (SimpleExoPlayerView) findViewById(R.id.vdoPlayer);
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        vdoPlayer.setPlayer(player);


// Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

// Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, ""), defaultBandwidthMeter);

// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        ExtractorsFactory extractorsFactoryHLS =new ExtractorsFactory() {
            @Override
            public Extractor[] createExtractors() {
                return new Extractor[0];
            }
        };
        HlsDataSourceFactory hlsDataSourceFactory = new DefaultHlsDataSourceFactory(dataSourceFactory);
        Uri hlsVdoUri= Uri.parse(mp4);
        Handler hlsHandler= new Handler();
        AdaptiveMediaSourceEventListener hlsEvenListener = new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
                showToast("started");
            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
                showToast("completed");
            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
                showToast("canceled");
            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
                showToast("error "+error.getMessage()+ " "+error.getCause().getMessage());
            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
                showToast("upstream discarded");
            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
                showToast("downstream format chagned");
            }
        };
        HlsMediaSource hlsMediaSource = new HlsMediaSource(hlsVdoUri,dataSourceFactory,hlsHandler,hlsEvenListener);
        player.prepare(hlsMediaSource);
        player.setPlayWhenReady(true);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar

        btnBack=(Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                player.stop();
                finish();
            }
        });
    }
    private void showToast(String text){
        return;
        //Toast.makeText(Contextor.getInstance().getContext(),
        //               text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        player.stop();
        finish();
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //do your stuff
            player.stop();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
