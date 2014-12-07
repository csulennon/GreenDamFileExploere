package com.cmcm.greendamexplorer.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;

import com.cmcm.greendamexplorer.fragment.IOnBackPressed;
import com.cmcm.greendamexplorer.fragment.LeftMenuFragment;
import com.cmcm.greendamexplorer.fragment.ViewPageFragment;
import com.cmcm.greendamexplorer.view.SlidingMenu;

public class MainActivity extends FragmentActivity {

    SlidingMenu mSlidingMenu = null;
    LeftMenuFragment mLeftFragment = null;
    ViewPageFragment mViewPageFragment = null;
    ActionBar mActionBar = null;
    IOnBackPressed mOnBackPressed = null;// 点击回退键触发

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        init();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);// 主布局(左中右)

        View lefMenuView = getLayoutInflater().inflate(R.layout.left_frame, null);
        View centerView = getLayoutInflater().inflate(R.layout.center_frame, null);
        mSlidingMenu.addViews(lefMenuView, centerView);

        // 替换左中右三页布局为Fragement
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        mLeftFragment = new LeftMenuFragment();
        transaction.replace(R.id.left_frame, mLeftFragment);

        mViewPageFragment = new ViewPageFragment();
        transaction.replace(R.id.center_frame, mViewPageFragment);

        transaction.commit();

    }

    public ViewPageFragment getViewPageFragment() {
        return mViewPageFragment;
    }

    private void initListener() {
        mViewPageFragment.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // Toast.makeText(MainActivity.this, "" + position, 0).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    public void setOnBackPressedListener(IOnBackPressed listener) {
        this.mOnBackPressed = listener;
    }

    public void showLeft() {
        mSlidingMenu.showLeftView();
    }
    
    public void hideLeft() {
        mSlidingMenu.hideLeft();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Process.killProcess(Process.myPid());

    }

    public void goToPage(int position) {
        mViewPageFragment.setPage(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
            if(mSlidingMenu.isShow()) {
                mSlidingMenu.hideLeft();
                return true;
            }

            if (mViewPageFragment.getCurrentPageIndex() == 2) {
                goToPage(1);
                return true;
            } else if (mViewPageFragment.getCurrentPageIndex() == 1) {
                goToPage(0);
                return true;
            } 

            if (mOnBackPressed != null) {
                boolean needExit = mOnBackPressed.onBackPressed();
                System.out.println(needExit);
                if (needExit) {
                    Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("是否退出？");
                    dialog.setNegativeButton("不退出", null);
                    dialog.setPositiveButton("退出", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    });
                    dialog.show();
//                    super.onKeyDown(keyCode, event);
                } else {
                    return true;
                }
            }
        }
        return true;
    }

}
