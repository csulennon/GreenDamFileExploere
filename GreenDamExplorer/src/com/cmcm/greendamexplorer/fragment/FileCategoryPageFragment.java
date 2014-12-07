package com.cmcm.greendamexplorer.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.greendamexplorer.activity.ApksActivity;
import com.cmcm.greendamexplorer.activity.ApplicationsActivity;
import com.cmcm.greendamexplorer.activity.AudioActivity;
import com.cmcm.greendamexplorer.activity.DocumentsActivity;
import com.cmcm.greendamexplorer.activity.ImageWallActivity;
import com.cmcm.greendamexplorer.activity.MainActivity;
import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.activity.VideoActivity;
import com.cmcm.greendamexplorer.activity.ZipActivity;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.core.engine.ResourceManager;
import com.cmcm.greendamexplorer.entity.Audio;
import com.cmcm.greendamexplorer.entity.Video;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;
import com.cmcm.greendamexplorer.view.PieChart;
import com.cmcm.greendamexplorer.view.PieChart.OnSelectedLisenter;

public class FileCategoryPageFragment extends Fragment implements OnClickListener, OnSelectedLisenter {

    private static final int MSG_SET_BASCI_CATEGORY_COUNT = 0x6001;
    private static final int MSG_SET_OTHER_CATEGORY_COUNT = 0x6002;
    private static final int INDEX_PICURE = 0;
    private static final int INDEX_VIDEO = 1;
    private static final int INDEX_AUDIO = 2;
    private static final int INDEX_DOCUMENT = 3;
    private static final int INDEX_ZIP = 4;
    private static final int INDEX_APK = 5;
    private static final int REQUEST_PATH = 0x1001;
    protected static final int MSG_REFRESH_UI = 0x6003;

    public static long mAllAudioSize = 0;
    public static long mAllVideoSize = 0;
    public static long mAllPictureSize = 0;
    public static long mAllApkSize = 0;
    public static long mAllDocumentsSize = 0;
    public static long mAllZipsSize = 0;
    public static long mAllCateSize = 0;

    private ScanCategoryThread mThread = null;
    private ContentResolver mContentResolver = MediaResourceManager.mContentResolver;
    private View mView = null;

    private List<Audio> mAudios = null;
    private static List<String> mPictrues = null;
    private List<String> mBluetooths = null;
    private List<String> mDownloads = null;
    private int mApplicationCount = 0;
    private static List<Video> mVideos = null;
    private static List<String> mApks = new ArrayList<String>();
    private static List<String> mDocuments = new ArrayList<String>();
    private static List<String> mZips = new ArrayList<String>();

    private MainActivity mActivity = null;
    private ViewPageFragment mViewPageFragment = null;
    private FileListPageFragment mFileListPageFragment = null;

    private View mViewPictures = null;
    private View mViewVideos = null;
    private View mViewAudios = null;
    private View mViewDocuments = null;
    private View mViewZips = null;
    private View mViewApks = null;
    private View mViewDownloads = null;
    private View mViewBluetoothes = null;
    private View mViewApplications = null;

    private PieChart mChart = null;
    private TextView mTvCateTotalSize = null;
    private TextView mTvCateUsedSize = null;
    private TextView mTvCateFreeSize = null;
    private TextView mTvCategoryName = null;
    private TextView mTvCateCount = null;
    private TextView mTvCateSize = null;
    private TextView mTvCateRate = null;

    private TextView mTvPictureCount = null;
    private TextView mTvVideoCount = null;
    private TextView mTvAudioCount = null;
    private TextView mTvDocumentCount = null;
    private TextView mTvZipsCount = null;
    private TextView mTvApksCount = null;
    private TextView mTvDownloadCount = null;
    private TextView mTvBluetoothCount = null;
    private TextView mTvApplicationsCount = null;
    private ArrayList<Float> mPercentages = new ArrayList<Float>();

    private static int mCurrentSelected = 0;// 当前选中的类别

    public static List<String> getDocuments() {
        return mDocuments;
    }

    public static List<String> getZips() {
        return mZips;
    }

    public static List<String> getApks() {
        return mApks;
    }

    public static List<String> getPictures() {
        return mPictrues;
    }

    public static List<Video> getVideos() {
        return mVideos;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_SET_BASCI_CATEGORY_COUNT) {
                setAllCateCount();
                setAbstractText();
            } else if (msg.what == MSG_SET_OTHER_CATEGORY_COUNT) {
                mTvDocumentCount.setText("(" + mDocuments.size() + ")");
                mTvZipsCount.setText("(" + mZips.size() + ")");
                mTvApksCount.setText("(" + mApks.size() + ")");

                // setAllStatistics();
                mChart.invalidate();
                setAllPercentages();
                try {
                    mChart.setAdapter(mPercentages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setDetailText();
            } else if (msg.what == MSG_REFRESH_UI) {
                setAllCateCount();
                setAbstractText();
                setAllPercentages();
                try {
                    mChart.setAdapter(mPercentages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setDetailText();
            }
        }

        private void setAllCateCount() {
            mTvPictureCount.setText("(" + mPictrues.size() + ")");
            mTvVideoCount.setText("(" + mVideos.size() + ")");
            mTvAudioCount.setText("(" + mAudios.size() + ")");
            mTvDownloadCount.setText("(" + mDownloads.size() + ")");
            mTvBluetoothCount.setText("(" + mBluetooths.size() + ")");
            mTvApplicationsCount.setText("(" + mApplicationCount + ")");
            mTvDocumentCount.setText("(" + mDocuments.size() + ")");
            mTvZipsCount.setText("(" + mZips.size() + ")");
            mTvApksCount.setText("(" + mApks.size() + ")");
        };
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.page_category, container, false);

        mThread = new ScanCategoryThread();
        mThread.start();

        initViews();

        Animation anim = AnimationUtils.loadAnimation(mView.getContext(), R.anim.pie_chart_animation);
        mChart.startAnimation(anim);

        return mView;

    }

    public void startPieChartAnim() {
        if (mView != null) {
            Animation anim = AnimationUtils.loadAnimation(mView.getContext(), R.anim.pie_chart_animation);
            mChart.startAnimation(anim);
        }
    }

    private void initViews() {
        mViewPictures = mView.findViewById(R.id.mViewPictures);
        mViewVideos = mView.findViewById(R.id.mViewVideos);
        mViewAudios = mView.findViewById(R.id.mViewAudios);
        mViewDocuments = mView.findViewById(R.id.mViewDocuments);
        mViewZips = mView.findViewById(R.id.mViewZips);
        mViewApks = mView.findViewById(R.id.mViewApks);
        mViewDownloads = mView.findViewById(R.id.mViewDownloads);
        mViewBluetoothes = mView.findViewById(R.id.mViewBluetoothes);
        mViewApplications = mView.findViewById(R.id.mViewApplications);

        mViewPictures.setOnClickListener(this);
        mViewVideos.setOnClickListener(this);
        mViewAudios.setOnClickListener(this);
        mViewDocuments.setOnClickListener(this);
        mViewZips.setOnClickListener(this);
        mViewApks.setOnClickListener(this);
        mViewDownloads.setOnClickListener(this);
        mViewBluetoothes.setOnClickListener(this);
        mViewApplications.setOnClickListener(this);

        mTvPictureCount = (TextView) mView.findViewById(R.id.mTvPictureCount);
        mTvVideoCount = (TextView) mView.findViewById(R.id.mTvVideoCount);
        mTvAudioCount = (TextView) mView.findViewById(R.id.mTvAudioCount);
        mTvDocumentCount = (TextView) mView.findViewById(R.id.mTvDocumentCount);
        mTvZipsCount = (TextView) mView.findViewById(R.id.mTvZipsCount);
        mTvApksCount = (TextView) mView.findViewById(R.id.mTvApksCount);
        mTvDownloadCount = (TextView) mView.findViewById(R.id.mTvDownloadCount);
        mTvBluetoothCount = (TextView) mView.findViewById(R.id.mTvBluetoothCount);
        mTvApplicationsCount = (TextView) mView.findViewById(R.id.mTvApplicationsCount);

        mTvCateTotalSize = (TextView) mView.findViewById(R.id.mTvCateTotalSize);
        mTvCateUsedSize = (TextView) mView.findViewById(R.id.mTvCateUsedSize);
        mTvCateFreeSize = (TextView) mView.findViewById(R.id.mTvCateFreeSize);
        mTvCategoryName = (TextView) mView.findViewById(R.id.mTvCategoryName);
        mTvCateCount = (TextView) mView.findViewById(R.id.mTvCateCount);
        mTvCateSize = (TextView) mView.findViewById(R.id.mTvCateSize);
        mTvCateRate = (TextView) mView.findViewById(R.id.mTvCateRate);

        mChart = (PieChart) mView.findViewById(R.id.mChart);

        mChart.setOnSelectedListener(this);
        mPercentages.add(10.0f);// 图片
        mPercentages.add(10.0f);// 视频
        mPercentages.add(10.0f);// 音乐
        mPercentages.add(10.0f);// 文档
        mPercentages.add(10.0f);// 压缩包
        mPercentages.add(10.0f);// 安装包
        try {
            mChart.setAdapter(mPercentages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity = (MainActivity) getActivity();
        mViewPageFragment = mActivity.getViewPageFragment();
        mFileListPageFragment = mViewPageFragment.getFileListPageFragment();
    }

    public void setAllPercentages() {
        setPicturesPercentage();
        setVideosPercentage();
        setAudiosPercentage();
        setDocumetsPercentage();
        setZipsPercentage();
        setApksPercentage();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setPicturesPercentage() {
        mPercentages.set(INDEX_PICURE, (float) (mAllPictureSize * 100.0 / mAllCateSize));
    }

    public void setVideosPercentage() {
        mPercentages.set(INDEX_VIDEO, (float) (mAllVideoSize * 100.0 / mAllCateSize));
    }

    public void setAudiosPercentage() {
        mPercentages.set(INDEX_AUDIO, (float) (mAllAudioSize * 100.0 / mAllCateSize));
    }

    public void setDocumetsPercentage() {
        mPercentages.set(INDEX_DOCUMENT, (float) (mAllDocumentsSize * 100.0 / mAllCateSize));
    }

    public void setZipsPercentage() {
        mPercentages.set(INDEX_ZIP, (float) (mAllZipsSize * 100.0 / mAllCateSize));
    }

    public void setApksPercentage() {
        float total = 0;
        for (int i = 0; i < mPercentages.size() - 1; i++) {
            total = total + mPercentages.get(i);
        }
        mPercentages.set(INDEX_APK, 100 - total);
    }

    public void reScan() {
        mThread = new ScanCategoryThread();
        mThread.start();
    }

    class ScanCategoryThread extends Thread {

        @Override
        public void run() {
            mAllCateSize = 0;
            mAudios = MediaResourceManager.getAudiosFromMedia();
            mAllCateSize += mAllAudioSize;

            mVideos = MediaResourceManager.getVideosFromMedia();
            mAllCateSize += mAllVideoSize;

            mPictrues = MediaResourceManager.getImagesFromMedia();
            mAllCateSize += mAllPictureSize;

            mDownloads = MediaResourceManager.getDownloads();
            mBluetooths = MediaResourceManager.getBluetooths();
            mApplicationCount = MediaResourceManager.getApplicationCount();

            mHandler.sendEmptyMessage(MSG_SET_BASCI_CATEGORY_COUNT);

            mAllApkSize = 0;
            mAllDocumentsSize = 0;
            mAllZipsSize = 0;

            // 扫描files文件库
            Cursor c = null;
            try {
                c = mContentResolver.query(MediaStore.Files.getContentUri("external"), new String[] { "_id", "_data", "_size" }, null, null, null);
                int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                mApks.clear();
                mDocuments.clear();
                mZips.clear();

                while (c.moveToNext()) {
                    String path = c.getString(dataindex);

                    if (FileType.isDocument(path)) {
                        if (!FileUtils.isExists(path)) {
                            continue;
                        }
                        mDocuments.add(path);
                        long size = c.getLong(sizeindex);
                        mAllDocumentsSize += size;
                        mAllCateSize += size;
                        mHandler.sendEmptyMessage(MSG_SET_OTHER_CATEGORY_COUNT);
                    } else if (FileType.isZip(path)) {
                        if (!FileUtils.isExists(path)) {
                            continue;
                        }
                        mZips.add(path);
                        long size = c.getLong(sizeindex);
                        mAllZipsSize += size;
                        mAllCateSize += size;
                        mHandler.sendEmptyMessage(MSG_SET_OTHER_CATEGORY_COUNT);
                    } else if (FileType.isApk(path)) {
                        if (!FileUtils.isExists(path)) {
                            continue;
                        }
                        mApks.add(path);
                        long size = c.getLong(sizeindex);
                        mAllApkSize += size;
                        mAllCateSize += size;
                        mHandler.sendEmptyMessage(MSG_SET_OTHER_CATEGORY_COUNT);
                    }
                }

                System.out.println("文档：" + mAllDocumentsSize + "压缩包：" + mAllZipsSize + "安装包+ " + mAllApkSize);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (c != null) {
                    c.close();
                }
            }
        }
    }

    public void setAbstractText() {
        ResourceManager.calcStorageSize();
        mTvCateTotalSize.setText("存储空间：" + TextUtil.getSizeSting(ResourceManager.mToalBytes));
        mTvCateUsedSize.setText("已用空间：" + TextUtil.getSizeSting(ResourceManager.mUsedBytes));
        mTvCateFreeSize.setText("剩余空间：" + TextUtil.getSizeSting(ResourceManager.mFreeBytes));
    }

    public void setDetailText() {
        int count = 0;
        long bytes = 0;
        switch (mCurrentSelected) {
        case INDEX_PICURE:
            count = mPictrues.size();
            bytes = mAllPictureSize;
            mTvCategoryName.setText("图片");
            break;
        case INDEX_VIDEO:
            count = mVideos.size();
            bytes = mAllVideoSize;
            mTvCategoryName.setText("视频");
            break;
        case INDEX_AUDIO:
            count = mAudios.size();
            bytes = mAllAudioSize;
            mTvCategoryName.setText("音乐");
            break;
        case INDEX_DOCUMENT:
            count = mDocuments.size();
            bytes = mAllDocumentsSize;
            mTvCategoryName.setText("文档");
            break;
        case INDEX_ZIP:
            count = mZips.size();
            bytes = mAllZipsSize;
            mTvCategoryName.setText("压缩包");
            break;
        case INDEX_APK:
            count = mApks.size();
            bytes = mAllApkSize;
            mTvCategoryName.setText("安装包");
            break;

        default:
            break;
        }
        mTvCateCount.setText("个数：" + count);
        mTvCateSize.setText("大小：" + TextUtil.getSizeSting(bytes));
        mTvCateRate.setText("占比：" + String.format("%.2f", bytes * 100.0 / mAllCateSize) + "%");
    }

    public void setAllStatistics() {
        ResourceManager.calcStorageSize();
        setAbstractText();
        setDetailText();
    }

    @Override
    public void onClick(View v) {
        mViewPictures.setOnClickListener(this);
        mViewVideos.setOnClickListener(this);
        mViewAudios.setOnClickListener(this);
        mViewDocuments.setOnClickListener(this);
        mViewZips.setOnClickListener(this);
        mViewApks.setOnClickListener(this);
        mViewDownloads.setOnClickListener(this);
        mViewBluetoothes.setOnClickListener(this);
        mViewApplications.setOnClickListener(this);

        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.mViewPictures:// 图片
            intent.setClass(getActivity(), ImageWallActivity.class);
            startActivityForResult(intent, REQUEST_PATH);
            break;
        case R.id.mViewVideos:
            intent.setClass(getActivity(), VideoActivity.class);
            startActivityForResult(intent, REQUEST_PATH);
            break;
        case R.id.mViewAudios:
            intent.setClass(getActivity(), AudioActivity.class);
            startActivityForResult(intent, REQUEST_PATH);
            break;
        case R.id.mViewDocuments:
            if (mThread.isAlive()) {
                Toast.makeText(getActivity(), "不要那么猴急，等我找出所有文档！", Toast.LENGTH_SHORT).show();
                break;
            }
            intent.setClass(getActivity(), DocumentsActivity.class);
            startActivityForResult(intent, REQUEST_PATH);
            break;

        case R.id.mViewZips:
            if (mThread.isAlive()) {
                Toast.makeText(getActivity(), "不要那么猴急，等我找出所有压缩包！", Toast.LENGTH_SHORT).show();
                break;
            }
            intent.setClass(getActivity(), ZipActivity.class);
            startActivityForResult(intent, REQUEST_PATH);
            break;
        case R.id.mViewApks:
            if (mThread.isAlive()) {
                Toast.makeText(getActivity(), "不要那么猴急，等我找出所有的安装包！", Toast.LENGTH_SHORT).show();
                break;
            }
            intent.setClass(getActivity(), ApksActivity.class);
            intent.putStringArrayListExtra("apks", (ArrayList<String>) mApks);
            startActivityForResult(intent, REQUEST_PATH);
            break;
        case R.id.mViewDownloads:
            mViewPageFragment.setPage(0);
            String downloadPath = MediaResourceManager.getDownloadPath();
            mFileListPageFragment.autoDeploymentTopNavisStack(downloadPath);
            break;
        case R.id.mViewBluetoothes:
            mViewPageFragment.setPage(0);
            String bluetoothPath = MediaResourceManager.getBluetoothPath();
            mFileListPageFragment.autoDeploymentTopNavisStack(bluetoothPath);
            break;
        case R.id.mViewApplications:
            intent.setClass(getActivity(), ApplicationsActivity.class);
            startActivity(intent);
            break;

        default:
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 检查所有集合对象是否合法
     */
    public void checkAllDataListExsists() {
        new Thread() {
            public void run() {
                checkPictrue();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                checkVideos();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                checkAudios();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                checkDocuments();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                checkZips();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
                checkApks();
                mHandler.sendEmptyMessage(MSG_REFRESH_UI);
            };

        }.start();

    }

    public void checkPictrue() {
        if (mPictrues != null) {
            for (int i = 0; i < mPictrues.size(); i++) {
                File file = new File(mPictrues.get(i));
                if (!file.exists()) {
                    mPictrues.remove(i);
                    i--;
                }
            }
        }
    }

    public void checkVideos() {
        if (mVideos != null) {
            for (int i = 0; i < mVideos.size(); i++) {
                File file = new File(mVideos.get(i).getPath());
                if (!file.exists()) {
                    mAllVideoSize -= mVideos.get(i).getSize();
                    mAllCateSize -= mVideos.get(i).getSize();
                    mVideos.remove(i);
                    i--;
                }
            }
        }
    }

    public void checkAudios() {
        if (mAudios != null) {
            for (int i = 0; i < mAudios.size(); i++) {
                File file = new File(mAudios.get(i).getPath());
                if (!file.exists()) {
                    mAllAudioSize -= mAudios.get(i).getSize();
                    mAllCateSize -= mAudios.get(i).getSize();
                    mAudios.remove(i);
                    i--;
                }
            }
        }
    }

    public void checkDocuments() {
        if (mDocuments != null) {
            for (int i = 0; i < mDocuments.size(); i++) {
                File file = new File(mDocuments.get(i));
                if (!file.exists()) {
                    mDocuments.remove(i);
                    i--;
                }
            }
        }
    }

    public void checkZips() {
        if (mZips != null) {
            for (int i = 0; i < mZips.size(); i++) {
                File file = new File(mZips.get(i));
                if (!file.exists()) {
                    mZips.remove(i);
                    i--;
                }
            }
        }
    }

    public void checkApks() {
        if (mApks != null) {
            for (int i = 0; i < mApks.size(); i++) {
                File file = new File(mApks.get(i));
                if (!file.exists()) {
                    mApks.remove(i);
                    i--;
                }
            }
        }
    }

    @Override
    public void onSelected(int iSelectedIndex) {
        mCurrentSelected = iSelectedIndex;
        setDetailText();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String path = data.getStringExtra("path");
            if (path != null) {
                mActivity.goToPage(0);
                mFileListPageFragment.autoDeploymentTopNavisStack(path);
            }
        }
    }

    public void refreshUi() {
        checkAllDataListExsists();
    }

}
