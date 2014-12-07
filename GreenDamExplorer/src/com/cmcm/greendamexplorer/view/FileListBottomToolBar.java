package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cmcm.greendamexplorer.activity.R;

public class FileListBottomToolBar extends LinearLayout implements OnClickListener {

    private View mRootView = null;

    private View mToolBarNew = null;
    private View mTollBarSort = null;
    private View mToolBarRefresh = null;
    private View mToolBarSetting = null;

    private IOnMenuItemClickListener mOnItemClickListener = null;

    public FileListBottomToolBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FileListBottomToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileListBottomToolBar(Context context) {
        super(context);
        init(context);
    }

    public void setOnItemClickListener(IOnMenuItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.file_list_bottom_menu, this, true);

        mToolBarNew = mRootView.findViewById(R.id.mToolBarNew);
        mTollBarSort = mRootView.findViewById(R.id.mToolBarSort);
        mToolBarRefresh = mRootView.findViewById(R.id.mToolBarRefresh);
        mToolBarSetting = mRootView.findViewById(R.id.mToolBarSetting);

        mToolBarNew.setOnClickListener(this);
        mTollBarSort.setOnClickListener(this);
        mToolBarRefresh.setOnClickListener(this);
        mToolBarSetting.setOnClickListener(this);

        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(mRootView, view);
        }
    }

}
