package com.cmcm.greendamexplorer.adapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.adapter.AudioListViewAdapter.LoadImageRunnable;
import com.cmcm.greendamexplorer.core.common.CMImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageWallGridAdapter extends BaseAdapter implements OnScrollListener {

    protected static final int MSG_UPDATE = 0x1041;
    private Context mContext = null;
    private List<String> mImages = null;
    private GridView mGridView = null;
    private CMImageLoader mImageLoader = CMImageLoader.getInstance();
    private int mLoadCount = 0;
    private int mStart = 0;
    private int mCount = 0;
    private boolean mIsRunning = true;
    private LoadImageThread mThread = null;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (msg.what == MSG_UPDATE) {
                Holder holder = (Holder) msg.obj;
                int index = holder.index;
                String path = mImages.get(index);
                ImageView imageView = (ImageView) mGridView.findViewWithTag(path);
                if (imageView != null) {
                    Bitmap bitmap = holder.bitmap;
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(R.drawable.empty_photo);
                    }
                }
            }

        };
    };

    public ImageWallGridAdapter(Context mContext, List<String> mImages, GridView mGridView) {
        this.mContext = mContext;
        this.mImages = mImages;
        this.mGridView = mGridView;
        mGridView.setOnScrollListener(this);
    }

    public void resetLoadCount() {
        mLoadCount = 0;
    }

    @Override
    public int getCount() {
        return mImages.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ImageView imageView = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.picture_wall_grid_iten, null);
            imageView = (ImageView) view.findViewById(R.id.mImageWallPicture);
            view.setTag(imageView);
        } else {
            imageView = (ImageView) view.getTag();
        }
        String path = mImages.get(position);
        imageView.setTag(path);
        Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(path);
        if(bitmap == null) {
            imageView.setImageResource(R.drawable.empty_photo);
        } else {
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            mThread = new LoadImageThread();
            mThread.start();
        }
    }
    

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mLoadCount < 3) {
            mThread = new LoadImageThread();
            mThread.start();
            mLoadCount++;
        }
        mStart = firstVisibleItem;
        mCount = visibleItemCount;
    }

    class LoadImageThread extends Thread {

        @Override
        public void run() {
            int end = mStart + mCount;
            for (int i = mStart; i < end; i++) {
                if (!mIsRunning) {
                    break;
                }

                String path = mImages.get(i);
                Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(path);
                if (bitmap == null) {
                    bitmap = CMImageLoader.decodeSampledBitmapFromResource(path, 120);
                    mImageLoader.addBitmapToMemoryCache(path, bitmap);
                }
                
                Message msg = new Message();
                msg.what = MSG_UPDATE;
                msg.obj = new Holder(bitmap, i);
                mHandler.sendMessage(msg);
            }
        }
    }

    class Holder {
        private Bitmap bitmap;
        private int index;

        public Holder(Bitmap bitmap, int inderx) {
            this.bitmap = bitmap;
            this.index = inderx;
        }

    }

}
