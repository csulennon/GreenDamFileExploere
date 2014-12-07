package com.cmcm.greendamexplorer.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cmcm.greendamexplorer.activity.R;

public class PieChart extends View {

    public interface OnSelectedLisenter {
        public abstract void onSelected(int iSelectedIndex);
    }

    private OnSelectedLisenter mOnSelectedListener = null;

    private static final String TAG = PieChart.class.getName();
    public static final String ERROR_NOT_EQUAL_TO_100 = "NOT_EQUAL_TO_100";
    private static final int DEGREE_360 = 360;
    private static String[] PIE_COLORS = null;
    private static int mColorListSize = 0;

    private Paint mPaintPieFill;
    private Paint mPaintPieBorder;
    private ArrayList<Float> mPercentages = new ArrayList<Float>();

    private int mDisplayWidth, mDisplayHeight;
    private int mSelectedIndex = -1;
    private int mCenterWidth = 0;
    private int mShift = 0;
    private int mMargin = 0;
    private int mDataSize = 0;

    private RectF mRectF = null;

    private float mDensity = 0.0f;
    private float mStartAngle = 0.0f;
    private float mEndAngle = 0.0f;

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        PIE_COLORS = getResources().getStringArray(R.array.colors);
        mColorListSize = PIE_COLORS.length;

        fnGetDisplayMetrics(context);
        mShift = (int) fnGetRealPxFromDp(30);
        mMargin = (int) fnGetRealPxFromDp(40);

        // 画圆
        mPaintPieFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPieFill.setStyle(Paint.Style.FILL);

        // 画边框
        mPaintPieBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPieBorder.setStyle(Paint.Style.STROKE);
        mPaintPieBorder.setStrokeWidth(fnGetRealPxFromDp(3));
        mPaintPieBorder.setColor(Color.WHITE);

    }

    public void setOnSelectedListener(OnSelectedLisenter listener) {
        this.mOnSelectedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mDataSize; i++) {

            if (i >= mColorListSize) {
                mPaintPieFill.setColor(Color.parseColor(PIE_COLORS[i % mColorListSize]));
            } else {
                mPaintPieFill.setColor(Color.parseColor(PIE_COLORS[i]));
            }

            mEndAngle = mPercentages.get(i);

            mEndAngle = mEndAngle / 100 * DEGREE_360;

            if (mSelectedIndex == i) {
                canvas.save(Canvas.MATRIX_SAVE_FLAG);
                float fAngle = mStartAngle + mEndAngle / 2;
                double dxRadius = Math.toRadians((fAngle + DEGREE_360) % DEGREE_360);
                float fY = (float) Math.sin(dxRadius);
                float fX = (float) Math.cos(dxRadius);
                canvas.translate(fX * mShift, fY * mShift);
            }

            canvas.drawArc(mRectF, mStartAngle, mEndAngle, true, mPaintPieFill);

            if (mSelectedIndex == i) {
                canvas.drawArc(mRectF, mStartAngle, mEndAngle, true, mPaintPieBorder);
                canvas.restore();
            }
            mStartAngle = mStartAngle + mEndAngle;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mDisplayWidth = MeasureSpec.getSize(widthMeasureSpec);
        mDisplayHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mDisplayWidth > mDisplayHeight) {
            mDisplayWidth = mDisplayHeight;
        }
        mCenterWidth = mDisplayWidth / 2;
        int iR = mCenterWidth - mMargin;
        if (mRectF == null) {
            mRectF = new RectF(mCenterWidth - iR, // top
                    mCenterWidth - iR, // left
                    mCenterWidth + iR, // rights
                    mCenterWidth + iR); // bottom
        }
        setMeasuredDimension(mDisplayWidth, mDisplayWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        double dx = Math.atan2(event.getY() - mCenterWidth, event.getX() - mCenterWidth);
        float fDegree = (float) (dx / (2 * Math.PI) * DEGREE_360);
        fDegree = (fDegree + DEGREE_360) % DEGREE_360;

        float fSelectedPercent = fDegree * 100 / DEGREE_360;

        float fTotalPercent = 0;
        for (int i = 0; i < mDataSize; i++) {
            fTotalPercent += mPercentages.get(i);
            if (fTotalPercent > fSelectedPercent) {
                mSelectedIndex = i;
                break;
            }
        }
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onSelected(mSelectedIndex);
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private void fnGetDisplayMetrics(Context cxt) {
        final DisplayMetrics dm = cxt.getResources().getDisplayMetrics();
        mDensity = dm.density;
    }

    private float fnGetRealPxFromDp(float fDp) {
        return (mDensity != 1.0f) ? mDensity * fDp : fDp;
    }

    public void setAdapter(ArrayList<Float> alPercentage) throws Exception {
        this.mPercentages = alPercentage;
        mDataSize = alPercentage.size();
        float fSum = 0;
        for (int i = 0; i < mDataSize; i++) {
            fSum += alPercentage.get(i);
        }
        if (fSum != 100) {
            Log.e(TAG, ERROR_NOT_EQUAL_TO_100);
            mDataSize = 0;
            throw new Exception(ERROR_NOT_EQUAL_TO_100);
        }

    }

}
