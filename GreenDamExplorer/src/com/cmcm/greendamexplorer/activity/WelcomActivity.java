package com.cmcm.greendamexplorer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.cmcm.greendamexplorer.utils.SharedPreferenceUtil;
import com.cmcm.greendamexplorer.view.FeatureAnimationListener;
import com.cmcm.greendamexplorer.view.IObservableScrollView;
import com.cmcm.greendamexplorer.view.IOnScrollChangedListener;

public class WelcomActivity extends Activity implements OnClickListener, OnGlobalLayoutListener, IOnScrollChangedListener {
    private IObservableScrollView mScrollView;
    private View mAnimView;
    private int mScrollViewHeight;
    private int mStartAnimateTop;
    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        mScrollView = (IObservableScrollView) this.findViewById(R.id.mSwelcomeScrollView);
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mScrollView.setOnScrollChangedListener(this);

        mAnimView = this.findViewById(R.id.anim1);
        mAnimView.setVisibility(View.INVISIBLE);

        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onGlobalLayout() {
        mScrollViewHeight = mScrollView.getHeight();
        mStartAnimateTop = mScrollViewHeight / 3 * 2;
    }

    boolean hasStart = false;

    @Override
    public void onScrollChanged(int top, int oldTop) {
        int animTop = mAnimView.getTop() - top;

        if (top > oldTop) {
            if (animTop < mStartAnimateTop && !hasStart) {
                Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.feature_anim2scale_in);
                anim1.setAnimationListener(new FeatureAnimationListener(mAnimView, true));

                mAnimView.startAnimation(anim1);
                hasStart = true;
            }
        } else {
            if (animTop > mStartAnimateTop && hasStart) {
                Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.feature_alpha_out);
                anim1.setAnimationListener(new FeatureAnimationListener(mAnimView, false));

                mAnimView.startAnimation(anim1);
                hasStart = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_start:
            if (SharedPreferenceUtil.getFirstTimeUse()) {
                SharedPreferenceUtil.setFirstTimeUse(false);
                Intent intent = new Intent(WelcomActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);
            } else {
                finish();
            }
            break;

        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
