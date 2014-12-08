package com.cmcm.greendamexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.cmcm.greendamexplorer.adapter.AudioListViewAdapter;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.common.MediaResourceManager;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Audio;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.fragment.FileCategoryPageFragment;
import com.cmcm.greendamexplorer.utils.DensityUtil;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.UiUtil;
import com.cmcm.greendamexplorer.view.AudioInfoDialog;

public class AudioActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    protected static final int MSG_UPDATE_DATA = 0x1031;

    protected static final int MSG_PRE_LOAD = 0x1032;

    protected static final int MSG_FINISH = 0x1033;

    private List<Audio> mAudios = null;

    private ListView mListView = null;
    private AudioListViewAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNothing = null;
    private int mChoosePosition = 0;
    private Dialog mProgressDialog = null;

    private AudioLoadThread mThread = null;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (msg.what == MSG_PRE_LOAD) {
                mProgressDialog.show();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mAudios.add((Audio) msg.obj);
                if (mAudios.size() % 15 == 0) {
                    mAdapter.notifyDataSetChanged();
                }
            } else if (msg.what == MSG_FINISH) {
                Collections.sort(mAudios, new Comparator<Audio>() {
                    @Override
                    public int compare(Audio lhs, Audio rhs) {
                        return lhs.getPath().compareTo(rhs.getPath());
                    }
                });
                mProgressDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                if (mAudios.size() == 0) {
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
        mAudios = new ArrayList<Audio>();
        setContentView(R.layout.activity_audio);
        mProgressDialog = UiUtil.createLoadingDialog(this, "正在为您加载音乐...");
        mViewNothing = findViewById(R.id.nothing);

        mListView = (ListView) findViewById(R.id.mAudioListView);
        mAdapter = new AudioListViewAdapter(this, mListView, mAudios);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mThread = new AudioLoadThread();
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
        Intent intent = OpenFileUtil.openFile(mAudios.get(position).getPath());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mChoosePosition = position;
        showWindow(view, position);
        return false;
    }

    @SuppressWarnings("deprecation")
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

        mVideoPopFavorite.setOnClickListener(this);
        mVideoPopInfo.setOnClickListener(this);
        mVideoPopDelete.setOnClickListener(this);
        mVideoPopShare.setOnClickListener(this);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + DensityUtil.getWindowSize()[0] / 3, location[1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mVideoPopInfo:
            mPopupWindow.dismiss();
            AudioInfoDialog mInfoDialog = new AudioInfoDialog(this, mAudios.get(mChoosePosition));
            mInfoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mAudios.get(mChoosePosition).getTilte();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mAudios.get(mChoosePosition).getPath());
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(AudioActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mAudios.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AudioActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mAudios.get(mChoosePosition).getPath();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "音乐分享");
            intent.putExtra(Intent.EXTRA_TEXT, "我想分享给你好心情！");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;
        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
            FavoriteDao dao = DaoFactory.getFavoriteDao(this);
            String path = mAudios.get(mChoosePosition).getPath();
            if (null != dao.findFavoriteByFullPath(path)) {
                Toast.makeText(this, "已经在收藏夹中了,你一定特别喜欢这首歌", Toast.LENGTH_SHORT).show();
                break;
            }
            File file = new File(path);
            Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_MP3, System.currentTimeMillis(), file.length(), "");
            dao.insertFavorite(favorite);
            Toast.makeText(this, "成功添加到收藏夹！", Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }

    }

    class AudioLoadThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_PRE_LOAD);

            Cursor c = null;
            try {
                FileCategoryPageFragment.mAllAudioSize = 0;
                c = AudioActivity.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                while (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                    if (!FileUtils.isExists(path)) {
                        continue;
                    }

                    int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 时间
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                    int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时间
                    long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                    String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                    String tilte = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                    int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    FileCategoryPageFragment.mAllAudioSize += size;

                    Audio audio = new Audio(id, path, tilte, artist, album, albumId, duration, size);

                    Message msg = new Message();
                    msg.what = MSG_UPDATE_DATA;
                    msg.obj = audio;
                    mHandler.sendMessage(msg);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mHandler.sendEmptyMessage(MSG_FINISH);
                if (c != null) {
                    c.close();
                }
            }

        }
    }

}
