package com.cmcm.greendamexplorer.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.CMImageLoader;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.entity.Video;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;

public class VideoGridViewAdapter extends BaseAdapter implements OnScrollListener {

    protected static final int MSG_REFRESH_VIEW = 0x1030;
    private List<Video> mVideos = null;
    private Context mContext = null;
    private CMImageLoader mImageLoader = CMImageLoader.getInstance();
    private GridView mGridView = null;
    private int mLoadCount = 0;
    private VideoLoadThread mThread = null;
    private int mStart = 0;
    private int mCount = 0;
    public boolean mIsRunning = true;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (msg.what == MSG_REFRESH_VIEW) {

                Holder holder = (Holder) msg.obj;
                int index = holder.index;
                Video video = mVideos.get(index);
                String tag = video.getPath();
                ImageView imageView = (ImageView) mGridView.findViewWithTag(tag);

                if (imageView != null) {

                    Bitmap bm = holder.bitmap;
                    if (bm != null) {
                        imageView.setImageBitmap(bm);
                    }
                }

            }

        };
    };

    public void resetLoadCount() {
        mLoadCount = 0;
    }

    public VideoGridViewAdapter(Context context, List<Video> videos, GridView gridView) {
        mContext = context;
        mVideos = videos;
        mGridView = gridView;
        mGridView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mVideos.size();
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

        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.video_grid_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.mImageVideoBitmap);
            viewHolder.imageVideoOpen = (ImageView) view.findViewById(R.id.mImageVideoOpen);
            viewHolder.videoName = (TextView) view.findViewById(R.id.mTvVideoName);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final int p = position;

        viewHolder.videoName.setText(mVideos.get(p).getName());
        viewHolder.imageVideoOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mVideos.get(p).getPath();
                File file = new File(path);
                if (file.exists()) {
                    Intent intent = OpenFileUtil.openFile(path);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "文件已经不存在了...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        int id = mVideos.get(position).getId();
        // Bitmap bitmap = MediaResourceManager.getVideoThumbnail(id);
        Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(mVideos.get(position).getPath());
        if (bitmap == null) {
            viewHolder.imageView.setImageResource(R.drawable.video_border);
        } else {
            viewHolder.imageView.setImageBitmap(bitmap);
        }
        viewHolder.imageView.setTag(mVideos.get(position).getPath());

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        mIsRunning = true;
        if (scrollState == SCROLL_STATE_IDLE) {
            mThread = new VideoLoadThread();
            mThread.start();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mLoadCount < 3) {
            mThread = new VideoLoadThread();
            mThread.start();
        } else {
            mIsRunning = false;
        }

        mStart = firstVisibleItem;
        mCount = visibleItemCount;
    }

    class VideoLoadThread extends Thread {

        @Override
        public void run() {
            int end = mStart + mCount;
            for (int i = mStart; i < end; i++) {
                if (!mIsRunning) {
                    break;
                }
                Video video = mVideos.get(i);
                String path = video.getPath();
                Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(path);
                if (bitmap == null) {
                    bitmap = MediaResourceManager.getVideoThumbnail(video.getId());
                    mImageLoader.addBitmapToMemoryCache(path, bitmap);
                }

                Message msg = new Message();
                msg.what = MSG_REFRESH_VIEW;
                msg.obj = new Holder(bitmap, i);
                mHandler.sendMessage(msg);
            }
        }

    }

    class Holder {
        Bitmap bitmap;
        int index;

        public Holder(Bitmap bitmap, int index) {
            this.bitmap = bitmap;
            this.index = index;
        }
    }

    class ViewHolder {
        private ImageView imageView = null;
        private ImageView imageVideoOpen = null;
        private TextView videoName = null;
    }

}
