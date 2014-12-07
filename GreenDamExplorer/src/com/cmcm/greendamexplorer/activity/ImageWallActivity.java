package com.cmcm.greendamexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.cmcm.greendamexplorer.adapter.ImageWallGridAdapter;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.fragment.FileCategoryPageFragment;
import com.cmcm.greendamexplorer.utils.DensityUtil;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.UiUtil;
import com.cmcm.greendamexplorer.view.FileInfoDialog;

public class ImageWallActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    public static final int MSG_PRE_LOAD = 0x1010;
    protected static final int MSG_FINSH_LOAD = 0x1011;
    public static final int MSG_UPDATE_DATA = 0x1012;
    private GridView mGridView = null;
    private ImageWallGridAdapter mAdapter = null;
    private List<String> mImages = new ArrayList<String>();
    private ContentResolver mContentResolver = null;

    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNoting = null;
    private int mChoosePosition = 0;

    private Dialog mDialog = null;

    private ImageLoadThread mThread = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_PRE_LOAD) {
                mDialog = UiUtil.createLoadingDialog(ImageWallActivity.this, "图片还真不少，您一定是位文艺青年!");
                mDialog.show();
            } else if (msg.what == MSG_UPDATE_DATA) {
                mImages.add((String) msg.obj);
            } else if (msg.what == MSG_FINSH_LOAD) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                mAdapter.notifyDataSetChanged();
                if (mImages.size() == 0) {
                    mViewNoting.setVisibility(View.VISIBLE);
                } else {
                    mViewNoting.setVisibility(View.GONE);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_wall);
        mDialog = UiUtil.createLoadingDialog(this, "你的图片好多...");
        mViewNoting = findViewById(R.id.nothing);

        mContentResolver = getContentResolver();
        mGridView = (GridView) findViewById(R.id.mListViewImageWall);
        mAdapter = new ImageWallGridAdapter(ImageWallActivity.this, mImages, mGridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mThread = new ImageLoadThread();
        mThread.start();

    }

    public void back(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class ImageLoadThread extends Thread {

        @Override
        public void run() {
            List<String> pictures = FileCategoryPageFragment.getPictures();
            if (pictures.size() > 500) {
                mHandler.sendEmptyMessage(MSG_PRE_LOAD);
            }

            for (int i = 0; i < pictures.size(); i++) {
                Message msg = new Message();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = pictures.get(i);
                mHandler.sendMessage(msg);
            }
            mHandler.sendEmptyMessage(MSG_FINSH_LOAD);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path = mImages.get(position);
        File file = new File(path);

        if (file.exists()) {
            Intent intent = OpenFileUtil.openFile(path);
            startActivity(intent);
        } else {
            Toast.makeText(this, "图片已经不存在了~~", Toast.LENGTH_SHORT).show();
        }
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
            FileInfoDialog mInfoDialog = new FileInfoDialog(this, mImages.get(mChoosePosition), true);
            mInfoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mImages.get(mChoosePosition);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mImages.get(mChoosePosition));
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(ImageWallActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mImages.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ImageWallActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mImages.get(mChoosePosition);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "图片");
            intent.putExtra(Intent.EXTRA_TEXT, "想分享给你我的全世界~~");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;

        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
            FavoriteDao dao = DaoFactory.getFavoriteDao(this);
            String path = mImages.get(mChoosePosition);
            if (null != dao.findFavoriteByFullPath(path)) {
                Toast.makeText(this, "已经在收藏夹中了,这张图片一定对你特别重要~~", Toast.LENGTH_SHORT).show();
                break;
            }
            File file = new File(path);
            Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_APK, System.currentTimeMillis(), file.length(), "");
            dao.insertFavorite(favorite);
            Toast.makeText(this, "成功添加到收藏夹！", Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }

    }

}
