package com.cmcm.greendamexplorer.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.DrawableLoder;
import com.cmcm.greendamexplorer.utils.DensityUtil;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class ApplicationListViewAdapter extends BaseAdapter implements OnScrollListener {

    protected static final int MSG_LOAD_APP_IMG = 0x8001;
    private List<PackageInfo> mPackgeInfos = new ArrayList<PackageInfo>();
    private Context mContext = null;
    private PackageManager mPackageManager = null;
    private int mStart = 0;
    private int mCount = 0;
    public boolean mIsRunning = true;
    private ListView mListView;
    private TextView mTvAppTile = null;
    private DrawableLoder mDrawableLoder = DrawableLoder.getInstance();
    private LoadDrableThread mThread = null;
    private int mLoadCount = 0;

    public static int mUserAppCount = 0;
    public static int mSysAppCount = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (msg.what == MSG_LOAD_APP_IMG) {
                MSGHolder holder = (MSGHolder) msg.obj;

                int index = holder.index;
                Drawable drawable = holder.drawable;
                PackageInfo info = mPackgeInfos.get(index);
                ImageView view = (ImageView) mListView.findViewWithTag(info.packageName);
                if (view != null) {
                    if (drawable != null) {
                        view.setImageDrawable(drawable);
                    }
                }
            }
        };

    };

    public void resetLoadCount() {
        mLoadCount = 0;
    }

    public List<PackageInfo> getPackageInfos() {
        return mPackgeInfos;
    }

    public ApplicationListViewAdapter(Context mContext, List<PackageInfo> mAppinfos, ListView listView, TextView tvAppTile) {
        mTvAppTile = tvAppTile;
        List<PackageInfo> mUserPackgeInfos = new ArrayList<PackageInfo>();
        List<PackageInfo> mSystemPackgeInfos = new ArrayList<PackageInfo>();
        for (PackageInfo packageInfo : mAppinfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                mSystemPackgeInfos.add(packageInfo);
            } else {
                mUserPackgeInfos.add(packageInfo);
            }
        }
        mUserAppCount = mUserPackgeInfos.size();
        mSysAppCount = mSystemPackgeInfos.size();

        mPackgeInfos.addAll(mUserPackgeInfos);
        mPackgeInfos.addAll(mSystemPackgeInfos);

        // this.mPackgeInfos = mAppinfos;
        this.mContext = mContext;
        this.mListView = listView;
        mPackageManager = mContext.getPackageManager();
        mListView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mPackgeInfos.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position == 0) {
            TextView tv = new TextView(mListView.getContext());
            tv.setText("用户应用：" + mUserAppCount);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.GRAY);
            tv.setTextSize(DensityUtil.dip2px(mListView.getContext(), 10));
            return tv;
        } else if (position == mUserAppCount - 1) {
            TextView tv = new TextView(mListView.getContext());
            tv.setText("系统应用：" + mSysAppCount);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.GRAY);
            tv.setTextSize(DensityUtil.dip2px(mListView.getContext(), 10));
            return tv;
        }

        View view = convertView;
        ViewHolder holder = null;
        if (view == null || !(view instanceof RelativeLayout)) {

            view = LayoutInflater.from(mContext).inflate(R.layout.applications_list_item, null);
            holder = new ViewHolder();
            findViews(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mImgBtnAppOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DELETE);
                if (position < mUserAppCount) {
                    intent.setData(Uri.parse("package:" + mPackgeInfos.get(position - 1).packageName));
                } else if (position > mUserAppCount + 1) {
                    intent.setData(Uri.parse("package:" + mPackgeInfos.get(position - 2).packageName));
                }
                mContext.startActivity(intent);
            }
        });
        if (position < mUserAppCount) {
            setViews(holder, position - 1);
            holder.mImageApp.setTag(mPackgeInfos.get(position - 1).packageName);
        } else if (position > mUserAppCount + 1) {
            setViews(holder, position - 2);
            holder.mImageApp.setTag(mPackgeInfos.get(position - 2).packageName);
        }

        return view;
    }

    private void setViews(ViewHolder holder, int position) {
        PackageInfo pkgInfo = mPackgeInfos.get(position);
        String pkgName = mPackgeInfos.get(position).packageName;
        Drawable drawable = mDrawableLoder.getDrawableFromMemoryCache(pkgName);
        if (drawable != null) {
            holder.mImageApp.setImageDrawable(drawable);
        } else {
            holder.mImageApp.setImageResource(R.drawable.type_apk);
        }
        holder.mTvAppName.setText(mPackageManager.getApplicationLabel(pkgInfo.applicationInfo));
        String path = pkgInfo.applicationInfo.sourceDir;
        holder.mTvAppVersion.setText("(" + pkgInfo.versionName + ")");
        File file = new File(path);
        holder.mTvAppInstallTime.setText(TextUtil.getDateStringString(file.lastModified()));
        holder.mTvAppSize.setText(TextUtil.getSizeSting(file.length()));
    }

    private void findViews(ViewHolder holder, View view) {
        holder.mImageApp = (ImageView) view.findViewById(R.id.mImageApp);
        holder.mTvAppName = (TextView) view.findViewById(R.id.mTvAppName);
        holder.mTvAppInstallTime = (TextView) view.findViewById(R.id.mTvAppInstallTime);
        holder.mTvAppVersion = (TextView) view.findViewById(R.id.mTvAppVersion);
        holder.mTvAppSize = (TextView) view.findViewById(R.id.mTvAppSize);
        holder.mImgBtnAppOpen = (ImageButton) view.findViewById(R.id.mImgBtnAppOpen);
    }

    static class ViewHolder {
        private ImageView mImageApp = null;
        private TextView mTvAppName = null;
        private TextView mTvAppInstallTime = null;
        private TextView mTvAppSize = null;
        private TextView mTvAppVersion = null;
        private ImageButton mImgBtnAppOpen = null;

    }

    class LoadDrableThread extends Thread {

        @Override
        public void run() {

            int end = mStart + mCount;
            for (int i = mStart; i < end; i++) {
                if (!mIsRunning) {
                    break;
                }
                if (i == 0 || i == mUserAppCount + 1) {
                    continue;
                }

                String pkgName = null;

                int p = i;
                if (p < mUserAppCount) {
                    p = p - 1;
                } else if (p > mUserAppCount + 1) {
                    p = p - 2;
                }
                pkgName = mPackgeInfos.get(p).packageName;
                Drawable drawable = mDrawableLoder.getDrawableFromMemoryCache(pkgName);
                if (drawable == null) {
                    // drawable =
                    // mPackageManager.getApplicationIcon(mPackgeInfos.get(i).applicationInfo);
                    try {
                        drawable = mPackageManager.getApplicationInfo(mPackgeInfos.get(p).packageName, 0).loadIcon(mPackageManager);
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mDrawableLoder.addDrawableToMemoryCache(pkgName, drawable);
                }

                Message msg = new Message();
                msg.what = MSG_LOAD_APP_IMG;
                msg.obj = new MSGHolder(drawable, p);
                mHandler.sendMessage(msg);

            }

        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            mIsRunning = true;
            mThread = new LoadDrableThread();
            mThread.start();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem < mUserAppCount - 1) {
            mTvAppTile.setText("用户应用：" + mUserAppCount);
            mTvAppTile.setVisibility(View.VISIBLE);
        } else if (firstVisibleItem >= mUserAppCount - 2) {
            mTvAppTile.setText("系统应用：" + mSysAppCount);
            mTvAppTile.setVisibility(View.VISIBLE);
        }

        if (mLoadCount < 2) {
            mThread = new LoadDrableThread();
            mThread.start();
            mLoadCount++;
        } else {
            // mIsRunning = false;
        }
        mStart = firstVisibleItem;
        mCount = visibleItemCount;

    }

    class MSGHolder {
        private Drawable drawable;
        private int index;

        public MSGHolder(Drawable drawable, int index) {
            this.drawable = drawable;
            this.index = index;
        }

    }

}
