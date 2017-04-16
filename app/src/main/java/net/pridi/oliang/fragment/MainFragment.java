package net.pridi.oliang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import net.pridi.oliang.R;
import net.pridi.oliang.activity.MainActivity;
import net.pridi.oliang.adapter.PostListAdapter;
import net.pridi.oliang.dao.PostItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.datatype.MutableInteger;
import net.pridi.oliang.manager.HttpManager;
import net.pridi.oliang.manager.PostListManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nuuneoi on 11/16/2014.
 */

public class MainFragment extends Fragment {
    //variables
    public interface FragmentListener{
        void onPostItemClicked(PostItemDao dao);

    }

    ListView listView;

    PostListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PostListManager postListManager;
    Button btnNewPosts;
    MutableInteger lastPositionInteger;
    public MutableInteger categoryId;
    //function
    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //init fragment
        init(savedInstanceState);
        if( savedInstanceState!=null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView,savedInstanceState);
        return rootView;
    }
    private void init(Bundle savedInstanceState){
        // Init fragment level variable
        postListManager= new PostListManager();
        lastPositionInteger=new MutableInteger(-1);
        Integer catid = this.getArguments().getInt("catid");
        showToast("catid "+catid);
        categoryId=new MutableInteger(catid);
    }

    private void initInstances(View rootView,Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        showToast("initInstance catid "+ categoryId.getValue());
        btnNewPosts = (Button) rootView.findViewById(R.id.btnNewPosts);
        btnNewPosts.setOnClickListener(buttonClickListener);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PostListAdapter(lastPositionInteger);
        listAdapter.setDao(postListManager.getDao());
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(listViewItemClickListener);

        swipeRefreshLayout=(SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);

        listView.setOnScrollListener(listViewScrollListener);
        if(savedInstanceState==null )
            refreshData();
    }
    private void refreshData() {
        if(postListManager.getCount()==0)
            reloadData();
        else 
            reloadNewData();
    }
    private void reloadNewData() {
        int maxId=postListManager.getLastId();
        Call<PostItemCollectionDao> call = HttpManager.getInstance().getService().loadNewPostList(categoryId.getValue(),maxId);
        call.enqueue(new PostListLoadCallback(PostListLoadCallback.MODE_RELOAD_NEWER));

    }
    boolean isLoadingMore = false;
    private void reloadOldData() {
        int minId=postListManager.getOldId();
        showToast("loading old for cat "+categoryId.getValue()+ "mind id "+minId);
        Call<PostItemCollectionDao> call = HttpManager.getInstance().getService().loadOldPostList(categoryId.getValue(),minId);
        call.enqueue(new PostListLoadCallback(PostListLoadCallback.MODE_RELOAD_OLDER));

    }
    private void reloadData() {
        if(isLoadingMore)
            return;
        isLoadingMore=true;
        showToast("loading for cat "+categoryId.getValue());
        Call<PostItemCollectionDao> call = HttpManager.getInstance().getService().loadCatPostList(categoryId.getValue());
        call.enqueue(new PostListLoadCallback(PostListLoadCallback.MODE_RELOAD));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here

        outState.putBundle("postListManager",
                postListManager.onSaveInstanceState());
        outState.putBundle("lastPositionInteger",
                lastPositionInteger.onSaveInstanceState());
        outState.putBundle("categoryId",
                categoryId.onSaveInstanceState());
    }


    private void onRestoreInstanceState(Bundle savedIntanceState){
        // restore instance state
        postListManager.onRestoreInstanceState(savedIntanceState.getBundle("postListManager"));
        lastPositionInteger.onRestoreInstanceState(savedIntanceState.getBundle("lastPositionInteger"));
        categoryId.onRestoreInstanceState(savedIntanceState.getBundle("categoryId"));
    }

    /*
  * Restore Instance State Here
  */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore Instance State here
        }
    }
    private void showButtonNewPosts(){
        btnNewPosts.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(),
                R.anim.zoom_fade_in);
        btnNewPosts.setAnimation(anim);

    }
    private void hideButtonNewPosts(){
        btnNewPosts.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(),
                R.anim.zoom_fade_out);
        btnNewPosts.setAnimation(anim);

    }
    private void showToast(String text){
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }
    //listener
    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v==btnNewPosts) {
                listView.smoothScrollToPosition(0);
                hideButtonNewPosts();
            }

        }
    };
    final SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };
    final AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(view==listView) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (postListManager.getCount() > 0) {
                        //Load more
                        reloadOldData();
                    }
                }
            }
        }
    };
    final AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position<postListManager.getCount()) {
                PostItemDao dao = postListManager.getDao().getData().get(position);
                FragmentListener listener = (FragmentListener) getActivity();
                listener.onPostItemClicked(dao);
            }
        }
    };

    // inner class

    class PostListLoadCallback implements Callback<PostItemCollectionDao> {
        public static final int MODE_RELOAD=1;
        public static final int MODE_RELOAD_NEWER=2;
        public static final int MODE_RELOAD_OLDER=3;
        int mode;
        public PostListLoadCallback(int mode){
            this.mode=mode;
        }
        @Override
        public void onResponse(Call<PostItemCollectionDao> call, Response<PostItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccessful()){

                PostItemCollectionDao dao= response.body();
                int firstVisiblePosition=listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if(mode==MODE_RELOAD_NEWER) {
                    postListManager.insertDaoToTop(dao);
                }
                else if(mode==MODE_RELOAD_OLDER) {
                    postListManager.appendDaoToBottom(dao);

                }else {
                    postListManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                listAdapter.setDao(postListManager.getDao());
                listAdapter.notifyDataSetChanged();
                if(mode==MODE_RELOAD_NEWER){
                    int additionalSize=
                            (dao!=null && dao.getData()!=null ) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop( firstVisiblePosition + additionalSize,
                            top );
                    if(additionalSize>0)
                        showButtonNewPosts();
                }else{

                }
                showToast("Load Completed");
            }else{
                clearLoadingMoreFlagIfCapable(mode);
                try {
                    showToast(response.errorBody().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<PostItemCollectionDao> call, Throwable t) {


            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());

        }
        private void clearLoadingMoreFlagIfCapable(int mode){
            if(mode==MODE_RELOAD_OLDER)
                isLoadingMore=false;
        }
    }
    public void setCategoryId(int id){
        categoryId.setValue(id);
        reloadData();

    }

}
