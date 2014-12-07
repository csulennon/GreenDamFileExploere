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

public class DocumentsActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

    private static final int MSG_SHOW_PROGRESS = 0x2001;
    private static final int MSG_FINISH = 0x2002;
    private static final int MSG_UPDATE_DATA = 0x2003;

    private ArrayList<String> mDocs = new ArrayList<String>();

    private ListView mListView = null;
    private DocAndZipsAdapter mAdapter = null;
    private PopupWindow mPopupWindow;
    private View mPopView = null;
    private View mVideoPopInfo = null;
    private View mVideoPopDelete = null;
    private View mVideoPopShare = null;
    private View mVideoPopFavorite = null;
    private View mViewNothing = null;
    private int mChoosePosition = 0;
    private Dialog mProgressDialog = null;
    private DocumentLoadThread mThread = new DocumentLoadThread();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_PROGRESS) {
                
                mProgressDialog = UiUtil.createLoadingDialog(DocumentsActivity.this, "小样，文件还不少...");
            } else if(msg.what == MSG_UPDATE_DATA){
                mDocs.add((String) msg.obj);
            }else if(msg.what == MSG_FINISH) {
                Collections.sort(mDocs);
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                
                if (mDocs.size() == 0) {
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
        mViewNothing = findViewById(R.id.nothing);
        
        FileUtils.checkFile(mDocs);
        Collections.sort(mDocs, new SimpleFileComparator());

        mListView = (ListView) findViewById(R.id.mDocListView);
        mAdapter = new DocAndZipsAdapter(this, mDocs);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
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
        String path = mDocs.get(position);
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
            FileInfoDialog mInfoDialog = new FileInfoDialog(this, mDocs.get(mChoosePosition), true);
            mInfoDialog.show();
            break;

        case R.id.mVideoPopDelete:
            mPopupWindow.dismiss();
            String fileName = mDocs.get(mChoosePosition);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提醒！").setMessage("你确定要删除 " + fileName + "吗？");
            dialog.setNegativeButton("取消", null);
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File file = new File(mDocs.get(mChoosePosition));
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(DocumentsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mDocs.remove(mChoosePosition);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DocumentsActivity.this, "很遗憾，没有帮您完成任务~~", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            break;

        case R.id.mVideoPopShare:
            mPopupWindow.dismiss();
            Intent intent = new Intent(Intent.ACTION_SEND);
            String filePath = mDocs.get(mChoosePosition);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "文档分享");
            intent.putExtra(Intent.EXTRA_TEXT, "我想分享给你最无价的知识！");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getTitle()));
            break;

        case R.id.mVideoPopFavorite:
            mPopupWindow.dismiss();
            FavoriteDao dao = DaoFactory.getFavoriteDao(this);
            String path = mDocs.get(mChoosePosition);
            if (null != dao.findFavoriteByFullPath(path)) {
                Toast.makeText(this, "已经在收藏夹中了,你一定特别喜欢这个文件~~", Toast.LENGTH_SHORT).show();
                break;
            }
            File file = new File(path);
            Favorite favorite = new Favorite(path, file.getName(), "", FileType.TYPE_UNKNOWN, System.currentTimeMillis(), file.length(), "");
            dao.insertFavorite(favorite);
            Toast.makeText(this, "成功添加到收藏夹！", Toast.LENGTH_SHORT).show();
            break;

        default:
            break;
        }
    }

    class DocumentLoadThread extends Thread {
        
        List<String> documents = FileCategoryPageFragment.getDocuments();
        @Override
        public void run() {
            
            if(documents.size() > 300) {
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
            }
            
            for (int i = 0; i < documents.size(); i++) {
                Message msg = new Message();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = documents.get(i);
                mHandler.sendMessage(msg );
            }
            
            mHandler.sendEmptyMessage(MSG_FINISH);
        }
    }

}
