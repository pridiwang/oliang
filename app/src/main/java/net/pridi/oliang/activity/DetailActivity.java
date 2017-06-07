package net.pridi.oliang.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.fragment.DetailFragment;

/**
 * Created by pridi on 31-Mar-17.
 */

public class DetailActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initInstance();
        PostItemDao dao  =getIntent().getParcelableExtra("dao");
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, DetailFragment.newInstance(dao))
                    .commit();
        }
    }

    private void initInstance() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
