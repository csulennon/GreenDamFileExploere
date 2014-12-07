package com.cmcm.greendamexplorer.adapter;

import java.io.File;
import java.util.AbstractList;
import java.util.List;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.CMImageLoader;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.entity.Audio;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.TextUtil;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class AudioListViewAdapter extends BaseAdapter implements OnScrollListener {

    protected static final int MSG_REFRESH_AUDIO_IMAGE = 0x7001;
    private List<Audio> mAudios = null;
    private Context mContext = null;
    private CMImageLoader mImageLoader = CMImageLoader.getInstance();
    private AbsListView mListView = null;
    private boolean mIsRunning = true;
    private int start = 0;
    private int count = 0;
    private int mLoadCount = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REFRESH_AUDIO_IMAGE) {
                Holder h = (Holder) msg.obj;
                String path = mAudios.get(h.index).getPath();
                ImageView view = (ImageView) mListView.findViewWithTag(path);
                if (view != null) {
                    if (h.bitmap == null) {
                        view.setBackgroundResource(R.drawable.type_mp3);
                    } else {
                        view.setImageBitmap(h.bitmap);
                    }
                }
            }
        };
    };

    public AudioListViewAdapter(Context context, ListView listView, List<Audio> mAudios) {
        this.mAudios = mAudios;
        this.mContext = context;
        this.mListView = listView;
        mListView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mAudios.size();
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
        ViewHolder holder = null;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.audio_list_item, null);
            holder = new ViewHolder();
            findViews(view, holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        setHolder(holder, position);
        return view;
    }

    private void setHolder(ViewHolder holder, int position) {
        final Audio audio = mAudios.get(position);
        Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(audio.getPath());
        if (bitmap != null) {
            holder.mImageAudio.setImageBitmap(bitmap);
        } else {
            holder.mImageAudio.setImageResource(R.drawable.type_mp3);
        }
        holder.mImageAudio.setTag(audio.getPath());
        holder.mTvAudioName.setText(FileUtils.getFileName(audio.getPath()));
        holder.mTvAudioPath.setText(audio.getPath());
        File file = new File(audio.getPath());
        holder.mTvAudioModifyTime.setText(TextUtil.getDateStringString(file.lastModified()));
        holder.mTvAudioDuration.setText(TextUtil.getDurationToString(audio.getDuration()));

        holder.mImgBtnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = OpenFileUtil.openFile(audio.getPath());
                mContext.startActivity(intent);
            }
        });
    }

    public void resetLoadCount() {
        mLoadCount = 0;
    }

    private void findViews(View view, ViewHolder holder) {
        holder.mImageAudio = (ImageView) view.findViewById(R.id.mImageAudio);
        holder.mTvAudioName = (TextView) view.findViewById(R.id.mTvAudioName);
        holder.mTvAudioPath = (TextView) view.findViewById(R.id.mTvAudioPath);
        holder.mTvAudioModifyTime = (TextView) view.findViewById(R.id.mTvAudioModifyTime);
        holder.mTvAudioDuration = (TextView) view.findViewById(R.id.mTvAudioDuration);
        holder.mImgBtnPlay = (ImageButton) view.findViewById(R.id.mImgBtnPlay);
    }

    static class ViewHolder {

        private ImageView mImageAudio = null;
        private TextView mTvAudioName = null;
        private TextView mTvAudioPath = null;
        private TextView mTvAudioModifyTime = null;
        private TextView mTvAudioDuration = null;
        private ImageButton mImgBtnPlay = null;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            mIsRunning = true;
            loadImage(start, count);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start = firstVisibleItem;
        count = visibleItemCount;
        if (mLoadCount < 2) {
            loadImage(firstVisibleItem, visibleItemCount);
            mLoadCount++;
        }

    }

    private void loadImage(int firstVisibleItem, int visibleItemCount) {
        new Thread(new LoadImageRunnable(start, count)).start();
    }

    class LoadImageRunnable implements Runnable {

        private int start = 0;
        private int count = 0;

        public LoadImageRunnable(int start, int count) {
            this.start = start;
            this.count = count;
        }

        @Override
        public void run() {
            int end = start + count;
            for (int i = start; i < end; i++) {
                if (!mIsRunning) {
                    return;
                }
                Audio audio = mAudios.get(i);
                String path = audio.getPath();

                Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(path);
                if (bitmap == null) {
                    bitmap = MediaResourceManager.getArtworkFromFile(audio.getId(), audio.getAlbumId());
                    if (bitmap != null) {
                        mImageLoader.addBitmapToMemoryCache(path, bitmap);
                    }
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_REFRESH_AUDIO_IMAGE;
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

}
