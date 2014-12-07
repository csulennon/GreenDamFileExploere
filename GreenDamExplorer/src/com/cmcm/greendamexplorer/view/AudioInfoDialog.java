package com.cmcm.greendamexplorer.view;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.entity.Audio;
import com.cmcm.greendamexplorer.utils.TextUtil;

public class AudioInfoDialog extends Dialog implements OnClickListener {
    private static final int RESULT_AUDIO = 0x1002;

    private View mView = null;

    private ImageView mImageAudioInfo = null;
    private TextView mTvAudioInfoName = null;
    private TextView mTvAudioInfoPath = null;
    private TextView mTvAudioInfoReadWrite = null;
    private TextView mTvAudioInfoFileSize = null;
    private TextView mTvAudioInfoCreateTime = null;
    private TextView mTvAudioInfoDuration = null;
    private TextView mTvAudioInfoAlbum = null;

    private Button mBtnAudioInfoOk = null;
    private Button mBtnAudioInfoGodir = null;

    private File mFile = null;

    private Audio mAudio = null;

    private Activity mActivity = null;

    public AudioInfoDialog(Context context, Audio audio) {
        super(context);
        mAudio = audio;
        init(context);
    }

    public AudioInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public AudioInfoDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public AudioInfoDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mActivity = (Activity) context;
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_audio_info, null);

        mImageAudioInfo = (ImageView) mView.findViewById(R.id.mImageAudioInfo);
        mTvAudioInfoName = (TextView) mView.findViewById(R.id.mTvAudioInfoName);
        mTvAudioInfoPath = (TextView) mView.findViewById(R.id.mTvAudioPath);
        mTvAudioInfoReadWrite = (TextView) mView.findViewById(R.id.mTvAudioInfoReadWrite);
        mTvAudioInfoFileSize = (TextView) mView.findViewById(R.id.mTvAudioInfoFileSize);
        mTvAudioInfoCreateTime = (TextView) mView.findViewById(R.id.mTvAudioInfoCreateTime);
        mTvAudioInfoDuration = (TextView) mView.findViewById(R.id.mTvAudioInfoDuration);
        mTvAudioInfoAlbum = (TextView) mView.findViewById(R.id.mTvAudioInfoAlbum);

        mBtnAudioInfoOk = (Button) mView.findViewById(R.id.mBtnAudioInfoOk);
        mBtnAudioInfoGodir = (Button) mView.findViewById(R.id.mBtnAudioInfoGoDir);

        mBtnAudioInfoOk.setOnClickListener(this);
        mBtnAudioInfoGodir.setOnClickListener(this);

        this.setTitle("音乐详情");
        this.setContentView(mView);
        this.setCanceledOnTouchOutside(false);

        mFile = new File(mAudio.getPath());
        Bitmap bitmap = MediaResourceManager.getArtworkFromFile(mAudio.getId(), mAudio.getAlbumId());
        if (bitmap != null) {
            mImageAudioInfo.setImageBitmap(bitmap);
        } else {
            mImageAudioInfo.setBackgroundResource(R.drawable.type_mp3);
        }
        mTvAudioInfoName.setText(mAudio.getTilte());
        mTvAudioInfoPath.setText(mAudio.getPath());
        setReadWrite(mFile);
        mTvAudioInfoFileSize.setText(TextUtil.getSizeSting(mAudio.getSize()));
        mTvAudioInfoCreateTime.setText(TextUtil.getDateStringString(mFile.lastModified()));
        mTvAudioInfoDuration.setText(TextUtil.getDurationToString(mAudio.getDuration()));
        if (mAudio.getAlbum() != null) {
            mTvAudioInfoAlbum.setText(mAudio.getAlbum());
        } else {
            mTvAudioInfoAlbum.setText("未知");
        }
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
        mTvAudioInfoReadWrite.setText(builder.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnAudioInfoOk:
            dismiss();
            break;

        case R.id.mBtnAudioInfoGoDir:
            dismiss();
            Intent intent = new Intent();
            intent.putExtra("path", mAudio.getPath());
            mActivity.setResult(RESULT_AUDIO, intent);
            mActivity.finish();
            break;
        default:
            break;
        }

    }
}
