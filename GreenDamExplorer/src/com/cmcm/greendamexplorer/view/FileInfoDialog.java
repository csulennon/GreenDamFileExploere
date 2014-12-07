package com.cmcm.greendamexplorer.view;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.CMImageLoader;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class FileInfoDialog extends Dialog implements OnClickListener {

    protected static final int MSG_CALC_SIZE = 0x4001;

    private static final int RESULT_DOC = 0x1003;

    private View mView = null;

    private ImageView mImageFileInfo = null;

    private TextView mTvFileInfoName = null;
    private TextView mTvFileInfoPath = null;
    private TextView mTvFileInfoReadWrite = null;
    private TextView mTvFileInfoFileSize = null;
    private TextView mTvFileInfoCreateTime = null;

    private Button mBtnFileInfoOk = null;
    private Button mBtnFileInfoGoDir = null;

    private String mFilePath = null;
    private boolean mIsShowGotoBtn = false;

    private long mSize = 0;
    private int mFileCount = 0;
    private int mFolderCount = 0;
    private File mFile = null;
    private Activity mActivity = null;

    private boolean mIsRuning = true;

    private FolderAnalysisThread mThread = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == MSG_CALC_SIZE) {
                StringBuilder text = new StringBuilder();
                text.append("文件数(").append(mFileCount).append("个)/目录数(").append(mFolderCount);
                text.append("个)/大小(").append(TextUtil.getSizeSting(mSize)).append(")");

                mTvFileInfoFileSize.setText(text.toString());
            }
        };
    };

    public FileInfoDialog(Context context, String filePath) {
        this(context, filePath, false);
    }

    public FileInfoDialog(Context context, String filePath, boolean showGoto) {
        super(context);
        this.mFilePath = filePath;
        mIsShowGotoBtn = showGoto;
        init(context);
    }

    public FileInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public FileInfoDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public FileInfoDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_file_info, null);
        
        if(mIsShowGotoBtn) {
            mActivity = (Activity) context;
        }

        mImageFileInfo = (ImageView) mView.findViewById(R.id.mImageFileInfo);
        mTvFileInfoName = (TextView) mView.findViewById(R.id.mTvFileInfoName);
        mTvFileInfoReadWrite = (TextView) mView.findViewById(R.id.mTvFileInfoReadWrite);
        mTvFileInfoFileSize = (TextView) mView.findViewById(R.id.mTvFileInfoFileSize);
        mTvFileInfoCreateTime = (TextView) mView.findViewById(R.id.mTvFileInfoCreateTime);
        mBtnFileInfoOk = (Button) mView.findViewById(R.id.mBtnFileInfoOk);
        mBtnFileInfoGoDir = (Button) mView.findViewById(R.id.mBtnFileInfoGoDir);
        mTvFileInfoPath = (TextView) mView.findViewById(R.id.mTvFileInfoPath);

        mBtnFileInfoOk.setOnClickListener(this);
        mBtnFileInfoGoDir.setOnClickListener(this);

        if (mIsShowGotoBtn) {
            mBtnFileInfoGoDir.setVisibility(View.VISIBLE);
        }

        this.setTitle("文件详情");
        this.setContentView(mView);
        this.setCanceledOnTouchOutside(false);

        mFile = new File(mFilePath);

        int type = FileType.getFileType(mFilePath.toLowerCase());
        int resId = FileType.getResourceIdByType(type);
        if (type == FileType.TYPE_PICTURE) {
            Bitmap bitmap = CMImageLoader.decodeSampledBitmapFromResource(mFilePath, 60);
            if (bitmap != null) {
                mImageFileInfo.setImageBitmap(bitmap);
            }
        } else {
            mImageFileInfo.setImageResource(resId);
        }

        mTvFileInfoName.setText(FileUtils.getFileName(mFilePath));
        mTvFileInfoPath.setText(mFilePath);

        setReadWrite(mFile);

        mTvFileInfoCreateTime.setText(TextUtil.getDateStringString(mFile.lastModified()));

        if (mFile.exists() && mFile.isFile()) {
            mTvFileInfoFileSize.setText(TextUtil.getSizeSting(mFile.length()));

        } else {
            mImageFileInfo.setImageResource(R.drawable.type_folder);
            mThread = new FolderAnalysisThread();
            mIsRuning = true;
            mThread.start();
        }
    }

    private void setReadWrite(File file) {
        StringBuilder builder = new StringBuilder();
        if (file.canRead()) {
            builder.append("可读");
        }
        if (file.canWrite()) {
            if (builder.length() > 0) {
                builder.append("/可写");
            } else {
                builder.append("可写");
            }
        }

        if (file.canExecute()) {
            if (file.isFile()) {
                if (builder.length() > 0) {
                    builder.append("/可执行");
                } else {
                    builder.append("可执行");
                }
            } else if (file.isDirectory()) {
                if (builder.length() > 0) {
                    builder.append("/可进入");
                } else {
                    builder.append("可进入");
                }
            }
        }

        if (builder.length() == 0) {
            builder.append("无权限");
        }

        mTvFileInfoReadWrite.setText(builder.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnFileInfoOk:
            if (mThread != null && mThread.isAlive()) {
                mIsRuning = false;
            }
            dismiss();
            break;
        case R.id.mBtnFileInfoGoDir:
            if (mThread != null && mThread.isAlive()) {
                mIsRuning = false;
            }
            dismiss();
            if (mActivity != null) {
                Intent data = new Intent();
                data.putExtra("path", mFilePath);
                mActivity.setResult(RESULT_DOC, data);
                mActivity.finish();
            }
            break;
        default:
            break;
        }

    }

    class FolderAnalysisThread extends Thread {

        @Override
        public void run() {
            analysisFolder(mFile);
            mHandler.sendEmptyMessage(MSG_CALC_SIZE);
        }

    }

    private void analysisFolder(File dir) {
        File[] files = dir.listFiles();
        mFileCount++;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!mIsRuning) {
                    break;
                }

                if (files[i].isFile()) {
                    mSize += files[i].length();
                    mFileCount++;
                } else if (files[i].isDirectory()) {
                    mFolderCount++;
                    analysisFolder(files[i]);
                }

                mHandler.sendEmptyMessage(MSG_CALC_SIZE);
            }
        }
    }

}
