package com.cmcm.greendamexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.cmcm.greendamexplorer.adapter.VideoGridViewAdapter;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.entity.Video;
import com.cmcm.greendamexplorer.fragment.FileCategoryPageFragment;
import com.cmcm.greendamexplorer.utils.DensityUtil;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.UiUtil;
import com.cmcm.greendamexplorer.view.VideoInfoDialog;

public class VideoActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    protected static final int MSG_PRE_LOAD = 0x1020;

    protected static final int MSG_UPDATE_DATA = 0x1021;

    protected static final int MSG_FINSH_LOAD = 0x1023;

    private List<Video> mVideos = new ArrayList<Video>();

    private GridView mGridView = null;
    private VideoGridViewAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNothing = null;
    private int mChoosePosition = 0;

    private Dialog mDialog = null;

    private VideoLoadThread mThread = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_PRE_LOAD) {
                mDialog = UiUtil.createLoadingDialog(VideoActivity.this, "视频还真不少呢，看我玩命加载...");
                mDialog.show();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mVideos.add((Video) msg.obj);
                if (mVideos.size() % 10 == 0) {
                    mAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == MSG_FINSH_LOAD) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                mAdapter.notifyDataSetChanged();
                if (mVideos.size() == 0) {
                    mViewNothing.setVisibility(View.VISIBLE);
                } else {
                    mViewNothing.setVisibility(View.GONE);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        mViewNothing = findViewById(R.id.nothing);

        mGridView = (GridView) findViewById(R.id.mVideoGrideView);
        mAdapter = new VideoGridViewAdapter(this, mVideos, mGridView);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mThread = new VideoLoadThread();
        mThread.start();
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
        String path = mVideos.get(position).getPath();
        File file = new File(path);
        if (file.exists()) {
            Intent intent = OpenFileUtil.openFile(path);
            startActivity(intent);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mChoosePosition = position;
        showWindow(view, position);
        return false;
    }

    private void showWindow(View parent, int position) {

        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPopView = layoutInflater.inflate(R.layout.pop_video_op, null);
            mPopupWindow = new PopupWindow(mPopView, DensityUtil.dip2px(this, 180), DensityUtil.dip2px(this, 45));
        }
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);

        mVideoPopInfo = mPopView.findViewById(R.id.mVideoPopInfo);
        mVideoPopDelete = mPopView.findViewById(R.id.mVideoPopDelete);
        mVideoPopShare = mPopView.findViewById(R.id.mVideoPopShare);
        mVideoPopFavorite = mPopView.findViewById(R.id.mVideoPopFavorite);

        mVideoPopInfo.setOnClickListener(this);
        mVideoPopDelete.setOnClickListener(this);
        mVideoPopShare.setOnClickListener(this);
        mVideoPopFavorite.setOnClickListener(this);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + DensityUtil.dip2px(this, 10));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mVideoPopInfo:
            mPopupWindow.dismiss();
            VideoInfoDialog infoDialog = new VideoInfoDialog(this, mVideos.get(mChoosePosition));
            infoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mVideos.get(mChoosePosition).getName();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mVideos.get(mChoosePosition).getPath());
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(VideoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mVideos.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(VideoActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mVideos.get(mChoosePosition).getPath();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "视频分享");
            intent.putExtra(Intent.EXTRA_TEXT, "我想分享给你好心情！");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;

        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
            FavoriteDao dao = DaoFactory.getFavoriteDao(this);
            String path = mVideos.get(mChoosePosition).getPath();
            if (null != dao.findFavoriteByFullPath(path)) {
                Toast.makeText(this, "已经在收藏夹中了,你一定特别喜欢这个视频", Toast.LENGTH_SHORT).show();
                break;
            }
            File file = new File(path);
            Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_MP4, System.currentTimeMillis(), file.length(), "");
            dao.insertFavorite(favorite);
            Toast.makeText(this, "成功添加到收藏夹！", Toast.LENGTH_SHORT).show();
            break;
        default:
            break;
        }
    }

    class VideoLoadThread extends Thread {

        @Override
        public void run() {
            List<Video> videos = FileCategoryPageFragment.getVideos();
            if (videos.size() > 20) {
                mHandler.sendEmptyMessage(MSG_PRE_LOAD);
            }
            FileCategoryPageFragment.mAllVideoSize = 0;
            for (int i = 0; i < videos.size(); i++) {
                Message msg = new Message();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = videos.get(i);
                mHandler.sendMessage(msg);
            }
            mHandler.sendEmptyMessage(MSG_FINSH_LOAD);

        }

    }

}
