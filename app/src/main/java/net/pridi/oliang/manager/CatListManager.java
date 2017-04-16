package net.pridi.oliang.manager;

import android.content.Context;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.dao.CatItemDao;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class CatListManager {
    private CatItemCollectionDao dao;
    private static CatListManager instance;

    public static CatListManager getInstance() {
        if (instance == null)
            instance = new CatListManager();
        return instance;
    }

    private Context mContext;

    private CatListManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public CatItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(CatItemCollectionDao dao) {
        if(dao==null){
            return;
        }
        this.dao = dao;
    }

}
