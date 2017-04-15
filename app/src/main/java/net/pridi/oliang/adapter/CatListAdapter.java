package net.pridi.oliang.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.view.PostListItem;

/**
 * Created by pridi on 15-Apr-17.
 */

public class CatListAdapter extends BaseAdapter {
    CatItemCollectionDao dao;

    public CatItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(CatItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if( dao==null)
            return 0;
        if( dao.getData()==null)
            return 0;

        return dao.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView item ;
        if(convertView!=null)
            item= (TextView) convertView;
        else
            item= new TextView(parent.getContext());
        item.setTextAppearance(android.R.style.TextAppearance_Large);
        item.setText(dao.getData().get(position).getName());

        return item;
    }
}
