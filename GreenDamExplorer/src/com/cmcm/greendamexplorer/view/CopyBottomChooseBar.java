package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.cmcm.greendamexplorer.activity.R;

public class CopyBottomChooseBar extends LinearLayout implements OnClickListener {

    private View mView = null;
    private View mCopyBottomCancel = null;
    private View mCopyBottomEnsure = null;
    private int mHeight = 200;
    private boolean mIsShow = false;

    private IOnBottomChooserBarClickListener mOnBarClickListener = null;

    public CopyBottomChooseBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }
    
    public boolean  isShow() {
        return mIsShow;
    }

    public CopyBottomChooseBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CopyBottomChooseBar(Context context) {
        super(context);
        init();
    }

    public void setOnBottomChooserBarClickListener(IOnBottomChooserBarClickListener listener) {
        mOnBarClickListener = listener;
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.copy_bottom_menu, this, true);
        mCopyBottomCancel = findViewById(R.id.mCopyBottomCancel);
        mCopyBottomEnsure = findViewById(R.id.mCopyBottomEnsure);

        mCopyBottomCancel.setOnClickListener(this);
        mCopyBottomEnsure.setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!mIsShow) {
            hide();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void show() {
        mIsShow = true;
        mView.scrollTo(0, 0);
    }

    public void hide() {
        mIsShow = false;
        mView.scrollTo(0, -mHeight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mCopyBottomEnsure:
            if (mOnBarClickListener != null) {
                mOnBarClickListener.onEnsure(mCopyBottomEnsure);
            }
            hide();
            break;

        case R.id.mCopyBottomCancel:
            if (mOnBarClickListener != null) {
                mOnBarClickListener.onCancel(mCopyBottomCancel);
            }
            hide();
            break;

        default:
            break;
        }
    }

}
