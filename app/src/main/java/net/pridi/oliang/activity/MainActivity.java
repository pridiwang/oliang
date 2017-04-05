package net.pridi.oliang.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.FragmentListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstance();
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MainFragment.newInstance())
                    .commit();
        }


    }

    private void initInstance() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onPostItemClicked(PostItemDao dao) {
        // check for Detail ofr Clip
        if(dao.getYt().length()>1){
            Intent intent = new Intent(MainActivity.this, ClipActivity.class);
            intent.putExtra("dao",dao);
            startActivity(intent);
        }else if(dao.getMp4().length() >1) {
            Toast.makeText(this, "MP4 "+dao.getMp4(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Mp4Activity.class);
            intent.putExtra("dao",dao);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("dao",dao);
            startActivity(intent);
        }




    }

}
