package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CMImageView extends ImageView {

    private int mRealWidth = 0;
    private int mRealHeight = 0;

    public CMImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CMImageView(Context context) {
        super(context);
    }

    public int getRealWidth() {
        return mRealWidth;
    }

    public int getRealHeight() {
        return mRealHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.AT_MOST) {
            // System.out.println("--->width:AT_MOST" + mRealWidth);
            mRealWidth = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            mRealWidth = specSize;
            // System.out.println("--->width:EXACTLY" + mRealWidth);
        }
        return mRealWidth;
    }

    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.AT_MOST) {
            ;
            // System.out.println("--->Height:AT_MOST" + mRealHeight);
        } else if (specMode == MeasureSpec.EXACTLY) {
            mRealHeight = specSize;
            // System.out.println("--->Height:EXACTLY" + mRealHeight);
        } else {
            mRealHeight = mRealWidth;
            // System.out.println("--->Height:未指定" + mRealHeight);
        }
        return mRealHeight;
    }

}
