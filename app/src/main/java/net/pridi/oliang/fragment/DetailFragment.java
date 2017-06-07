package net.pridi.oliang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.manager.HttpManager;


import org.jsoup.Jsoup;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class DetailFragment extends Fragment {
    PostItemDao dao;
    TextView tvTitle;
    TextView tvDetail;
    ImageView ivImg;
    WebView wvDetail;
    ScrollView svMain;
    TextView tvViewed;

    public DetailFragment() {
        super();
    }

    public static DetailFragment newInstance(PostItemDao dao) {
        DetailFragment fragment = new DetailFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        setHasOptionsMenu(true);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        svMain=(ScrollView) rootView.findViewById(R.id.svMain);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvDetail = (TextView) rootView.findViewById(R.id.tvDetail);
        ivImg = (ImageView) rootView.findViewById(R.id.ivImg);
        wvDetail = (WebView) rootView.findViewById(R.id.wvDetail);
        tvViewed=(TextView) rootView.findViewById(R.id.tvViewed);

        tvViewed.setText(""+dao.getViewed());
        tvTitle.setText(dao.getTitle());
        tvDetail.setText(html2text(dao.getContent()));
        wvDetail.getSettings().setJavaScriptEnabled(true);
        wvDetail.loadDataWithBaseURL("",dao.getContent(),"text/html","UTF-8","");
        Glide.with(DetailFragment.this)
                .load(dao.getImg())
                .placeholder(R.drawable.dummy)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImg);
        dao.setUnread(0);
        Call<Object> call= HttpManager.getInstance().getService().readPost(dao.getId());
        call.enqueue(new EmptyCallback());
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
    public static String html2text(String html) {
        String temp = Jsoup.parse(html.replaceAll("(?i)<br[^>]*>", "br2n")).text();
        temp = Jsoup.parse(temp.replaceAll("\\\n", "br2n")).text();
        String text = temp.replaceAll("br2n", System.getProperty("line.separator"));
        return text;
    }
    public static String text2html(String text) {

        String html = text.replaceAll(System.getProperty("line.separator"),"<br>");
        return html;
    }

    public class EmptyCallback<T> implements Callback<T> {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {

        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail,menu);
        MenuItem menuItem=(MenuItem) menu.findItem(R.id.action_share);
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        shareActionProvider.setShareIntent(getShareIntent());
    }

    private Intent getShareIntent(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,dao.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT,html2text(dao.getContent()));
        return intent;
    }
}
