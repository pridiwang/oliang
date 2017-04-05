package net.pridi.oliang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemDao;


import org.jsoup.Jsoup;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class DetailFragment extends Fragment {
    PostItemDao dao;
    TextView tvTitle;
    TextView tvDetail;
    ImageView ivImg;

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
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        // Note: State of variable initialized here could not be saved
        //       in onSavedInstanceState
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvDetail = (TextView) rootView.findViewById(R.id.tvDetail);
        ivImg = (ImageView) rootView.findViewById(R.id.ivImg);



        tvTitle.setText(dao.getTitle());
        tvDetail.setText(html2text(dao.getContent()));
        Glide.with(DetailFragment.this)
                .load(dao.getImg())
                .placeholder(R.drawable.dummy)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImg);


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
}
