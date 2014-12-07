package com.cmcm.greendamexplorer.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

import com.cmcm.greendamexplorer.activity.R;

public class SwipListView extends ListView {

    private Scroller mScroller = null;
    private VelocityTracker mVelocityTracker = null;
    private static final int SNAP_VELOCITY = 600;
    private static final int DURATION = 600;

    private int mDownX = 0;
    private int mDownY = 0;

    private int mAlpha = 255;
    private boolean mIsScrollEnable = true; // 设置是否滑动使能

    private int mItemPosition = 0;
    private View mItemView = null;
    private View mFrontView = null;

    private boolean mIsSlide = false;
    private int mScreenWidth = 0;
    private int mBackgoundResource = R.drawable.list_item_front_selector;

    private OnSwipListItemRemoveListener mRemoveListener = null;

    public SwipListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SwipListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipListView(Context context) {
        super(context);
        init(context);
    }

    public void setOnItemRemoveListener(OnSwipListItemRemoveListener listener) {
        mRemoveListener = listener;
    }

    public boolean getScrollEnable() {
        return mIsScrollEnable;
    }

    public void setScrollEnable(boolean enable) {
        this.mIsScrollEnable = enable;
    }

    private void init(Context context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(outMetrics);

        mScreenWidth = outMetrics.widthPixels;
        BounceInterpolator interpolator = new BounceInterpolator();
        mScroller = new Scroller(context, interpolator);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (!mIsScrollEnable) {
            return false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            addVelocityTracker(event);

            if (mFrontView != null && mFrontView.getScrollX() != 0) {
                mFrontView.scrollTo(0, 0);
                mFrontView.setBackgroundResource(mBackgoundResource);
            }

            // 滚动没有结束就返回
            if (!mScroller.isFinished()) {
                return super.dispatchTouchEvent(event);
            }

            mDownX = (int) event.getX();
            mDownY = (int) event.getY();

            // 判断是按下的哪个Item
            mItemPosition = pointToPosition(mDownX, mDownY);

            // 如果是无效的Item 直接返回
            if (mItemPosition == AdapterView.INVALID_POSITION) {
                return super.dispatchTouchEvent(event);
            }

            // 获取点击的View
            mItemView = getChildAt(mItemPosition - getFirstVisiblePosition());
            mFrontView = mItemView.findViewWithTag("swipe_front");
            break;

        case MotionEvent.ACTION_MOVE:
            int xDiff = (int) Math.abs(event.getX() - mDownX);
            int yDiff = (int) Math.abs(event.getY() - mDownY);

            if (mDownX < mScreenWidth - 5 && xDiff > yDiff + 5) {
                mIsSlide = true; // 设置开始滑动
            }

            break;
        case MotionEvent.ACTION_UP:
            recycleVelocityTracker();
            break;
        default:
            break;
        }
        return super.dispatchTouchEvent(event);
    }

    public enum RemoveDirection {
        LEFT, RIGHT
    }

    public void scrollRight() {
        final int delta = (mScreenWidth + mFrontView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        mScroller.startScroll(mFrontView.getScrollX(), 0, -delta, 0, DURATION);
        mAlpha = 255;
        mFrontView.setBackgroundResource(mBackgoundResource);
        postInvalidate(); // 刷新itemView
    }

    private void scrollLeft(MotionEvent event) {
        // mScroller.startScroll(mFrontView.getScrollX(), 0,
        // -mFrontView.getScrollX(), 0, 0);
        mFrontView.scrollTo(0, 0);
        mFrontView.setBackgroundResource(mBackgoundResource);
        mAlpha = 255;
        dispatchTouchEvent(event);
        // mFrontView.setBackgroundColor(Color.alpha(255));
        // mAlpha = 255;
        // postInvalidate();
        // 滚动动画结束的时候调用回调接口
        if (mRemoveListener == null) {
            throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
        } else {
            mRemoveListener.removeItem(mItemPosition);
            // mFrontView.setBackgroundResource(mBackgoundResource);
            // mFrontView.scrollTo(0, 0);
        }
        postInvalidate(); // 刷新itemView
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    public void scrollByDistanceX(MotionEvent event) {
        // 如果向左滚动的距离大于屏幕的二分之一，就让其删除
        if (mFrontView.getScrollX() >= mScreenWidth / 4) {
            scrollLeft(event);
        } else if (mFrontView.getScrollX() <= -mScreenWidth / 2) {
            scrollRight();
        } else {
            mScroller.startScroll(mFrontView.getScrollX(), 0, -mFrontView.getScrollX(), 0, DURATION);
            mFrontView.setBackgroundResource(mBackgoundResource);
            mAlpha = 255;
        }
    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 有效位置
        if (mIsSlide && mItemPosition != AdapterView.INVALID_POSITION) {
            requestDisallowInterceptTouchEvent(true);// 请求不允许拦截事件

            addVelocityTracker(ev);// 添加速度跟踪
            int x = (int) ev.getX();

            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = mDownX - x;
                mDownX = x;

                // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                if (deltaX > 0 || mFrontView.getScrollX() > 0) {
                    if (mFrontView.getScrollX() < mScreenWidth - 20) {
                        mFrontView.scrollBy((int) Math.round(deltaX * 2.0 / 4), 0);
                    }
                    mAlpha = (int) (255 - mFrontView.getScrollX() / 2.0);
                    mFrontView.setBackgroundColor(Color.argb(mAlpha, 255, 255, 255));
                    mFrontView.setAlpha(mAlpha);

                    if (mRemoveListener != null) {
                        mRemoveListener.cancalRemove(mItemPosition);
                    }
                }
                return true; // 拖动的时候ListView不滚动

            case MotionEvent.ACTION_UP:
                int velocityX = (int) getScrollVelocityX();
                if (velocityX > SNAP_VELOCITY) {
                    scrollRight();
                } else if (velocityX < -SNAP_VELOCITY) {
                    mFrontView.scrollTo(0, 0);
                    mFrontView.setBackgroundResource(mBackgoundResource);
                    scrollLeft(ev);
                } else {
                    scrollByDistanceX(ev);
                }

                recycleVelocityTracker();
                // 手指离开的时候就不响应左右滚动
                mIsSlide = false;
                break;
            }
        }

        // 否则直接交给ListView来处理onTouchEvent事件
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (mScroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            mFrontView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            postInvalidate();

        }
    }

    private int getScrollVelocityX() {
        mVelocityTracker.computeCurrentVelocity(1);
        return (int) mVelocityTracker.getXVelocity();
    }

    private void addVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface OnSwipListItemRemoveListener {
        public void removeItem(int position);

        public void cancalRemove(int position);
    }
}
