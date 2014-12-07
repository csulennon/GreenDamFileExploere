package com.cmcm.greendamexplorer.fragment;

import java.util.ArrayList;
import java.util.List;

import org.lmw.demo.slidingtab.widget.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmcm.greendamexplorer.activity.R;

public class ViewPageFragment extends Fragment {

    private FileListPageFragment mFileListPageFragment = null;
    private FileCategoryPageFragment mFileCategoryPageFragment = null;
    private FavoritePageFragment mFavoritePageFragment = null;

    private ViewPagerAdapter mAdapter = null;
    private ViewPager mViewPager = null;
    public ArrayList<Fragment> mPagerItemList = new ArrayList<Fragment>();

    private View mView = null;
    private PagerSlidingTabStrip mTabs = null;
    public OnPageChangeListener mDelegatePageListener = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        findViews(inflater);
        initFragment();

        mPagerItemList.add(mFileListPageFragment);
        mPagerItemList.add(mFileCategoryPageFragment);
        mPagerItemList.add(mFavoritePageFragment);

        mAdapter = new ViewPagerAdapter(getFragmentManager(), new String[] { "目录", "分类", "收藏" });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs = (PagerSlidingTabStrip) mView.findViewById(R.id.pageTabs);
        mTabs.setViewPager(mViewPager);
        

        mTabs.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(mView.getContext(), "位置" + position, 0).show();
                if(position == 2) {
                    mFavoritePageFragment.reLoadFavoriteList();
                    mFavoritePageFragment.startListAnim();
                } else if (position == 0) {
                    mFileListPageFragment.startAnim();
                } else if(position == 1) {
                    mFileCategoryPageFragment.startPieChartAnim();
                    mFileCategoryPageFragment.refreshUi();
                }
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageScrolled(arg0, arg1, arg2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageScrollStateChanged(arg0);
                }
            }
        });
        return mView;
    }
    
    public FileCategoryPageFragment getFileCategoryPageFragment() {
        return mFileCategoryPageFragment;
    }

    public List<Fragment> getPageFragments() {
        return mPagerItemList;
    }
    
    public FileListPageFragment getFileListPageFragment() {
        return mFileListPageFragment;
    }

    private void findViews(LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.view_pager, null);
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
    }

    private void initFragment() {
        mFileListPageFragment = new FileListPageFragment();
        mFileCategoryPageFragment = new FileCategoryPageFragment();
        mFavoritePageFragment = new FavoritePageFragment();

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public boolean isFirst() {
        if (mViewPager.getCurrentItem() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnd() {
        if (mViewPager.getCurrentItem() == mPagerItemList.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    /* ViewPager适配器 */
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitiles = null;

        public ViewPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitiles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitiles[position];
        }

        @Override
        public int getCount() {
            return mTitiles.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position < mPagerItemList.size()) {
                fragment = mPagerItemList.get(position);
            } else {
                fragment = mPagerItemList.get(0);
            }
            return fragment;
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mDelegatePageListener = listener;
    }
    
    public int getCurrentPageIndex() {
        return mViewPager.getCurrentItem();
    }

    /**
     * 设置显示第几页
     * 
     * @param position
     */
    public void setPage(int position) {
        mViewPager.setCurrentItem(position);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        if(mViewPager.getCurrentItem() == 1) {
            mFileCategoryPageFragment.refreshUi();
            
        }
    }

}
