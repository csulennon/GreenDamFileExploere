package com.cmcm.greendamexplorer.dao.test;

import android.test.AndroidTestCase;

import com.cmcm.greendamexplorer.core.engine.DeploymentOperation;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;

public class TestFavorite extends AndroidTestCase {

    DeploymentOperation app = new DeploymentOperation();

    public void testFindByPath() {

        FavoriteDao dao = DaoFactory.getFavoriteDao(app.getApplicationContext());

        Favorite favorite = dao.findFavoriteByFullPath("/sdcard/360/a.txt");
        System.out.println(favorite);

    }

}
