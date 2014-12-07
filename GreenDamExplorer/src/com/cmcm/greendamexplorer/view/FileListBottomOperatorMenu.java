package com.cmcm.greendamexplorer.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmcm.greendamexplorer.activity.R;
import com.cmcm.greendamexplorer.entity.SimpleFileInfo;
import com.cmcm.greendamexplorer.utils.DensityUtil;

public class FileListBottomOperatorMenu extends LinearLayout implements OnClickListener {

    private View mRootView = null;
    private View mFileListBottomOperatorMenu = null;

    private View mOpDel = null;
    private View mOpCopy = null;
    private View mOpMove = null;
    private View mOpSelectAll = null;
    private View mOpMore = null;
    private View mOpAddToFavorite = null;
    private View mOpRename = null;
    private View mOpShare = null;
    private View mOpFileInfo = null;

    private ImageView mIconSelecteAll = null;
    private TextView mTvSelecteAll = null;

    private boolean isShow = false;
    private boolean isShowMore = false;
    private boolean isSelecte = false;

    private int mLayoutHeight = 0;

    private IOnMenuItemClickListener mOnItemClickListener = null;

    public FileListBottomOperatorMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    public FileListBottomOperatorMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileListBottomOperatorMenu(Context context) {
        super(context);
        init(context);
    }

    public boolean isShow() {
        return isShow;
    }

    public boolean isShowMore() {
        return isShowMore;
    }

    public void setOnItemClickListener(IOnMenuItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void init(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.file_list_bottom_oprator_menu, this, true);

        mFileListBottomOperatorMenu = mRootView.findViewById(R.id.mFileListBottomOperatorMenu);

        mOpCopy = mRootView.findViewById(R.id.mOpCopy);
        mOpDel = mRootView.findViewById(R.id.mOpDel);
        mOpMore = mRootView.findViewById(R.id.mOpMore);
        mOpMove = mRootView.findViewById(R.id.mOpMove);
        mOpSelectAll = mRootView.findViewById(R.id.mOpSelectAll);

        mOpAddToFavorite = mFileListBottomOperatorMenu.findViewById(R.id.mOpAddToFavorite);
        mOpRename = mFileListBottomOperatorMenu.findViewById(R.id.mOpReName);
        mOpShare = mFileListBottomOperatorMenu.findViewById(R.id.mOpShare);
        mOpFileInfo = mFileListBottomOperatorMenu.findViewById(R.id.mOpFileInfo);
        mIconSelecteAll = (ImageView) mFileListBottomOperatorMenu.findViewById(R.id.mIconSelecteAll);
        mTvSelecteAll = (TextView) mFileListBottomOperatorMenu.findViewById(R.id.mTvSelecteAll);

        mOpCopy.setOnClickListener(this);
        mOpDel.setOnClickListener(this);
        mOpMore.setOnClickListener(this);
        mOpMove.setOnClickListener(this);
        mOpSelectAll.setOnClickListener(this);
        mOpAddToFavorite.setOnClickListener(this);
        mOpRename.setOnClickListener(this);
        mOpShare.setOnClickListener(this);
        mOpFileInfo.setOnClickListener(this);

        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mLayoutHeight = height;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isShow) {
            hide();
        }
    }

    public void show() {
        isShow = true;
        mRootView.scrollTo(0, -DensityUtil.dip2px(getContext(), 45 * 4 + 6));
        isShowMore = false;
        postInvalidate();
    }

    public void showMore() {
        isShow = true;
        if (isShowMore == false) {
            mRootView.scrollTo(0, 0);
            isShowMore = true;
            postInvalidate();
        } else {
            show();
        }
    }

    public void hide() {
        isShow = false;
        isShowMore = false;
        mRootView.scrollTo(0, -mLayoutHeight);
        postInvalidate();
    }

    public void setSelecteAll() {
        mIconSelecteAll.setImageResource(R.drawable.op_select_all);
        mTvSelecteAll.setText("全选");
        isSelecte = false;
    }

    public void setSelectNothing() {
        mIconSelecteAll.setImageResource(R.drawable.op_select_nothing);
        mTvSelecteAll.setText("取消全选");
        isSelecte = true;
    }

    public boolean hasSelecteAll(List<SimpleFileInfo> infos, List<String> checks) {
        if (infos.size() == checks.size()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.mOpAddToFavorite:
        case R.id.mOpShare:
        case R.id.mOpFileInfo:
        case R.id.mOpReName:
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mFileListBottomOperatorMenu, view);
            }
            break;
        case R.id.mOpSelectAll:
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onSelecteAll(view, isSelecte);
                mOnItemClickListener.onItemClick(mRootView, view);
            }
            break;
        case R.id.mOpMore:
            showMore();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mRootView, view);
            }
            // 穿透
        default:
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mRootView, view);
            }
            break;
        }
    }

}
