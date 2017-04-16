package net.pridi.oliang.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.pridi.oliang.dao.PostItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;

import java.util.ArrayList;

import retrofit2.http.POST;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PostListManager {
    private PostItemCollectionDao dao;


    private Context mContext;

    public PostListManager() {
        mContext = Contextor.getInstance().getContext();
        //loadCache();

    }

    public PostItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PostItemCollectionDao dao) {
        this.dao = dao;
        //saveCache();
    }

    public int getLastId() {
        if (dao == null)
            return 0;
        if (dao.getData() == null)
            return 0;
        if (dao.getData().size() == 0)
            return 0;
        int maxId = dao.getData().get(0).getId();
        for (int i = 1; i < dao.getData().size(); i++)
            maxId = Math.max(maxId, dao.getData().get(i).getId());
        return maxId;
    }

    public int getOldId() {
        if (dao == null)
            return 0;
        if (dao.getData() == null)
            return 0;
        if (dao.getData().size() == 0)
            return 0;
        int minId = dao.getData().get(0).getId();
        for (int i = 1; i < dao.getData().size(); i++)
            minId = Math.min(minId, dao.getData().get(i).getId());
        return minId;
    }

    public int getCount() {
        if (dao == null)
            return 0;
        if (dao.getData() == null)
            return 0;
        return dao.getData().size();
    }

    public void insertDaoToTop(PostItemCollectionDao newDao) {
        if (dao == null)
            dao = new PostItemCollectionDao();
        if (dao.getData() == null)
            dao.setData(new ArrayList<PostItemDao>());

        dao.getData().addAll(0, newDao.getData());
        //saveCache();
    }

    public void appendDaoToBottom(PostItemCollectionDao newDao) {
        if (dao == null)
            dao = new PostItemCollectionDao();
        if (dao.getData() == null)
            dao.setData(new ArrayList<PostItemDao>());

        dao.getData().addAll(dao.getData().size(), newDao.getData());
        //saveCache();
    }

    private void saveCache() {
        PostItemCollectionDao cacheDao = new PostItemCollectionDao();
        if (dao != null && dao.getData() != null)
            cacheDao.setData(dao.getData().subList(0, Math.min(10, dao.getData().size())));
        String json = new Gson().toJson(cacheDao);

        SharedPreferences prefs = mContext.getSharedPreferences("posts",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // add /edit /delete
        editor.putString("json", json);
        editor.apply();

    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("posts",
                Context.MODE_PRIVATE);
        String json = prefs.getString("json", null);
        if (json == null)
            return;
        dao = new Gson().fromJson(json, PostItemCollectionDao.class);
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("dao", dao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        dao = savedInstanceState.getParcelable("dao");

    }
}
