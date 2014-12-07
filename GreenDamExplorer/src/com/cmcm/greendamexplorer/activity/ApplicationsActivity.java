package com.cmcm.greendamexplorer.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.adapter.ApplicationListViewAdapter;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;

public class ApplicationsActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {

    private List<PackageInfo> mPkgInfos = null;

    private ListView mListView = null;
    private ApplicationListViewAdapter mAdapter = null;
    private TextView mTvApppsTitle = null;
    private TextView mTvAppTile = null;
    private PackageManager mPackageManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        mTvApppsTitle = (TextView) findViewById(R.id.mTvTopTitle);
        mTvAppTile = (TextView) findViewById(R.id.tv_apptitle);

        mTvApppsTitle.setText("所有应用");

        mPackageManager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // mPkgInfos = ResourceManager.getInstance().getCustomApps();
        mPkgInfos = ResourceManager.getInstance().getAllApps();
        Collections.sort(mPkgInfos, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs) {
                String nameL = mPackageManager.getApplicationLabel(lhs.applicationInfo).toString();
                String nameR = mPackageManager.getApplicationLabel(rhs.applicationInfo).toString();
                return nameL.compareToIgnoreCase(nameR);
            }
        });

        mListView = (ListView) findViewById(R.id.mApplicationListView);
        mAdapter = new ApplicationListViewAdapter(this, mPkgInfos, mListView, mTvAppTile);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    public void back(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < ApplicationListViewAdapter.mUserAppCount && position != 0) {
            position--;
        } else {
            return;
        }
        PackageInfo pkgInfo = mAdapter.getPackageInfos().get(position);

        Intent intent = mPackageManager.getLaunchIntentForPackage(pkgInfo.packageName);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}
