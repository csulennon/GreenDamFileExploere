package com.cmcm.greendamexplorer.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.cmcm.greendamexplorer.utils.DensityUtil;

public class SlidingMenu extends RelativeLayout {

	private static final int VELOCITY = 10;
	private static final int DURATION = 300;

	private View mSlidingView = null;

	private View mMenuView = null;

	private Context mContext;
	private Scroller mScroller = null;
	private VelocityTracker mVelocityTracker; // 速度跟踪，用来计算速度

	private int mScreenWidth;
	private int mLeftScrollMaxWidth;
	private int mTouchSlop;

	float mLastX = 0;
	float mLastY = 0;

	float downX = 0;
	float downY = 0;

	private boolean mIsBeingDragged = true;

	public SlidingMenu(Context context) {
		super(context);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		DecelerateInterpolator interpolator = new DecelerateInterpolator();
		mScroller = new Scroller(getContext(), interpolator);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		WindowManager windowManager = ((Activity) context).getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();

		Point point = new Point();
		display.getSize(point);
		mScreenWidth = point.x;

		mLeftScrollMaxWidth = mScreenWidth * 4 / 5;
		
		
	}

	public void addViews(View left, View center) {
		setLeftMenuView(left);
		setCenterView(center);
	}

	private void setLeftMenuView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		addView(view, behindParams);
		mMenuView = view;
		mMenuView.setPadding(0, 0, mScreenWidth / 5, 0);
	}

	/**
	 * 设置中间的内容布局
	 * 
	 * @param centerView
	 */
	private void setCenterView(View centerView) {
		LayoutParams centerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);// 实际参数
		addView(centerView, centerParams);

		mSlidingView = centerView;
		mSlidingView.bringToFront();
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();// 请求刷新
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = mSlidingView.getScrollX();
				int x = mScroller.getCurrX();
				if (oldX != x) {
					if (mSlidingView != null) {
						mSlidingView.scrollTo(x, 0);
					}
				}
			}
		}
	}

	/* 拦截touch事件 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		float oldScrollX = mSlidingView.getScrollX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mIsBeingDragged = false;
			break;

		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastY);

			if ((mLastX < mScreenWidth - DensityUtil.dip2px(mContext, 20) && mLastX > DensityUtil.dip2px(mContext, 20)) && oldScrollX == 0) {
				mIsBeingDragged = false;
				return false;
			}

			if (xDiff > mTouchSlop && xDiff > yDiff) {// 判断开始
				oldScrollX = mSlidingView.getScrollX();
				if (oldScrollX < 0) {// 手指向右滑动
					mIsBeingDragged = true;
					mLastX = x;
				} else {// 向左滑动
					if (dx > 0) {
						mIsBeingDragged = true;
						mLastX = x;
					}
				}
			}
			break;

		}
		return mIsBeingDragged;
	}

	/* 处理拦截后的touch事件 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// 先终止动画
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastX = x;
			mLastY = y;

			if (mSlidingView.getScrollX() == -mLeftScrollMaxWidth && mLastX < mLeftScrollMaxWidth) {
				return false;
			}

			if (mSlidingView.getScrollX() == mLeftScrollMaxWidth && mLastX > mLeftScrollMaxWidth) {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsBeingDragged) {
				final float deltaX = mLastX - x;
				mLastX = x;
				float oldScrollX = mSlidingView.getScrollX();
				float scrollX = oldScrollX + deltaX;
				if (scrollX > 0)
					scrollX = 0;

				if (deltaX < 0 && oldScrollX < 0) { // left view
					final float leftBound = 0;
					final float rightBound = -mLeftScrollMaxWidth;
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}
				}

				if (mSlidingView != null) {
					mSlidingView.scrollTo((int) scrollX, mSlidingView.getScrollY());
				}

			}
			break;
		case MotionEvent.ACTION_UP:
			if (mSlidingView.getScaleX() != 0) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(100);
				float xVelocity = velocityTracker.getXVelocity();// 滑动的速度
				int dx = 0;

				int oldScrollX = mSlidingView.getScrollX();
				if (xVelocity > VELOCITY) {
					dx = -mLeftScrollMaxWidth - oldScrollX;
				} else if (xVelocity <= -VELOCITY) {
					dx = -oldScrollX;
				} else if (oldScrollX < -mLeftScrollMaxWidth / 2) {
					dx = -mLeftScrollMaxWidth - oldScrollX;
				} else if (oldScrollX >= -mLeftScrollMaxWidth / 2) {
					dx = -oldScrollX;
				}
				smoothScrollTo(dx);
			}
			break;
		}
		return true;
	}

	void smoothScrollTo(int dx) {
		int oldScrollX = mSlidingView.getScrollX();
		mScroller.startScroll(oldScrollX, mSlidingView.getScrollY(), dx, mSlidingView.getScrollY(), DURATION);
		if(mSlidingView.getScrollX() != 0) {
			mSlidingView.scrollTo(0, 0);
		}
		invalidate();
	}

	public boolean isShow() {
		return mSlidingView.getScrollX() != 0;
	}

	/* 显示左侧边的view */
	public void showLeftView() {
		smoothScrollTo(-mLeftScrollMaxWidth);
	}

	public void hideLeft() {
		int oldScrollX = mSlidingView.getScrollX();
		smoothScrollTo(-oldScrollX);
	}
}
