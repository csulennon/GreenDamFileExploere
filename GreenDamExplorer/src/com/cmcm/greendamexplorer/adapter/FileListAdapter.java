package com.cmcm.greendamexplorer.adapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.CMImageLoader;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.entity.SimplePackgeInfo;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class FileListAdapter extends BaseAdapter {

    private static final int MSG_TYPE_DISPLAY_IMAGE = 0x2001;

    private List<SimpleFileInfo> mFileItems = null;
    private Context mContext = null;
    private ExecutorService mPool = null;
    private CMImageLoader mImageLoader = null;
    private boolean mIsFirstLoad = true;
    private AbsListView mListView = null;
    private CMOnScrollListener mOnScrollListener = new CMOnScrollListener();
    private OnCheckBoxChangedListener mOnCheckBoxChangedListener = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TYPE_DISPLAY_IMAGE) {
                Holder holder = (Holder) msg.obj;
                ImageView imageView = (ImageView) mListView.findViewWithTag(holder.url);
                if (imageView != null && holder.bitmap != null) {
                    imageView.setImageBitmap(holder.bitmap);

                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.alpha_action_long);
                    imageView.startAnimation(anim);
                }
            }
        };
    };

    public void OnCheckBoxChangedListener(OnCheckBoxChangedListener listener) {
        mOnCheckBoxChangedListener = listener;
    }

    public void setOnScrollListenerToListView(AbsListView view) {
        if (view != null) {
            view.setOnScrollListener(mOnScrollListener);
        }
    }

    public void setFirstTimeLoad(boolean first) {
        mIsFirstLoad = first;
    }

    public FileListAdapter(Context context, List<SimpleFileInfo> fileItems) {
        this.mFileItems = fileItems;
        this.mContext = context;
        mImageLoader = CMImageLoader.getInstance();
        mPool = Executors.newFixedThreadPool(3);
    }

    @Override
    public int getCount() {
        return mFileItems.size();
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
        ViewHolder holder = null;

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false);
            holder = new ViewHolder();
            // 初始化holder变量
            findHolder(holder, view);
            view.setTag(holder);
            setCheckBoxListener(holder.mCbChecke, position);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        setHolder(holder, position);
        return view;
    }

    private void setCheckBoxListener(CheckBox cbChecke, final int position) {
        final SimpleFileInfo info = mFileItems.get(position);
        cbChecke.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = false;
                if (info.isChecked()) {
                    isChecked = false;
                } else {
                    isChecked = true;
                    if (mListView != null) {
                        int firstPosition = mListView.getFirstVisiblePosition();
                        int lastPosition = mListView.getLastVisiblePosition();
                        int moveTo = firstPosition;
                        if (firstPosition == position) {
                            moveTo = moveTo - 1 > 0 ? moveTo - 1 : 0;
                        } else if (lastPosition == position) {
                            moveTo = moveTo + 1 > mFileItems.size() ? moveTo : moveTo + 1;
                        }
                        mListView.setSelection(moveTo);
                    }
                }
                info.setChecked(isChecked);
                if (mOnCheckBoxChangedListener != null) {
                    mOnCheckBoxChangedListener.onCheckChanged(position, isChecked);
                }
            }
        });
    }

    /**
     * 设置Holder上的每一个组件的值
     * 
     * @param holder
     * @param position
     */
    private void setHolder(ViewHolder holder, int position) {
        SimpleFileInfo info = mFileItems.get(position);

        // 设置图片
        if (info.getFileType() == FileType.TYPE_PICTURE || info.getFileType() == FileType.TYPE_MP4 || info.getFileType() == FileType.TYPE_AVI
                || info.getFileType() == FileType.TYPE_3GP || info.getFileType() == FileType.TYPE_RMVB) {
            Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(info.getPath());
            if (bitmap == null) {
                holder.mImageFileType.setImageResource(FileType.getResourceIdByType(info.getFileType()));
            } else {
                holder.mImageFileType.setImageBitmap(bitmap);
            }
        } else {
            holder.mImageFileType.setImageResource(FileType.getResourceIdByType(info.getFileType()));
        }
        holder.mImageFileType.setTag(info.getPath());

        // 是否选中
        if (info.isChecked()) {
            holder.mCbChecke.setChecked(true);
        } else {
            holder.mCbChecke.setChecked(false);
        }

        // 文件名
        holder.mTvName.setText(info.getName());

        if (info.getFileType() == FileType.TYPE_APK) {
            SimplePackgeInfo pkgInfo = ResourceManager.getApkPackgageInfo(info.getPath());
            if (pkgInfo != null) {
                holder.mImageFileType.setImageDrawable(pkgInfo.getIcon());
            } else {
                holder.mImageFileType.setImageResource(R.drawable.type_apk);
            }
        }
        holder.mTvPath.setText(info.getPath());

        // 创建时间
        holder.mTvCreateTime.setText(TextUtil.getDateStringString(info.getCreateTime()));

        if (info.getFileSize() == 0) {
            info.setFileSize(FileUtils.getChildCount(info.getPath()));
        }

        // APP名字和大小
        if (info.getFileType() == FileType.TYPE_FOLDER) {
            // 设置App名字
            if (info.getAppName() == null) {
                String appName = FileUtils.getAppNameFromMap(info.getName());
                info.setAppName(appName);
            }

            if (!TextUtil.isEmpty(info.getAppName())) {
                holder.mTvAppName.setText("(" + info.getAppName() + ")");
            } else {
                holder.mTvAppName.setText("");
            }

            // 设置大小
            if (info.getFileSize() == 0) {
                holder.mTvSizeCount.setText("( 空 )");
            } else {
                holder.mTvSizeCount.setText("(" + info.getFileSize() + "个)");
            }

        } else {

            holder.mTvAppName.setText("");
            holder.mTvSizeCount.setText(TextUtil.getSizeSting(info.getFileSize()));
        }
    }

    class LoadImageToImageLoaderRunnable implements Runnable {

        private String mUrl = null;
        private int mIndex = 0;

        public LoadImageToImageLoaderRunnable(String url, int index) {
            mUrl = url;
            mIndex = index;
        }

        @Override
        public void run() {
            Bitmap bitmap = CMImageLoader.decodeSampledBitmapFromResource(mUrl, 80);
            mImageLoader.addBitmapToMemoryCache(mUrl, bitmap);

            Message msg = new Message();
            msg.what = MSG_TYPE_DISPLAY_IMAGE;
            msg.obj = new Holder(mUrl, bitmap, mIndex);

            mHandler.sendMessage(msg);
        }
    }

    static class Holder {
        String url = null;
        Bitmap bitmap = null;
        int index = 0;

        public Holder(String url, Bitmap bitmap, int index) {
            this.url = url;
            this.bitmap = bitmap;
            this.index = index;
        }
    }

    private void findHolder(ViewHolder holder, View view) {
        holder.mImageFileType = (ImageView) view.findViewById(R.id.mImageFileType);
        holder.mTvName = (TextView) view.findViewById(R.id.mTvName);
        holder.mTvPath = (TextView) view.findViewById(R.id.mTvPath);
        holder.mCbChecke = (CheckBox) view.findViewById(R.id.mCbChecke);
        holder.mTvCreateTime = (TextView) view.findViewById(R.id.mTvCreateTime);
        holder.mTvAppName = (TextView) view.findViewById(R.id.mTvAppName);
        holder.mTvSizeCount = (TextView) (view.findViewById(R.id.mTvSize));
    }

    class ViewHolder {
        ImageView mImageFileType; // 文件图标
        TextView mTvName;
        TextView mTvPath; // 父路径
        CheckBox mCbChecke; // 选中
        TextView mTvCreateTime; // 时间
        TextView mTvAppName; // 应用程序名字
        TextView mTvSizeCount; // 文件大小
    }

    public class CMOnScrollListener implements OnScrollListener {
        private int mFirstVisibleItem = 0;
        private int mVisibleItemCount = 0;
        private int mCount = 0;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                loadBitmaps(view, mFirstVisibleItem, mVisibleItemCount);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            mListView = view;

            if (mIsFirstLoad) {// 第一次加载
                loadBitmaps(view, firstVisibleItem, visibleItemCount);
                if (mCount < 5) {
                    mCount++;
                } else {
                    mCount = 0;
                    mIsFirstLoad = false;
                }
            }
        }

        // 加载图片
        public void loadBitmaps(AbsListView view, int first, int pageCount) {
            for (int i = first; i < first + pageCount; i++) {
                int fileType = mFileItems.get(i).getFileType();
                if (i < mFileItems.size()
                        && (fileType == FileType.TYPE_PICTURE || fileType == FileType.TYPE_MP4 || fileType == FileType.TYPE_AVI
                                || fileType == FileType.TYPE_3GP || fileType == FileType.TYPE_RMVB)) {

                    String url = mFileItems.get(i).getPath();
                    Bitmap bitmap = mImageLoader.getBitmapFromMemoryCache(url);
                    if (bitmap == null) {
                        // 异步加载
                        LoadImageToImageLoaderRunnable runnable = new LoadImageToImageLoaderRunnable(url, i);
                        mPool.execute(runnable);
                    } else {
                        ImageView imageView = (ImageView) view.findViewWithTag(url);
                        if (imageView != null && bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }
            }
        }
    }

    public interface OnCheckBoxChangedListener {
        public void onCheckChanged(int position, boolean isChecked);
    }

}
