package com.cmcm.greendamexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cmcm.greendamexplorer.adapter.DocAndZipsAdapter;
import com.cmcm.greendamexplorer.core.common.FileType;
import com.cmcm.greendamexplorer.core.common.SimpleFileComparator;
import com.cmcm.greendamexplorer.dao.DaoFactory;
import com.cmcm.greendamexplorer.dao.impl.FavoriteDao;
import com.cmcm.greendamexplorer.entity.Favorite;
import com.cmcm.greendamexplorer.fragment.FileCategoryPageFragment;
import com.cmcm.greendamexplorer.utils.DensityUtil;
import com.cmcm.greendamexplorer.utils.FileUtils;
import com.cmcm.greendamexplorer.utils.OpenFileUtil;
import com.cmcm.greendamexplorer.utils.UiUtil;
import com.cmcm.greendamexplorer.view.FileInfoDialog;

public class ZipActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    private ArrayList<String> mZips = new ArrayList<String>();
    private static final int MSG_SHOW_PROGRESS = 0x2001;
    private static final int MSG_FINISH = 0x2002;
    private static final int MSG_UPDATE_DATA = 0x2003;

    private ListView mListView = null;
    private DocAndZipsAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private TextView mTvZipsTitle = null;
    private int mChoosePosition = 0;
    private Dialog mProgressDialog = null;
    private ZipsLoadThread mThread = null;
    

    private View mViewNothing = null;
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_PROGRESS) {

                mProgressDialog = UiUtil.createLoadingDialog(ZipActivity.this, "小样，文件还不少...");
            } else if (msg.what == MSG_UPDATE_DATA) {
                mZips.add((String) msg.obj);
            } else if (msg.what == MSG_FINISH) {
                
                Collections.sort(mZips);

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                if (mZips.size() == 0) {
                    mViewNothing.setVisibility(View.VISIBLE);
                } else {
                    mViewNothing.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        mTvZipsTitle = (TextView) findViewById(R.id.mTvTopTitle);
        mTvZipsTitle.setText("浓缩的就是精华");

        mViewNothing = findViewById(R.id.nothing);
        if (mZips.size() == 0) {
            mViewNothing.setVisibility(View.VISIBLE);
        } else {
            mViewNothing.setVisibility(View.GONE);
        }

        FileUtils.checkFile(mZips);
        Collections.sort(mZips, new SimpleFileComparator());

        mListView = (ListView) findViewById(R.id.mDocListView);
        mAdapter = new DocAndZipsAdapter(this, mZips);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mThread = new ZipsLoadThread();
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
        String path = mZips.get(position);
        File file = new File(path);

        if (file.exists()) {
            Intent intent = OpenFileUtil.openFile(path);
            startActivity(intent);
        } else {
            Toast.makeText(this, "文件已经不存在了~~", Toast.LENGTH_SHORT).show();
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
            FileInfoDialog mInfoDialog = new FileInfoDialog(this, mZips.get(mChoosePosition), true);
            mInfoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mZips.get(mChoosePosition);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mZips.get(mChoosePosition));
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(ZipActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mZips.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ZipActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mZips.get(mChoosePosition);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "文档分享");
            intent.putExtra(Intent.EXTRA_TEXT, "我想分享给你浓缩的精华！");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;
        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
            FavoriteDao dao = DaoFactory.getFavoriteDao(this);
            String path = mZips.get(mChoosePosition);
            if (null != dao.findFavoriteByFullPath(path)) {
                Toast.makeText(this, "已经在收藏夹中了,这个压缩包里面一定有秘密~~", Toast.LENGTH_SHORT).show();
                break;
            }
            File file = new File(path);
            Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_RAR, System.currentTimeMillis(), file.length(), "");
            dao.insertFavorite(favorite);
            Toast.makeText(this, "成功添加到收藏夹！", Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }

    }

    class ZipsLoadThread extends Thread {

        List<String> zips = FileCategoryPageFragment.getZips();

        @Override
        public void run() {

            if (zips.size() > 300) {
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
            }

            for (int i = 0; i < zips.size(); i++) {
                Message msg = new Message();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = zips.get(i);
                mHandler.sendMessage(msg);
            }

            mHandler.sendEmptyMessage(MSG_FINISH);
        }
    }
}
