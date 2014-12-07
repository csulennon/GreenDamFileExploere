package com.cmcm.greendamexplorer.view;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.entity.Video;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class VideoInfoDialog extends Dialog implements OnClickListener {
    private static final int RESULT_VIDEO = 0x1001;

    private View mView = null;

    private ImageView mImageVideoInfo = null;
    private TextView mTvVideoInfoName = null;
    private TextView mTvVideoInfoPath = null;
    private TextView mTvVideoInfoReadWrite = null;
    private TextView mTvVideoInfoFileSize = null;
    private TextView mTvVideoInfoCreateTime = null;
    private TextView mTvVideoInfoDuration = null;
    private TextView mTvVideoInfoResolution = null;

    private Button mBtnVideoInfoOk = null;
    private Button mBtnVideoInfoGodir = null;
    private Activity mActivity = null;

    private File mFile = null;

    private Video mVideo = null;;

    public VideoInfoDialog(Context context, Video video) {
        super(context);
        mVideo = video;
        init(context);
    }

    public VideoInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public VideoInfoDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public VideoInfoDialog(Context context) {
        super(context);
    }

    private void init(Context context) {
        mActivity = (Activity) context;
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_video_info, null);

        mImageVideoInfo = (ImageView) mView.findViewById(R.id.mImageVideoInfo);
        mTvVideoInfoName = (TextView) mView.findViewById(R.id.mTvVideoInfoName);
        mTvVideoInfoPath = (TextView) mView.findViewById(R.id.mTvVideoPath);
        mTvVideoInfoReadWrite = (TextView) mView.findViewById(R.id.mTvVideoInfoReadWrite);
        mTvVideoInfoFileSize = (TextView) mView.findViewById(R.id.mTvVideoInfoFileSize);
        mTvVideoInfoCreateTime = (TextView) mView.findViewById(R.id.mTvVideoInfoCreateTime);
        mTvVideoInfoDuration = (TextView) mView.findViewById(R.id.mTvVideoInfoDuration);
        mTvVideoInfoResolution = (TextView) mView.findViewById(R.id.mTvVideoInfoResolution);

        mBtnVideoInfoOk = (Button) mView.findViewById(R.id.mBtnVideoInfoOk);
        mBtnVideoInfoGodir = (Button) mView.findViewById(R.id.mBtnVideoInfoGoDir);

        mBtnVideoInfoOk.setOnClickListener(this);
        mBtnVideoInfoGodir.setOnClickListener(this);

        this.setTitle("视频详情");
        this.setContentView(mView);
        this.setCanceledOnTouchOutside(false);

        mFile = new File(mVideo.getPath());

        mImageVideoInfo.setImageBitmap(MediaResourceManager.getVideoThumbnail(mVideo.getId()));
        mTvVideoInfoName.setText(mVideo.getName());
        mTvVideoInfoPath.setText(mVideo.getPath());
        setReadWrite(mFile);
        mTvVideoInfoFileSize.setText(TextUtil.getSizeSting(mVideo.getSize()));
        mTvVideoInfoCreateTime.setText(TextUtil.getDateStringString(mFile.lastModified()));
        mTvVideoInfoDuration.setText(TextUtil.getDurationToString(mVideo.getDuration()));
        mTvVideoInfoResolution.setText(mVideo.getResolution());
        setReadWrite(mFile);
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
        mTvVideoInfoReadWrite.setText(builder.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnVideoInfoOk:
            dismiss();
            break;

        case R.id.mBtnVideoInfoGoDir:
            dismiss();
            Intent intent = new Intent();
            intent.putExtra("path", mVideo.getPath());
            mActivity.setResult(RESULT_VIDEO, intent);
            mActivity.finish();
            break;
        default:
            break;
        }

    }
}
