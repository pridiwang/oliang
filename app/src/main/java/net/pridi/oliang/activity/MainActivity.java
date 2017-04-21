package net.pridi.oliang.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.sip.SipAudioCall;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.pridi.oliang.R;
import net.pridi.oliang.adapter.CatListAdapter;
import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.dao.CatItemDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.datatype.MutableInteger;
import net.pridi.oliang.fragment.MainFragment;
import net.pridi.oliang.manager.CatListManager;
import net.pridi.oliang.manager.HttpManager;
import net.pridi.oliang.manager.http.ApiService;
import net.pridi.oliang.utils.FireBaseRegist;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MainFragment.FragmentListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    Button btnPostNew;
    private ListView lvCatList;
    private CatListAdapter catListAdapter;
    CatListManager catListManager;
    MutableInteger categoryId;
    public Integer catid;
    FireBaseRegist fbRegist = new FireBaseRegist();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initInstance();
        if(savedInstanceState==null) {
            //categoryId.setValue(2);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MainFragment.newInstance(),"main_fragment")
                    .commit();

        }else{

        }
        btnPostNew = (Button) findViewById(R.id.btnPostNew);
        btnPostNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PostActivity.class);
                startActivity(intent);
            }
        });


    }

    private void initInstance() {
        FirebaseMessaging.getInstance().subscribeToTopic("newpost");
        categoryId= new MutableInteger(0);
        lvCatList = (ListView) findViewById(R.id.lvCatList);
        catListAdapter = new CatListAdapter();
        lvCatList.setAdapter(catListAdapter);
        catid=0;
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
        Call<CatItemCollectionDao> call = HttpManager.getInstance().getService().loadCatList();
        call.enqueue(new Callback<CatItemCollectionDao>() {
            @Override
            public void onResponse(final Call<CatItemCollectionDao> call, Response<CatItemCollectionDao> response) {
                if(response.isSuccessful()) {
                    catListAdapter.setDao(response.body());
                   //catListManager.setDao(response.body());
                    CatItemDao allCat= new CatItemDao();
                    allCat.setId(0);
                    allCat.setName("--ทั้งหมด--");
                    catListAdapter.getDao().getData().add(allCat);
                    lvCatList.setAdapter(catListAdapter);
                    lvCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Integer catid=catListAdapter.getDao().getData().get(position).getId();
                            Log.d("CLK","id:"+catid);

                            //CatItemDao dao= catListAdapter.getInstance().getDao().getData().get(position);
                            Bundle bundle=new Bundle();
                            categoryId.setValue(catid);
                            bundle.putInt("catid",catid);
                            MainFragment fraginfo= new MainFragment();
                            fraginfo.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.contentContainer,fraginfo,"main_fragment")
                                    .commit();
                            ;
                            drawerLayout.closeDrawers();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CatItemCollectionDao> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public Integer getCatid() {
        return catid;
    }

    public void setCatid(Integer catid) {
        this.catid = catid;
    }
}
