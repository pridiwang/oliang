package net.pridi.oliang.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import net.pridi.oliang.R;
import net.pridi.oliang.dao.PostItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.datatype.MutableInteger;
import net.pridi.oliang.manager.PostListManager;
import net.pridi.oliang.view.PostListItem;

/**
 * Created by pridi on 29-Mar-17.
 */

public class PostListAdapter extends BaseAdapter {
    PostItemCollectionDao dao;
    MutableInteger lastPositionInteger;

    public PostListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public void setDao(PostItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if( dao==null)
            return 1;
        if( dao.getData()==null)
            return 1;

        return dao.getData().size()+1;
    }

    @Override
    public Object getItem(int position) {

        return dao.getData().get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position==getCount()- 1 ? 1: 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position==getCount()-1){
            ProgressBar item;
            if(convertView!=null)
                item = (ProgressBar) convertView;
            else
                item= new ProgressBar(parent.getContext());
            return item;
        }
        PostListItem item;
        if(convertView!=null)
            item= (PostListItem) convertView;
        else
            item= new PostListItem(parent.getContext());

        PostItemDao dao=  (PostItemDao) getItem(position);
        item.setTitleText(dao.getTitle());
        //item.setDetailText(dao.getContent());
        item.setImgUrl(dao.getImg());
        if(position>lastPositionInteger.getValue()) {
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(),
                    R.anim.up_from_bottom);

            item.startAnimation(anim);
            lastPositionInteger.setValue(position);

        }
        return item;
    }
    public void increaseLastPosition(int amount){
        lastPositionInteger.setValue(lastPositionInteger.getValue()+amount);
    }
}
